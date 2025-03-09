package com.example.banking;

import org.springframework.boot.SpringApplication;

public class TestBankingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(BankingBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
