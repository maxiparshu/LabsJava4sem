FROM gradle:8.5.0-jdk21 AS build
COPY --chown=gradle:gradle . /home
WORKDIR /home
RUN gradle build --no-daemon -x test

FROM openjdk:21

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/build/libs/*.jar /app/

RUN ls /app/

ENTRYPOINT ["java", "-jar",  "/app/home-0.0.1-SNAPSHOT.jar"]
