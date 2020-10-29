package com.capg.javaio.enums;

public enum AggregateFunctions {
	
	SUM{
	public String getQuery() {
		return "Select Sum(salary) from temp_payroll_table where gender = ?";
	}
	},
	AVERAGE{

		@Override
		public String getQuery() {
			return "Select Avg(salary) from temp_payroll_table where gender = ?";
		}
		
	}, COUNT{
		@Override
		public String getQuery() {
			return "Select Count(salary) from temp_payroll_table where gender = ?";
		}
		
	}, MIN{
		@Override
		public String getQuery() {
			return "Select Min(salary) from temp_payroll_table where gender = ?";
		}
	}, MAX{ 
		@Override
		public String getQuery() {
			return "Select Max(salary) from temp_payroll_table where gender = ?";
		}
	};
	
	public abstract String getQuery();
}
