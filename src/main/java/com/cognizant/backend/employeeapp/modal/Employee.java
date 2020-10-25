package com.cognizant.backend.employeeapp.modal;

public class Employee {

	private String id;
	private String name;
	private String login;
	private Double salary;

	public Employee() {

	}

	public Employee(String id, String name, String login, Double salary) {
		this.id = id;
		this.name = name;
		this.login = login;
		this.salary = salary;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

}
