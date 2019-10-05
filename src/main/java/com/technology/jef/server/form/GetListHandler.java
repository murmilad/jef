package com.technology.jef.server.form;

import java.util.List;

import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.exceptions.ServiceException;

@FunctionalInterface
public interface GetListHandler {
	public List<OptionDto> handle() throws ServiceException;

}
