package com.capg.javaio.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.capg.javaio.enums.AggregateFunctions;
import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.exceptions.CustomMySqlException.ExceptionType;
import com.capg.javaio.model.EmployeePayrollData;

public class EmployeePayrollDBService {

//	static List<EmployeePayrollData> employeePayrollList;
	private static EmployeePayrollDBService employeePayrollDBService;
	private static PreparedStatement employeePayrollDataStatement;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private static Connection getConnection() {
		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";

		// Database credentials
		final String USER = "root";
		final String PASS = "hardik@#123";
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Driver Loaded!");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cant find another classpath!", e);
		}
		try {
			System.out.println("Conecting to dbs : " + DB_URL);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connection is Successfull" + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static List<EmployeePayrollData> readData() throws SQLException {
		String sql = "Select * from temp_payroll_table";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		Connection conn = getConnection();
		Statement statement = conn.createStatement();
		ResultSet result = statement.executeQuery(sql);
		employeePayrollList = getEmployeePayrollData(result);
		conn.close();
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeePayrollDataInList(String name) throws CustomMySqlException {
		List<EmployeePayrollData> employeePayrollDataList = null;
		if (this.employeePayrollDataStatement == null)
			this.preparedStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollDataList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.OTHER_TYPE);
		}
		return employeePayrollDataList;
	}

	private void preparedStatementForEmployeeData() throws CustomMySqlException {
		try {
			Connection conn = this.getConnection();
			String sql = "Select * from temp_payroll_table where name = ?";
			employeePayrollDataStatement = conn.prepareStatement(sql);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.OTHER_TYPE);
		}
	}

	private static List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeeList = new ArrayList<EmployeePayrollData>();
		try {
			while (result.next()) {
				int id = result.getInt("emp_id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start_date").toLocalDate();
				employeeList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return employeeList;
	}

	public int updateEmployeeData(String name, double salary, int flag) throws CustomMySqlException {
		if (flag == 0)
			return this.updateEmployeeDataUsingSQLQuery(name, salary);
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);

	}

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		int result = 0;
		try (Connection conn = this.getConnection()) {
			String sql = "update temp_payroll_table set salary =? where name = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, salary);
			stmt.setString(2, name);
			result = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private int updateEmployeeDataUsingSQLQuery(String name, double salary) throws CustomMySqlException {
		try (Connection conn = getConnection()) {
			String sql = String.format("update temp_payroll_table set salary = %.2f where name = '%s';", salary, name);
			Statement stmt = conn.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.NO_DATA_FOUND);
		}
	}

	public List<EmployeePayrollData> getEmployeeRecordsByDate(LocalDate date1, LocalDate date2) throws SQLException {
		try(Connection conn = getConnection()){
			String sql = "select * from temp_payroll_table where start_date between ? and ?";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setDate(1, java.sql.Date.valueOf(date1));
			pStmt.setDate(2, java.sql.Date.valueOf(date2));
			ResultSet result = pStmt.executeQuery();
			List<EmployeePayrollData> resultlist = getEmployeePayrollData(result);
			System.out.println("Employee records after Query: "+resultlist.toString());
			return resultlist;
		}catch(SQLException e) {
			throw new CustomMySqlException(e.getMessage(),ExceptionType.OTHER_TYPE);
		}
	}

	public double getEmployeeSalaryByAggrgation(AggregateFunctions methodType, String gender) throws SQLException {
		double agg = 0;
		try(Connection conn = getConnection()){
			PreparedStatement pStmt = conn.prepareStatement(methodType.getQuery());
			pStmt.setString(1, gender);
			ResultSet result = pStmt.executeQuery();
			while(result.next()) {
				 agg = result.getDouble(1);
			}
			return agg;
		}
	}
	
	

}
