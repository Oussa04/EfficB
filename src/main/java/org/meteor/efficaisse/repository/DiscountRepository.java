package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Discount;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DiscountRepository extends CrudRepository<Discount,Long> {

    void deleteByStoreAndName(String store,String name);
    List<Discount> findAllByStore(String store);
}
