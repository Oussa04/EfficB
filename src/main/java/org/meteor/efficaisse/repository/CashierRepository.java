package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Cashier;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CashierRepository extends CrudRepository<Cashier,Long> {
    Cashier findFirstByUsernameAndStore(String username,Store store);
    List<Cashier> findAllByStore(Store store);
}
