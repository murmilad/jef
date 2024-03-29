package com.technology.jef.server.form.parameters;


public class Value {

	private String name;
	private String value;
	private String visibleValue;
	private Boolean isRequired;
	private Boolean isChanged;

	public Value(String name) {
		setName(name);
		setValue("");
		setIsRequired(false);
		setIsChanged(false);
		setVisibleValue("");
	}

	public Value(String name, String value) {
		setName(name);
		setValue(value);
		setIsRequired(false);
		setIsChanged(false);
		setVisibleValue("");
	}
	
	public Value(String name, String value, Boolean isRequired, Boolean isChanged, String visibleValue) {
		setName(name);
		setValue(value);
		setIsRequired(isRequired);
		setIsChanged(isChanged);
		setVisibleValue(visibleValue);
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
	public Double getDoubleOrZero() {
		return value == null 
				? 0 
				: "".equals(value)
					? 0
					: Double.parseDouble(value);
	}
	public Integer getIntegerOrNull() {
		return value == null 
				? null 
				: "".equals(value)
					? null
					: Integer.parseInt(value);
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

	public String getVisibleValue() {
		return visibleValue;
	}

	public void setVisibleValue(String visibleValue) {
		this.visibleValue = visibleValue;
	}

	public int getIntegerOrZero() {
		return value == null 
				? 0 
				: "".equals(value)
					? 0
					: Integer.parseInt(value);
	}


}
