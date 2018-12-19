package org.meteor.efficaisse;

import org.meteor.efficaisse.service.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@EnableConfigurationProperties(StorageProperties.class)
@SpringBootApplication
public class EfficaisseApplication {

	public static void main(String[] args) {
		SpringApplication.run(EfficaisseApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}





}
