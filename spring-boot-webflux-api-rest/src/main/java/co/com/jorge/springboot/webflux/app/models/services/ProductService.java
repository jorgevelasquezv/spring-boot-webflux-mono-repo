package co.com.jorge.springboot.webflux.app.models.services;

import co.com.jorge.springboot.webflux.app.models.documents.Category;
import co.com.jorge.springboot.webflux.app.models.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> findAll();

    Flux<Product> findAllWithNameUpperCase();

    Flux<Product> findAllWithNameUpperCaseRepeat();

    Mono<Product> findById(String id);

    Mono<Product> save(Product product);

    Mono<Void> delete(Product product);

    Mono<Product> findProductByName(String name);

    Flux<Category> findAllCategory();

    Mono<Category> findCategoryById(String id);

    Mono<Category> saveCategory(Category category);

    Mono<Void> deleteCategory(Category category);

    Mono<Category> findCategoryByName(String name);

}
