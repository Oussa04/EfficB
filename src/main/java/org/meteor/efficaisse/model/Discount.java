package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Discount implements Serializable {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String store;

    private float discount;

    private String name;

    private Date dateBegin;

    private Date dateEnd;


    @OneToMany(mappedBy = "discount",fetch = FetchType.EAGER)
    private List<DiscountGroup> discountGroups ;


    @OneToMany(mappedBy = "discount",fetch = FetchType.LAZY)
    private List<DiscountProduct> discountProducts ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<DiscountGroup> getDiscountGroups() {
        return discountGroups;
    }

    public void setDiscountGroups(List<DiscountGroup> discountGroups) {
        this.discountGroups = discountGroups;
    }

    public List<DiscountProduct> getDiscountProducts() {
        return discountProducts;
    }

    public void setDiscountProducts(List<DiscountProduct> discountProducts) {
        this.discountProducts = discountProducts;
    }
}
