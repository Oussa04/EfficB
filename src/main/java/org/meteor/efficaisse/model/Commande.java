package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Commande implements Serializable{

	@EmbeddedId
	private CommandeId id;

	public CommandeId getId() {
		return id;
	}

	public void setId(CommandeId id) {
		this.id = id;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@MapsId("storeid")
	@ManyToOne
	private Store store;

	private Date date;


	@ManyToOne
	private Customer client;

	@OneToMany(mappedBy = "commande",fetch = FetchType.EAGER)
	private List<Payment> payments;

	@ManyToOne
	@Cascade(value = { CascadeType.SAVE_UPDATE})
	private Cashier cashier;

	private boolean status = false;

	@OneToMany(mappedBy = "commande",cascade = javax.persistence.CascadeType.ALL)

	private List<DetailCommande> detailsCommandes ;
	@OneToMany(mappedBy = "commande",cascade = javax.persistence.CascadeType.ALL)

	private List<DetailCommandeIngredient> ingredients ;
	@SuppressWarnings("unused")
	private Commande()
	{
	}

	public Commande(Cashier cashier)
	{
		this.date = new Date();
		this.cashier = cashier;
		id = new CommandeId();
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Cashier getCashier() {
		return cashier;
	}

	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
	}

	public List<DetailCommande> getDetailsCommandes() {
		return detailsCommandes;
	}

	public void setDetailsCommandes(List<DetailCommande> detailsCommandes) {
		this.detailsCommandes = detailsCommandes;
	}

	public List<DetailCommandeIngredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<DetailCommandeIngredient> ingredients) {
		this.ingredients = ingredients;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

    public Customer getClient() {
        return client;
    }

    public void setClient(Customer client) {
        this.client = client;
    }

    @Embeddable
	public static class CommandeId implements Serializable {
		private int storeid;
		private int commandeNumber;

		public int getCommandeNumber() {
			return commandeNumber;
		}

		public void setCommandeNumber(int commandeNumber) {
			this.commandeNumber = commandeNumber;
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
			CommandeId that = (CommandeId) o;
			return storeid == that.storeid &&
					commandeNumber == that.commandeNumber;
		}

		@Override
		public int hashCode() {

			return Objects.hash(storeid, commandeNumber);
		}
	}
}
