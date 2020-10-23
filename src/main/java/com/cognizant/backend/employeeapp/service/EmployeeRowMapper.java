package com.cognizant.backend.employeeapp.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.cognizant.backend.employeeapp.modal.Employee;

public class EmployeeRowMapper implements RowMapper<Employee> {

	@Override
	public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
		Employee emp = new Employee();
		emp.setEmployeeID(rs.getString("employee_id"));
		emp.setEmployeeLogin(rs.getString("employee_login"));
		emp.setEmployeeName(rs.getString("employee_name"));
		emp.setEmployeeSalary(rs.getDouble("employee_salary"));
		return emp;
	}
}
