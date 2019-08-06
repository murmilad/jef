package com.technology.jef.server.dto;


import static com.technology.jef.server.WebServiceConstant.*;


public class ErrorDto {

	private Integer error_code;
	private String error_description;
	private String error_message;
	
	public ErrorDto(Integer errorCode, String errorText) {
		this.error_code = errorCode;
		this.error_description = errorText;
		
		switch(errorCode) {
			case 0 :
				this.error_message = errorText; break;
			case INCORRECT_PARAMETER_ERROR_CODE : 
				this.error_message = "Неверно введен параметр"; break;
		}
		
	}

	public ErrorDto(Integer errorCode, String errorText, String message) {
		this(errorCode, errorText);
		this.error_message = message;
	}
	public Integer getError_code() {
		return error_code;
	}
	public void setError_code(Integer error_code) {
		this.error_code = error_code;
	}
	public String getError_description() {
		return error_description;
	}
	public void setError_description(String error_description) {
		this.error_description = error_description;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
}
