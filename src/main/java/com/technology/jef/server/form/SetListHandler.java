package com.technology.jef.server.form;

import com.technology.jef.server.exceptions.ServiceException;

@FunctionalInterface
public interface SetListHandler {
	public void handle(String id) throws ServiceException;
}
