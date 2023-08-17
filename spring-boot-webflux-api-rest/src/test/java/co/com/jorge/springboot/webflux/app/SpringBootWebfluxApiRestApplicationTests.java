package co.com.jorge.springboot.webflux.app;

import co.com.jorge.springboot.webflux.app.models.documents.Category;
import co.com.jorge.springboot.webflux.app.models.documents.Product;
import co.com.jorge.springboot.webflux.app.models.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

//Test con servidor Api
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

//Test con mock
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringBootWebfluxApiRestApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductService service;

    @Value("${config.base.path}")
    private String BASE_PATH ;

    @Test
    void listTest() {
        client.get()
                .uri(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
//                .hasSize(7)
                .consumeWith(response -> {
                    List<Product> products = response.getResponseBody();
                    products.forEach(product -> System.out.println(product.getName()));
                    Assertions.assertThat(products.size() > 0).isTrue();
                });
    }

    @Test
    void seeProductTest(){

        Product product = service.findProductByName("TV LG LCD 43").block();

        client.get()
                .uri(BASE_PATH.concat("/{id}"), Collections.singletonMap("id", product.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("TV LG LCD 43");
    }

    @Test
    void createProductTest(){
        Category category = service.findCategoryByName("Electronica").block();
        Product product =  new Product("TV Caisun LCD 43", 500.0, category);

        if(BASE_PATH.contains("v2")) {
            client.post()
                    .uri(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(product), Product.class)
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product prod = response.getResponseBody();
                    System.out.println("Producto creado: " + prod);
                    Assertions.assertThat(prod.getId()).isNotEmpty();
                    Assertions.assertThat(prod.getName()).isNotEmpty();
                    Assertions.assertThat(prod.getCategory()).isNotNull();
                    Assertions.assertThat(prod.getName()).isEqualTo("TV Caisun LCD 43");
                });
        }else {
            client.post()
                    .uri(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(product), Product.class)
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                    .expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {
            })
                    .consumeWith(response -> {
                        Object objectResponse = response.getResponseBody().get("product");
                        Product prod = new ObjectMapper().convertValue(objectResponse, Product.class);
                        System.out.println("Producto creado: " + prod);
                        Assertions.assertThat(prod.getId()).isNotEmpty();
                        Assertions.assertThat(prod.getName()).isNotEmpty();
                        Assertions.assertThat(prod.getCategory()).isNotNull();
                        Assertions.assertThat(prod.getName()).isEqualTo("TV Caisun LCD 43");
                    })
            ;
        }
    }

    @Test
    void editProductTest(){
        Product product = service.findProductByName("Monitor Gamer LG 24").block();
        Category category = service.findCategoryByName("Electronica").block();
        Product productEdited =  new Product("Monitor Gamer LG 27", 600.0, category);

        client.put()
                .uri(BASE_PATH.concat("/{id}"), Collections.singletonMap("id", product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productEdited), Product.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Monitor Gamer LG 27")
                .jsonPath("$.price").isNotEmpty()
                .jsonPath("$.category").isNotEmpty()
                .jsonPath("$.category.name").isEqualTo("Electronica");

    }

    @Test
    void deleteProductTest(){
        Product product = service.findProductByName("Silla Gamer Kangu").block();

        client.delete()
                .uri(BASE_PATH.concat("/{id}"), Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();

        client.get()
                .uri(BASE_PATH.concat("/{id}"), Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty()
        ;
    }
}
