package com.capg.javaio;

import java.util.ArrayList;
import java.util.Scanner;

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
        employeePayrollService.writeEmployeePayrollData();
    }
}
