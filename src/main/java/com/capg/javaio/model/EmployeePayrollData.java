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

	public EmployeePayrollData() {
	}

	public EmployeePayrollData(int id, String name, Double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(int id, String name, Double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}

	public EmployeePayrollData(int id, String name, Double salary, LocalDate startDate, String gender) {
		this(id, name, salary, startDate);
		this.gender = gender;
	}

	public EmployeePayrollData(int id, String name, Double salary, LocalDate startDate, String gender,
			String companyName, List<Department> departments) {
		this(id, name, salary, startDate, gender);
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

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", salary=" + salary + ", startDate=" + startDate
				+ ", gender=" + gender + ", companyName=" + companyName + ", departments=" + departments + "]";
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
		return Integer.compare(this.id, other.id) == 0 && this.name.equals(other.name)
				&& this.gender.equals(other.gender) && Double.compare(this.salary, other.salary) == 0
				&& this.startDate.compareTo(other.startDate) == 0;

	}
}
