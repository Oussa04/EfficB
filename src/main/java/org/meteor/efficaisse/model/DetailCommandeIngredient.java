package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class DetailCommandeIngredient {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonIgnore
    @ManyToOne
    private Commande commande;
    private String name;

    @Enumerated(value = EnumType.STRING)
    private Unit unit;

    private float price;

    private float quantity;


    public DetailCommandeIngredient() {
    }
    public DetailCommandeIngredient(DetailCommandeIngredient dci,Commande commande) {
        this.commande = commande;
        this.name = dci.name;
        this.price = dci.price;
        this.quantity = dci.quantity;
        this.unit = dci.unit;
    }


    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
