import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import kotlinx.serialization.json.*
import java.io.File
import java.util.Locale

open class GenerateContractTask : DefaultTask() {

    @TaskAction
    fun generate() {
        // When running as a task in the root project, project.rootDir is the project root (e.g. /app)
        val projectDir = project.rootDir
        val contractFile = File(projectDir, "api_contract.json")

        if (!contractFile.exists()) {
             throw RuntimeException("api_contract.json not found at ${contractFile.absolutePath}")
        }

        val jsonString = contractFile.readText()
        val json = Json.parseToJsonElement(jsonString).jsonObject

        val outputDir = File(projectDir, "src/main/kotlin/com/quizbackend/contracts/generated")
        outputDir.mkdirs()

        // Generate DTOs
        val dtos = json["dtos"]?.jsonObject ?: emptyMap()
        val enums = json["enums"]?.jsonObject ?: emptyMap()
        generateDTOs(outputDir, dtos, enums)

        // Generate Services & Routes
        val routes = json["routes"]?.jsonArray ?: emptyList()
        generateServicesAndRoutes(outputDir, routes)

        // Generate OpenAPI
        generateOpenApi(projectDir, json)

        // Generate Dart Client
        generateDartClient(projectDir, json)
    }

    private fun generateOpenApi(projectDir: File, json: JsonObject) {
        val dtos = json["dtos"]?.jsonObject ?: emptyMap()
        val enums = json["enums"]?.jsonObject ?: emptyMap()
        val routes = json["routes"]?.jsonArray ?: emptyList()

        val openApi = buildJsonObject {
            put("openapi", "3.0.0")
            putJsonObject("info") {
                put("title", json["specificationName"]?.jsonPrimitive?.content ?: "API")
                put("version", json["version"]?.jsonPrimitive?.content ?: "1.0.0")
            }

            putJsonObject("paths") {
                val routesByPath = routes.groupBy { it.jsonObject["path"]!!.jsonPrimitive.content }

                routesByPath.forEach { (path, routeList) ->
                    putJsonObject(path) {
                        routeList.forEach { routeElement ->
                            val route = routeElement.jsonObject
                            val method = route["method"]!!.jsonPrimitive.content.lowercase()
                            val bodyType = route["bodyType"]!!.jsonPrimitive.content
                            val paramsType = route["paramsType"]!!.jsonPrimitive.content
                            val responseType = route["responseType"]!!.jsonPrimitive.content
                            val requiresAuth = route["requiresAuth"]?.jsonPrimitive?.boolean ?: false

                            putJsonObject(method) {
                                put("operationId", generateOperationId(method, path))

                                if (requiresAuth) {
                                    putJsonArray("security") {
                                        addJsonObject {
                                            putJsonArray("bearerAuth") {}
                                        }
                                    }
                                }

                                // Parameters
                                val paramsList = buildJsonArray {
                                    val paramDto = dtos[paramsType]?.jsonObject
                                    if (paramDto != null) {
                                        val properties = paramDto["properties"]?.jsonArray ?: emptyList()
                                        properties.forEach { prop ->
                                            val p = prop.jsonObject
                                            val pName = p["name"]!!.jsonPrimitive.content
                                            val pType = p["type"]!!.jsonPrimitive.content
                                            val isNullable = p["isNullable"]?.jsonPrimitive?.boolean == true

                                            val isPath = path.contains("{$pName}")
                                            val paramIn = if (isPath) "path" else "query"

                                            addJsonObject {
                                                put("name", pName)
                                                put("in", paramIn)
                                                put("required", isPath || !isNullable)
                                                putJsonObject("schema") {
                                                    fillTypeSchema(pType)
                                                }
                                            }
                                        }
                                    }
                                }
                                if (paramsList.isNotEmpty()) {
                                    put("parameters", paramsList)
                                }

                                // Request Body
                                if (method in listOf("post", "put", "patch", "delete") && bodyType != "EmptyRequestDTO") {
                                    putJsonObject("requestBody") {
                                        put("required", true)
                                        putJsonObject("content") {
                                            putJsonObject("application/json") {
                                                putJsonObject("schema") {
                                                    put("\$ref", "#/components/schemas/$bodyType")
                                                }
                                            }
                                        }
                                    }
                                }

                                // Responses
                                putJsonObject("responses") {
                                    putJsonObject("200") {
                                        put("description", "Successful response")
                                        putJsonObject("content") {
                                            putJsonObject("application/json") {
                                                putJsonObject("schema") {
                                                    put("type", "object")
                                                    putJsonObject("properties") {
                                                        putJsonObject("success") { put("type", "boolean") }
                                                        putJsonObject("message") { put("type", "string"); put("nullable", true) }
                                                        putJsonObject("error") { put("\$ref", "#/components/schemas/ErrorDetailsDTO"); put("nullable", true) }
                                                        putJsonObject("data") {
                                                            if (responseType == "T" || responseType == "Unit" || responseType == "Void") {
                                                                put("type", "object")
                                                                put("nullable", true)
                                                            } else {
                                                                put("\$ref", "#/components/schemas/$responseType")
                                                                put("nullable", true)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            putJsonObject("components") {
                putJsonObject("securitySchemes") {
                    putJsonObject("bearerAuth") {
                        put("type", "http")
                        put("scheme", "bearer")
                        put("bearerFormat", "JWT")
                    }
                }

                putJsonObject("schemas") {
                    // Enums
                    enums.forEach { (name, element) ->
                        putJsonObject(name) {
                            put("type", "string")
                            putJsonArray("enum") {
                                element.jsonObject["values"]?.jsonArray?.forEach { add(it) }
                            }
                        }
                    }

                    // DTOs
                    dtos.forEach { (name, element) ->
                        if (name == "DTOResponse" || name == "DTOParams") return@forEach

                        val dtoObj = element.jsonObject
                        val properties = dtoObj["properties"]?.jsonArray ?: emptyList()

                        putJsonObject(name) {
                            put("type", "object")
                            putJsonObject("properties") {
                                properties.forEach { prop ->
                                    val p = prop.jsonObject
                                    val pName = p["name"]!!.jsonPrimitive.content
                                    val pType = p["type"]!!.jsonPrimitive.content
                                    val isNullable = p["isNullable"]?.jsonPrimitive?.boolean == true

                                    putJsonObject(pName) {
                                        fillTypeSchema(pType)
                                        if (isNullable) put("nullable", true)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        File(projectDir, "openapi.json").writeText(openApi.toString())
    }

    private fun JsonObjectBuilder.fillTypeSchema(type: String) {
        when {
            type == "Int" || type == "Long" -> {
                put("type", "integer")
                if (type == "Long") put("format", "int64")
            }
            type == "String" -> put("type", "string")
            type == "Boolean" -> put("type", "boolean")
            type == "Double" || type == "Float" -> {
                put("type", "number")
                if (type == "Double") put("format", "double")
            }
            type.startsWith("List<") -> {
                val innerType = type.removePrefix("List<").removeSuffix(">")
                put("type", "array")
                putJsonObject("items") {
                    fillTypeSchema(innerType)
                }
            }
            type == "T" -> {
                put("type", "object")
            }
            else -> {
                put("\$ref", "#/components/schemas/$type")
            }
        }
    }

    private fun generateOperationId(method: String, path: String): String {
        val segments = path.trim('/').split('/')
        val sb = StringBuilder(method)
        segments.forEach { seg ->
            if (seg.isNotEmpty()) {
                if (seg.startsWith("{")) {
                    sb.append("By").append(seg.trim('{', '}').replaceFirstChar { it.uppercase() })
                } else {
                    sb.append(seg.replaceFirstChar { it.uppercase() })
                }
            }
        }
        return sb.toString()
    }

    private fun generateDTOs(outputDir: File, dtos: Map<String, JsonElement>, enums: Map<String, JsonElement>) {
        val fileContent = StringBuilder()
        fileContent.append("package com.quizbackend.contracts.generated\n\n")
        fileContent.append("import kotlinx.serialization.Serializable\n\n")

        // Enums
        enums.forEach { (name, element) ->
            val values = element.jsonObject["values"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
            fileContent.append("@Serializable\n")
            fileContent.append("enum class $name {\n")
            values.forEachIndexed { index, value ->
                fileContent.append("    $value${if (index < values.size - 1) "," else ""}\n")
            }
            fileContent.append("}\n\n")
        }

        // DTOs
        dtos.forEach { (name, element) ->
            val dtoObj = element.jsonObject
            val inherits = dtoObj["inherits"]?.jsonPrimitive?.contentOrNull
            val properties = dtoObj["properties"]?.jsonArray ?: emptyList()

            fileContent.append("@Serializable\n")
            if (properties.isEmpty() && inherits == null) {
                if (name == "DTOParams") {
                    fileContent.append("abstract class $name\n\n")
                } else if (inherits == "DTOParams") {
                     fileContent.append("class $name : DTOParams()\n\n")
                } else {
                     fileContent.append("class $name\n\n")
                }
            } else {
                val isDataClass = properties.isNotEmpty()
                if (isDataClass) {
                    fileContent.append("data class $name(\n")
                    properties.forEachIndexed { index, prop ->
                        val p = prop.jsonObject
                        val pName = p["name"]!!.jsonPrimitive.content
                        val pType = p["type"]!!.jsonPrimitive.content
                        val isNullable = p["isNullable"]?.jsonPrimitive?.boolean == true
                        val typeStr = if (pType == "T") "T" else pType
                        val suffix = if (index < properties.size - 1) "," else ""
                        val default = if (isNullable) " = null" else ""
                        fileContent.append("    val $pName: $typeStr${if (isNullable) "?" else ""}$default$suffix\n")
                    }
                    fileContent.append(")")
                } else {
                     fileContent.append("class $name")
                }

                if (inherits != null) {
                    fileContent.append(" : $inherits()")
                }

                 if (properties.any { it.jsonObject["type"]!!.jsonPrimitive.content == "T" }) {
                     val idx = fileContent.lastIndexOf("data class $name(")
                     if (idx != -1) {
                         fileContent.replace(idx, idx + "data class $name(".length, "data class $name<T>(")
                     }
                 }

                fileContent.append("\n\n")
            }
        }

        File(outputDir, "DTOs.kt").writeText(fileContent.toString())
    }

    private fun generateServicesAndRoutes(outputDir: File, routes: List<JsonElement>) {
        val serviceMap = mutableMapOf<String, MutableList<JsonElement>>()

        routes.forEach { route ->
            val path = route.jsonObject["path"]!!.jsonPrimitive.content
            val segments = path.trim('/').split('/')
            val serviceName = segments.firstOrNull() ?: "root"
            serviceMap.computeIfAbsent(serviceName) { mutableListOf() }.add(route)
        }

        val servicesContent = StringBuilder()
        servicesContent.append("package com.quizbackend.contracts.generated\n\n")
        servicesContent.append("import com.quizbackend.contracts.generated.*\n\n")

        val routesContent = StringBuilder()
        routesContent.append("package com.quizbackend.contracts.generated\n\n")
        routesContent.append("import io.ktor.server.application.*\n")
        routesContent.append("import io.ktor.server.routing.*\n")
        routesContent.append("import io.ktor.http.HttpMethod\n")
        routesContent.append("import com.quizbackend.routing.defineRoute\n")
        routesContent.append("import com.quizbackend.routing.RouteDefinition\n")
        routesContent.append("import com.quizbackend.configureRoute\n\n")


        val serviceArgs = serviceMap.keys.joinToString(", ") { "${it}Service: ${it.replaceFirstChar { c -> c.uppercase() }}Service" }
        routesContent.append("fun Application.configureGeneratedRoutes($serviceArgs) {\n")
        routesContent.append("    val routes = listOf(\n")

        serviceMap.forEach { (serviceName, routesList) ->
            val className = "${serviceName.replaceFirstChar { it.uppercase() }}Service"
            servicesContent.append("interface $className {\n")

            routesList.forEach { route ->
                val r = route.jsonObject
                val method = r["method"]!!.jsonPrimitive.content
                val path = r["path"]!!.jsonPrimitive.content
                val bodyType = r["bodyType"]!!.jsonPrimitive.content
                val paramsType = r["paramsType"]!!.jsonPrimitive.content
                val responseType = r["responseType"]!!.jsonPrimitive.content
                val requiresAuth = r["requiresAuth"]?.jsonPrimitive?.boolean ?: false

                val segments = path.trim('/').split('/')
                val lastSegment = segments.last()
                val isParam = lastSegment.startsWith("{") && lastSegment.endsWith("}")

                val methodNameBuilder = StringBuilder()
                methodNameBuilder.append(method.lowercase().replaceFirstChar { it.uppercase() })

                fun String.toPascalCase(): String {
                    return this.split('-', '_').joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }
                }

                if (isParam) {
                    if (segments.size > 1) {
                        val prev = segments[segments.size - 2]
                        methodNameBuilder.append(prev.toPascalCase())
                    }
                    val paramName = lastSegment.trim('{', '}')
                    methodNameBuilder.append(paramName.toPascalCase())
                } else {
                    methodNameBuilder.append(lastSegment.toPascalCase())
                }

                val methodName = methodNameBuilder.toString()

                servicesContent.append("    suspend fun $methodName(body: $bodyType, params: $paramsType): DTOResponse<$responseType>\n")

                val methodEnum = when(method) {
                    "GET" -> "HttpMethod.Get"
                    "POST" -> "HttpMethod.Post"
                    "PUT" -> "HttpMethod.Put"
                    "DELETE" -> "HttpMethod.Delete"
                    "PATCH" -> "HttpMethod.Patch"
                    else -> "HttpMethod.Get"
                }

                routesContent.append("        defineRoute<$bodyType, $paramsType, $responseType>($methodEnum, \"$path\", requiresAuth = $requiresAuth) { body, params -> ${serviceName}Service.$methodName(body, params) },\n")
            }
            servicesContent.append("}\n\n")
        }

        routesContent.append("    )\n\n")
        routesContent.append("    routing {\n")
        routesContent.append("        routes.forEach { def ->\n")
        routesContent.append("            configureRoute(def)\n")
        routesContent.append("        }\n")
        routesContent.append("    }\n")
        routesContent.append("}\n")

        File(outputDir, "Services.kt").writeText(servicesContent.toString())
        File(outputDir, "Routes.kt").writeText(routesContent.toString())
    }

    // --- Dart Generation ---

    private fun generateDartClient(projectDir: File, json: JsonObject) {
        val dartRoot = File(projectDir, "dart_client")
        dartRoot.mkdirs()

        generateDartPubspec(dartRoot, json)

        val libDir = File(dartRoot, "lib")
        libDir.mkdirs()

        val srcDir = File(libDir, "src")
        srcDir.mkdirs()
        val servicesDir = File(srcDir, "services")
        servicesDir.mkdirs()

        val enums = json["enums"]?.jsonObject ?: emptyMap()
        generateDartEnums(srcDir, enums)

        val dtos = json["dtos"]?.jsonObject ?: emptyMap()
        generateDartDTOs(srcDir, dtos)

        val routes = json["routes"]?.jsonArray ?: emptyList()
        generateDartServices(servicesDir, routes, dtos)

        generateDartClientEntryPoint(libDir, routes)
    }

    private fun generateDartPubspec(dartRoot: File, json: JsonObject) {
        val name = "quiz_api_client"
        val description = json["description"]?.jsonPrimitive?.content ?: "A Dart API client"
        val version = json["version"]?.jsonPrimitive?.content ?: "1.0.0"

        val content = """
name: $name
description: $description
version: $version
environment:
  sdk: '>=2.17.0 <4.0.0'

dependencies:
  dio: ^5.0.0
  json_annotation: ^4.8.0

dev_dependencies:
  build_runner: ^2.3.3
  json_serializable: ^6.6.0
  lints: ^2.0.0
""".trimIndent()
        File(dartRoot, "pubspec.yaml").writeText(content)
    }

    private fun generateDartEnums(outputDir: File, enums: Map<String, JsonElement>) {
        val sb = StringBuilder()
        sb.append("import 'package:json_annotation/json_annotation.dart';\n\n")

        enums.forEach { (name, element) ->
            val values = element.jsonObject["values"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
            sb.append("@JsonEnum()\n")
            sb.append("enum $name {\n")
            values.forEach { value ->
                sb.append("  @JsonValue('$value')\n")
                sb.append("  $value,\n")
            }
            sb.append("}\n\n")
        }
        File(outputDir, "enums.dart").writeText(sb.toString())
    }

    private fun generateDartDTOs(outputDir: File, dtos: Map<String, JsonElement>) {
        val sb = StringBuilder()
        sb.append("import 'package:json_annotation/json_annotation.dart';\n")
        sb.append("import 'enums.dart';\n\n")
        sb.append("part 'dtos.g.dart';\n\n")

        dtos.forEach { (name, element) ->
            val dtoObj = element.jsonObject
            val inherits = dtoObj["inherits"]?.jsonPrimitive?.contentOrNull
            val properties = dtoObj["properties"]?.jsonArray ?: emptyList()

            val isGeneric = properties.any { it.jsonObject["type"]!!.jsonPrimitive.content == "T" }
            val className = if (isGeneric) "$name<T>" else name

            val annotation = if (isGeneric) "@JsonSerializable(genericArgumentFactories: true)" else "@JsonSerializable()"

            sb.append("$annotation\n")
            if (inherits != null) {
                sb.append("class $className extends $inherits {\n")
            } else {
                sb.append("class $className {\n")
            }

            // Properties
            properties.forEach { prop ->
                val p = prop.jsonObject
                val pName = p["name"]!!.jsonPrimitive.content
                val pType = p["type"]!!.jsonPrimitive.content
                val isNullable = p["isNullable"]?.jsonPrimitive?.boolean == true

                val dartType = mapToDartType(pType)
                sb.append("  final $dartType${if (isNullable) "?" else ""} $pName;\n")
            }
            sb.append("\n")

            // Constructor
            sb.append("  $className({")
            properties.forEach { prop ->
                 val pName = prop.jsonObject["name"]!!.jsonPrimitive.content
                 val isNullable = prop.jsonObject["isNullable"]?.jsonPrimitive?.boolean == true
                 if (!isNullable) {
                     sb.append("required this.$pName, ")
                 } else {
                     sb.append("this.$pName, ")
                 }
            }
            sb.append("});\n\n")

            // Factory
            if (isGeneric) {
                sb.append("  factory $name.fromJson(Map<String, dynamic> json, T Function(Object? json) fromJsonT) => _\$${name}FromJson(json, fromJsonT);\n")
                sb.append("  Map<String, dynamic> toJson(Object? Function(T value) toJsonT) => _\$${name}ToJson(this, toJsonT);\n")
            } else {
                sb.append("  factory $name.fromJson(Map<String, dynamic> json) => _\$${name}FromJson(json);\n")
                sb.append("  Map<String, dynamic> toJson() => _\$${name}ToJson(this);\n")
            }
            sb.append("}\n\n")
        }

        File(outputDir, "dtos.dart").writeText(sb.toString())
    }

    private fun mapToDartType(type: String): String {
        return when {
            type == "Int" || type == "Long" -> "int"
            type == "Double" || type == "Float" -> "double"
            type == "Boolean" -> "bool"
            type == "String" -> "String"
            type.startsWith("List<") -> {
                val inner = type.removePrefix("List<").removeSuffix(">")
                "List<${mapToDartType(inner)}>"
            }
            type == "T" -> "T"
            else -> type
        }
    }

    private fun generateDartServices(outputDir: File, routes: List<JsonElement>, dtos: Map<String, JsonElement>) {
        val serviceMap = mutableMapOf<String, MutableList<JsonElement>>()
        routes.forEach { route ->
            val path = route.jsonObject["path"]!!.jsonPrimitive.content
            val segments = path.trim('/').split('/')
            val serviceName = segments.firstOrNull() ?: "root"
            serviceMap.computeIfAbsent(serviceName) { mutableListOf() }.add(route)
        }

        serviceMap.forEach { (serviceName, routesList) ->
            val className = "${serviceName.replaceFirstChar { it.uppercase() }}Service"
            val sb = StringBuilder()
            sb.append("import 'package:dio/dio.dart';\n")
            sb.append("import '../dtos.dart';\n")
            sb.append("import '../enums.dart';\n\n")

            sb.append("class $className {\n")
            sb.append("  final Dio _dio;\n")
            sb.append("  $className(this._dio);\n\n")

            routesList.forEach { route ->
                val r = route.jsonObject
                val method = r["method"]!!.jsonPrimitive.content.lowercase()
                val path = r["path"]!!.jsonPrimitive.content
                val bodyType = r["bodyType"]!!.jsonPrimitive.content
                val paramsType = r["paramsType"]!!.jsonPrimitive.content
                val responseType = r["responseType"]!!.jsonPrimitive.content

                // Function Name
                val segments = path.trim('/').split('/')
                val lastSegment = segments.last()
                val isParam = lastSegment.startsWith("{")
                 val methodNameBuilder = StringBuilder()
                methodNameBuilder.append(method.lowercase().replaceFirstChar { it.uppercase() })

                fun String.toPascalCase(): String {
                    return this.split('-', '_').joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }
                }

                if (isParam) {
                    if (segments.size > 1) {
                        val prev = segments[segments.size - 2]
                        methodNameBuilder.append(prev.toPascalCase())
                    }
                    val paramName = lastSegment.trim('{', '}')
                    methodNameBuilder.append(paramName.toPascalCase())
                } else {
                    methodNameBuilder.append(lastSegment.toPascalCase())
                }
                val methodName = methodNameBuilder.toString().replaceFirstChar { it.lowercase() }

                // Params
                val paramsDto = dtos[paramsType]?.jsonObject
                val paramProperties = paramsDto?.get("properties")?.jsonArray ?: emptyList()

                val args = mutableListOf<String>()
                val pathReplacements = mutableListOf<String>()
                val queryParams = mutableListOf<String>()

                // Handle Body
                if (method in listOf("post", "put", "patch", "delete") && bodyType != "EmptyRequestDTO") {
                    args.add("required $bodyType body")
                }

                paramProperties.forEach { prop ->
                    val p = prop.jsonObject
                    val pName = p["name"]!!.jsonPrimitive.content
                    val pType = p["type"]!!.jsonPrimitive.content
                    val isNullable = p["isNullable"]?.jsonPrimitive?.boolean == true
                    val dartType = mapToDartType(pType)

                    val isPath = path.contains("{$pName}")
                    if (isPath) {
                        pathReplacements.add(pName)
                        args.add("required $dartType $pName")
                    } else {
                        queryParams.add(pName)
                        if (isNullable) {
                            args.add("$dartType? $pName")
                        } else {
                            args.add("required $dartType $pName")
                        }
                    }
                }

                sb.append("  Future<DTOResponse<$responseType>> $methodName({${args.joinToString(", ")}}) async {\n")

                // Construct path
                var dartPath = "'$path'"
                if (pathReplacements.isNotEmpty()) {
                    dartPath = "'$path'"
                    pathReplacements.forEach { pName ->
                         dartPath = dartPath.replace("{$pName}", "\$$pName")
                    }
                }

                sb.append("    final response = await _dio.$method($dartPath")

                if (method in listOf("post", "put", "patch", "delete") && bodyType != "EmptyRequestDTO") {
                    sb.append(", data: body.toJson()")
                }

                if (queryParams.isNotEmpty()) {
                    sb.append(", queryParameters: {")
                    queryParams.forEach { qParam ->
                         sb.append("'$qParam': $qParam, ")
                    }
                    sb.append("}")
                }

                sb.append(");\n")
                sb.append("    return DTOResponse<$responseType>.fromJson(response.data, (json) => $responseType.fromJson(json as Map<String, dynamic>));\n")
                sb.append("  }\n\n")
            }
            sb.append("}\n")
            File(outputDir, "${serviceName}_service.dart").writeText(sb.toString())
        }
    }

    private fun generateDartClientEntryPoint(libDir: File, routes: List<JsonElement>) {
        val serviceNames = routes.map {
            val path = it.jsonObject["path"]!!.jsonPrimitive.content
            val segments = path.trim('/').split('/')
            segments.firstOrNull() ?: "root"
        }.distinct()

        val sb = StringBuilder()
        sb.append("import 'package:dio/dio.dart';\n")
        serviceNames.forEach {
             sb.append("import 'src/services/${it}_service.dart';\n")
        }
        sb.append("export 'src/dtos.dart';\n")
        sb.append("export 'src/enums.dart';\n")
        serviceNames.forEach {
             sb.append("export 'src/services/${it}_service.dart';\n")
        }
        sb.append("\n")

        sb.append("class ApiClient {\n")
        sb.append("  final Dio dio;\n")
        sb.append("  late final String baseUrl;\n\n")

        serviceNames.forEach {
             val className = "${it.replaceFirstChar { c -> c.uppercase() }}Service"
             sb.append("  late final $className ${it}Service;\n")
        }
        sb.append("\n")

        sb.append("  ApiClient({String? baseUrl, Dio? dio}) : this.dio = dio ?? Dio() {\n")
        sb.append("    this.baseUrl = baseUrl ?? '';\n")
        sb.append("    this.dio.options.baseUrl = this.baseUrl;\n")

        serviceNames.forEach {
             val className = "${it.replaceFirstChar { c -> c.uppercase() }}Service"
             sb.append("    ${it}Service = $className(this.dio);\n")
        }
        sb.append("  }\n")
        sb.append("}\n")

        File(libDir, "api_client.dart").writeText(sb.toString())
    }
}
