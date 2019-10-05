package com.technology.jef.server.form;

import com.technology.jef.server.exceptions.ServiceException;

@FunctionalInterface
public interface ClearListHandler {
	public void handle() throws ServiceException;
}
