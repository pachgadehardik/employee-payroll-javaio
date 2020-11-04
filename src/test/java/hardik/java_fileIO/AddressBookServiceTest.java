package hardik.java_fileIO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.capg.javaio.model.ContactData;
import com.capg.javaio.model.Email;
import com.capg.javaio.model.Phone;
import com.capg.javaio.services.AddressBookService;
import com.capg.javaio.services.EmployeePayrollService.IOService;

public class AddressBookServiceTest {

	
	/**
	 * @throws SQLException
	 * Get database records
	 * Uc16
	 */
	@Test
	public void givenContactsFromDB_Should_ReturnCorrectCount() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData().forEach(System.out::print);
		assertEquals(7, addressBookService.readContactData().size());
	}

	/**
	 * @throws SQLException
	 * updating Phone List by passing first name
	 * Uc17
	 */
	@Test
	public void updateContactsToDB() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		List<Phone> phoneList = new ArrayList<>(); 
		List<Email> emailList = new ArrayList<>();
		Collections.addAll(phoneList,new Phone("9878998787"), new Phone("1233456781"));
		Collections.addAll(emailList, new Email("abc@asda.com"), new Email("arasd@sejg.com"));
		ContactData newObj = new ContactData(0, "David", "Beckham", "Baker Street","London","London","322311",emailList,phoneList);
		addressBookService.updateContactObject(newObj, IOService.DB_IO);
		boolean result = addressBookService.checkContactInSyncWithDB("David");
		assertTrue(result);
	}
	
	
	/**
	 * @throws SQLException
	 * UC 18  retireive contacts by giving dates
	 */
	@Test
	public void getRecordsByPassingDates() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		LocalDate date1= LocalDate.parse("2019-01-01");
		LocalDate date2 = LocalDate.parse("2020-01-01");
		int result = addressBookService.getRecordsByDates(date1,date2);
		assertEquals(2, result);
	}
	
	
	/**
	 * @throws SQLException
	 * Uc19
	 */
	@Test
	public void getNoCountofContactsByCityOrState() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		int result = addressBookService.getCountByStates("Kerala");
		assertEquals(0, result);
	}
	
	
	/**
	 * @throws SQLException
	 * Testing Retrieval as well as insertion in multiple tables using Transactions
	 * UC 20 Adding data to the table
	 */
	@Test
	public void addContactsToDB_AlongWithPhoneEmails() throws SQLException
	{
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		List<Phone> phoneList = new ArrayList<>(); 
		List<Email> emailList = new ArrayList<>();
		Collections.addAll(phoneList,new Phone("9878998787"), new Phone("7788999887"));
		Collections.addAll(emailList, new Email("abc@asda.com"), new Email("arasd@sejg.com"));
		addressBookService.addContactToTable("David", "Beckham", "Baker Street","London","London","322311",phoneList,emailList);
		boolean result = addressBookService.checkContactInSyncWithDB("David");
		assertTrue(result);
	}
	
	

	
	
	@Test
	public void getCountOfContactsByCityOrState() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		int result = addressBookService.getCountByStates("Maharashtra");
		assertEquals(1, result);
	}
	
	
	/**
	 * @throws SQLException
	 * UC21 Adding Multiple Contacts using Threads
	 */
	@Test
	public void addMultipleContactsUsingThreadInDB() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		ContactData[] contactDataArray = {
				new ContactData(0, "Hardik", "P", "lakeshore greens", "Mumbai", "Maharashtra", "123432"),
				new ContactData(0, "Kunal", "M", "Mahavir nagar", "Mumbai", "Maharashtra", "453432"),
				new ContactData(0, "Aditya", "M", "akot", "Akola", "Maharashtra", "434432")
		};
		
		addressBookService.readContactData();
		Instant threadStart = Instant.now();
		addressBookService.addContactToDBWithThreads(Arrays.asList(contactDataArray));
		Instant threadEnd = Instant.now();
		System.out.println("Duration With Thread: " + java.time.Duration.between(threadStart, threadEnd));
		assertEquals(4, addressBookService.getCount());
	}
	
	
}
