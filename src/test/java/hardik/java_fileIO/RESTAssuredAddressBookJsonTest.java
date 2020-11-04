package hardik.java_fileIO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.model.ContactData;
import com.capg.javaio.model.Email;
import com.capg.javaio.model.EmployeePayrollData;
import com.capg.javaio.model.Phone;
import com.capg.javaio.services.AddressBookService;
import com.capg.javaio.services.EmployeePayrollService;
import com.capg.javaio.services.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RESTAssuredAddressBookJsonTest {

	public String getDataInJson() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		String json = new Gson().toJson(addressBookService.readContactData());
		System.out.println(json);
		return json;
	}

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public Response getResponse() {
		Response response = RestAssured.get("/contact");
		return response;
	}

	public ContactData[] getContactList() {
		Response response = RestAssured.get("/contact");
		ContactData[] arrayOfContact = new Gson().fromJson(response.asString(), ContactData[].class);
		return arrayOfContact;
	}

	private Response addContactToJsonServer(ContactData contactData) {
		String contactJson = new Gson().toJson(contactData);
		RequestSpecification request = RestAssured.given().header("Content-Type", "application/json").body(contactJson);
		return request.post("/contact");
	}

	private boolean addMultipleContactsUsingThreads(ContactData[] arrayOfContacts) {
		Map<Integer, Boolean> contactAddStatus = new HashMap<>();
		Map<Integer, Boolean> contactCodeStatus = new HashMap<Integer, Boolean>();
		Arrays.asList(arrayOfContacts).forEach(obj -> {
			Runnable task = () -> {
				contactAddStatus.put(obj.hashCode(), false);
				contactCodeStatus.put(obj.hashCode(), false);
				Response response = addContactToJsonServer(obj);
				contactAddStatus.put(obj.hashCode(), true);
				contactCodeStatus.put(obj.hashCode(), response.getStatusCode() == 201);
			};

			Thread thread = new Thread(task, obj.getFirstName());
			thread.start();

		});

		while (contactAddStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return contactCodeStatus.containsValue(false);
	}
	
	private Response updateContactName(ContactData contactData) {
		String contactJson = new Gson().toJson(contactData);
		RequestSpecification request = RestAssured.given().header("Content-Type", "application/json").body(contactJson);
		Response response = request.put("contact/"+contactData.getId());
		return response;		
	}

	@Test
	public void OnCallingList_ReturnContactlist() {
		Response response = getResponse();
		System.out.println("At first: " + response.asString());
		response.then().body("id", Matchers.hasItems(1, 2, 3));
		response.then().body("firstName", Matchers.hasItems("Terissa", "Tejas", "Jake"));
	}

	@Test
	public void givenNewContact_WhenAddedShouldMatch201ResponseAndCount() throws SQLException {
		AddressBookService addressBookService;
		ContactData[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		Email[] emailsList = { new Email("asd@asd.com") };
		Phone[] phoneList = { new Phone("9878987890"), new Phone("7777123123") };
		ContactData contactData = new ContactData(0, "Hardik", "Pachgade", "Lakeshore Greens", "Mumbai", "Maharashtra",
				"432211", Arrays.asList(emailsList), Arrays.asList(phoneList), LocalDate.now());
		Response response = addContactToJsonServer(contactData);
		int statusCode = response.getStatusCode();
		assertEquals(201, statusCode);

		contactData = new Gson().fromJson(response.asString(), ContactData.class);
		addressBookService.addContactToDB(contactData, IOService.REST_IO);
		assertEquals(4, addressBookService.countEntries(IOService.REST_IO));
	}

	@Test
	public void addMultipleContactToServerUsingThreadShouleReturn201Code() {
		AddressBookService addressBookService;
		ContactData[] arrayOfContacts = getContactList();
		Email[] emailsList = { new Email("asd@asd.com"), new Email("test@gmail.com") };
		Phone[] phoneList = { new Phone("9878987890"), new Phone("7777123123") };
		arrayOfContacts = new ContactData[] {
				new ContactData(0, "Aditya", "M", "Akot", "Akola", "Maharashtra", "456543", Arrays.asList(emailsList),
						Arrays.asList(phoneList), LocalDate.now()),
				new ContactData(0, "Parth", "D", "Parel", "Mumbai", "Maharashtra", "400012", Arrays.asList(emailsList),
						Arrays.asList(phoneList), LocalDate.now()) };
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		assertFalse(addMultipleContactsUsingThreads(arrayOfContacts));
		addressBookService.addContactToDBWithThreads(Arrays.asList(arrayOfContacts));
		assertEquals(2, addressBookService.countEntries(IOService.REST_IO));

	}
	
	@Test
	public void givenNameForContact_WhenUpdated_ShouldReturn200Response() throws CustomMySqlException {
		AddressBookService addressBookService;
		ContactData[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		
		try {
			addressBookService.updateContactData("Aditya", "Malani(UpdatedSurname)", IOService.REST_IO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ContactData contactData =  addressBookService.getContactData("Aditya");
		Response respose = updateContactName(contactData);
		int statusCode = respose.getStatusCode();
		assertEquals(200, statusCode);
	}

	@Test
	public void givenContactWhenDeletedShouldMatchResponse() {
		AddressBookService addressBookService;
		ContactData[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		
		ContactData contactData =  addressBookService.getContactData("Parth");
		String contactJson = new Gson().toJson(contactData);
		RequestSpecification request = RestAssured.given().header("Content-Type", "application/json").body(contactJson);
		Response response = request.delete("contact/"+contactData.getId());
		assertEquals(200, response.getStatusCode());
		
		addressBookService.deleteFromCache(contactData.getFirstName(),IOService.REST_IO);
		assertEquals(7, addressBookService.countEntries(IOService.REST_IO));
	}


}
