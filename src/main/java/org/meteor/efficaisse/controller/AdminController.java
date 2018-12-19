package org.meteor.efficaisse.controller;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.meteor.efficaisse.model.*;
import org.meteor.efficaisse.repository.*;
import org.meteor.efficaisse.service.CustomUserDetailsService;
import org.meteor.efficaisse.service.StorageService;
import org.meteor.efficaisse.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class  AdminController {
    private boolean exists;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private StoreTypeRepository storeTypeRepo;
    @Autowired
    private LicenceRepository licenceRepo;
    @Autowired
    private CategoryPrototypeRepository categoryPrototypeRepo;
    @Autowired
    private StoreTypeRepository typeRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private PackageRequestRepository packageRequestRepo;
    @Autowired
    private CashierRepository cashierRepo;
    @Autowired
    private StackTraceRepository stackTraceRepo;

    private final StorageService storageService;

    @Autowired
    public AdminController(StorageService storageService) {
        this.storageService = storageService;
    }


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    public void confirmMail(String s, String m, String mail) {

        final SimpleMailMessage email = constructEmailMessage(s,m,mail);
        mailSender.send(email);
    }


    private final SimpleMailMessage constructEmailMessage(String subjectt, String messages,String mail){
        final String recipientAddress = "youssef.draoui94@gmail.com";
        final String subject = subjectt;

        final String message = messages;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Message recieved from: " + mail +" \n\n" + message);
        email.setFrom(mail);

        return email;
    }


    @RequestMapping(value = "/client/contactSend", method = RequestMethod.POST)
    public ResponseEntity contactGet(@RequestParam(value = "message", required = false) String message,
                                     @RequestParam(value = "mail") String mail,
                                    @RequestParam(value = "subject") String subject)

    {
        final SimpleMailMessage email = constructEmailMessage(subject,message,mail);
        mailSender.send(email);
        return ResponseEntity.ok().body("message sent");
    }


    @RequestMapping(value = "/client/add", method = RequestMethod.POST)
    public ResponseEntity addClient(@RequestParam(value = "id", required = false) Integer requestId, @RequestBody User u) {
        if (userRepo.findByUsernameOrEmail(u.getUsername(), u.getEmail()) != null)
            return ResponseEntity.badRequest().body("username ou Email existe déja");

        u.setActivationCode(UUID.randomUUID().toString());
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setRoles(new HashSet<>());
        u.getRoles().add(roleRepo.findById("MANAGER"));
        u.getRoles().add(roleRepo.findById("CASHIER"));
        Store store = u.getStore();
        u.setStore(null);
        store.setLicence(licenceRepo.findByName(store.getLicence().getName()));
        store.setPayDate(new Date());
        store.setType(typeRepo.findOne(store.getType().getName()));

        if (storeRepo.findByRegisterDC(store.getRegisterDC()) != null) {
            return ResponseEntity.badRequest().body("Store existe déja");
        }

        userRepo.save(u);
        store.setManager(u);
        storeRepo.save(store);
        for (CategoryPrototype categoryP : categoryPrototypeRepo.findAllByTagContaining(store.getType().getName())) {

            Category category = new Category(store, categoryP);
            categoryRepo.save(category);
        }
        u.setStore(store);
        userRepo.save(u);
        Cashier cashier = new Cashier();
        cashier.setStore(store);
        cashier.setName(u.getName());
        cashier.setUsername(u.getUsername());
        cashierRepo.save(cashier);

        customUserDetailsService.confirmRegistration(u);
        if (requestId != null) {
            packageRequestRepo.delete(requestId);
        }
        return ResponseEntity.ok().body(store);


    }

    @RequestMapping(value = "/store/list", method = RequestMethod.GET)
    public List<Store> getUser() {
        return (List<Store>) storeRepo.findAll();
    }

    @RequestMapping(value = "/user/get", method = RequestMethod.GET)
    public User getUser(@AuthenticationPrincipal User user) {
        return user;
    }

    @RequestMapping(value = "/admin/list", method = RequestMethod.GET)
    public List<User> getAdmins() {
        Role r = new Role();
        r.setId("ADMIN");
        return userRepo.findAllByRolesContains(r);
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    public ResponseEntity addAdmin(@RequestBody User u) {

        if (userRepo.findByUsernameOrEmail(u.getUsername(), u.getEmail()) != null)
            return ResponseEntity.badRequest().body("Nom d'utilisateur ou Email existe déja");

        u.setActivationCode(UUID.randomUUID().toString());
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setRoles(new HashSet<>());
        u.getRoles().add(roleRepo.findById("ADMIN"));
        customUserDetailsService.confirmRegistration(u);
        userRepo.save(u);

        return ResponseEntity.ok().body(userRepo.findByUsername(u.getUsername()));
    }

    @RequestMapping(value = "/licence/add", method = RequestMethod.POST)
    public ResponseEntity addLicence(@RequestBody Licence l) {
        if (licenceRepo.findFirstByNameOrLength(l.getName(), l.getLength()) != null) {
            return ResponseEntity.badRequest().body("Licence Existe Déja");

        }
        licenceRepo.save(l);
        return ResponseEntity.ok(l);

    }

    @RequestMapping(value = "/licence/remove", method = RequestMethod.POST)
    public ResponseEntity removeLicence(@RequestBody Licence l) {
        exists = false;
        Licence licence = licenceRepo.findOne(l.getId());
        Licence zero = licenceRepo.findFirstByLength(0);
        if (licence == null) {
            return ResponseEntity.badRequest().body("Licence n'existe pas");

        }
        if (licence.getLength() == 0) {
            return ResponseEntity.badRequest().body("La licence par default ne peut pas être supprimé");
        }
        List<Store> stores = storeRepo.findAllByLicenceIs(licence);

        stores.forEach(store -> {
            if (store.getManager().isAccountNonExpired())
                exists = true;
            else {
                store.setLicence(zero);
                storeRepo.save(store);
            }
        });
        if (exists) {
            return ResponseEntity.badRequest().body("Cette licence est affectée à des utilisateurs");
        }

        licenceRepo.delete(licence);
        return ResponseEntity.ok("success");

    }

    @RequestMapping(value = "/licence/get", method = RequestMethod.GET)
    public ResponseEntity getLicences() {

        return ResponseEntity.ok(licenceRepo.findAll());

    }

    @RequestMapping(value = "/category/get", method = RequestMethod.GET)
    public ResponseEntity getCategories() {

        return ResponseEntity.ok(categoryPrototypeRepo.findAll());

    }

    @RequestMapping(value = "/category/add", method = RequestMethod.POST)
    public ResponseEntity addCategory(@RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("tag") String tag) {


        if (categoryPrototypeRepo.findOne(name) != null) {
            return ResponseEntity.badRequest().body("Categorie existe déja");
        }
        String filename = storageService.store(file, "categories");
        CategoryPrototype category = new CategoryPrototype();
        category.setName(name);
        category.setPhoto(filename);
        category.setTag(tag);
        categoryPrototypeRepo.save(category);
        return ResponseEntity.ok(category);
    }


    @RequestMapping(value = "/category/delete", method = RequestMethod.POST)
    public ResponseEntity removeCategory(@RequestBody CategoryPrototype category) {

        CategoryPrototype cat = categoryPrototypeRepo.findOne(category.getName());

        if (cat == null) {
            return ResponseEntity.badRequest().body("Categorie n'existe pas");
        }
        if (cat.getInstances().isEmpty()) {
            storageService.delete(cat.getPhoto(), "categories");

        }
        for (Category c : cat.getInstances()) {
            c.setPrototype(null);
            categoryRepo.save(c);
        }


        categoryPrototypeRepo.delete(cat);

        return ResponseEntity.ok("Categorie supprimé");
    }

    @RequestMapping(value = "/client/addLicence", method = RequestMethod.POST)
    public ResponseEntity addLicence(@RequestBody Store s) {


        Store store = storeRepo.findByRegisterDC(s.getRegisterDC());
        Date expiration = DateUtils.addMonths(store.getPayDate(), licenceRepo.findByName(store.getLicence().getName()).getLength());
        if (new Date().before(expiration)) {
            store.setPayDate(expiration);

        } else {
            store.setPayDate(new Date());
        }
        store.setLicence(licenceRepo.findByName(s.getLicence().getName()));
        storeRepo.save(store);

        return ResponseEntity.ok(store);


    }

    @RequestMapping(value = "/request/list", method = RequestMethod.GET)
    public ResponseEntity getPackageRequests() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, -14);

        packageRequestRepo.delete(packageRequestRepo.findAllByDateBefore(now.getTime()));
        return ResponseEntity.ok(packageRequestRepo.findAll());


    }

    @RequestMapping(value = "/store/type/get", method = RequestMethod.GET)
    public ResponseEntity getStoreTypes() {

        return ResponseEntity.ok(storeTypeRepo.findAll());


    }

    @RequestMapping(value = "/store/type/add", method = RequestMethod.POST)
    public ResponseEntity addStoreType( @RequestBody StoreType type) {

        if (storeTypeRepo.findOne(type.getName()) != null)
            return ResponseEntity.badRequest().body("Type Already Exists");

        storeTypeRepo.save(type);
        return ResponseEntity.ok(type);


    }
    @RequestMapping(value = "/store/type/remove", method = RequestMethod.POST)
    public ResponseEntity removeStoreType( @RequestBody StoreType type) {
        StoreType st = storeTypeRepo.findOne(type.getName());
        if (st == null)
            return ResponseEntity.badRequest().body("Type doesn't exist");

        storeTypeRepo.delete(st);
        return ResponseEntity.ok("success");


    }

    @RequestMapping(value = "/stacktrace/list", method = RequestMethod.GET)
    public ResponseEntity getStacktraces() {

        return ResponseEntity.ok(stackTraceRepo.findAll());
    }

    @RequestMapping(value = "/stats/showNbrLicence", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStats() {
        ObjectMapper mapper = new ObjectMapper();
        Pageable topTen = new PageRequest(0,10);

        try {
            String json = mapper.writeValueAsString(storeRepo.showNbrLicence(topTen));
            List<ArrayList<Object>> l = new ArrayList<ArrayList<Object>>();
            l =   mapper.readValue(json,l.getClass());
            List<Statistique> stat = new ArrayList<>();
            convertObjectToJson(l,stat);
            return ResponseEntity.ok(stat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("0");

    }

    @RequestMapping(value = "/stats/numberOfStores", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity showNbrOf() {
        return ResponseEntity.ok(storeRepo.count());
    }


    @RequestMapping(value = "/stats/showNbrOfStoresByType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity showNbrOfStoresByType() {
        Pageable topTen = new PageRequest(0,10);
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(storeRepo.showNbrOfStoresByType(topTen));
            List<ArrayList<Object>> l = new ArrayList<ArrayList<Object>>();
            l =   mapper.readValue(json,l.getClass());
            List<Statistique> stat = new ArrayList<>();
            convertObjectToJson(l,stat);
            return ResponseEntity.ok(stat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(storeRepo.showNbrOfStoresByType(topTen));
    }

    @RequestMapping(value = "/stats/infos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity newUser() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar3 = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar2.add(Calendar.MONTH, -1);
        calendar3.add(Calendar.YEAR, -1);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("package", packageRequestRepo.countAllByDateAfter(calendar.getTime()));
        objectNode.put("LicenceEnded",
                storeRepo.countAllByPayDateLessThanAndLicence_IdEqualsAndManagerEnabled(calendar2.getTime(), 2, true)
        + storeRepo.countAllByPayDateLessThanAndLicence_IdEqualsAndManagerEnabled(calendar3.getTime(), 3, true));

        objectNode.put("bug",stackTraceRepo.countAllByDateAfter(calendar.getTime()));
        try {
            return ResponseEntity.ok(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("");
        }


    }



    private void convertObjectToJson( List<ArrayList<Object>> l, List<Statistique> stat)
    {
        for (ArrayList al :l) {
            Statistique s = new Statistique();
            s.setX((String) al.get(0));
            s.setY((Integer)al.get(1));
            stat.add(s);
        }
    }
}
