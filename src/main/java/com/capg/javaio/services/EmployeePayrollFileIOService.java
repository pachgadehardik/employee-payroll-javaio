package com.capg.javaio.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.capg.javaio.model.EmployeePayrollData;

public class EmployeePayrollFileIOService {

	public static String PAYROLL_FILE = "payroll-file.txt";

	public void writeData(List<EmployeePayrollData> employeePayrollList) {
		StringBuffer empBuffer = new StringBuffer();
		employeePayrollList.forEach(employee -> {
			String employeeDataString = employee.toString().concat("\n");
			empBuffer.append(employeeDataString);
		});

		try {
			Files.write(Paths.get(PAYROLL_FILE), empBuffer.toString().getBytes());
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	public void printData() {
		try {
			Files.lines(new File(PAYROLL_FILE).toPath()).forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long countEntries() {
		long entries = 0;
		try {
			entries = Files.lines(new File(PAYROLL_FILE).toPath()).count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;

	}

	public List<EmployeePayrollData> readData() {
		List<EmployeePayrollData> employeePayrollDataList = new ArrayList<EmployeePayrollData>();
		try {
			Files.lines(new File(PAYROLL_FILE).toPath()).map(line -> line.trim())
					.forEach(line -> parseData(employeePayrollDataList, line));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return employeePayrollDataList;
	}

	public void parseData(List<EmployeePayrollData> employeePayrollDataList, String line) {

		String arr[] = line.split(",");

		int id = Integer.valueOf(arr[0].split("=")[1]);
		String name = arr[1].split("=")[1];
		Double salary = Double.valueOf(arr[2].split("=")[1]);
		EmployeePayrollData employeePayrollData = new EmployeePayrollData(id, name, salary);
		employeePayrollDataList.add(employeePayrollData);
	}

}
