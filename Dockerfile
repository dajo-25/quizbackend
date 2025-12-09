FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copiem tot el projecte
COPY . .

# --- SOLUCIÓ DE L'ERROR 127 ---
# 1. Donem permisos d'execució (+x)
RUN chmod +x ./gradlew

# 2. Arreglem els salts de línia de Windows (CRLF) a Linux (LF)
# Això elimina els caràcters invisibles que fan que Linux no trobi la comanda
RUN sed -i 's/\r$//' gradlew

# -----------------------------

# Ara sí, descarreguem dependències
RUN ./gradlew dependencies --no-daemon

# I compilem (recorda: shadowJar o build, segons el que tinguis)
RUN ./gradlew shadowJar --no-daemon -x test

# --- ETAPA RUNNER ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]