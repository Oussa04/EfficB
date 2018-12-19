package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Category;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CategoryRepository extends CrudRepository<Category,Category.CategoryId> {
    List<Category> findAllByStore(Store store);
    Category findByStoreAndId_Name(Store store,String name);
}
