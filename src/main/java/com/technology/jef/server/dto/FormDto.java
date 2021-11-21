package com.technology.jef.server.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.technology.jef.server.form.FormData;


public class FormDto  extends ResultDto {

	private List<FormDto> groups;
	private Map<String,ParameterDto> parameters = new HashMap<String,ParameterDto>();

	public FormDto(ErrorDto error) {
		super(error);
	}

	public FormDto(Integer status_code) {
		super(status_code);
	}

	public FormDto(Integer status_code, List<FormDto> groups) {
		super(status_code);
		setGroups(groups);
	}

	public List<FormDto> getGroups() {
		return groups;
	}

	public void setGroups(List<FormDto> groups) {
		this.groups = groups;
	}
	public void addGroup(FormDto groupData) {
		this.groups.add(groupData);
	}
	
	public Map<String,ParameterDto> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String,ParameterDto> parameters) {
		this.parameters = parameters;
	}

	public void putParameter(String parameterName, ParameterDto parameter) {
		this.parameters.put(parameterName, parameter);
	}

	public void setResult(List<FormErrorDto> formErrors, Map<String, List<FormErrorDto>> parametersErrors) {
		getErrors().setFormErrors(formErrors);
		getErrors().setParametersErrors(parametersErrors);
	}

	public void setResult(ResultDto result) {
		getErrors().setFormErrors(result.getErrors().getFormErrors());
		getErrors().setParametersErrors(result.getErrors().getParametersErrors());
	}
	

}
