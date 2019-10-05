package com.technology.jef.server.form;

import java.util.List;
import java.util.Map;

import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.exceptions.ServiceException;

public class Field {

	private String fieldName;

	public Field(String fieldName) {
		setFieldName(fieldName);
	}

	public Field() {
		setFieldName(null);
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


	public List<String> checkHandler(String parameterName, Map<String, String> parameters) throws ServiceException {
		return null;
	}

	public String getValueHandler(String parameterName, Map<String, String> parameters) throws ServiceException {
		throw new ServiceException("getValueHandler is not definded for parameter: '" + parameterName + "'");
	}

	
	public List<OptionDto> getListHandler(String parameterName, Map<String, String> parameters) throws ServiceException {
		throw new ServiceException("getListHandler is not definded for parameter: '" + parameterName + "'");
	}

	
	public List<OptionDto> getListInteractiveHandler(String parameterName, Map<String, String> parameters) throws ServiceException {
		throw new ServiceException("getListInteractiveHandler is not definded for parameter: '" + parameterName + "'");
	}

	public Boolean isVisibleHandler(String parameterName, Map<String, String> parameters) throws ServiceException {
		throw new ServiceException("isVisibleHandler is not definded for parameter: '" + parameterName + "'");
	}

	public Boolean isActiveHandler(String parameterName, Map<String, String> parameters) throws ServiceException {
		throw new ServiceException("isActiveHandler is not definded for parameter: '" + parameterName + "'");
	}

	public Boolean isRequiredHandler(String parameterName, Map<String, String> parameters) throws ServiceException {
		return null;
	}

}
