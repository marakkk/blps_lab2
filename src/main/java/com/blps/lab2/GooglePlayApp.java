package com.blps.lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class GooglePlayApp {
	public static void main(String[] args) {
		SpringApplication.run(GooglePlayApp.class, args);
	}
}

