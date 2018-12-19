package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Category;
import org.meteor.efficaisse.model.Product;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product,Product.ProductId> {

    Product findByStoreAndName(Store store, String name);
    List<Product> findAllByCategory(Category category);
    Product findByStoreAndId_Id(Store store,int id);
    List<Product> findByStore(Store store);

}
