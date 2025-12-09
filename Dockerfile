# --- ETAPA 1: Builder ---
# En lloc d'una imatge de Gradle, usem la mateixa de Java que farem servir després.
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copiem els fitxers del gradle wrapper
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties* .

# Donem permisos d'execució al wrapper
RUN chmod +x ./gradlew

# Aquesta línia és màgica: descarrega només les dependències primer.
# Si falla aquí, sabrem que és un problema de xarxa/repositoris.
RUN ./gradlew dependencies --no-daemon

# Copiem la resta del codi
COPY src ./src

# Compilem usant el wrapper (assegura mateixa versió que local).
# Afegim -x test per estalviar memòria i temps.
RUN ./gradlew shadowJar --no-daemon -x test

# --- ETAPA 2: Runner ---
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copiem el JAR. L'asterisc ens salva d'errors de noms.
COPY --from=builder /app/build/libs/*-all.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]