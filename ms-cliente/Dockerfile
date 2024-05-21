FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/*.jar ms-cliente.jar
ENTRYPOINT ["java", "-jar", "ms-cliente.jar"]