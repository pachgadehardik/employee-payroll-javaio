package com.capg.javaio.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.model.ContactData;
import com.capg.javaio.model.Email;
import com.capg.javaio.model.Phone;



public class AddressBookService {

	private List<ContactData> contactDataList = null;
	AddressBookDBService addressBookDBService;
	
	public List<ContactData> readContactData() throws SQLException {
		this.contactDataList = addressBookDBService.readData();
		return contactDataList;
	}
	
	public AddressBookService()  {
		addressBookDBService = AddressBookDBService.getInstance();
	}
	
	public int getCount() {
		return contactDataList.size();
	}

	public void addContactToTable(String firstName, String lastName, String address, String city, String state,
			String zip, List<Phone> phoneList, List<Email> emailList) throws SQLException {
		
		contactDataList.add(addressBookDBService.addContactToTables(firstName,lastName,address,city,state,zip,phoneList,emailList));
	}

	public boolean checkContactInSyncWithDB(String name) throws CustomMySqlException {
		List<ContactData> contactDataList =  addressBookDBService.getInstance().getContactDataInList(name);
			return contactDataList.get(0).equals(getContactData(name));
	}	

	private ContactData getContactData(String name) {
		ContactData temp = this.contactDataList.stream()
				.filter(ContactDataItem -> ContactDataItem.getFirstName().equals(name)).findFirst()
				.orElse(null);
		return temp;
	}

	public void updateContactData(String firstName, String lastName) {
		int result = addressBookDBService.getInstance().updateContactDataUsingPreparedStatement(firstName,lastName);
		if (result == 0)
			return;
		ContactData contactData = this.getContactData(firstName);
		if (contactData != null)
			contactData.setLastName(lastName);
	}

	public int getRecordsByDates(LocalDate date1, LocalDate date2) {
		List<ContactData> contactList = addressBookDBService.getInstance().getRecordsByDate(date1,date2);
		return contactList.size();
	}

	public int getCountByStates(String state) {
		int count = addressBookDBService.getInstance().getCountByStates(state);
		return count;
	}
}
