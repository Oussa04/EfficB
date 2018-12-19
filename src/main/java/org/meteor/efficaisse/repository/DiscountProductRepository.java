package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.DiscountProduct;
import org.springframework.data.repository.CrudRepository;

public interface DiscountProductRepository extends CrudRepository<DiscountProduct,Long> {
    void deleteByDiscount_NameAndDiscount_Store(String name,String store);
}
