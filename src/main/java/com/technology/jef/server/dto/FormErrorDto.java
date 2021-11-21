package com.technology.jef.server.dto;


public class FormErrorDto {

	private String message;
	private String code;
	private String field;
	private Boolean block;
	
	public FormErrorDto(String errorMessage) {
		setMessage(errorMessage);
		setField("");
		setCode("");
		setBlock(true);
	}

	public FormErrorDto(String errorMessage, String errorField) {
		setMessage(errorMessage);
		setField(errorField);
		setCode("");
		setBlock(true);
	}
	public FormErrorDto(String errorMessage, String errorField, String errorCode) {
		setMessage(errorMessage);
		setField(errorField);
		setCode(errorCode);
		setBlock(true);
	}
	public FormErrorDto(String errorMessage, String errorField, String errorCode, Boolean block) {
		setMessage(errorMessage);
		setField(errorField);
		setCode(errorCode);
		setBlock(block);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String error_message) {
		this.message = error_message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String error_code) {
		this.code = error_code;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Boolean getBlock() {
		return block;
	}

	public void setBlock(Boolean block) {
		this.block = block;
	}


}
