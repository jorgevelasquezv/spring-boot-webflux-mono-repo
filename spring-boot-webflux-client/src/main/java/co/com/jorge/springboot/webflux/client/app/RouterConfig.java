package co.com.jorge.springboot.webflux.client.app;

import co.com.jorge.springboot.webflux.client.app.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@Configuration
public class RouterConfig {

    private final ProductHandler handler;

    public RouterConfig(ProductHandler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler){
        return route(GET("/api/client"), handler::listProducts)
                .andRoute(GET("/api/client/{id}"), handler::seeProductById)
                .andRoute(POST("/api/client"), handler::createProduct)
                .andRoute(PUT("/api/client/{id}"), handler::updateProduct)
                .andRoute(DELETE("/api/client/{id}"), handler::deleteProductById)
                .andRoute(POST("/api/client/upload/photo/{id}"), handler::uploadPhoto)
                ;
    }
}
