FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/*.jar ms-pedidos.jar
ENTRYPOINT ["java", "-jar", "ms-pedidos.jar"]