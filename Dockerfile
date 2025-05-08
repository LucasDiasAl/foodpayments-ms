FROM openjdk:17-jdk-slim

WORKDIR /app

COPY mvnw .
COPY .mvn ./.mvn
COPY pom.xml .

# Executa o Maven para baixar as dependências
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests

# Encontra o arquivo JAR gerado
RUN ls target/*.jar
ARG JAR_FILE=target/*.jar

# Renomeia o arquivo JAR para um nome mais simples
RUN mv $JAR_FILE payments-ms.jar

EXPOSE 8080

CMD ["/bin/sh", "-c", "java -jar /app/payments-ms.jar"]