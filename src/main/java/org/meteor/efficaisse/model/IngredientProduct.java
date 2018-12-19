package org.meteor.efficaisse.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class IngredientProduct implements Serializable {


    @Embeddable
    public static class IngredientProductId implements Serializable {


        private Product.ProductId productId;

        private Ingredient.IngredientId ingredientId;


        public IngredientProductId() {
        }

        public IngredientProductId(Product.ProductId productId, Ingredient.IngredientId ingredientId) {
            this.productId = productId;
            this.ingredientId = ingredientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IngredientProductId that = (IngredientProductId) o;
            return Objects.equals(productId, that.productId) &&
                    Objects.equals(ingredientId, that.ingredientId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(productId, ingredientId);
        }

        public Product.ProductId getProductId() {
            return productId;
        }

        public void setProductId(Product.ProductId productId) {
            this.productId = productId;
        }

        public Ingredient.IngredientId getIngredientId() {
            return ingredientId;
        }

        public void setIngredientId(Ingredient.IngredientId ingredientId) {
            this.ingredientId = ingredientId;
        }
    }

    @EmbeddedId
    private IngredientProductId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredientId")
    private Ingredient ingredient;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne( fetch = FetchType.EAGER)
    @MapsId("productId")
    private Product product;

    private float quantity;


    public IngredientProduct() {
    }


    public IngredientProductId getId() {
        return id;
    }

    public void setId(IngredientProductId id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
