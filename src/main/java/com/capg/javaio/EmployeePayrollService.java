package com.capg.javaio;

import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

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

	public long readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO))
			this.employeePayrollList = new EmployeePayrollFileIOService().readData();
		System.out.println(employeePayrollList);
		return employeePayrollList.size();
	}

}
