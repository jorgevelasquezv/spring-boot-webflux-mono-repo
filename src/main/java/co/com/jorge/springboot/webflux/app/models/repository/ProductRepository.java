package co.com.jorge.springboot.webflux.app.models.repository;

import co.com.jorge.springboot.webflux.app.models.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
