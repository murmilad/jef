package com.technology.jef.server.dto;

import java.util.List;
import java.util.Map;


public class ResultDto  extends StatusDto {


	public ResultDto(ErrorDto error) {
		super(error);
	}

	public ResultDto(Integer status_code) {
		super(status_code);
	}

	public ResultDto(List<FormErrorDto> formErrors, Map<String, List<FormErrorDto>> parameterErrors) {
		this.errors = new FormErrorsDto(formErrors, parameterErrors);
	}

	private FormErrorsDto errors = new FormErrorsDto();
	private String id;
	private Boolean release;

	public FormErrorsDto getErrors() {
		return errors;
	}

	public void setErrors(FormErrorsDto form) {
		this.errors = form;
	}

	public void appendResult(ResultDto result, String parameterPrefix) {
		for (FormErrorDto formError: result.getErrors().getFormErrors()) {
			addFormError(formError);
		}

		for (String parameterName: result.getErrors().getParametersErrors().keySet()) {
			addParametersError(parameterName + parameterPrefix, result.getErrors().getParametersErrors().get(parameterName));

		}
	}

	private void addFormError(FormErrorDto formError) {
		getErrors().addFormError(formError);
	}


	private void addParametersError(String string, List<FormErrorDto> list) {
		getErrors().addParametersError(string, list);
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getRelease() {
		return this.release;
	}

	public void setRelease(Boolean release) {
		this.release = release;
	}
	

}
