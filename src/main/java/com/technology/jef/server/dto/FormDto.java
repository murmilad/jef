package com.technology.jef.server.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.technology.jef.server.form.FormData;


public class FormDto  extends ResultDto {


	public FormDto(ErrorDto error) {
		super(error);
	}

	public FormDto(Integer status_code) {
		super(status_code);
	}

	public FormDto(Integer serviceStatus, FormData formData) {
		super(serviceStatus);
		
		setGroups(formData.getGroups());
		for (String parameterName: formData.getValues().keySet()) {
			putParameter(parameterName, new ParameterDto(formData.getValues().get(parameterName), formData.getAttributes().get(parameterName)));
		}
	}

	private List<String> groups;
	private Map<String,ParameterDto> parameters = new HashMap<String,ParameterDto>();

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
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

	public void setResult(List<String> formErrors, Map<String, List<String>> parametersErrors) {
		getErrors().setFormErrors(formErrors);
		getErrors().setParametersErrors(parametersErrors);
	}

	public void setResult(ResultDto result) {
		getErrors().setFormErrors(result.getErrors().getFormErrors());
		getErrors().setParametersErrors(result.getErrors().getParametersErrors());
	}
	

}
