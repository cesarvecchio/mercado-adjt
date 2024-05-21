package br.com.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigGateway {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("cliente", r -> r.path("/cliente/**")
                        .uri("http://localhost:8081"))
                .route("produtos", r -> r.path("/produtos/**")
                        .uri("http://localhost:8082"))
                .route("pedidos", r -> r.path("/pedidos/**")
                        .uri("http://localhost:8083"))
                .route("entrega", r -> r.path("/entregas/**")
                        .uri("http://localhost:8084"))
                .route("entrega", r -> r.path("/entregadores/**")
                        .uri("http://localhost:8084"))
                .build();
    }

}
