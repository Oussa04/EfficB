package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.DiscountGroup;
import org.springframework.data.repository.CrudRepository;

public interface DiscountGroupRepository extends CrudRepository<DiscountGroup,Long> {
    void deleteByDiscount_NameAndDiscount_Store(String name,String store);
}
