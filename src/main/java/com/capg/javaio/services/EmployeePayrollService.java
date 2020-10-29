package com.capg.javaio.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.capg.javaio.enums.AggregateFunctions;
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
			this.employeePayrollList = employeePayrollDBService.readData();
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
	
	public List<EmployeePayrollData> getSalaryBasedOnDateRange(String date1, String date2) throws SQLException {
		
		List<EmployeePayrollData> employeePayrollDataList = new ArrayList<EmployeePayrollData>();
		
		LocalDate localDate1 = LocalDate.parse(date1);
		LocalDate localDate2 = LocalDate.parse(date2);
		employeePayrollDataList = employeePayrollDBService.getEmployeeRecordsByDate(localDate1,localDate2);
		
		return employeePayrollDataList;
	}
	public Double getAggregateSalaryRecords(AggregateFunctions methodType, String gender) throws SQLException {
		
		return employeePayrollDBService.getEmployeeSalaryByAggrgation(methodType, gender);
		 
	}
	public Map<String, Double> readAvergaeSalaryByGender(IOService ioService) throws SQLException {
		if(ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getAverageSalaryByGender();
		return null;
	}
	public void addEmployeeToPayrollTable(String name, double salary, LocalDate startDate, String gender) throws SQLException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayrollTable(name,salary,startDate,gender));
	}

	
}
