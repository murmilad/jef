package com.technology.jef.server.form.parameters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class FormParameters {

	private String parrentApi;
	private String currentApi;
	private String superApi;
	
	private List<FormParameters> children = new LinkedList<FormParameters>();
	private Map<String, Map<String,ParameterList>> parameters = new HashMap<String, Map<String,ParameterList>>();

	public FormParameters(String currentApi) {
		setCurrentApi(currentApi);
	}
	
	public Map<String, Map<String,ParameterList>> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Map<String,ParameterList>> parameters) {
		this.parameters = parameters;
	}

	public List<Value> getFormParameters(String formPath, String formPrefix) {
		return parameters.get(formPath).get(formPrefix).getFormParameters();
	}

	public Parameters getInputParameters(String formPath, String formPrefix) {
		return parameters.get(formPath).get(formPrefix).getInputParameters();
	}

	public String getParrentApi() {
		return parrentApi;
	}

	public void setParrentApi(String parrentApi) {
		this.parrentApi = parrentApi;
	}

	public void addParameter(String name, Value parameter, String formPath, String formPrefix, Parameters allInputParameters) {
		if (!this.parameters.containsKey(formPath)) {
			this.parameters.put(formPath, new HashMap<String, ParameterList>());
		}
		if (!this.parameters.get(formPath).containsKey(formPrefix)) {
			this.parameters.get(formPath).put(formPrefix, new ParameterList(new Parameters(allInputParameters)));
		}
		this.parameters.get(formPath).get(formPrefix).addFormParameter(parameter);
		this.parameters.get(formPath).get(formPrefix).putInputParameter(name, parameter);
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

	public String getSuperApi() {
		return superApi;
	}

	public void setCurrentApi(String currentApi) {
		this.currentApi = currentApi;
	}

	public void setSuperApi(String superApi) {
		this.superApi = superApi;
		
	}

}
