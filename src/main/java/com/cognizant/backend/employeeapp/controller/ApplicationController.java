package com.cognizant.backend.employeeapp.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cognizant.backend.employeeapp.modal.Employee;
import com.cognizant.backend.employeeapp.service.StoreRetrieveService;

@RestController
@CrossOrigin("*")
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
	public ResponseEntity<Map<String, List<Employee>>> retrieveUsers(
			@RequestParam("minSalary") int minSalary, @RequestParam("maxSalary") int maxSalary,
			@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("sort") String sort)
			throws UnsupportedEncodingException {
		
		String sortParam = sort.replace("%2B", "+");
		Map<String, List<Employee>> map = new HashMap<>();
		List<Employee> data = storeRetrieveService.retrieveFilteredEmployees(minSalary, maxSalary, offset, limit,
				sortParam);
		map.put("results", data);
		return new ResponseEntity<Map<String, List<Employee>>>(map, HttpStatus.OK);
		
	}
	
	@DeleteMapping("/users/{id}")
	public /*ResponseEntity<Object>*/void deleteUser(@PathVariable String id) {
		//System.out.println("Delete user" + id);
		storeRetrieveService.delete(id);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Object> handleMissingParams(MissingServletRequestParameterException e) {
		String name = e.getParameterName();
		return new ResponseEntity<Object>("Missing query params " + name, HttpStatus.BAD_REQUEST);
	}

}
