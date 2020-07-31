package com.technology.jef.server.form.parameters;


public class Value {

	private String name;
	private String value;
	private Boolean isRequired;
	private Boolean isChanged;

	public Value(String name) {
		setName(name);
		setValue("");
		setIsRequired(false);
		setIsChanged(false);
	}

	public Value(String name, String value) {
		setName(name);
		setValue(value);
		setIsRequired(false);
		setIsChanged(false);
	}
	
	public Value(String name, String value, Boolean isRequired, Boolean isChanged) {
		setName(name);
		setValue(value);
		setIsRequired(isRequired);
		setIsChanged(isChanged);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value == null ? "" : value;
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

	public Boolean getIsChanged() {
		return isChanged;
	}

	public void setIsChanged(Boolean isChanged) {
		this.isChanged = isChanged;
	}


}
