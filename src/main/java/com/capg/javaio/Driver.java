package com.capg.javaio;

import java.util.ArrayList;
import java.util.Scanner;

import com.capg.javaio.model.EmployeePayrollData;
import com.capg.javaio.services.EmployeePayrollService;
import com.capg.javaio.services.EmployeePayrollService.IOService;

/**
 * Java IO 
 */
public class Driver 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInput  = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollDataFromConsole(consoleInput);
        employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
    }
}
