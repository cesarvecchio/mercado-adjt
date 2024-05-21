package br.com.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigGateway {
    @Value("${ms-cliente-url}")
    String msClienteUrl;
    @Value("${ms-produtos-url}")
    String msProdutosUrl;
    @Value("${ms-pedidos-url}")
    String msPedidosUrl;
    @Value("${ms-logistica-entrega-url}")
    String msLogisticaEntregaUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("cliente", r -> r.path("/cliente/**")
                        .uri(msClienteUrl))
                .route("produtos", r -> r.path("/produtos/**")
                        .uri(msProdutosUrl))
                .route("pedidos", r -> r.path("/pedidos/**")
                        .uri("http://localhost:8083"))
                .route("entrega", r -> r.path("/entregas/**")
                        .uri(msPedidosUrl))
                .route("entrega", r -> r.path("/entregadores/**")
                        .uri(msLogisticaEntregaUrl))
                .build();
    }

}
