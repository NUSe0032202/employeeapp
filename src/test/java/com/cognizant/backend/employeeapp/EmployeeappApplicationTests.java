package com.cognizant.backend.employeeapp;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.util.ReflectionTestUtils;

import com.cognizant.backend.employeeapp.modal.Employee;
import com.cognizant.backend.employeeapp.service.StoreRetrieveService;

class EmployeeappApplicationTests {

	DataSource dataSource;

	StoreRetrieveService service;

	JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void setup() {
		dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).generateUniqueName(true)
				.addScript("classpath:schema.sql").build();
		service = new StoreRetrieveService();
		jdbcTemplate = new JdbcTemplate(dataSource);
		ReflectionTestUtils.setField(service, "jdbcTemplate", jdbcTemplate);
	}

	@Test
	public void test_SuccessfulInsert() {
		Employee emp1 = new Employee("e001", "peter", "guest123", 100.0);
		Employee emp2 = new Employee("e002", "jane", "qwerty", 71263.0);

		// Get test data into String array form
		String[] input1 = { emp1.getId(), emp1.getLogin(), emp1.getName(), Double.toString(emp1.getSalary()) };
		String[] input2 = { emp2.getId(), emp2.getLogin(), emp2.getName(), Double.toString(emp2.getSalary()) };

		service.insert(input1);
		service.insert(input2);

		List<Employee> data = service.retrieveFilteredEmployees(0, 99999, 0, 2, "+ID");

		assertThat(data).isNotEmpty();
		assertThat(data)
				.extracting(emp -> emp.getId(), emp -> emp.getName(), emp -> emp.getLogin(), emp -> emp.getSalary())
				.containsExactly(tuple("e001", "peter", "guest123", 100.0), tuple("e002", "jane", "qwerty", 71263.0));
	}

	@Test
	public void test_ExistingID() {
		Employee emp1 = new Employee("e001", "peter", "guest123", 100.0);
		Employee emp2 = new Employee("e001", "peter", "qwerty", 71263.0);

		String[] input1 = { emp1.getId(), emp1.getLogin(), emp1.getName(), Double.toString(emp1.getSalary()) };
		String[] input2 = { emp2.getId(), emp2.getLogin(), emp2.getName(), Double.toString(emp2.getSalary()) };

		// Assert that an error is thrown due to same employee id
		assertThatThrownBy(() -> {
			service.insert(input1);
			service.insert(input2);
		}).isInstanceOf(DuplicateKeyException.class);

		List<Employee> data = service.retrieveFilteredEmployees(0, 99999, 0, 2, "+ID");

		assertThat(data).isNotEmpty();
		assertThat(data)
				.extracting(emp -> emp.getId(), emp -> emp.getName(), emp -> emp.getLogin(), emp -> emp.getSalary())
				.containsExactly(tuple("e001", "peter", "qwerty", 71263.0));
	}

	@Test
	public void test_ExistingLogin() {
		Employee emp1 = new Employee("e001", "peter", "guest123", 100.0);
		Employee emp2 = new Employee("e002", "jane", "guest123", 71263.0);

		String[] input1 = { emp1.getId(), emp1.getLogin(), emp1.getName(), Double.toString(emp1.getSalary()) };
		String[] input2 = { emp2.getId(), emp2.getLogin(), emp2.getName(), Double.toString(emp2.getSalary()) };

		// Assert that an error is thrown due to same login already registered in dB
		assertThatThrownBy(() -> {
			service.insert(input1);
			service.insert(input2);
		}).isInstanceOf(DuplicateKeyException.class);
		
		List<Employee> data = service.retrieveFilteredEmployees(0, 99999, 0, 2, "+ID");
		
		assertThat(data).isNotEmpty();
		assertThat(data)
		.extracting(emp -> emp.getId(), emp -> emp.getName(), emp -> emp.getLogin(), emp -> emp.getSalary())
		.containsExactly(tuple("e001", "peter", "guest123", 100.0));
	}

}
