EXPOSE 8080
FROM openjdk:21
ARG APP_JAR=*.jar
COPY ${APP_JAR} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]