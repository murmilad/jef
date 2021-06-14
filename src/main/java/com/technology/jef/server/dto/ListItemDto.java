package com.technology.jef.server.dto;



import java.util.ArrayList;
import java.util.List;


public class ListItemDto {

	private String id;
	private String name;
	private Boolean disabled;
	private String error;
	
	public ListItemDto(String id, String name) {
		this(id, name, false, null);
	}

	public ListItemDto(String id, String name, Boolean disabled, String error) {
		this.setId(id);
		this.setName(name);
		this.setDisabled(disabled);
		this.setError(error);
	}

	public ListItemDto(OptionDto item){
		this(item.getValue() == null ? "" : item.getValue().toString(), 
				item.getName(), 
				item.containsKey("disabled") ? (Boolean) item.get("disabled") : false,
				item.containsKey("error") ? (String)item.get("error") : null
		);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	  public static List<ListItemDto> asList(List<OptionDto> listData) {
		    
		    List<ListItemDto> result = new ArrayList<ListItemDto>();
		    for (OptionDto item : listData) {
		      ListItemDto type = new ListItemDto(item);
		      result.add(type);
		    }
		    return result;
		  }

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
