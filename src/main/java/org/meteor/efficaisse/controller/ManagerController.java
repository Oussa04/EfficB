package org.meteor.efficaisse.controller;

import org.meteor.efficaisse.model.*;
import org.meteor.efficaisse.repository.*;
import org.meteor.efficaisse.service.CustomUserDetailsService;
import org.meteor.efficaisse.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private DetailCommandeRepository detailCommandeRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private IngredientRepository ingredientRepo;
    @Autowired
    private IngredientProductRepository ingredientProductRepo;
    @Autowired
    private CashierRepository cashierRepo;

    private final StorageService storageService;

    @Autowired
    public ManagerController(StorageService storageService) {
        this.storageService = storageService;
    }

    //****************************************Cashier***************************************************************
    @RequestMapping("/cashier/add")
    public ResponseEntity addCashier(@AuthenticationPrincipal User user, @RequestBody Cashier cashier) {

        if (cashierRepo.findFirstByUsernameAndStore(cashier.getUsername(),user.getStore()) != null)
            return ResponseEntity.badRequest().body(new Message("username  existe déja"));

        Store store = user.getStore();
        cashier.setStore(store);
        cashierRepo.save(cashier);
        return ResponseEntity.ok(cashier);


    }


    //***********************************Product*********************************************
    @RequestMapping("/product/add")
    public ResponseEntity addProduct(@AuthenticationPrincipal User user,
                                     @RequestParam("file") MultipartFile file, @RequestParam("name") String name,
                                     @RequestParam("categoryName") String category, @RequestParam("favoris") boolean favoris,
                                     @RequestParam(value = "cost", required = false) Float cost, @RequestParam(value = "quantity", required = false) Float quantity,
                                     @RequestParam("price") float price, @RequestParam("id") int id

    ) {


        if (productRepo.findByStoreAndName(user.getStore(), name) != null || productRepo.findByStoreAndId_Id(user.getStore(), id) != null) {
            return ResponseEntity.badRequest().body("Produit déja existant");
        }
        Product product = new Product();
        product.setCategory(categoryRepo.findByStoreAndId_Name(user.getStore(), category));
        product.getId().setId(id);
        product.setStore(user.getStore());
        product.setName(name);
        if (cost != null)
            product.setCost(cost);
        product.setFavoris(favoris);
        product.setPrice(price);
        if (quantity != null)
            product.setStockQuantity(quantity);
        product.setPhoto(storageService.store(file, "products"));
        productRepo.save(product);

        return ResponseEntity.ok(product);
    }


    @RequestMapping(value = "/product/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteProduct(@PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        Product product = productRepo.findByStoreAndId_Id(user.getStore(), id);
        if (product != null) {
            storageService.delete(product.getPhoto(), "products");
            for (IngredientProduct ip : product.getIngredients()) {
                deleteIngredientProduct(ip, user);
            }
            for (DetailCommande dc : product.getDetailsCommandes()) {
                dc.setProduct(null);
                detailCommandeRepo.save(dc);
            }
            productRepo.delete(product);


        } else {
            return ResponseEntity.badRequest().body("Produit introuvable");
        }
        return ResponseEntity.ok(new Message("deleted"));
    }


    @RequestMapping(value = "/product/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProduct(@AuthenticationPrincipal User user,
                                        @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("name") String name,
                                        @RequestParam("favoris") boolean favoris,
                                        @RequestParam(value = "cost", required = false) Float cost, @RequestParam(value = "quantity", required = false) Float quantity,
                                        @RequestParam("price") float price, @RequestParam("id") int id
    ) {
        Product product = productRepo.findByStoreAndId_Id(user.getStore(), id);
        if (product == null) {
            return ResponseEntity.badRequest().body("Produit introuvable");
        }
        if (file != null) {
            storageService.delete(product.getPhoto(), "products");
            product.setPhoto(storageService.store(file, "products"));
        }
        product.setName(name);
        if (cost != null)
            product.setCost(cost);
        product.setFavoris(favoris);
        product.setPrice(price);
        if (quantity != null)
            product.setStockQuantity(quantity);
        productRepo.save(product);
        return ResponseEntity.ok(product);
    }

    //***********************************************************IngredientProduct****************************************************
    @RequestMapping(value = "/product/ingredient/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity matchIngredientToProduct(@RequestBody List<IngredientProduct> ips, @AuthenticationPrincipal User user) {


        for (IngredientProduct ip : ips) {

            Product product = productRepo.findByStoreAndId_Id(user.getStore(), ip.getId().getProductId().getId());
            Ingredient ingredient = ingredientRepo.findByStoreAndId_Id(user.getStore(), ip.getId().getIngredientId().getId());


            if (product == null) {

                return ResponseEntity.badRequest().body(new Message("Produit introuvable"));
            }
            if (ingredient == null) {

                return ResponseEntity.badRequest().body(new Message("Ingredient introuvable"));
            }
            if (ingredientProductRepo.findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(ingredient.getId().getId(), product.getId().getId(), user.getStore().getId()) != null) {
                return ResponseEntity.badRequest().body(new Message("Produit deja lié"));
            }


            IngredientProduct ingredientProduct = new IngredientProduct();
            ingredientProduct.setId(new IngredientProduct.IngredientProductId(product.getId(), ingredient.getId()));
            ingredientProduct.setIngredient(ingredient);
            ingredientProduct.setProduct(product);
            ingredientProduct.setQuantity(ip.getQuantity());
            ingredientProductRepo.save(ingredientProduct);

        }
        return ResponseEntity.ok(new Message("ingredient ajouté au produit"));

    }

    @RequestMapping(value = "/product/ingredient/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateIngredientProduct(@RequestBody IngredientProduct ip, @AuthenticationPrincipal User user) {


        IngredientProduct ingredientProduct = ingredientProductRepo.findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(ip.getId().getIngredientId().getId(), ip.getId().getProductId().getId(), user.getStore().getId());

        if (ingredientProduct == null) {
            return ResponseEntity.badRequest().body(new Message("Liaison introuvable"));
        }

        ingredientProduct.setQuantity(ip.getQuantity());
        ingredientProductRepo.save(ingredientProduct);

        return ResponseEntity.ok(new Message("Liaison modifié"));

    }

    @RequestMapping(value = "/product/ingredient/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteIngredientProduct(@RequestBody IngredientProduct ip, @AuthenticationPrincipal User user) {


        IngredientProduct ingredientProduct = ingredientProductRepo.findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(ip.getId().getIngredientId().getId(), ip.getId().getProductId().getId(), user.getStore().getId());

        if (ingredientProduct == null) {
            return ResponseEntity.badRequest().body(new Message("Liaison introuvable"));
        }

        ingredientProduct.setQuantity(ip.getQuantity());
        ingredientProductRepo.delete(ingredientProduct);

        return ResponseEntity.ok(new Message("Liaison supprimé"));

    }

//************************************************Ingredient*********************************************************

    @RequestMapping(value = "/ingredient/add")
    public ResponseEntity addIngredient(@AuthenticationPrincipal User user,
                                        @RequestParam("file") MultipartFile file, @RequestParam("name") String name,
                                        @RequestParam("cost") float price, @RequestParam("quantity") float quantity,
                                        @RequestParam("unit") Unit unit, @RequestParam("id") int id) {

        if (ingredientRepo.findByStoreAndName(user.getStore(), name) != null || ingredientRepo.findByStoreAndId_Id(user.getStore(), id) != null) {
            return ResponseEntity.badRequest().body("ingredient déja existant");
        }

        Ingredient ingredient = new Ingredient();

        ingredient.setStore(user.getStore());
        ingredient.getId().setId(id);
        ingredient.setName(name);
        ingredient.setPrice(price);
        ingredient.setStockQuantity(quantity);
        ingredient.setUnit(unit);
        ingredient.setPhoto(storageService.store(file, "ingredients"));
        ingredientRepo.save(ingredient);

        return ResponseEntity.ok(ingredient);
    }


    @RequestMapping(value = "/ingredient/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteIngredient(@AuthenticationPrincipal User user, @PathVariable("id") Integer id) {
        Ingredient ingredient = ingredientRepo.findByStoreAndId_Id(user.getStore(), id);
        if (ingredient != null) {
            if (!ingredient.getProducts().isEmpty())
                return ResponseEntity.badRequest().body(new Message("ingredient utilisé par des produits"));
            storageService.delete(ingredient.getPhoto(), "ingredients");
            ingredientRepo.delete(ingredient);
            return ResponseEntity.ok(new Message("ingredient supprimé"));
        } else {
            return ResponseEntity.badRequest().body(new Message("ingredient Introuvable"));
        }


    }


    @RequestMapping(value = "/ingredient/update", method = RequestMethod.PUT)
    public ResponseEntity updateIngredient(@AuthenticationPrincipal User user,
                                           @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("name") String name,
                                           @RequestParam("cost") float price, @RequestParam("quantity") float quantity,
                                           @RequestParam("unit") Unit unit, @RequestParam("id") int id) {

        Ingredient ingredient = ingredientRepo.findByStoreAndId_Id(user.getStore(), id);
        if (ingredient == null) {
            return ResponseEntity.badRequest().body("ingredient introuvable");
        }


        if (file != null) {
            storageService.delete(ingredient.getPhoto(), "ingredients");
        }
        ingredient.setPhoto(storageService.store(file, "ingredients"));
        ingredient.setName(name);
        ingredient.setPrice(price);
        ingredient.setStockQuantity(quantity);
        ingredient.setUnit(unit);

        ingredientRepo.save(ingredient);

        return ResponseEntity.ok(ingredient);

    }
//****************************************************************************Category******************************************

    @RequestMapping(value = "/category/add", method = RequestMethod.POST)
    public ResponseEntity addCategory(@AuthenticationPrincipal User user,
                                      @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("name") String name) {

        if (categoryRepo.findByStoreAndId_Name(user.getStore(), name) != null) {
            return ResponseEntity.badRequest().body("category déja existant");
        }
        String filename = storageService.store(file, "categories");
        Category category = new Category();
        category.getId().setName(name);
        category.setStore(user.getStore());
        category.setPhoto(filename);
        categoryRepo.save(category);
        return ResponseEntity.ok(new Message("category added"));
    }

    @RequestMapping(value = "/category/delete/{name}", method = RequestMethod.DELETE)
    public ResponseEntity deleteCategory(@AuthenticationPrincipal User user, @PathVariable("name") String name) {

        Store store = user.getStore();
        Category category = categoryRepo.findByStoreAndId_Name(store, name);
        if (category != null) {
            if (category.getPrototype() == null) {
                storageService.delete(category.getPhoto(), "categories");
            }
            for (Product p : category.getProducts()) {
                deleteProduct(p.getId().getId(), user);
            }
            categoryRepo.delete(category);
            return ResponseEntity.ok(new Message("category deleted"));
        } else {
            return ResponseEntity.ok(new Message("category not found"));
        }

    }

    @RequestMapping(value = "/category/update/{name}", method = RequestMethod.PUT)
    public ResponseEntity updateCategory(@AuthenticationPrincipal User user, @PathVariable("name") String name,
                                         @RequestParam("file") MultipartFile file, @RequestParam("name") String newName) {

        Store store = user.getStore();
        Category category = categoryRepo.findByStoreAndId_Name(store, name);
        if (category != null) {
            if (file != null) {
                String filename = storageService.store(file, "categories");
                category.setPhoto(filename);
            }


            category.getId().setName(newName);

            categoryRepo.save(category);
            return ResponseEntity.ok(new Message("category updated"));
        } else {
            return ResponseEntity.ok(new Message("category not found"));
        }

    }

}
