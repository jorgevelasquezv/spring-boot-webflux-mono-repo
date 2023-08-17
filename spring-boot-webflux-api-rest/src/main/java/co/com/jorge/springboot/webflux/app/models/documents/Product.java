package co.com.jorge.springboot.webflux.app.models.documents;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "productos")
public class Product {

    @Id
    private String id;

    @NotEmpty
    private String name;

    @NotNull
    private Double price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    @Valid
    @NotNull
    private Category category;

    private String photo;

    public Product() {
    }

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public Product(String name, Double price, Category category) {
        this(name, price);
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public Product setPrice(Double price) {
        this.price = price;
        return this;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Category getCategory() {
        return category;
    }

    public Product setCategory(Category category) {
        this.category = category;
        return this;
    }

    public String getPhoto() {
        return photo;
    }

    public Product setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    @Override
    public String toString() {
        return "Product { " +
                "id: '" + id + '\'' +
                ", name: '" + name + '\'' +
                ", price: " + price +
                ", createAt: " + createAt +
                ", category: " + category.getName() +
                ", photo: " + photo +
                " }";
    }
}
