package com.technology.jef.server.form.parameters;


public class Parameter {

	private String name;
	private String value;
	private Boolean isRequired;

	public Parameter(String name, String value, Boolean isRequired) {
		setName(name);
		setValue(value);
		setIsRequired(isRequired);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getIsRequired() {
		return isRequired;
	}
	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}


}
