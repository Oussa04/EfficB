package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.IngredientProduct;
import org.springframework.data.repository.CrudRepository;

public interface IngredientProductRepository extends CrudRepository<IngredientProduct,IngredientProduct.IngredientProductId> {

    IngredientProduct findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(int ingredient,int product,int store);




}
