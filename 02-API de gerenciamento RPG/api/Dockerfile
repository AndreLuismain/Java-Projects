# Estágio 1: Build (Usando Maven com Eclipse Temurin 17)
FROM maven:3.8-eclipse-temurin-17 AS build
WORKDIR /app
# Copia o conteúdo da pasta atual para o container
COPY . .
# Compila o projeto
RUN mvn clean package -DskipTests

# Estágio 2: Run (Usando JRE do Eclipse Temurin 17 - mais leve e seguro)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar
# Porta padrão
EXPOSE 8080
# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]