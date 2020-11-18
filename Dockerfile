#FROM openjdk:14-alpine
FROM openjdk:11.0.7-jre-slim
VOLUME /tmp
COPY . .
RUN mvn package -DskipTests
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
