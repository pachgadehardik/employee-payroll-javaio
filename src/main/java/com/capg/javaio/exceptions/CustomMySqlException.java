package com.capg.javaio.exceptions;

import java.sql.SQLException;

public class CustomMySqlException extends SQLException {

	private static final long serialVersionUID = -6015737653052400220L;

	public enum ExceptionType{
		NO_DATA_FOUND, OTHER_TYPE
	};
	
	ExceptionType exceptionType;
	
	public CustomMySqlException(String msg, ExceptionType exceptionType) {
		super(msg);
		this.exceptionType = exceptionType;
	}
	
}
