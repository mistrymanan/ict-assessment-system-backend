FROM adoptopenjdk/openjdk11:alpine-jre
COPY  target/grading-service-0.0.1-SNAPSHOT.jar grading-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "grading-service.jar"]