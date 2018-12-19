package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;


    @Embeddable
    public static class CategoryId implements Serializable {

        private int storeid;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStoreid() {
            return storeid;
        }

        public void setStoreid(int storeid) {
            this.storeid = storeid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CategoryId that = (CategoryId) o;
            return storeid == that.storeid &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(storeid, name);
        }
    }


    @EmbeddedId
    private CategoryId id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @MapsId("storeid")
    @ManyToOne
    private Store store;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private CategoryPrototype prototype;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Product> products;

    @Column(nullable = false)
    private String photo;


    public Category() {
        super();
        id = new  CategoryId();
    }


    public Category(Store store, CategoryPrototype prototype) {
        super();
        id = new  CategoryId();
        this.store = store;
        this.id.name = prototype.getName();
        this.prototype = prototype;
        this.photo = prototype.getPhoto();

    }

    public CategoryId getId() {
        return id;
    }

    public void setId(CategoryId id) {
        this.id = id;
    }

    public CategoryPrototype getPrototype() {
        return prototype;
    }

    public void setPrototype(CategoryPrototype prototype) {
        this.prototype = prototype;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

}


