package com.capg.javaio.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.exceptions.CustomMySqlException.ExceptionType;
import com.capg.javaio.model.ContactData;
import com.capg.javaio.model.Email;
import com.capg.javaio.model.Phone;

public class AddressBookDBService {

	static AddressBookDBService addressBookDBService;
	private PreparedStatement contactDataStatement;
	private static final org.apache.log4j.Logger logger = LogManager.getLogger(AddressBookDBService.class);
	
	private AddressBookDBService() {
	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	private static Connection getConnection() {
		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://localhost:3306/addressbookservice?useSSL=false";

		// Database credentials
		final String USER = "root";
		final String PASS = "hardik@#123";
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cant find another classpath!", e);
		}
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static List<ContactData> readData() throws SQLException {
		String sql = "Select * from contact_table";
		List<ContactData> contactList = new ArrayList<ContactData>();
		Connection conn = getConnection();
		Statement statement = conn.createStatement();
		ResultSet result = statement.executeQuery(sql);
		contactList = getContactData(result);
		conn.close();
		return contactList;
	}

	private static List<ContactData> getContactData(ResultSet result) {
		List<ContactData> contactList = new ArrayList<>();
		try {
			while (result.next()) {
				int id = result.getInt("contact_id");
				String firstName = result.getString("firstname");
				String lastName = result.getString("lastname");
				String address = result.getString("address");
				String state = result.getString("state");
				String city = result.getString("city");
				String zip = result.getString("zip");
				contactList.add(new ContactData(id, firstName, lastName, address, city, state, zip));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return contactList;
	}

	public List<ContactData> getContactDataInList(String name) throws CustomMySqlException {
		List<ContactData> contactDataList = null;
		if (this.contactDataStatement == null)
			this.preparedStatementForContactData();
		try {
			contactDataStatement.setString(1, name);
			ResultSet resultSet = contactDataStatement.executeQuery();
			contactDataList = this.getContactData(resultSet);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.OTHER_TYPE);
		}
		return contactDataList;
	}

	private void preparedStatementForContactData() throws CustomMySqlException {
		try {
			Connection conn = this.getConnection();
			String sql = "Select * from contact_table where firstname = ?";
			contactDataStatement = conn.prepareStatement(sql);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.OTHER_TYPE);
		}
	}

	public ContactData addContactToTables(String firstName, String lastName, String address, String city, String state,
			String zip, List<Phone> phoneList, List<Email> emailList) throws SQLException {

		int contact_id = -1;
		ContactData contactData = null;
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.OTHER_TYPE);
		}

		try (Statement statement = conn.createStatement()) {
			DOMConfigurator.configure("log4j.xml");
			String sql = String.format(
					"INSERT INTO contact_table (firstname,lastname,address,state,city,zip)"
							+ "Values ('%s', '%s', '%s', '%s', '%s', '%s' );",
					firstName, lastName, address, state, city, zip);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				logger.info("1st Query Executed");
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					contact_id = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			conn.rollback();
			logger.info("GOCHI!!!!!!!!!!!!!!!!!");
			throw new CustomMySqlException(e.getMessage(), ExceptionType.OTHER_TYPE);
		}
		
		if(phoneList != null && emailList != null) {
		try (Statement stmt = conn.createStatement()) {

			for (int i = 0; i < phoneList.size(); i++) {
				String sql = String.format("INSERT INTO phone (contact_id, number) values ('%s','%s')", contact_id,
						phoneList.get(i).getNumber());
				int rowAffected = stmt.executeUpdate(sql);
				if (rowAffected != 1)
					conn.rollback();
			}
		} catch (SQLException e) {
			conn.rollback();
			throw new CustomMySqlException(e.getMessage(), ExceptionType.OTHER_TYPE);
		}

		try (Statement stmt = conn.createStatement()) {

			for (int i = 0; i < emailList.size(); i++) {

				String sql = String.format("INSERT INTO email (contact_id, email_string) values ('%s','%s')",
						contact_id, emailList.get(i).getEmail());
				int rowAffected = stmt.executeUpdate(sql);
				if (rowAffected != 1)
					conn.rollback();
			}
			contactData = new ContactData(contact_id, firstName, lastName, address, city, state, zip, emailList,
					phoneList);
			return contactData;
		} catch (SQLException e) {
			conn.rollback();
			e.printStackTrace();
		}
		}
//		finally {
			if (conn != null) {
				try {
					conn.commit();
					conn.close();
					logger.info("COnnection Comimmitted");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
//		}

		return null;

	}

	public int updateContactDataUsingPreparedStatement(String firstName, String lastName) {
		int result = 0;
		try (Connection conn = this.getConnection()) {
			String sql = "update contact_table set lastname =? where firstname = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, lastName);
			stmt.setString(2, firstName);
			result = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<ContactData> getRecordsByDate(LocalDate date1, LocalDate date2) {
		List<ContactData> listContact = null;
		try (Connection conn = this.getConnection()) {
			String sql = "select * from contact_table where date_added between ? and ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setDate(1, java.sql.Date.valueOf(date1));
			stmt.setDate(2, java.sql.Date.valueOf(date2));
			ResultSet resultSet = stmt.executeQuery();
			listContact = getContactData(resultSet);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listContact;
	}

	public int getCountByStates(String state) {
		int numberRow = 0;
		try (Connection conn = this.getConnection()) {
			String sql = "select count(*) from contact_table where state = ? group by state";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, state);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				numberRow = resultSet.getInt("count(*)");
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numberRow;
	}

}
