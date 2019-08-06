package com.technology.jef.server.dto;

import java.util.HashMap;
import java.util.Map;

import com.technology.jef.server.form.FormData.Attribute;

public class ParameterDto {

	private Object value;
	private Map<Attribute, Boolean> attributes = new HashMap<Attribute, Boolean>();

	public ParameterDto(String value, Map<Attribute, Boolean> attribuetes) {
		setValue(value);
		setAttributes(attribuetes);
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Map<Attribute, Boolean> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<Attribute, Boolean> attributes) {
		this.attributes = attributes;
	}
	
}
