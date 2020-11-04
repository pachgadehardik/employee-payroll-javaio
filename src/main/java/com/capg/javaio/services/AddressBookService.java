package com.capg.javaio.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.exceptions.CustomMySqlException.ExceptionType;
import com.capg.javaio.model.ContactData;
import com.capg.javaio.model.Email;
import com.capg.javaio.model.EmployeePayrollData;
import com.capg.javaio.model.Phone;
import com.capg.javaio.services.EmployeePayrollService.IOService;

public class AddressBookService {

	private List<ContactData> contactDataList = null;
	AddressBookDBService addressBookDBService;
	private static final Logger logger = LogManager.getLogger(AddressBookService.class);

	public List<ContactData> readContactData() throws SQLException {
		this.contactDataList = addressBookDBService.readData();
		return contactDataList;
	}

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public AddressBookService(List<ContactData> contactDataList) {
		this();
		this.contactDataList = new ArrayList<>(contactDataList);
	}

	public int getCount() {
		return contactDataList.size();
	}

	public void addContactToTable(String firstName, String lastName, String address, String city, String state,
			String zip, List<Phone> phoneList, List<Email> emailList) throws SQLException {

		contactDataList.add(addressBookDBService.addContactToTables(firstName, lastName, address, city, state, zip,
				phoneList, emailList));
	}

	public void addContactToTable(String firstName, String lastName, String address, String city, String state,
			String zip) throws SQLException {
		contactDataList.add(
				addressBookDBService.addContactToTables(firstName, lastName, address, city, state, zip, null, null));
		logger.info("ContactDataList is :"+contactDataList);
	}

	public boolean checkContactInSyncWithDB(String name) throws CustomMySqlException {
		List<ContactData> contactDataList = addressBookDBService.getInstance().getContactDataInList(name);
		return contactDataList.get(0).equals(getContactData(name));
	}

	public ContactData getContactData(String name) {
		ContactData temp = this.contactDataList.stream()
				.filter(ContactDataItem -> ContactDataItem.getFirstName().equals(name)).findFirst().orElse(null);
		return temp;
	}

	public void updateContactData(String firstName, String lastName, IOService ioService) throws CustomMySqlException {
		ContactData contactData = this.getContactData(firstName);
		if(contactData == null) throw new CustomMySqlException("Contact Doesnt Exist", ExceptionType.NO_DATA_FOUND);
		else {
			if (ioService.equals(IOService.DB_IO)) {
				int result = addressBookDBService.getInstance().updateContactDataUsingPreparedStatement(firstName,
						lastName);
				if (result == 0)
					return;
			}
			contactData.setLastName(lastName);
		}
	}
	
	public void updateContactObject(ContactData contactObj, IOService ioService) throws SQLException {
		ContactData contactData = this.getContactData(contactObj.getFirstName());
		if(contactData == null) throw new CustomMySqlException("Contact Doesnt Exist", ExceptionType.NO_DATA_FOUND);
		else {
			if (ioService.equals(IOService.DB_IO)) {
				boolean result = addressBookDBService.getInstance().updateContactDataPhoneNumber(contactObj,contactData.getId());
				if (result) {
					contactData = contactObj;
				}
			}
		}
	}

	public int getRecordsByDates(LocalDate date1, LocalDate date2) {
		List<ContactData> contactList = addressBookDBService.getInstance().getRecordsByDate(date1, date2);
		return contactList.size();
	}

	public int getCountByStates(String state) {
		int count = addressBookDBService.getInstance().getCountByStates(state);
		return count;
	}

	public void addContactToDBWithThreads(List<ContactData> asList) {
		DOMConfigurator.configure("log4j.xml");
		Map<Integer, Boolean> contactAdditionStatus = new HashMap<Integer, Boolean>();
		asList.forEach(contactData -> {
			Runnable task = () -> {
				contactAdditionStatus.put(contactData.hashCode(), false);
				logger.info("Contact Being Added: " + Thread.currentThread().getName());
				try {
					logger.info("INSIDE TRY");
					this.addContactToTable(contactData.getFirstName(), contactData.getLastName(),
							contactData.getAddress(), contactData.getCity(), contactData.getState(),
							contactData.getZip());
					contactAdditionStatus.put(contactData.hashCode(), true);
					System.out.println("Employee Added : " + Thread.currentThread().getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			};
			Thread thread = new Thread(task, contactData.getFirstName());
			thread.start();

		});
		while (contactAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int countEntries(IOService restIo) {
		return contactDataList.size();
	}

//	public int countEntries() {
//		return contactDataList.size();
//	}

	public void addContactToDB(ContactData contactData, IOService ioService) throws SQLException {
		if (ioService.equals(IOService.DB_IO))
			this.addContactToTable(contactData.getFirstName(), contactData.getLastName(), contactData.getAddress(),
					contactData.getCity(), contactData.getState(), contactData.getZip(), contactData.getPhoneList(),
					contactData.getEmailList());
		else
			contactDataList.add(contactData);
	}

	public void deleteFromCache(String firstName, IOService ioService) {
		if(ioService.equals(IOService.REST_IO)) {
			ContactData contactData = this.getContactData(firstName);
			contactDataList.remove(contactData);
		}
	}

}
