# --- ETAPA DE BUILD ---
FROM gradle:7.6-jdk17 AS builder
WORKDIR /app

# Copiem fitxers de configuració
COPY build.gradle.kts settings.gradle.kts gradle.properties* ./
# Copiem el codi
COPY src ./src

# TRUC: Fem servir 'build' estàndard en lloc de shadowJar.
# -x test salta els tests per estalviar temps i memòria
RUN gradle build -x test --no-daemon

# --- ETAPA D'EXECUCIÓ ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Busquem el JAR generat. Normalment la tasca 'build' el deixa a build/libs/
# ATENCIÓ: Sense shadowJar, necessitem copiar també les dependències si no és un fat jar,
# però molts setups de Ktor ja inclouen el fat jar amb la tasca 'buildFatJar' si tens el plugin de Ktor.

# Si tens el plugin de Ktor, prova de copiar:
COPY --from=builder /app/build/libs/*-all.jar app.jar
# Si això falla, canvia l'asterisc per: /app/build/libs/*.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]