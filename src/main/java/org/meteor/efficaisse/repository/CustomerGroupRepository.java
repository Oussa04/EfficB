package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.CustomerGroup;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerGroupRepository extends CrudRepository<CustomerGroup,Integer> {
    CustomerGroup findFirstByNameAndStore(String name,Store store);
    List<CustomerGroup> findAllByStore(Store store);
}
