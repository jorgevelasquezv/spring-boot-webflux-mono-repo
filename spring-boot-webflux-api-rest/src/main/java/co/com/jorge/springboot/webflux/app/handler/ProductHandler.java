package co.com.jorge.springboot.webflux.app.handler;

import co.com.jorge.springboot.webflux.app.models.documents.Category;
import co.com.jorge.springboot.webflux.app.models.documents.Product;
import co.com.jorge.springboot.webflux.app.models.services.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static org.springframework.web.reactive.function.BodyInserters.*;

@Component
public class ProductHandler {

    private ProductService service;

    private Validator validator;

    @Value("${config.uploads.path}")
    private String path;

    public ProductHandler(ProductService service, Validator validator) {
        this.service = service;
        this.validator = validator;
    }

    public Mono<ServerResponse> listProducts(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Product.class);
    }

    public Mono<ServerResponse> seeProduct(ServerRequest request){
        String id = request.pathVariable("id");
        return service.findById(id)
                .flatMap(product -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(product)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request){
        Mono<Product> product = request.bodyToMono(Product.class);
        return product.flatMap(prod -> {
                    Errors errors = new BeanPropertyBindingResult(prod, Product.class.getName());
                    validator.validate(prod, errors);
                    if(errors.hasFieldErrors()) return Flux.fromIterable(errors.getFieldErrors())
                            .map(fieldError -> "El campo " + fieldError.getField() + " " +fieldError.getDefaultMessage())
                            .collectList()
                            .flatMap(list -> ServerResponse.badRequest().body(fromValue(list)));
                    if (prod.getCreateAt() == null) prod.setCreateAt(new Date());
                    return service.save(prod)
                            .flatMap(pro -> ServerResponse
                            .created(URI.create("/api/v2/products".concat(pro.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromValue(pro)));
        });

    }

    public Mono<ServerResponse> editProduct(ServerRequest request){
        Mono<Product> product = request.bodyToMono(Product.class);
        String id = request.pathVariable("id");

        Mono<Product> productDB = service.findById(id);

        return productDB.zipWith(product, (db, req) -> {
            db.setName(req.getName());
            db.setPrice(req.getPrice());
            db.setCategory(req.getCategory());
            return db;
        })
                .flatMap(prod -> ServerResponse
                        .created(URI.create("/api/v2/products".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.save(prod), Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request){
        String id = request.pathVariable("id");

        Mono<Product> productDB = service.findById(id);

        return productDB
                .flatMap(product -> service.delete(product).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> uploadFile(ServerRequest request){
        String id = request.pathVariable("id");
        return request
                .multipartData()
                .map(multiPart -> multiPart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> service
                                .findById(id)
                                .flatMap(product -> {
                    product.setPhoto(UUID.randomUUID() + "-" + filePart.filename()
                            .replace(" ","")
                            .replace(":","")
                            .replace("\\","")
                    );
                    return filePart
                            .transferTo(new File(path, product.getPhoto()))
                            .then(service.save(product));
                }))
                .flatMap(product -> ServerResponse
                        .created(URI.create("/api/v2/products".concat(product.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(product)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorReturn(ServerResponse.badRequest().build().block());
    }

    public Mono<ServerResponse> saveWithUploadFile(ServerRequest request){
        Mono<Product> productMono = request
                .multipartData()
                .map(multiPart -> {
                    FormFieldPart name = (FormFieldPart) multiPart.toSingleValueMap().get("name");
                    FormFieldPart price = (FormFieldPart) multiPart.toSingleValueMap().get("price");
                    FormFieldPart categoryId = (FormFieldPart) multiPart.toSingleValueMap().get("category.id");
                    FormFieldPart categoryName = (FormFieldPart) multiPart.toSingleValueMap().get("category.name");

                    Category category = new Category(categoryName.value());
                    category.setId(categoryId.value());

                    return new Product(name.value(), Double.parseDouble(price.value()), category);
                });
        return request
                .multipartData()
                .map(multiPart -> multiPart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> productMono
                        .flatMap(product -> {
                            product
                                    .setPhoto(UUID.randomUUID() + "-" + filePart.filename()
                                    .replace(" ","")
                                    .replace(":","")
                                    .replace("\\",""))
                                    .setCreateAt(new Date());
                            return filePart
                                    .transferTo(new File(path, product.getPhoto()))
                                    .then(service.save(product));
                        }))
                .flatMap(product -> ServerResponse
                        .created(URI.create("/api/v2/products".concat(product.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(product)))
                .onErrorReturn(ServerResponse.badRequest().build().block());
    }
}
