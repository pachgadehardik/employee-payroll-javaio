package com.capg.javaio.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.DOMConfiguration;

import com.capg.javaio.enums.AggregateFunctions;
import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.model.Department;
import com.capg.javaio.model.EmployeePayrollData;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList = null;
	EmployeePayrollDBService employeePayrollDBService;
	private static final Logger logger = LogManager.getLogger(EmployeePayrollService.class);
	
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
		if(ioService.equals(IOService.DB_IO))
			return employeePayrollList.size();
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
	public void addEmployeeToPayrollTable(String name, String gender ,double salary, LocalDate startDate, List<Department> deptList) throws SQLException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayrollTable(name,salary,startDate,gender,deptList));
	}
	
	/**
	 * @param name
	 * @param gender
	 * @param salary
	 * @param startDate
	 * @throws SQLException
	 * only for Multithreading understanding purpose
	 */
	public void addEmployeeToPayrollTableUC7(String name, String gender ,double salary, LocalDate startDate) throws SQLException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayrollTableUC7(name,salary,startDate,gender));
	}
	public void removeEmployeeFromPayroll(int id) throws CustomMySqlException {
		employeePayrollDBService.updatePayrollData(id,employeePayrollList);
	}
	
	public void addEmployeeToPayrollTable(List<EmployeePayrollData> asList) {
		asList.forEach(employeePayrollData -> {
			System.out.println("Employee BEing Added : " + employeePayrollData.getName());
			try {
				this.addEmployeeToPayrollTableUC7(employeePayrollData.getName(), employeePayrollData.getGender(),
						employeePayrollData.getSalary(), employeePayrollData.getStartDate());
				System.out.println("Employee Added: " + employeePayrollData.getName());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
		System.out.println(this.employeePayrollList);
	}

	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
		Map<Integer,Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		employeePayrollDataList.forEach(employeePayrollData ->{
			Runnable task = () -> {
				employeeAdditionStatus.put(employeePayrollData.hashCode(),false);
				System.out.println("Employee Being Added: "+Thread.currentThread().getName());
				try {
					this.addEmployeeToPayrollTable(employeePayrollData.getName(), employeePayrollData.getGender(),
							employeePayrollData.getSalary(), employeePayrollData.getStartDate(),employeePayrollData.getDepartments());
					employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
					System.out.println("Employee Added : "+Thread.currentThread().getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			};
			Thread thread = new Thread(task,employeePayrollData.getName());
			thread.start();
			
		});
		while(employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(this.employeePayrollList);  
	}
	
	public void updateEmployeeSalaries(List<Object[]> listObj) {
		DOMConfigurator.configure("log4j.xml");
		Map<Integer,Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		System.out.println("Employee PAyrol SIze: "+employeePayrollList.size());
		listObj.forEach(obj ->{
			Runnable task = () ->{
				try {
					employeeAdditionStatus.put(obj.hashCode(),false);
					logger.info("INSIDE UPDATE---");
					logger.info("Curretn Thread is :"+Thread.currentThread().getName());
					employeePayrollDBService.getInstance().updateEmployeeSalary((String)obj[0],(Double) obj[1],employeePayrollList);
					employeeAdditionStatus.put(obj.hashCode(),true);
					logger.info("Thread Completed--"+Thread.currentThread().getName());
				} catch (Exception e) {
					e.printStackTrace();
					logger.info(e.getMessage());
				}
			};
			Thread thread = new Thread(task,(String)obj[0]);
			logger.info("Thread NAme UPDATE---"+thread.getName());
			thread.start();
		});
		
		while(employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(employeePayrollList);
	}
	
	public EmployeePayrollData filterData(String name) {
	 
		return employeePayrollList.stream().filter(obj -> name.equals(obj.getName())).findFirst().orElse(null);
		
	}
	
	
	
}
