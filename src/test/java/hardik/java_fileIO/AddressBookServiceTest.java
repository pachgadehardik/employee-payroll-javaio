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

public class AddressBookServiceTest {

	
	@Test
	public void givenContactsFromDB_Should_ReturnCorrectCount() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		assertEquals(4, addressBookService.getCount());
	}
//	
	/**
	 * @throws SQLException
	 * Testing Retireval as well as insertion in multiple tables using Transactions
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
		addressBookService.addContactToTable("Mark", "Zuccky", "PaloAlto","LA","California","123123",phoneList,emailList);
		boolean result = addressBookService.checkContactInSyncWithDB("Mark");
		assertTrue(result);
	}
	
	/**
	 * @throws SQLException
	 * updating last name by passing first name
	 */
	@Test
	public void updateContactsToDB() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		addressBookService.updateContactData("Mark","Zuckerberg");
		boolean result = addressBookService.checkContactInSyncWithDB("Mark");
		assertTrue(result);
	}
	
	@Test
	public void getRecordsByPassingDates() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		LocalDate date1= LocalDate.parse("2019-01-01");
		LocalDate date2 = LocalDate.parse("2020-01-01");
		int result = addressBookService.getRecordsByDates(date1,date2);
		assertEquals(3, result);
	}
	
	@Test
	public void getCountOfContactsByCityOrState() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		int result = addressBookService.getCountByStates("Maharashtra");
		assertEquals(2, result);
	}
	
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
		assertEquals(4, addressBookService.countEntries());
	}
}
