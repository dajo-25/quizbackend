# Utilitza una imatge base amb Gradle per compilar el projecte
FROM gradle:7.6-jdk17 AS builder

# Defineix el directori de treball per la compilació
WORKDIR /app

# Copia tots els fitxers del projecte al contenidor
COPY . .

# Compila el projecte i genera el fat JAR
RUN gradle clean buildFatJar --no-daemon

# Utilitza una imatge base amb Java per executar l'aplicació
FROM eclipse-temurin:17-jdk-jammy

# Defineix el directori de treball per l'aplicació
WORKDIR /app

# Copia el fat JAR generat al contenidor
COPY --from=builder /app/build/libs/quizbackend-all.jar app.jar

# Exposa el port de Ktor
EXPOSE 8080

# Comanda per executar el fat JAR
CMD ["java", "-jar", "app.jar"]
