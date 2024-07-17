FROM openjdk:11-jre-slim
VOLUME /tmp
COPY target/ai-service-1.0-SNAPSHOT.jar ai-service.jar
ENTRYPOINT ["java","-jar","/ai-service.jar"]
