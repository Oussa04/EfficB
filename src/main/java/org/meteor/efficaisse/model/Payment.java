package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Payment {
    @EmbeddedId
    private PaymentId id;



    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @MapsId("storeid")
    @ManyToOne
    private Store store;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private Commande commande;

    private String type;
    private float montant;
    private String commentaire;
    private Integer quantity;


    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public PaymentId getId() {
        return id;
    }

    public void setId(PaymentId id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    @Embeddable
    public static class PaymentId implements Serializable {
        private int  storeid;
        private int  id;

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
            PaymentId paymentId = (PaymentId) o;
            return storeid == paymentId.storeid &&
                    id == paymentId.id;
        }

        @Override
        public int hashCode() {

            return Objects.hash(storeid, id);
        }
    }
}
