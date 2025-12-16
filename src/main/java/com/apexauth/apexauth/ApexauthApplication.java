package com.apexauth.apexauth;

import com.apexauth.apexauth.config.DotEnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApexauthApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ApexauthApplication.class);
		app.addInitializers(new DotEnvConfig());
		app.run(args);
	}

}
