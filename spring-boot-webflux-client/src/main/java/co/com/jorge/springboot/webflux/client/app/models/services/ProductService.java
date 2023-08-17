package co.com.jorge.springboot.webflux.client.app.models.services;

import co.com.jorge.springboot.webflux.client.app.models.dto.Category;
import co.com.jorge.springboot.webflux.client.app.models.dto.Product;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> findAllProducts();

    Mono<Product> findProductById(String id);

    Mono<Product> createProduct(Product product);

    Mono<Product> updateProduct(String id, Product product);

    Mono<Void> deleteProduct(String id);

    Mono<Product> uploadFile(FilePart file, String id);

    Flux<Category> findAllCategories();

    Mono<Category> findCategoryById(String id);

    Mono<Category> createCategory(Product product);

    Mono<Category> updateCategory(String id, Product product);

    Mono<Void> deleteCategory(String id);
}
