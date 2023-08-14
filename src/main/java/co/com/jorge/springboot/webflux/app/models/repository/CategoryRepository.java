package co.com.jorge.springboot.webflux.app.models.repository;

import co.com.jorge.springboot.webflux.app.models.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
}
