package com.technology.jef.server.form.parameters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class FormParameters {

	private String parrentApi;
	private String currentApi;
	private List<FormParameters> children = new LinkedList<FormParameters>();
	private Map<String, ParameterList> parameters = new HashMap<String, ParameterList>();

	public FormParameters(String currentApi) {
		setCurrentApi(currentApi);
	}
	
	public Map<String, ParameterList> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, ParameterList> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getFormParameters(String formPrefix) {
		return parameters.get(formPrefix).getFormParameters();
	}

	public Map<String,String> getInputParameters(String formPrefix) {
		return parameters.get(formPrefix).getInputParameters();
	}

	public String getParrentApi() {
		return parrentApi;
	}

	public void setParrentApi(String parrentApi) {
		this.parrentApi = parrentApi;
	}

	public void addParameter(String name, String value, Boolean isRequired, String formPrefix) {
		if (!this.parameters.containsKey(formPrefix)) {
			this.parameters.put(formPrefix, new ParameterList());
		}
		this.parameters.get(formPrefix).addFormParameter(new Parameter(name, value, isRequired));
		this.parameters.get(formPrefix).putInputParameter(name, value);
	}

	public void addChildren(FormParameters formParameters) {
		children.add(formParameters);
	}
	
	public List<FormParameters> getChildren(){
		return children;
	}

	public String getCurrentApi() {
		return currentApi;
	}

	public void setCurrentApi(String currentApi) {
		this.currentApi = currentApi;
	}
}
