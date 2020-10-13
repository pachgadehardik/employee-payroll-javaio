package hardik.java_fileIO;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.capg.javaio.EmployeePayrollData;
import com.capg.javaio.EmployeePayrollService;
import com.capg.javaio.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] empArray = {
				new EmployeePayrollData(1, "John", 100000),
				new EmployeePayrollData(2, "Jay", 250000),
				new EmployeePayrollData(3, "Roy", 300000),
				
		};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empArray));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
//		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		long entries = employeePayrollService.readEmployeePayrollData(IOService.FILE_IO);
		assertEquals(3, entries);
		
	}
}
