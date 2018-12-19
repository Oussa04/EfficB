package org.meteor.efficaisse.repository;


import org.meteor.efficaisse.model.Ingredient;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface IngredientRepository extends CrudRepository<Ingredient,Ingredient.IngredientId>{

    Ingredient findByStoreAndName(Store store,String name);
    List<Ingredient> findAllByStore(Store store);
    Ingredient findByStoreAndId_Id(Store store , int id);

}
