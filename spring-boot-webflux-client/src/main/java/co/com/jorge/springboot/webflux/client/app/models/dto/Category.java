package co.com.jorge.springboot.webflux.client.app.models.dto;

public class Category {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public Category setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "Category { " +
                "id: '" + id + '\'' +
                ", name: '" + name + '\'' +
                " }";
    }
}
