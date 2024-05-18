FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/*.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]