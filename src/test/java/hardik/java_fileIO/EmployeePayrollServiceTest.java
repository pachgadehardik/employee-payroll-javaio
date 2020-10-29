package hardik.java_fileIO;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.capg.javaio.enums.AggregateFunctions;
import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.exceptions.CustomMySqlException.ExceptionType;
import com.capg.javaio.model.EmployeePayrollData;
import com.capg.javaio.services.EmployeePayrollService;
import com.capg.javaio.services.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() throws SQLException {
		EmployeePayrollData[] empArray = { new EmployeePayrollData(1, "John", (double) 100000),
				new EmployeePayrollData(2, "Jay", (double) 250000), new EmployeePayrollData(3, "Roy", (double) 300000),

		};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empArray));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
//		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		long entries = employeePayrollService.readEmployeePayrollData(IOService.FILE_IO).size();
		assertEquals(0, entries);
	}

	@Test // DB UC1 and 2
	public void givenEmployeePayrollInDB_When_RetrievedShouldMAtchEmployeeCount() throws SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
//		employeePayrollDBService = employeePayrollDBService;
		int count = employeePayrollService.readEmployeePayrollData(IOService.DB_IO).size();
		assertEquals(3, count);
	}

	@Test // DB UC3
	public void givenNewSalaryForEmployee_WhenUpdated_ShoudlMatchWithDB() throws SQLException {
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			List<EmployeePayrollData> employeePayrollDataList = employeePayrollService
					.readEmployeePayrollData(IOService.DB_IO);
			employeePayrollService.updateEmployeeSalary("Teressa", 250000.00, 0); // using sql query
			boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Teressa");
			assertTrue(result);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.NO_DATA_FOUND);
		}
	}

	@Test // DB UC4
	public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShoudlMatchWithDB() throws SQLException {
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			List<EmployeePayrollData> employeePayrollData = employeePayrollService
					.readEmployeePayrollData(IOService.DB_IO);
			employeePayrollService.updateEmployeeSalary("Teressa", 500000.00, 1); // using preparedStatement
			boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Teressa");
			assertTrue(result);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.NO_DATA_FOUND);
		}
	}

	@Test // DB UC 5
	public void givenEmployeePayrollInDB_ShouldReturnSalariesBasedOnDateRange() throws CustomMySqlException {
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
			int recordCount = employeePayrollService.getSalaryBasedOnDateRange("2020-01-01","2020-12-01").size();
			assertEquals(2, recordCount);
		}
		catch (Exception e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.NO_DATA_FOUND);
		}
	}
	

	@Test //DB UC6
	public void givenEmployeePayrollInDB_ShouldReturnAggregateFunctions() throws CustomMySqlException {
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
			assertEquals(600000.0,employeePayrollService.getAggregateSalaryRecords(AggregateFunctions.SUM,"M"));
			assertEquals(300000.0,employeePayrollService.getAggregateSalaryRecords(AggregateFunctions.AVERAGE,"M"));
			assertEquals(1.0,employeePayrollService.getAggregateSalaryRecords(AggregateFunctions.COUNT,"F"));
			assertEquals(100000.0,employeePayrollService.getAggregateSalaryRecords(AggregateFunctions.MIN,"M"));
			assertEquals(500000.0,employeePayrollService.getAggregateSalaryRecords(AggregateFunctions.MAX,"M"));
		}catch(SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.NO_DATA_FOUND);
		}
	}
	
	
	@Test //DBUC6 Average
	public void givenEmployeePayrollInDB_ShouldReturnAverageByGender() throws SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String,Double> averageSalaryByGender = employeePayrollService.readAvergaeSalaryByGender(IOService.DB_IO);
		assertTrue(averageSalaryByGender.get("M").equals(300000.00) && averageSalaryByGender.get("F").equals(250000.00));
	}
	
	
	@Test //DBUC 7 & 8 inserting employee
	public void givenEmployee_WhenAdded_Should_SyncWithDB() throws SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayrollTable("Jane", 340000.00, LocalDate.now(),"F");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Jane");
		assertTrue(result);
	}
	
	
	

}
