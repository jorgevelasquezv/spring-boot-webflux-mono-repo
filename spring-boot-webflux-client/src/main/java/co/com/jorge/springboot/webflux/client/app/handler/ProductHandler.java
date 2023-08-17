package co.com.jorge.springboot.webflux.client.app.handler;

import co.com.jorge.springboot.webflux.client.app.models.dto.Product;
import co.com.jorge.springboot.webflux.client.app.models.services.ProductService;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductHandler {

    private ProductService service;

    public ProductHandler(ProductService service) {
        this.service = service;
    }

    public Mono<ServerResponse> listProducts(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAllProducts(), Product.class);
    }

    public Mono<ServerResponse> seeProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        return errorHandler(service
                .findProductById(id)
                .flatMap(product -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(product)))
                .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Mono<Product> product = request.bodyToMono(Product.class);
        return product
                .flatMap(prod -> service.createProduct(prod))
                .flatMap(prod -> ServerResponse
                        .created(URI.create("/api/client".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(prod))
                .onErrorResume(error -> {
                    WebClientResponseException errorResponse = (WebClientResponseException) error;
                    if (errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return ServerResponse
                                .badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(errorResponse.getResponseBodyAsString());
                    }
                    return Mono.error(error);
                });
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> product = request.bodyToMono(Product.class);
        return errorHandler(product
                .flatMap(prod -> service.updateProduct(id, prod))
                .flatMap(prod -> ServerResponse
                        .created(URI.create("/api/client".concat(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(prod)));
    }

    public Mono<ServerResponse> deleteProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        return errorHandler(service.deleteProduct(id)
                .then(ServerResponse
                        .noContent()
                        .build()));
    }

    public Mono<ServerResponse> uploadPhoto(ServerRequest request){
        String id = request.pathVariable("id");
        return errorHandler(request
                .multipartData()
                .map(multiPart -> multiPart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(file -> service.uploadFile(file, id))
                .flatMap(product -> ServerResponse
                        .created(URI.create("/api/client/upload/photo".concat(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product)));
    }

    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            WebClientResponseException errorResponse = (WebClientResponseException) error;
            if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                Map<String, Object> body = new HashMap<>();
                body.put("error", "No existe el producto: ".concat(error.getMessage()));
                body.put("timeStamp", new Date());
                body.put("status", errorResponse.getStatusCode().value());
                return ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(body);
            }
            return Mono.error(error);
        });
    }
}
