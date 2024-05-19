FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/*.jar ms-logistica-entrega.jar
ENTRYPOINT ["java", "-jar", "ms-logistica-entrega.jar"]