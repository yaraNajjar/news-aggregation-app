# Dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/news-service-1.0-SNAPSHOT.jar news-service.jar
ENTRYPOINT ["java","-jar","/news-service.jar"]
