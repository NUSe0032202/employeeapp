package com.cognizant.backend.employeeapp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cognizant.backend.employeeapp.modal.Employee;
import com.cognizant.backend.employeeapp.service.StoreRetrieveService;

@RestController
@CrossOrigin("http://localhost:4200")
public class ApplicationController {

	@Autowired
	StoreRetrieveService storeRetrieveService;

	@PostMapping("/users/upload")
	public synchronized ResponseEntity<Object> uploadUsers(@RequestParam("file") MultipartFile file) {
		List<String> errors = storeRetrieveService.readAndValidate(file);
		if (errors.size() > 0) {
			return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
		} else {
			ArrayList<String> parseSuccess = new ArrayList<String>();
			parseSuccess.add("Successful File Upload !");
			return new ResponseEntity<Object>(parseSuccess, HttpStatus.OK);
		}
	}

	@GetMapping("/users")
	public  /*ResponseEntity<List<Employee>>*/ResponseEntity <Map<String, List<Employee>>> retrieveUsers(@RequestParam("minSalary") int minSalary, @RequestParam("maxSalary") int maxSalary,
			@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("sort") String sort) throws UnsupportedEncodingException {
		// List<Employee> data = storeRetrieveService.retrieveFilteredEmployees();
		// return new ResponseEntity<List<Employee>>(data,HttpStatus.OK);
		System.out.println(minSalary);
		System.out.println(maxSalary);
		System.out.println(offset);
		System.out.println(limit);
		String sortParam = sort.replace("%2B", "+");
		System.out.println(sortParam);
		Map<String, List<Employee>> map = new HashMap<>();
		List<Employee> data = storeRetrieveService.retrieveFilteredEmployees(minSalary,maxSalary,offset,limit,sortParam);
		map.put("results",data);
		return new ResponseEntity <Map<String, List<Employee>>>(map,HttpStatus.OK);
		//return new ResponseEntity<List<Employee>>(data,HttpStatus.OK);
	}

}
