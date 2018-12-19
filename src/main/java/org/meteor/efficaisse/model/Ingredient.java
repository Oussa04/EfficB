package org.meteor.efficaisse.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Ingredient {

    @EmbeddedId
    private IngredientId id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private Unit unit;

    private float price;


    private String photo;

    private float stockQuantity;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @MapsId("storeid")
    @ManyToOne
    private Store store;



    @OneToMany(mappedBy = "ingredient",fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<IngredientProduct> products = new HashSet<>();

    public Ingredient() {
        super();
        id = new IngredientId();
    }

    public IngredientId getId() {
        return id;
    }

    public void setId(IngredientId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Set<IngredientProduct> getProducts() {
        return products;
    }

    public void setProducts(Set<IngredientProduct> products) {
        products = products;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public float getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(float stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Embeddable
    public static class IngredientId implements Serializable {
        private int storeid;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
            IngredientId that = (IngredientId) o;
            return storeid == that.storeid &&
                    id == that.id;
        }

        @Override
        public int hashCode() {

            return Objects.hash(storeid, id);
        }
    }

}
