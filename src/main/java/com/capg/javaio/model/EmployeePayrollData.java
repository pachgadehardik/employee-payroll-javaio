package com.capg.javaio.model;

import java.time.LocalDate;
import java.util.List;

public class EmployeePayrollData {

	private int id;
	private String name;
	private Double salary;
	private LocalDate startDate;
	private String gender;
	private String companyName;
	private List<Department> departments;
	public EmployeePayrollData(int id, String name, Double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(int id, String name, Double salary, LocalDate startDate) {
		this(id,name,salary);
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(int id, String name, Double salary, LocalDate startDate, String gender) {
		this(id,name,salary,startDate);
		this.gender = gender;
	}
	
	public EmployeePayrollData(int id,String name, Double salary, LocalDate startDate, String gender, String companyName, List<Department> departments) {
		this(id,name,salary,startDate,gender);
		this.companyName = companyName;
		this.departments = departments;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (salary == null) {
			if (other.salary != null)
				return false;
		} else if (!salary.equals(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
	
	
	

}
