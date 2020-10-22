CREATE TABLE IF NOT EXISTS employees
(
   employee_id VARCHAR(20) NOT NULL primary key,
   employee_login VARCHAR(50) NOT NULL,
   employee_name VARCHAR(60) NOT NULL,
   employee_salary FLOAT(9) NOT NULL
);