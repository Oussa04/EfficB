package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.LoungTabl;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LoungeTableRepository extends CrudRepository<LoungTabl,Long> {
    List<LoungTabl> findAllByStore(String store);
}
