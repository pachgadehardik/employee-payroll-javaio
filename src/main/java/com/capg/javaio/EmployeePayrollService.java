package com.capg.javaio;

import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {

	private List<EmployeePayrollData> employeePayrollList;

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}
	
	public void readEmployeePayrollDataFromConsole(Scanner consoleInputReader) {
		System.out.println("Employee Id: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Employee Name: ");
		String name = consoleInputReader.next();
		System.out.println("Employee Salary: ");
		int salary = consoleInputReader.nextInt();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}
	
	public void writeEmployeePayrollData() {
		System.out.println("Employee Payroll Roaster\n"+employeePayrollList);
	}
	
}
