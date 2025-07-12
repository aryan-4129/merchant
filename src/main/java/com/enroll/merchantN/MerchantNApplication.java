package com.enroll.merchantN;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableAsync
public class MerchantNApplication {

	public static void main(String[] args) {
		SpringApplication.run(MerchantNApplication.class, args);
	}

}
