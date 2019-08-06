package com.technology.jef.server.dto;

import java.util.HashMap;


public class OptionDto extends HashMap<String, Object>{

	private static final long serialVersionUID = 1L;
	
	public static final String OPTION_NAME = "name";
	public static final String OPTION_VALUE = "value";

	public OptionDto() {
	}
	  
	public OptionDto(String name, Object value) {
	  setName(name);
	  setValue(value);    
	}

	@SuppressWarnings("unchecked")
	  public static <X> X getValue(Object option) {
	    if (option instanceof OptionDto) {
	      return (X)((OptionDto)option).getValue();
	    }

	    return (X) option;
	  }

	  public Object getValue() {
		  return super.get(OPTION_VALUE);
	  }

	  public String getName() {
		  Object name = super.get(OPTION_NAME);
		    
		  return name == null ? null : name.toString();
	  }
		  
	  public void setValue(Object value) {
		  put(OPTION_VALUE, value);
	  }
		  
	  public void setName(String name) {
		  put(OPTION_NAME, name);
	  }

}
