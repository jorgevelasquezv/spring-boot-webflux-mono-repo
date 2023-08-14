package co.com.jorge.springboot.webflux.app.models.services;

import co.com.jorge.springboot.webflux.app.models.documents.Category;
import co.com.jorge.springboot.webflux.app.models.repository.CategoryRepository;
import co.com.jorge.springboot.webflux.app.models.repository.ProductRepository;
import co.com.jorge.springboot.webflux.app.models.documents.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServicesImpl implements ProductService{

    private ProductRepository productRepository;

    private CategoryRepository categoryRepository;

    public ProductServicesImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Flux<Product> findAllWithNameUpperCase() {
        return productRepository.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        });
    }

    @Override
    public Flux<Product> findAllWithNameUpperCaseRepeat() {
        return findAllWithNameUpperCase().repeat(100);
    }

    @Override
    public Mono<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productRepository.delete(product);
    }

    @Override
    public Flux<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Mono<Category> findCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Mono<Category> saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Mono<Void> deleteCategory(Category category) {
        return categoryRepository.delete(category);
    }
}
