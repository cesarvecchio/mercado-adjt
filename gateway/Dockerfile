FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/*.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]