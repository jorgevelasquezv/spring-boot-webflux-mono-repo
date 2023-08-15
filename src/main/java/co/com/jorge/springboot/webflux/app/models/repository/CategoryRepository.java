package co.com.jorge.springboot.webflux.app.models.repository;

import co.com.jorge.springboot.webflux.app.models.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {

    Mono<Category> findByName(String name);
}
