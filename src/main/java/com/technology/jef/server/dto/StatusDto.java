package com.technology.jef.server.dto;

import static com.technology.jef.server.WebServiceConstant.SERVICE_STATUS_ERROR;
import static com.technology.jef.server.WebServiceConstant.SERVICE_STATUS_OK;

public class StatusDto {

	private Integer status_code;
	private ErrorDto error;
	
	public StatusDto() {
		this.setStatus_code(SERVICE_STATUS_OK);
	}

	public StatusDto(Integer status_code) {
		this.setStatus_code(status_code);
	}

	public StatusDto(ErrorDto error) {
		this.setStatus_code(SERVICE_STATUS_ERROR);
		this.setError(error);
	}

	public Integer getStatus_code() {
		return status_code;
	}

	public void setStatus_code(Integer status_code) {
		this.status_code = status_code;
	}

	public ErrorDto getError() {
		return error;
	}

	public void setError(ErrorDto error) {
		this.error = error;
	}

}
