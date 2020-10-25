package com.cognizant.backend.employeeapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cognizant.backend.employeeapp.modal.Employee;

@Service
public class StoreRetrieveService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	ArrayList<String> errorMsgs = new ArrayList<String>();

	private final Path root = Paths.get("uploads");

	public void init() {
		try {
			Files.createDirectories(root);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	public List<String> readAndValidate(MultipartFile file) {
		Path savedFilePath;
        errorMsgs.clear();
		try {
			
			if (file.getSize() == 0) {
				errorMsgs.add("File is empty");
				return errorMsgs;
			}

			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()),
					StandardCopyOption.REPLACE_EXISTING);
			savedFilePath = this.root.resolve(file.getOriginalFilename());

			BufferedReader br = Files.newBufferedReader(savedFilePath);
			String line;
			while ((line = br.readLine()) != null) {

				
				if (line.charAt(0) == '#' || line.contains("login")) {
					continue;
				}
				String[] columns = line.split(",");
				if (columns.length != 4) {
					errorMsgs.add("Please make sure there are only 4 columns per row");
					return errorMsgs;
				}

				try {
					Float.parseFloat(columns[3]);
				} catch (NumberFormatException e) {
					errorMsgs.add("Unable to parse salary, make sure it is formatted correctly");
					return errorMsgs;
				}

				if (Float.parseFloat(columns[3]) < 0) {
					errorMsgs.add("An entered salary has been detected to be negative");
					return errorMsgs;
				}
			}
			// Insert into dB only after checks have been completed
			br = Files.newBufferedReader(savedFilePath);
			boolean duplicateExceptionFlag = false;
			while ((line = br.readLine()) != null) {
				if (line.charAt(0) == '#' || line.contains("login")) {
					continue;
				}
				String[] columns = line.split(",");
				try {
				   this.insert(columns);
				} catch (DuplicateKeyException exception) {
					duplicateExceptionFlag = true;
				}
			}
			
			if(duplicateExceptionFlag) {
				errorMsgs.add("Duplicate ID/Login detected");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return errorMsgs;
	}

	public List<Employee> retrieveFilteredEmployees(int minSalary, int maxSalary, int offset, int limit,
			String sortBy) {

		String queryStatement;
		String orderBy = sortBy.substring(1);
		String column = null;

		switch (orderBy) {

		case "ID":
			column = "employee_id";
			break;
		case "Name":
			column = "employee_name";
			break;
		case "Login":
			column = "employee_login";
			break;
		case "Salary":
			column = "employee_salary";
			break;
		}

		if (sortBy.charAt(0) == '+') {

			queryStatement = "SELECT * FROM employees WHERE employee_salary >= ? AND employee_salary <= ?"
					+ " ORDER BY " + column + " LIMIT ? OFFSET ?";
		} else {
			queryStatement = "SELECT * FROM employees WHERE employee_salary >= ? AND employee_salary <= ?"
					+ " ORDER BY " + column + " DESC LIMIT ? OFFSET ?";
		}

		List<Employee> data = jdbcTemplate.query(queryStatement, new Object[] { minSalary, maxSalary, limit, offset },
				new EmployeeRowMapper());

		return data;
	}

	public void insert(String[] entry) {
		try {
			final String insertStatement = "INSERT INTO employees (employee_id,employee_login,employee_name"
					+ ",employee_salary) VALUES(?,?,?,?)";
			jdbcTemplate.update(insertStatement,
					new Object[] { entry[0], entry[1], entry[2], Float.parseFloat(entry[3]) });
		} catch (DuplicateKeyException e) {
			final String overwriteStatement = "UPDATE employees SET employee_login = ?, employee_name = ?, "
					+ "employee_salary = ? WHERE employee_id = ?";
			jdbcTemplate.update(overwriteStatement, new Object[] { entry[1], entry[2], entry[3], entry[0] });
			throw new DuplicateKeyException("Dupicate Key Exception");
		}
	}
	public void delete(String id) {
		final String deleteStatement ="DELETE from employees WHERE employee_id = ?";
		jdbcTemplate.update(deleteStatement, new Object[] {id});
	}

}
