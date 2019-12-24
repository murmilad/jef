package com.technology.jef.server.form;

import java.util.HashMap;
import java.util.Map;

import com.technology.jef.server.exceptions.ServiceException;

@FunctionalInterface
public interface FieldHandler  {
	public abstract String handle (String parameterName, Map parameters) throws ServiceException;
}
