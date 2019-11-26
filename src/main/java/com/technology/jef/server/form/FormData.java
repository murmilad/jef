package com.technology.jef.server.form;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class FormData {
	public enum Attribute {
		READONLY,
		INVISIBLE,
	}

	private Map<String, String> values = new HashMap<String, String>();
	private Map<String, Map<Attribute, Boolean>> attributes = new HashMap<String, Map<Attribute, Boolean>>();

	public Map<String,String> getValues() {
		return values;
	}

	public void setValues(Map<String,String> values) {
		this.values = values;
	}

	public Map<String, Map<Attribute, Boolean>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Map<Attribute, Boolean>> attributes) {
		this.attributes = attributes;
	}

	public void putValue(String interfaceFieldName, String value) {
		this.values.put(interfaceFieldName, value);
	}

	public String getValue(String interfaceFieldName) {
		return this.values.get(interfaceFieldName);
	}

	public void putAttributes(String parameterName, Map<Attribute, Boolean> attributes) {
		this.attributes.put(parameterName, attributes);
		
	}

	public void putExtraValues(Map<String, String> parameters) {
		for (String name : parameters.keySet()) {
			putValue(name,parameters.get(name));
		}
	}

	
	
}