package com.technology.jef.server.dto;


import static com.technology.jef.server.WebServiceConstant.*;

import com.technology.jef.CurrentLocale;


public class ErrorDto {
	public static enum ErrorType {
		INCORRECT_PARAMETER_ERROR_CODE,
		AUTHORIZATION_REQUIRED,
		SYSTEM_ERROR,
		REDIRECT_REQUIRED,
		INCORRECT_FORM_ERROR,
	}

	public static enum ErrorButton {
		  BACK,
		  CLOSE,
		  RELOAD,
		  NONE,
	}

	public static Integer errorTypeToFlag(ErrorType type){
		switch (type) {		
		  case INCORRECT_PARAMETER_ERROR_CODE: return 1;
		  case AUTHORIZATION_REQUIRED: return 2;
		  case SYSTEM_ERROR: return 3;
		  case REDIRECT_REQUIRED: return 4;
		  case INCORRECT_FORM_ERROR: return 5;
		}
		return null;
	}

	private Integer error_code;
	private String error_description;
	private String error_message;
	private String error_button;
	
	public ErrorDto(ErrorType errorType, String errorText) {
		this.error_code = errorTypeToFlag(errorType);
		this.error_description = errorText;
		
		switch(errorType) {
			case INCORRECT_PARAMETER_ERROR_CODE : 
				this.error_message = CurrentLocale.getInstance().getTextSource().getString("incorrect_value"); break;
			default:
				this.error_message = errorText; break;
		}
		
	}

	public ErrorDto(ErrorType errorType, String errorText, String message, ErrorButton errorButton) {
		this(errorType, errorText, errorButton);
		this.error_message = message;
	}
	public ErrorDto(ErrorType errorType, String errorText, String message) {
		this(errorType, errorText, ErrorButton.NONE);
		this.error_message = message;
	}

	public ErrorDto(ErrorType errorType, String errorText, ErrorButton errorButton) {
		this(errorType, errorText);
		this.setError_button(errorButton.toString().toLowerCase());
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

	public String getError_button() {
		return error_button;
	}

	public void setError_button(String error_button) {
		this.error_button = error_button;
	}
}
