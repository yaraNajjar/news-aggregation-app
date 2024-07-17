# Dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/email-service-1.0-SNAPSHOT.jar email-service.jar
ENTRYPOINT ["java","-jar","/email-service.jar"]
