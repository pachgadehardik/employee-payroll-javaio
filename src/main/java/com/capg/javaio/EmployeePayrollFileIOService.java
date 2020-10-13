package com.capg.javaio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class EmployeePayrollFileIOService {

	public static String PAYROLL_FILE = "payroll-file.txt";
	
	public void writeData(List<EmployeePayrollData> employeePayrollList) {
		 StringBuffer empBuffer = new StringBuffer();
		 employeePayrollList.forEach(employee -> {
			 String employeeDataString = employee.toString().concat("\n");
			 empBuffer.append(employeeDataString);
		 });
		 
		 try {
			 Files.write(Paths.get(PAYROLL_FILE),empBuffer.toString().getBytes());
		 }
		 catch(IOException x) {
			 x.printStackTrace();
		 }
	}

	public void printData() {
		try {
			Files.lines(new File(PAYROLL_FILE).toPath()).forEach(System.out::println);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long countEntries() {
		long entries = 0;
		try {
			entries = Files.lines(new File(PAYROLL_FILE).toPath()).count();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return entries;
		
	}
	
}
