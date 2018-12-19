package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Product implements Serializable {

    @EmbeddedId
    private ProductId id;


    @NotEmpty
    @Column(nullable = false)
    private String name;


    private Float cost;


    @Column(nullable = false)
    private double price;

    @NotEmpty
    @Column(nullable = false)
    private String photo;
    @ManyToOne
    private Category category;

    private boolean  actif;
    private boolean favoris;

    private Float stockQuantity;

    @JsonIgnore
    @OneToMany(mappedBy = "product" , fetch = FetchType.EAGER)
    private Set<DetailCommande> detailsCommandes = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @MapsId("storeid")
    @ManyToOne
    private Store store;

    @OneToMany(mappedBy = "product",fetch = FetchType.EAGER)
    private Set<IngredientProduct> ingredients = new HashSet<>();

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean isFavoris() {
        return favoris;
    }

    public void setFavoris(boolean favoris) {
        this.favoris = favoris;
    }


    public Product() {
        super();
        id = new ProductId();
        actif = true;
    }



    @JsonIgnore
    public int getNbCommandes() {
        return detailsCommandes.size();
    }

    public ProductId getId() {
        return id;
    }

    public void setId(ProductId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public Set<DetailCommande> getDetailsCommandes() {
        return detailsCommandes;
    }

    public void setDetailsCommandes(Set<DetailCommande> detailsCommandes) {
        this.detailsCommandes = detailsCommandes;
    }


    public Set<IngredientProduct> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<IngredientProduct> ingredients) {
        this.ingredients = ingredients;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Float getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Float stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Embeddable
    public static class ProductId implements Serializable {
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
            ProductId productId = (ProductId) o;
            return storeid == productId.storeid &&
                    id == productId.id;
        }

        @Override
        public int hashCode() {

            return Objects.hash(storeid, id);
        }
    }
}
