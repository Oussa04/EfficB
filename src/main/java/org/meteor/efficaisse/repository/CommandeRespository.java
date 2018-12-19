package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Commande;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandeRespository extends CrudRepository<Commande,Commande.CommandeId> {

    Commande findByStoreAndId_CommandeNumber(Store store , int id);
    List<Commande> findAllByStore(Store store);




}
