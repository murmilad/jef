package com.technology.jef.server.dto;

import java.util.HashMap;


public class RecordDto extends HashMap<String, Object>{

	private static final long serialVersionUID = 1L;
	
	public <X> X get(String key) {
		return (X) super.get(key);
	}

}
