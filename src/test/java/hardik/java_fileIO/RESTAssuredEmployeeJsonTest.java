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

import com.capg.javaio.model.EmployeePayrollData;
import com.capg.javaio.services.EmployeePayrollService;
import com.capg.javaio.services.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RESTAssuredEmployeeJsonTest {

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 4000;
		
	}

	public Response getEmployeeList1() {
		Response response = RestAssured.get("/employees");
		return response;
	}

	public EmployeePayrollData[] getEmployeeList2() {
		Response response = RestAssured.get("/employees");
		EmployeePayrollData[] arrayOfEmp = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmp;
	}
	
	private boolean addMultipleEmployeeToServerUsingThread(EmployeePayrollData[] employeePayrollData) {
		Map<Integer, Boolean> empAdditionStatus = new HashMap<>();
		Map<Integer, Boolean> empCodeStatus = new HashMap<Integer, Boolean>();
		Arrays.asList(employeePayrollData).forEach(empObj ->{
			Runnable task = () ->{
				empAdditionStatus.put(empObj.hashCode(),false);
				empCodeStatus.put(empObj.hashCode(),false);
				Response response = addEmployeeToJsonServer(empObj);
				empAdditionStatus.put(empObj.hashCode(),true);
				empCodeStatus.put(empObj.hashCode(),response.getStatusCode() == 201);
			};
			
			Thread thread = new Thread(task, empObj.getName());
			thread.start();
			
		});
		
		while(empAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return empCodeStatus.containsValue(false);
	}
	
	private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification requestSpecification = RestAssured.given();
		requestSpecification.header("Content-Type", "application/json");
	    requestSpecification.body(empJson);
	    return requestSpecification.post("/employees");
	}
	
	private Response updateEmployeeSalary(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given().header("Content-Type", "application/json").body(empJson);
		Response response = request.put("employees/"+employeePayrollData.getId());
		return response;		
	}
	
	@Test
	public void OnCallingList_ReturnEmployeelist() {
		Response response = getEmployeeList1();
		System.out.println("At first: " + response.asString());
		System.out.println(response);
		response.then().body("id", Matchers.hasItems(93, 94, 95));
		response.then().body("name", Matchers.hasItems("Jeff", "Mukesh", "Elon"));
	}

	@Test
	public void givenNewEmployee_WhenAddedShouldMatch201ResponseAndCount() throws SQLException {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayofEmp = getEmployeeList2();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayofEmp ));
		
		EmployeePayrollData employeePayrollData = new EmployeePayrollData(0,"Henry Ford",600000.00,LocalDate.now(),"M");
		Response response = addEmployeeToJsonServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		assertEquals(201, statusCode);
		
		employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		employeePayrollService.addEmployeeToPayroll(employeePayrollData,IOService.REST_IO);
	    assertEquals(5, employeePayrollService.countEntries(IOService.REST_IO));

	}

	@Test
	public void addMultipleEmployeeToServerUsingThreadShouleReturn201Code() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayofEmp = getEmployeeList2();
		arrayofEmp = new EmployeePayrollData[] {
				new EmployeePayrollData(0, "Modi", 450000.00, LocalDate.now(),"M"),
				new EmployeePayrollData(0, "Ria", 550000.00, LocalDate.now(),"F"),
				new EmployeePayrollData(0, "Mamta", 250000.00, LocalDate.now(),"F")
		};
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayofEmp));
		assertFalse(addMultipleEmployeeToServerUsingThread(arrayofEmp));
		employeePayrollService.addEmployeeToPayrollTable(Arrays.asList(arrayofEmp));
		assertEquals(11, employeePayrollService.countEntries(IOService.REST_IO));
		
	}
	
	
	@Test
	public void givenSalaryForEmployee_WhenUpdated_ShouldReturn200Response() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayofEmp = getEmployeeList2();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayofEmp));
		
		employeePayrollService.updateEmployeeSalary("Modi", 345345.00, IOService.REST_IO);
		EmployeePayrollData employeePayrollData =  employeePayrollService.getEmployeePayrollData("Modi");
		Response respose = updateEmployeeSalary(employeePayrollData);
		int statusCode = respose.getStatusCode();
		assertEquals(200, statusCode);
	}

	@Test
	public void givenEmployeeWhenDeletedShouldMatchResponse() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayofEmp = getEmployeeList2();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayofEmp));
		
		EmployeePayrollData employeePayrollData =  employeePayrollService.getEmployeePayrollData("Ria");
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given().header("Content-Type", "application/json").body(empJson);
		Response response = request.delete("employees/"+employeePayrollData.getId());
		assertEquals(200, response.getStatusCode());
		
		employeePayrollService.deleteFromCache(employeePayrollData.getName(),IOService.REST_IO);
		assertEquals(8, employeePayrollService.countEntries(IOService.REST_IO));
	}

}
