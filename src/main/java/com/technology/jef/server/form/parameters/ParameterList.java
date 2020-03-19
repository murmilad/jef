package com.technology.jef.server.form.parameters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ParameterList {

	private List<Value> formParameters = new LinkedList<Value>();
	private Parameters inputParameters = new Parameters();
	public ParameterList(Parameters allInputParameters) {
		setInputParameters(allInputParameters);
	}
	public List<Value> getFormParameters() {
		return formParameters;
	}
	public void setFormParameters(List<Value> formParameters) {
		this.formParameters = formParameters;
	}
	public Parameters getInputParameters() {
		return inputParameters;
	}
	public void setInputParameters(Parameters inputParameters) {
		this.inputParameters = inputParameters;
	}
	public void addFormParameter(Value parameterDto) {
		getFormParameters().add(parameterDto);
	}
	public void putInputParameter(String name, Value value) {
		getInputParameters().put(name,value);
	}


}
