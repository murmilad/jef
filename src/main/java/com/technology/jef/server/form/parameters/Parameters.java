package com.technology.jef.server.form.parameters;

import java.util.HashMap;
import java.util.Map;

public class Parameters extends HashMap<String, Value>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public Parameters() {
		super();
	}

	public Parameters(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public Parameters(int initialCapacity) {
		super(initialCapacity);
	}

	public Parameters(Map<? extends String, ? extends Value> m) {
		super(m);
	}

	public String getValue(String parameterName) {
		return this.containsKey(parameterName) ? this.get(parameterName).getValue() : "";
	}

	public Boolean isChanged(String parameterName) {
		return this.containsKey(parameterName) ? this.get(parameterName).getIsChanged() : false;
	}

	public Boolean isRequired(String parameterName) {
		return this.containsKey(parameterName) ? this.get(parameterName).getIsRequired() : false;
	}

	public void putValue(String name, String value) {
		this.put(name, new Value(name, value));
	}
}
