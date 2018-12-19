/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.meteor.efficaisse.controller;


import org.meteor.efficaisse.model.User;
import org.meteor.efficaisse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/confirm",method = RequestMethod.GET)
    public ResponseEntity confirm(@RequestParam("token")String code) {
        User u = userRepository.findByActivationCode(code);
        if (u == null) {
            return ResponseEntity.badRequest().body("Token non valide");
        }
        if (u.isEnabled())
            return ResponseEntity.badRequest().body("Utilisateur déja activé");
        u.setEnabled(true);
        userRepository.save(u);
        return ResponseEntity.ok("Utilisateur " + u.getUsername() + " est activé avec succée");


    }

}
