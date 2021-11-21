package com.technology.jef.server.form.parameters;

import static com.technology.jef.server.serialize.SerializeConstant.*;

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

	public Parameters(Parameters parameters) {
		this();

		for (String parameterName: parameters.keySet()) {
			put(parameterName, parameters.get(parameterName));
		}
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

	public Double getDoubleOrZero(String parameterName) {
		return this.containsKey(parameterName) ? this.get(parameterName).getDoubleOrZero() : 0;
	}

	public Integer getIntegerOrNull(String parameterName) {
		return this.containsKey(parameterName) ? this.get(parameterName).getIntegerOrNull() : null;
	}

	public Boolean isEmptyValue(String parameterName) {
		return !this.containsKey(parameterName) || "".equals(this.get(parameterName).getValue());
	}

	public String getGroupValue(String parameterName, String formName, Integer groupNumber) {
		return getValue(parameterName + GROUP_SEPARATOR + formName + "_" + groupNumber);
	}
	
	public String getGroupCount() {
		return getValue("group_count");
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

	public Object getLastJoinedGroupId(String string) {
		// TODO Auto-generated method stub
		return getValue("id_add_joined_group_" + string);
	}
}
