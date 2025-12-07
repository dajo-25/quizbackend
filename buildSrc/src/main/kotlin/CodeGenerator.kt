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
        // We need to handle dependencies/ordering? Or just dump them. Kotlin allows forward references in same file.
        dtos.forEach { (name, element) ->
            val dtoObj = element.jsonObject
            val inherits = dtoObj["inherits"]?.jsonPrimitive?.contentOrNull
            val properties = dtoObj["properties"]?.jsonArray ?: emptyList()

            fileContent.append("@Serializable\n")
            if (properties.isEmpty() && inherits == null) {
                // Special case for abstract DTOParams if it has no properties?
                // Or EmptyRequestDTO
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

                        // generic T handling for DTOResponse
                        val typeStr = if (pType == "T") "T" else pType

                        val suffix = if (index < properties.size - 1) "," else ""
                        // default null if nullable
                        val default = if (isNullable) " = null" else ""
                        // If type is T, we need <T> in class def.

                        fileContent.append("    val $pName: $typeStr${if (isNullable) "?" else ""}$default$suffix\n")
                    }
                    fileContent.append(")")
                } else {
                     // Class with no properties but might inherit
                     fileContent.append("class $name")
                }

                if (inherits != null) {
                    fileContent.append(" : $inherits()") // Assuming base classes have empty constructor or we handle them specially
                }

                // Add <T> to class definition if one property is T
                // Quick hack: check if "T" is used in properties
                 if (properties.any { it.jsonObject["type"]!!.jsonPrimitive.content == "T" }) {
                     // We need to inject <T> before constructor
                     // Replace "data class Name(" with "data class Name<T>("
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
        // Group by service (first path segment)
        val serviceMap = mutableMapOf<String, MutableList<JsonElement>>()

        routes.forEach { route ->
            val path = route.jsonObject["path"]!!.jsonPrimitive.content
            val segments = path.trim('/').split('/')
            val serviceName = segments.firstOrNull() ?: "root"
            serviceMap.computeIfAbsent(serviceName) { mutableListOf() }.add(route)
        }

        // Generate Services.kt
        val servicesContent = StringBuilder()
        servicesContent.append("package com.quizbackend.contracts.generated\n\n")
        servicesContent.append("import com.quizbackend.contracts.generated.*\n\n") // Import DTOs

        // Generate Routes.kt
        val routesContent = StringBuilder()
        routesContent.append("package com.quizbackend.contracts.generated\n\n")
        routesContent.append("import io.ktor.server.application.*\n")
        routesContent.append("import io.ktor.server.routing.*\n")
        routesContent.append("import io.ktor.http.HttpMethod\n")
        routesContent.append("import com.quizbackend.routing.defineRoute\n") // We will reuse defineRoute helper
        routesContent.append("import com.quizbackend.routing.RouteDefinition\n\n")


        // Build configure function signature
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

                // Determine function name
                val segments = path.trim('/').split('/')
                val lastSegment = segments.last()
                val isParam = lastSegment.startsWith("{") && lastSegment.endsWith("}")

                val methodNameBuilder = StringBuilder()
                methodNameBuilder.append(method.lowercase().replaceFirstChar { it.uppercase() }) // Get, Post...

                fun String.toPascalCase(): String {
                    return this.split('-', '_').joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }
                }

                if (isParam) {
                    // Use second to last + last param name (cleaned)
                    if (segments.size > 1) {
                        val prev = segments[segments.size - 2]
                        methodNameBuilder.append(prev.toPascalCase())
                    }
                    // Clean param {id} -> Id
                    val paramName = lastSegment.trim('{', '}')
                    methodNameBuilder.append(paramName.toPascalCase())
                } else {
                    methodNameBuilder.append(lastSegment.toPascalCase())
                }

                // Edge case: if generated name is just "Get", append ServiceName to avoid ambiguity?
                // E.g. /questions -> GetQuestions.
                // My logic: segments=['questions'], last='questions', not param. -> GetQuestions. Correct.

                // Edge case: /auth/login -> segments=['auth','login']. last='login'. -> PostLogin. Correct.

                val methodName = methodNameBuilder.toString()

                servicesContent.append("    suspend fun $methodName(body: $bodyType, params: $paramsType): DTOResponse<$responseType>\n")

                // Add to routes definition
                // defineRoute<Body, Params, Response>(Method, Path, Auth) { body, params -> service.method(body, params) }
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
        routesContent.append("            configureRoute(def)\n") // Assuming configureRoute is accessible (it is an extension method in Routing.kt, we might need to import it or make it available)
        // Wait, configureRoute is in Routing.kt package com.quizbackend.
        // We are in com.quizbackend.contracts.generated.
        // We need to import com.quizbackend.configureRoute
        // Checking Routing.kt... function signature: `fun <Body : Any, Params : DTOParams, Response : Any> Route.configureRoute(...)`
        // It's a top level function in `com.quizbackend` package?
        // File starts with `package com.quizbackend`.
        // So yes.
        routesContent.insert(routesContent.indexOf("import com.quizbackend.routing.RouteDefinition") + "import com.quizbackend.routing.RouteDefinition\n".length, "import com.quizbackend.configureRoute\n")

        routesContent.append("        }\n")
        routesContent.append("    }\n")
        routesContent.append("}\n")

        File(outputDir, "Services.kt").writeText(servicesContent.toString())
        File(outputDir, "Routes.kt").writeText(routesContent.toString())
    }
}
