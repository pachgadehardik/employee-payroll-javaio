package com.capg.javaio.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.model.EmployeePayrollData;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList = null;
	EmployeePayrollDBService employeePayrollDBService;
	
	
	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}


	public void readEmployeePayrollDataFromConsole(Scanner consoleInputReader) {
		System.out.println("Employee Id: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Employee Name: ");
		String name = consoleInputReader.next();
		System.out.println("Employee Salary: ");
		Double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	public void writeEmployeePayrollData(IOService serviceType) {
		if (serviceType == IOService.CONSOLE_IO)
			System.out.println("Employee Payroll Roaster\n" + employeePayrollList);
		else if (serviceType == IOService.FILE_IO) {
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
		}
	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().printData();
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return 0;
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) throws SQLException {
		if (ioService.equals(IOService.FILE_IO))
			this.employeePayrollList = new EmployeePayrollFileIOService().readData();
		if (ioService.equals(IOService.DB_IO)) {
//			EmployeePayrollDBService employeePayrollDBService = (EmployeePayrollDBService) new EmployeePayrollDBService()
//					.getInstance();
			this.employeePayrollList = EmployeePayrollDBService.readData();
		}
		System.out.println(employeePayrollList);
		return employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary, int i) throws CustomMySqlException {
		int result = employeePayrollDBService.getInstance().updateEmployeeData(name, salary, i);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.setSalary(salary);
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(EmployeePayrollDataItem -> EmployeePayrollDataItem.getName().equals(name)).findFirst()
				.orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws CustomMySqlException {
		List<EmployeePayrollData> employeePayrollDataList =  employeePayrollDBService.getInstance().getEmployeePayrollDataInList (name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

}
