package hardik.java_fileIO;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.capg.javaio.exceptions.CustomMySqlException;
import com.capg.javaio.exceptions.CustomMySqlException.ExceptionType;
import com.capg.javaio.model.EmployeePayrollData;
import com.capg.javaio.services.EmployeePayrollService;
import com.capg.javaio.services.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() throws SQLException {
		EmployeePayrollData[] empArray = {
				new EmployeePayrollData(1, "John", (double) 100000),
				new EmployeePayrollData(2, "Jay", (double) 250000),
				new EmployeePayrollData(3, "Roy", (double) 300000),
				
		};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empArray));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
//		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		long entries = employeePayrollService.readEmployeePayrollData(IOService.FILE_IO).size();
		assertEquals(3, entries);
	}
	
	@Test //DB UC1 and 2
	public void givenEmployeePayrollInDB_When_RetrievedShouldMAtchEmployeeCount() throws SQLException {
		EmployeePayrollService employeePayrollService =  new EmployeePayrollService();
//		employeePayrollDBService = employeePayrollDBService;
		int count = employeePayrollService.readEmployeePayrollData(IOService.DB_IO).size();
		assertEquals(3, count);
	}
	
	@Test //DB UC3
	public void givenNewSalaryForEmployee_WhenUpdated_ShoudlMatchWithDB() throws SQLException {
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
			employeePayrollService.updateEmployeeSalary("Teressa", 250000.00,0); //using sql query 
			boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Teressa");
			assertTrue(result);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.NO_DATA_FOUND);
		}
	}
	
	@Test //DB UC4
	public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShoudlMatchWithDB() throws SQLException {
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
			employeePayrollService.updateEmployeeSalary("Teressa", 500000.00,1); //using preparedStatement
			boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Teressa");
			assertTrue(result);
		} catch (SQLException e) {
			throw new CustomMySqlException(e.getMessage(), ExceptionType.NO_DATA_FOUND);
		}
	}
	
}
