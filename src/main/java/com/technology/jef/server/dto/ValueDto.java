package com.technology.jef.server.dto;

import static com.technology.jef.server.serialize.SerializeConstant.PARAMETER_NAME_VALUE_SEPARATOR;

public class ValueDto<D> extends StatusDto {

	private D value;
	private String name;
	private String message;
	
	public ValueDto(Integer status_code, D value) {
		super(status_code);

		if (value != null && value.getClass().equals(String.class) ) {
			if (((String) value).contains(PARAMETER_NAME_VALUE_SEPARATOR)) {
				this.setName(((String) value).split(PARAMETER_NAME_VALUE_SEPARATOR)[0]);
				if (((String) value).split(PARAMETER_NAME_VALUE_SEPARATOR).length > 1) {
					this.setValue((D)((String) value).split(PARAMETER_NAME_VALUE_SEPARATOR)[1]);
				} else {
					this.setValue((D) "");
				}
			} else  {
				this.setName("");
				this.setValue(value);
			}
		} else  {
			this.setName("");
			this.setValue(value);
		}
	}

	public ValueDto(Integer status_code, D value, String name) {
		super(status_code);

		this.setName(name);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
