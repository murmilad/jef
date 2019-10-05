package com.technology.jef.server.form;

import java.util.Map;

import com.technology.jef.server.exceptions.ServiceException;

@FunctionalInterface
public interface FieldHandler<T, F>  {
	public abstract T handle (String parameterName, F parameters) throws ServiceException;
}
