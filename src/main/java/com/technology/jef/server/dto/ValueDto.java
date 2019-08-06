package com.technology.jef.server.dto;

public class ValueDto<D> extends StatusDto {

	private D value;
	private String message;
	
	public ValueDto(Integer status_code, D value) {
		super(status_code);

		this.setValue(value);
	}

	public ValueDto(ErrorDto error) {
		super(error);
	}

	public D getValue() {
		return value;
	}

	public void setValue(D value) {
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
