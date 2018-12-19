package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.CategoryPrototype;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryPrototypeRepository extends CrudRepository<CategoryPrototype, String> {

    List<CategoryPrototype> findAllByTagContaining(String type);

}
