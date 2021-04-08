FROM ubuntu:20.04
RUN apt-get update
RUN apt-get install openjdk-11-jre -y
COPY ./target/plagiarism-service-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","./app.jar"]