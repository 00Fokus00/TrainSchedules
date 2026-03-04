FROM amazoncorretto:21-alpine-jdk
WORKDIR /train_shcedules
ARG WAR_FILE=target/schedules-0.0.1-SNAPSHOT.war
COPY ${WAR_FILE} application.war
COPY src/main .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]
