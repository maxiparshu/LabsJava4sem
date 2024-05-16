FROM gradle:8.5.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM openjdk:21

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/

RUN ls /app/

ENTRYPOINT ["java", "-jar","/app/GeoData-0.0.1-SNAPSHOT.jar"]