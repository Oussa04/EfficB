package org.meteor.efficaisse.controller;

import org.meteor.efficaisse.model.*;
import org.meteor.efficaisse.repository.*;
import org.meteor.efficaisse.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sync")

public class SyncronizationController {

    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private DiscountRepository discountRepo;

    @Autowired
    private DiscountGroupRepository discountGroupRepo;

    @Autowired
    private DiscountProductRepository discountProductRepo;

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private IngredientRepository ingredientRepo;
    @Autowired
    private IngredientProductRepository ingredientProductRepo;
    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommandeRespository commandeRepo;
    @Autowired
    private DetailCommandeIngredientRepository detailCommandeIngredientRepo;
    @Autowired
    private DetailCommandeRepository detailCommandeRepo;
    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private HistoryRepository historyRepo;

    @Autowired
    private ContreBonRepository contreBonRepo;
    @Autowired
    private LoungeTableRepository loungeTableRepo;

    @Autowired
    private CustomerGroupRepository customerGroupRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private SessionRepository sessionRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CashierRepository cashierRepo;
    @Autowired
    private StackTraceRepository stackTraceRepo;
    private final StorageService storageService;

    @Autowired
    public SyncronizationController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping("/product/add")
    public ResponseEntity addProduct(
            @RequestParam("file") MultipartFile file, @RequestParam("name") String name,
            @RequestParam("categoryName") String category, @RequestParam("favoris") boolean favoris,
            @RequestParam(value = "cost", required = false) Float cost, @RequestParam(value = "quantity", required = false) Float quantity,
            @RequestParam("price") float price, @RequestParam("id") int id, @RequestParam("store") String storeRDC

    ) {


        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        if (productRepo.findByStoreAndName(store, name) != null || productRepo.findByStoreAndId_Id(store, id) != null) {
            return ResponseEntity.badRequest().body("Produit déja existant");
        }
        Product product = new Product();
        product.setCategory(categoryRepo.findByStoreAndId_Name(store, category));
        product.getId().setId(id);
        product.setStore(store);
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
    public ResponseEntity deleteProduct(@PathVariable("id") Integer id, @RequestParam("store") String storeRDC) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Product product = productRepo.findByStoreAndId_Id(store, id);
        if (product != null) {

            storageService.delete(product.getPhoto(), "products");
            for (IngredientProduct ip : product.getIngredients()) {
                deleteIngredientProduct(ip, storeRDC);
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
    public ResponseEntity updateProduct(
            @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("name") String name,
            @RequestParam("favoris") boolean favoris,
            @RequestParam(value = "cost", required = false) Float cost, @RequestParam(value = "quantity", required = false) Float quantity,
            @RequestParam("price") float price, @RequestParam("id") int id, @RequestParam("store") String storeRDC
    ) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Product product = productRepo.findByStoreAndId_Id(store, id);
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
    public ResponseEntity matchIngredientToProduct(@RequestBody List<IngredientProduct> ips, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Product product = null;
        if (!ips.isEmpty())
            product = productRepo.findByStoreAndId_Id(store, ips.get(0).getId().getProductId().getId());

        if (product == null) {

            return ResponseEntity.badRequest().body(new Message("Produit introuvable"));
        }
        List<Integer> errors = new ArrayList<>();
        boolean toAdd = true;
        for (IngredientProduct ip : ips) {
            toAdd = true;

            Ingredient ingredient = ingredientRepo.findByStoreAndId_Id(store, ip.getId().getIngredientId().getId());


            if (ingredient == null) {

                errors.add(ip.getId().getIngredientId().getId());
                toAdd = false;
            }


            if (toAdd) {
                if (ingredientProductRepo.findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(ingredient.getId().getId(), product.getId().getId(), store.getId()) == null) {
                    IngredientProduct ingredientProduct = new IngredientProduct();
                    ingredientProduct.setId(new IngredientProduct.IngredientProductId(product.getId(), ingredient.getId()));
                    ingredientProduct.setIngredient(ingredient);
                    ingredientProduct.setProduct(product);
                    ingredientProduct.setQuantity(ip.getQuantity());
                    ingredientProductRepo.save(ingredientProduct);
                } else {
                    IngredientProduct ingredientProduct = ingredientProductRepo.findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(ingredient.getId().getId(), product.getId().getId(), store.getId());
                    ingredientProduct.setQuantity(ip.getQuantity());
                    ingredientProductRepo.save(ingredientProduct);
                }

            }
        }
        if (errors.isEmpty())
            return ResponseEntity.ok(new Message("ingredient ajouté au produit"));
        else {
            return ResponseEntity.badRequest().body(errors);
        }
    }

    @RequestMapping(value = "/product/ingredient/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateIngredientProduct(@RequestBody IngredientProduct ip, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        IngredientProduct ingredientProduct = ingredientProductRepo.findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(ip.getId().getIngredientId().getId(), ip.getId().getProductId().getId(), store.getId());

        if (ingredientProduct == null) {
            return ResponseEntity.badRequest().body(new Message("Liaison introuvable"));
        }

        ingredientProduct.setQuantity(ip.getQuantity());
        ingredientProductRepo.save(ingredientProduct);

        return ResponseEntity.ok(new Message("Liaison modifié"));

    }

    @RequestMapping(value = "/product/ingredient/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteIngredientProduct(@RequestBody IngredientProduct ip, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        IngredientProduct ingredientProduct = ingredientProductRepo.findById_IngredientId_IdAndId_ProductId_IdAndProduct_Store_Id(ip.getId().getIngredientId().getId(), ip.getId().getProductId().getId(), store.getId());

        if (ingredientProduct == null) {
            return ResponseEntity.badRequest().body(new Message("Liaison introuvable"));
        }

        ingredientProduct.setQuantity(ip.getQuantity());
        ingredientProductRepo.delete(ingredientProduct);

        return ResponseEntity.ok(new Message("Liaison supprimé"));

    }

//************************************************Ingredient*********************************************************

    @RequestMapping(value = "/ingredient/add", method = RequestMethod.POST)
    public ResponseEntity addIngredient(
            @RequestParam("file") MultipartFile file, @RequestParam("name") String name,
            @RequestParam("cost") float price, @RequestParam("quantity") float quantity,
            @RequestParam("unit") Unit unit, @RequestParam("id") int id, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        if (ingredientRepo.findByStoreAndName(store, name) != null || ingredientRepo.findByStoreAndId_Id(store, id) != null) {
            return ResponseEntity.badRequest().body("ingredient déja existant");
        }

        Ingredient ingredient = new Ingredient();

        ingredient.setStore(store);
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
    public ResponseEntity deleteIngredient(@PathVariable("id") Integer id, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Ingredient ingredient = ingredientRepo.findByStoreAndId_Id(store, id);
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
    public ResponseEntity updateIngredient(
            @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("name") String name,
            @RequestParam("cost") float price, @RequestParam("quantity") float quantity,
            @RequestParam("unit") Unit unit, @RequestParam("id") int id, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Ingredient ingredient = ingredientRepo.findByStoreAndId_Id(store, id);
        if (ingredient == null) {
            return ResponseEntity.badRequest().body("ingredient introuvable");
        }


        if (file != null) {
            storageService.delete(ingredient.getPhoto(), "ingredients");
            ingredient.setPhoto(storageService.store(file, "ingredients"));
        }

        ingredient.setName(name);
        ingredient.setPrice(price);
        ingredient.setStockQuantity(quantity);
        ingredient.setUnit(unit);

        ingredientRepo.save(ingredient);

        return ResponseEntity.ok(ingredient);

    }
//****************************************************************************Category******************************************

    @RequestMapping(value = "/category/add", method = RequestMethod.POST)
    public ResponseEntity addCategory(
            @RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        if (categoryRepo.findByStoreAndId_Name(store, name) != null) {
            return ResponseEntity.badRequest().body("category déja existant");
        }
        String filename = storageService.store(file, "categories");
        Category category = new Category();
        category.getId().setName(name);
        category.setStore(store);
        category.setPhoto(filename);
        categoryRepo.save(category);
        return ResponseEntity.ok(new Message("category added"));
    }

    @RequestMapping(value = "/category/delete/{name}", method = RequestMethod.DELETE)
    public ResponseEntity deleteCategory(@PathVariable("name") String name, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Category category = categoryRepo.findByStoreAndId_Name(store, name);
        if (category != null) {
            if (category.getPrototype() == null) {
                storageService.delete(category.getPhoto(), "categories");
            }
            for (Product p : category.getProducts()) {
                deleteProduct(p.getId().getId(), storeRDC);
            }
            categoryRepo.delete(category);
            return ResponseEntity.ok(new Message("category deleted"));
        } else {
            return ResponseEntity.ok(new Message("category not found"));
        }

    }

    @RequestMapping(value = "/category/update/{name}", method = RequestMethod.PUT)
    public ResponseEntity updateCategory(@PathVariable("name") String name,
                                         @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Category category = categoryRepo.findByStoreAndId_Name(store, name);
        if (category != null) {
            if (file != null) {
                String filename = storageService.store(file, "categories");
                category.setPhoto(filename);
            }


            categoryRepo.save(category);
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.ok(new Message("category not found"));
        }

    }
//*************************************************************************************Commande***************************************************************

    @RequestMapping(value = "/commande/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addCommande(@RequestParam("username") String username, @RequestBody Commande commande, @RequestParam("commande") int commandeNumber, @RequestParam("store") String storeRDC) {


        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store not found"));
        }
        Cashier cashier = cashierRepo.findFirstByUsernameAndStore(username, store);
        if (cashier == null) {
            return ResponseEntity.badRequest().body(new Message("Cashier not found"));
        }
        Commande c = new Commande(cashier);
        c.getId().setCommandeNumber(commandeNumber);
        c.setDate(commande.getDate());
        c.setStore(store);
        c.setStatus(false);
        if (commande.getClient() != null) {
            Customer cer = customerRepo.findFirstByCodeAndStore(commande.getClient().getCode(), store);
            c.setClient(cer);
        }


        commandeRepo.save(c);
        List<Integer> errors = new ArrayList<>();

        for (DetailCommande dc : commande.getDetailsCommandes()) {
            Product p = productRepo.findByStoreAndName(store, dc.getProductName());


            if (p != null) {
                if (p.getStockQuantity() != null) {
                    p.setStockQuantity(p.getStockQuantity() - dc.getQuantity());
                    productRepo.save(p);
                } else {
                    for (IngredientProduct i : p.getIngredients()) {
                        i.getIngredient().setStockQuantity(i.getIngredient().getStockQuantity() - i.getQuantity() * dc.getQuantity());
                        ingredientRepo.save(i.getIngredient());
                    }
                }
                DetailCommande Dcommande = new DetailCommande();

                Dcommande.setProduct(p);
                Dcommande.setCommande(c);
                Dcommande.setCost(dc.getCost());
                Dcommande.setPrice(dc.getPrice());
                Dcommande.setProductName(dc.getProductName());
                Dcommande.setQuantity(dc.getQuantity());
                detailCommandeRepo.save(Dcommande);

            } else {
                DetailCommande Dcommande = new DetailCommande();
                Dcommande.setCommande(c);
                Dcommande.setCost(dc.getCost());
                Dcommande.setPrice(dc.getPrice());
                Dcommande.setProductName(dc.getProductName());
                Dcommande.setQuantity(dc.getQuantity());
                detailCommandeRepo.save(Dcommande);
            }

        }
        for (DetailCommandeIngredient dci : commande.getIngredients()) {
            DetailCommandeIngredient detailCommandeIngredient = new DetailCommandeIngredient(dci, c);
            detailCommandeIngredientRepo.save(detailCommandeIngredient);
        }
        if (errors.isEmpty())
            return ResponseEntity.ok(new Message("commande added"));
        else
            return ResponseEntity.badRequest().body(errors);


    }

    //*****************************************************************************************************Payment********************************************************
    @RequestMapping(value = "/payment/assign", method = RequestMethod.POST)
    public ResponseEntity assignPayementsToCommand
    (@RequestBody List<Payment> payments, @RequestParam("commande") int commandNumber, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        if (!payments.isEmpty()) {

            Commande commande = commandeRepo.findByStoreAndId_CommandeNumber(store, commandNumber);
            for (Payment payment : payments) {
                payment.setStore(store);
                payment.setCommande(commande);
                if (paymentRepo.findByStoreAndId_Id(store, payment.getId().getId()) == null) {
                    paymentRepo.save(payment);
                }
            }
            commande.setStatus(true);
            commandeRepo.save(commande);

        }

        return ResponseEntity.ok(new Message("Payment ajouté"));
    }

    //*****************************************************History******************************************************************
    @RequestMapping(value = "/history/add", method = RequestMethod.POST)
    public ResponseEntity addHistory(@RequestBody List<History> histories, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        for (History history : histories) {
            history.setStore(store);
            historyRepo.save(history);
        }

        return ResponseEntity.ok(new Message("History Added"));
    }

    //***************************************************ContreBon*********************************************************************
    @RequestMapping(value = "/contreBon/add", method = RequestMethod.POST)
    public ResponseEntity addContreBon(@RequestBody List<ContreBon> contreBons, @RequestParam("store") String storeRDC) {

        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        for (ContreBon contreBon : contreBons) {
            ContreBon db = contreBonRepo.findFirstByCodeAndStore(contreBon.getCode(), store);
            if (db != null) {
                if (contreBon.isPayed()) {
                    db.setDatePay(contreBon.getDatePay());
                    db.setPayed(true);
                    contreBonRepo.save(db);
                }
            } else {
                contreBon.setStore(store);
                contreBonRepo.save(contreBon);
            }
        }

        return ResponseEntity.ok(new Message("History Added"));
    }

    //*************************************************Clients*******************************************************

    @RequestMapping(value = "/customer/addGroup", method = RequestMethod.POST)
    public ResponseEntity addCustomerGroup(@RequestBody CustomerGroup group, @RequestParam("store") String storeRDC) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        CustomerGroup cg = customerGroupRepo.findFirstByNameAndStore(group.getName(), store);
        if (cg != null) {
            return ResponseEntity.badRequest().body(new Message("Groupe existe déja"));
        }
        group.setStore(store);
        customerGroupRepo.save(group);
        return ResponseEntity.ok(group);

    }

    @RequestMapping(value = "/customer/modifyGroup", method = RequestMethod.POST)
    public ResponseEntity modifyCustomerGroup(@RequestBody CustomerGroup group, @RequestParam("store") String storeRDC) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        CustomerGroup cg = customerGroupRepo.findFirstByNameAndStore(group.getName(), store);
        if (cg == null) {
            return ResponseEntity.badRequest().body(new Message("Groupe n'existe pas"));
        }
        cg.setDiscount(group.getDiscount());
        customerGroupRepo.save(cg);
        return ResponseEntity.ok(group);

    }

    @RequestMapping(value = "/customer/deleteGroup", method = RequestMethod.DELETE)
    public ResponseEntity deleteCustomerGroup(@RequestParam("group") String group, @RequestParam("store") String storeRDC) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        CustomerGroup cg = customerGroupRepo.findFirstByNameAndStore(group, store);
        if (cg == null) {
            return ResponseEntity.badRequest().body(new Message("Groupe n'existe pas"));
        }
        for (Customer c :
                cg.getCustomers()) {
            c.setGroup(null);
            customerRepo.save(c);
        }
        customerGroupRepo.delete(cg);
        return ResponseEntity.ok(store);

    }

    @RequestMapping(value = "/customer/add", method = RequestMethod.POST)
    public ResponseEntity addCustomer(@RequestBody Customer customer, @RequestParam("store") String storeRDC, @RequestParam(value = "group", required = false) String group) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }

        if (customerRepo.findFirstByCodeAndStore(customer.getCode(), store) != null) {
            return ResponseEntity.badRequest().body(new Message("Customer existe deja"));
        }
        if (group != null) {
            CustomerGroup cg = customerGroupRepo.findFirstByNameAndStore(group, store);
            if (cg == null) {
                return ResponseEntity.badRequest().body(new Message("Groupe n'existe pas"));
            }
            customer.setGroup(cg);
        } else {
            customer.setGroup(null);
        }
        customer.setStore(store);

        customerRepo.save(customer);
        return ResponseEntity.ok(customer);

    }

    @RequestMapping(value = "/customer/update", method = RequestMethod.POST)
    public ResponseEntity updateCustomer(@RequestBody Customer customer, @RequestParam("store") String storeRDC, @RequestParam(value = "group", required = false) String group) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }
        Customer c = customerRepo.findFirstByCodeAndStore(customer.getCode(), store);
        if (c == null) {
            return ResponseEntity.badRequest().body(new Message("Customer  n'existe pas"));
        }
        if (group != null) {
            CustomerGroup cg = customerGroupRepo.findFirstByNameAndStore(group, store);
            if (cg == null) {
                return ResponseEntity.badRequest().body(new Message("Groupe n'existe pas"));
            }
            c.setGroup(cg);
        } else {
            c.setGroup(null);
        }


        customerRepo.save(c);
        return ResponseEntity.ok(customer);

    }

    //***************************************************************************Sessions************************************************************************
    @RequestMapping(value = "/session/add", method = RequestMethod.POST)
    public ResponseEntity addSession(@RequestBody Session session, @RequestParam("store") String storeRDC) {
        Store store = storeRepo.findByRegisterDC(storeRDC);
        boolean error = false;
        if (store == null) {
            return ResponseEntity.badRequest().body(new Message("Store inexistant"));
        }


        Cashier c = cashierRepo.findFirstByUsernameAndStore(session.getUser().getUsername(), store);
        if (c == null) {
            return ResponseEntity.badRequest().body(new Message("Cashier inexistant"));
        }
        session.setStore(store);
        session.setUser(c);
        sessionRepo.save(session);


        return ResponseEntity.ok(session);
    }

    //***********************************************************************User*********************************************************************************
    @RequestMapping("/user/pattern")
    public ResponseEntity addCashier(@RequestParam("pattern") String pattern, @RequestParam("username") String username, @RequestParam("store") String storeRDC) {

        Cashier userM = cashierRepo.findFirstByUsernameAndStore(username, storeRepo.findByRegisterDC(storeRDC));
        if (userM == null)
            return ResponseEntity.badRequest().body(new Message("Utilisateur n'existe n'existe pas"));

        userM.setPattern(pattern);

        cashierRepo.save(userM);
        return ResponseEntity.ok(userM);


    }

    //********************************************************************Discount*************************************************************************************

    @RequestMapping(value = "/discount/add", method = RequestMethod.POST)
    public ResponseEntity addDiscount(@RequestBody Discount discount) {

        Discount d = new Discount();
        d.setDateBegin(discount.getDateBegin());

        d.setDateEnd(discount.getDateEnd());
        d.setName(discount.getName());
        d.setDiscount(discount.getDiscount());
        d.setStore(discount.getStore());


        discountRepo.save(d);

        for (DiscountGroup group :
                discount.getDiscountGroups()) {
            group.setStore(discount.getStore());
            group.setDiscount(d);
            discountGroupRepo.save(group);

        }
        for (DiscountProduct product :
                discount.getDiscountProducts()) {
            product.setDiscount(d);
            product.setStore(discount.getStore());
            discountProductRepo.save(product);
        }
        return ResponseEntity.ok(discount);


    }
    @Transactional
    @RequestMapping(value = "/discount/update", method = RequestMethod.POST)
    public ResponseEntity modifyDiscount(@RequestBody Discount discount) {


        discountProductRepo.deleteByDiscount_NameAndDiscount_Store(discount.getName(), discount.getStore());
        discountGroupRepo.deleteByDiscount_NameAndDiscount_Store(discount.getName(), discount.getStore());
        discountRepo.deleteByStoreAndName(discount.getStore(), discount.getName());
        Discount d = new Discount();
        d.setDateBegin(discount.getDateBegin());

        d.setDateEnd(discount.getDateEnd());
        d.setName(discount.getName());
        d.setDiscount(discount.getDiscount());
        d.setStore(discount.getStore());


        discountRepo.save(d);

        for (DiscountGroup group :
                discount.getDiscountGroups()) {
            group.setStore(discount.getStore());
            group.setDiscount(d);
            discountGroupRepo.save(group);

        }
        for (DiscountProduct product :
                discount.getDiscountProducts()) {
            product.setStore(discount.getStore());
            product.setDiscount(d);
            discountProductRepo.save(product);
        }
        return ResponseEntity.ok(discount);


    }
    //***************************************************StackTrace****************************************************

    @RequestMapping(value = "/stackTrace/add", method = RequestMethod.POST)
    public ResponseEntity addStackTrace(@RequestBody List<StackTrace> list) {

        for (StackTrace s:
             list) {
            s.setStoreE(storeRepo.findByRegisterDC(s.getStore()));
        }
        stackTraceRepo.save(list);
        return ResponseEntity.ok(new Message("Success"));

    }

    //****************************************************Table*****************************************************
    @RequestMapping(value = "/table/add", method = RequestMethod.POST)
    public ResponseEntity addTable(@RequestBody List<LoungTabl> list, @RequestParam("store") String storeRDC) {

        for (LoungTabl lt :
                list) {
            lt.setStore(storeRDC);
            lt.setStore(storeRDC);
            loungeTableRepo.save(lt);
        }

        return ResponseEntity.ok(new Message("Success"));

    }
}
