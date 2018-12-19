package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.StoreType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import java.util.List;

public interface StoreTypeRepository extends CrudRepository<StoreType, String> {

}
