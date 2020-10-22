package com.cognizant.backend.employeeapp;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cognizant.backend.employeeapp.service.StoreRetrieveService;

@SpringBootApplication
public class EmployeeappApplication implements CommandLineRunner {
	@Resource
	StoreRetrieveService storeRetrieveService;

	public static void main(String[] args) {
		SpringApplication.run(EmployeeappApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		storeRetrieveService.init();
	}

}
