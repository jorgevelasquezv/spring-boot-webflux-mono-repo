package co.com.jorge.springboot.webflux.app.controllers;

import co.com.jorge.springboot.webflux.app.models.documents.Product;
import co.com.jorge.springboot.webflux.app.models.services.ProductService;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductService service;

    @Value("${config.uploads.path}")
    private String path;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Product>>> listAllProducts(){
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> seeProduct(@PathVariable String id){
        return service.findById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String , Object>>> saveProduct(@Valid @RequestBody Mono<Product> monoProduct){

        Map<String , Object> response = new HashMap<>();

        return  monoProduct.flatMap(product -> {
            if (product.getCreateAt() == null) product.setCreateAt(new Date());
            return service.save(product)
                    .map(prod -> {
                        response.put("product", prod);
                        return ResponseEntity
                                .created(URI.create("/api/products/".concat(prod.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response);
                    })
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        })
                .onErrorResume(th -> Mono.just(th).cast(WebExchangeBindException.class)
                            .flatMap(err -> Mono.just(err.getFieldErrors()))
                            .flatMapMany(Flux::fromIterable)
                            .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                            .collectList()
                            .flatMap(list -> {
                                response.put("errors", list);
                                return Mono.just(ResponseEntity.badRequest().body(response));
                            }));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable String id, @RequestBody Product product){
        return service.findById(id)
                .flatMap(prodDB -> {
                    prodDB.setName(product.getName())
                            .setPrice(product.getPrice())
                            .setCategory(product.getCategory());
                    return service.save(prodDB);
                })
                .map(prod -> ResponseEntity.created(URI.create("/api/products/".concat(prod.getId()))).body(prod))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id){
        return service.findById(id)
                .flatMap(product -> service.delete(product).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/upload/photo/{id}")
    public Mono<ResponseEntity<Product>> uploadsPhoto(@PathVariable String id, @RequestPart FilePart file){
        return service.findById(id).flatMap(prod -> {
            prod.setPhoto(UUID.randomUUID() +  "-" + file.filename()
                    .replace(" ", "")
                    .replace(":", "")
                    .replace("\\", ""));
            return file.transferTo(new File(path + prod.getPhoto()))
                    .then(service.save(prod));
        })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/save/with-photo")
    public Mono<ResponseEntity<Product>> saveProductWithPhoto(@Valid Product product, @RequestPart FilePart file){
        if (product.getCreateAt() == null) product.setCreateAt(new Date());
        product.setPhoto(UUID.randomUUID() +  "-" + file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));
        return file.transferTo(new File(path + product.getPhoto()))
                .then(service.save(product))
                .map(prod -> ResponseEntity
                            .created(URI.create("/api/products/".concat(prod.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(prod))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
