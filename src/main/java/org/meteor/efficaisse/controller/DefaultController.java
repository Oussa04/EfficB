package org.meteor.efficaisse.controller;

import com.github.mkopylec.recaptcha.validation.RecaptchaValidator;
import com.github.mkopylec.recaptcha.validation.ValidationResult;
import org.meteor.efficaisse.model.PackageRequest;
import org.meteor.efficaisse.repository.PackageRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class DefaultController {

    @Autowired
    private RecaptchaValidator recaptchaValidator;
    @Autowired
    private PackageRequestRepository packageRequestRepo;


    @RequestMapping(value = "/pack/request",method = RequestMethod.POST)
    public ResponseEntity packRequest(@RequestBody PackageRequest request) {

        ValidationResult result = recaptchaValidator.validate(request.getRecaptcha());
        if(result.isSuccess()){

            request.setDate(new Date());
            packageRequestRepo.save(request);

            return ResponseEntity.ok("Commande envoyé");
        }
        return ResponseEntity.badRequest().body("Recaptcha invalide");
    }
}
