package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class DetailCommande implements Serializable,Comparable<DetailCommande> {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;




	@JsonIgnore
	@ManyToOne
	private Commande commande;



	@ManyToOne
	private Product product;

	private float price;


	private String productName;
	private int quantity;
	private Float cost;

	@SuppressWarnings("unused")
	public DetailCommande()
	{
	}




	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Float getCost() {
		return cost;
	}

	public void setCost(Float cost) {
		this.cost = cost;
	}

	@Override
	public String toString()
	{
		return "" + quantity + " * " + product.getName();
	}


	public Commande getCommande() {
		return commande;
	}

	public void setCommande(Commande commande) {
		this.commande = commande;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Override
	public int compareTo(DetailCommande o) {
		return 0;
	}

}
