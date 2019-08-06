package com.technology.jef.server.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.technology.jef.server.form.FormData;


public class FormErrorsDto {

	private List<String> form = new LinkedList<String>();
	private Map<String,List<String>> parameters = new HashMap<String, List<String>>();

	public FormErrorsDto(List<String> formErrors, Map<String,List<String>> parameters) {
		this.form = formErrors;
		this.parameters = parameters;
	}

	public FormErrorsDto() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getFormErrors() {
		return form;
	}

	public void setFormErrors(List<String> errors) {
		this.form = errors;
	}

	public void addFormError(String formError) {
		this.form.add(formError);
	}

	public Map<String,List<String>> getParametersErrors() {
		return parameters;
	}

	public void setParametersErrors(Map<String, List<String>> errors) {
		this.parameters = errors;
	}

	public void addParametersError(String string, List<String> list) {
		parameters.put(string, list);
	}

}
