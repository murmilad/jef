package com.technology.jef.server.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class FormErrorsDto {

	private List<FormErrorDto> form = new LinkedList<FormErrorDto>();
	private Map<String,List<FormErrorDto>> parameters = new HashMap<String, List<FormErrorDto>>();

	public FormErrorsDto(List<FormErrorDto> formErrors, Map<String,List<FormErrorDto>> parameters) {
		this.form = formErrors;
		this.parameters = parameters;
	}

	public FormErrorsDto() {
		// TODO Auto-generated constructor stub
	}

	public List<FormErrorDto> getFormErrors() {
		return form;
	}

	public void setFormErrors(List<FormErrorDto> errors) {
		this.form = errors;
	}

	public void addFormError(FormErrorDto formError) {
		this.form.add(formError);
	}

	public Map<String,List<FormErrorDto>> getParametersErrors() {
		return parameters;
	}

	public void setParametersErrors(Map<String, List<FormErrorDto>> errors) {
		this.parameters = errors;
	}

	public void addParametersError(String string, List<FormErrorDto> list) {
		if (parameters.containsKey(string)) {
			parameters.get(string).addAll(list);
		} else {
			parameters.put(string,list);
		}
	}

}
