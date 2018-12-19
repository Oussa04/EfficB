package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Customer;
import org.meteor.efficaisse.model.CustomerGroup;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer,Integer> {
    Customer findFirstByCodeAndStore(String code,Store Store);
    List<Customer> findAllByGroupAndStore(CustomerGroup cg , Store s);
}
