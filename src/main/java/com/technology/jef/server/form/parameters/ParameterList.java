package com.technology.jef.server.form.parameters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ParameterList {

	private List<Parameter> formParameters = new LinkedList<Parameter>();
	private Map<String, String> inputParameters = new HashMap<String, String>();
	public List<Parameter> getFormParameters() {
		return formParameters;
	}
	public void setFormParameters(List<Parameter> formParameters) {
		this.formParameters = formParameters;
	}
	public Map<String, String> getInputParameters() {
		return inputParameters;
	}
	public void setInputParameters(Map<String, String> inputParameters) {
		this.inputParameters = inputParameters;
	}
	public void addFormParameter(Parameter parameterDto) {
		getFormParameters().add(parameterDto);
	}
	public void putInputParameter(String name, String value) {
		getInputParameters().put(name,value);
	}


}
