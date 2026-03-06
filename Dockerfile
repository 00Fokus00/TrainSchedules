#FROM amazoncorretto:21-alpine-jdk
#WORKDIR /train_shcedules
#ARG WAR_FILE=target/schedules-0.0.1-SNAPSHOT.war
#COPY ${WAR_FILE} application.war
#COPY src/main .
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "application.jar"]
#

# Stage 1 - build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Stage 2 - final
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]