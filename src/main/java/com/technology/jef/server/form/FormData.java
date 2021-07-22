package com.technology.jef.server.form;

import static com.technology.jef.server.serialize.SerializeConstant.PARAMETER_NAME_VALUE_SEPARATOR;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.technology.jef.server.form.parameters.Parameters;
import com.technology.jef.server.form.parameters.Value;


public class FormData {
	public enum Attribute {
		READONLY,
		INVISIBLE,
	}

	private Parameters values = new Parameters();
	private Map<String, Map<Attribute, Boolean>> attributes = new HashMap<String, Map<Attribute, Boolean>>();

	public Parameters getValues() {
		return values;
	}

	public void setValues(Parameters values) {
		this.values = values;
	}

	public Map<String, Map<Attribute, Boolean>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Map<Attribute, Boolean>> attributes) {
		this.attributes = attributes;
	}

	public void putValue(String interfaceFieldName, Value value) {
		this.values.put(interfaceFieldName, value);
	}

	public void putValue(String interfaceFieldName, String value) {
		this.values.put(interfaceFieldName, new Value(interfaceFieldName, value));
	}

	public String getValue(String interfaceFieldName) {
		return this.values.get(interfaceFieldName).getValue();
	}

	public Boolean isEmptyValue(String interfaceFieldName) {
		return "".equals(this.values.get(interfaceFieldName).getValue())
		|| PARAMETER_NAME_VALUE_SEPARATOR.equals(this.values.get(interfaceFieldName).getValue());
	}

	public void putAttributes(String parameterName, Map<Attribute, Boolean> attributes) {
		this.attributes.put(parameterName, attributes);
		
	}

	public void putExtraValues(Parameters parameters) {
		for (String name : parameters.keySet()) {
			putValue(name,parameters.get(name));
		}
	}

	
	
}
