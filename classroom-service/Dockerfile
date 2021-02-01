#FROM maven:3.5-jdk-8-alpine as build
#COPY . .
#RUN mvn clean package

FROM adoptopenjdk/openjdk11:alpine-jre
COPY  target/classroom-service-0.0.1-SNAPSHOT.jar classroom-service-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "classroom-service-0.0.1-SNAPSHOT.jar"]
