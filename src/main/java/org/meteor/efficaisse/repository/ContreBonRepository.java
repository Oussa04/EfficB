package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.ContreBon;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContreBonRepository extends CrudRepository<ContreBon,Long> {


    List<ContreBon> findAllByStore(Store store);
    ContreBon findFirstByCodeAndStore(String code,Store store);
}
