package org.meteor.efficaisse.controller;

import org.meteor.efficaisse.model.*;
import org.meteor.efficaisse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/cashier")
public class CashierController {

    @Autowired
    ProductRepository productRepos;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CommandeRespository commandeRepo;

    @Autowired
    private DetailCommandeRepository detailCommandeRepo;

    @Autowired
    private IngredientRepository ingredientRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private HistoryRepository historyRepo;

    @Autowired
    private ContreBonRepository contreBonRepo;
    @Autowired
    private CustomerGroupRepository customerGroupRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private SessionRepository sessionRepo;
    @Autowired
    private CashierRepository cashierRepo;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private LoungeTableRepository loungeTableRepository;



    @RequestMapping(value = "/product/show", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findAllProduct(@AuthenticationPrincipal User user) {

        Set<Product> products = new HashSet<>();
        for (Category category : categoryRepo.findAllByStore(user.getStore()))
            products.addAll(productRepo.findAllByCategory(category));
        return ResponseEntity.ok(products);
    }

    @RequestMapping(value = "/commande/cancel/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity cancelCommande(@AuthenticationPrincipal User user, @PathVariable(value = "id") Integer id) {

        Commande.CommandeId commandeId = new Commande.CommandeId();
        commandeId.setStoreid(user.getStore().getId());
        commandeId.setCommandeNumber(id);
        Commande commande = commandeRepo.findOne(commandeId);
        if (commande == null) {
            return ResponseEntity.badRequest().body(new Message("commande no trouvé"));
        }
        commande.setStatus(false);
        commandeRepo.save(commande);
        return ResponseEntity.ok(new Message("commande annulé"));
    }

    @RequestMapping(value = "/user/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    public User getUser(@AuthenticationPrincipal User user) {

        return userRepo.findByUsername(user.getUsername());
    }

    @RequestMapping(value = "/ingredient/show", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findAllIngredient(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ingredientRepo.findAllByStore(user.getStore()));
    }


    @RequestMapping(value = "/category/show", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity showCategory(@AuthenticationPrincipal User user) {


        Set<Category> categories = new HashSet<>(categoryRepo.findAllByStore(user.getStore()));
        return ResponseEntity.ok(categories);


    }
    @RequestMapping(value = "/history/show", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity showHistory(@AuthenticationPrincipal User user) {

  

        return ResponseEntity.ok(historyRepo.findAllByStore(user.getStore()));


    }
    @RequestMapping(value = "/commande/show", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCommandes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commandeRepo.findAllByStore(user.getStore()));

    }
    @RequestMapping(value = "/key/show", method = RequestMethod.GET)
    public ResponseEntity getKey(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new Message(user.getStore().getManager().getActivationCode()));

    }
    @RequestMapping(value = "/contreBon/show", method = RequestMethod.GET)
    public ResponseEntity getContreBon(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contreBonRepo.findAllByStore(user.getStore()));

    }
    @RequestMapping(value = "/payment/assign", method = RequestMethod.POST)
    public ResponseEntity assignPayementsToCommand(@AuthenticationPrincipal User user, @RequestBody List<Payment> payments) {
        if (!payments.isEmpty()) {
            Commande commande =payments.get(0).getCommande();
            commande = commandeRepo.findByStoreAndId_CommandeNumber(user.getStore(),commande.getId().getCommandeNumber());
           for(Payment payment  : payments){
                payment.setStore(user.getStore());
                payment.setCommande(commande);
                if(paymentRepo.findByStoreAndId_Id(user.getStore(),payment.getId().getId())==null){
                    paymentRepo.save(payment);
                }
           }

        }
        return ResponseEntity.ok(new Message("Payment ajouté"));
    }


    @RequestMapping(value = "/customerGroup/get", method = RequestMethod.GET)
    public ResponseEntity getCostumers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(customerGroupRepo.findAllByStore(user.getStore()));
    }
    @RequestMapping(value = "/customer/get", method = RequestMethod.GET)
    public ResponseEntity getDefaultCostumers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(customerRepo.findAllByGroupAndStore(null,user.getStore()));
    }
    @RequestMapping(value = "/session/get", method = RequestMethod.GET)
    public ResponseEntity getSessions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(sessionRepo.findAllByStore(user.getStore()));
    }
    @RequestMapping(value = "/user/getAll", method = RequestMethod.GET)
    public List<Cashier> getCashiers(@AuthenticationPrincipal User user) {

        return cashierRepo.findAllByStore(user.getStore());
    }
    @RequestMapping(value = "/discount/get", method = RequestMethod.GET)
    public ResponseEntity getDiscounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(discountRepository.findAllByStore(user.getStore().getRegisterDC()));
    }
    @RequestMapping(value = "/table/get", method = RequestMethod.GET)
    public ResponseEntity getTable(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loungeTableRepository.findAllByStore(user.getStore().getRegisterDC()));
    }
}
