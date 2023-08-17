package co.com.jorge.springboot.webflux.client.app.models.services;

import co.com.jorge.springboot.webflux.client.app.models.dto.Category;
import co.com.jorge.springboot.webflux.client.app.models.dto.Product;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private WebClient.Builder client;

    public ProductServiceImpl(WebClient.Builder client) {
        this.client = client;
    }

    @Override
    public Flux<Product> findAllProducts() {
        return client
                .build()
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> response.bodyToFlux(Product.class));
    }

    @Override
    public Mono<Product> findProductById(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return client
                .build()
                .get()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Product> createProduct(Product product) {
        return client
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Product> updateProduct(String id, Product product) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return client
                .build()
                .put()
                .uri("/{id}", params)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Void> deleteProduct(String id) {
        return client
                .build()
                .delete()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Product> uploadFile(FilePart file, String id) {
        MultipartBodyBuilder body = new MultipartBodyBuilder();
        body.asyncPart("file", file.content(), DataBuffer.class)
                .headers(header -> header.setContentDispositionFormData("file", file.filename()));
        return client
                .build()
                .post()
                .uri("/upload/photo/{id}", Collections.singletonMap("id", id))
                .bodyValue(body.build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                ;
    }

    @Override
    public Flux<Category> findAllCategories() {
        return null;
    }

    @Override
    public Mono<Category> findCategoryById(String id) {
        return null;
    }

    @Override
    public Mono<Category> createCategory(Product product) {
        return null;
    }

    @Override
    public Mono<Category> updateCategory(String id, Product product) {
        return null;
    }

    @Override
    public Mono<Void> deleteCategory(String id) {
        return null;
    }
}
