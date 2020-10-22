package com.cognizant.backend.employeeapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cognizant.backend.employeeapp.service.StoreRetrieveService;

@RestController
@CrossOrigin("http://localhost:4200")
public class ApplicationController {

	@Autowired
	StoreRetrieveService storeRetrieveService;

	@PostMapping("/users/upload")
	public ResponseEntity<Object> uploadUsers(@RequestParam("file") MultipartFile file) {
		System.out.println("uploadUsers");
		List<String> errors = storeRetrieveService.readAndValidate(file);
		if (errors.size() > 0) {
			return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
		} else {
			return ResponseEntity.ok("Upload done!");
		}
	}

}
