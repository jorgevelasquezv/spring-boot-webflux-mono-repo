package co.com.jorge.springboot.webflux.app;

import co.com.jorge.springboot.webflux.app.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    private static final String BASE_PATH = "/api/v2/products";

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler){
        return route(GET(BASE_PATH), handler::listProducts)
                .andRoute(GET(BASE_PATH.concat("/{id}")), handler::seeProduct)
                .andRoute(POST(BASE_PATH), handler::saveProduct)
                .andRoute(PUT(BASE_PATH.concat("/{id}")), handler::editProduct)
                .andRoute(DELETE(BASE_PATH.concat("/{id}")), handler::deleteProduct)
                .andRoute(POST(BASE_PATH.concat("/upload/photo/{id}")), handler::uploadFile)
                .andRoute(POST(BASE_PATH.concat("/save/with-photo")), handler::saveWithUploadFile);

    }

}
