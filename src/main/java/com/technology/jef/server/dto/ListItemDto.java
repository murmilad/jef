package com.technology.jef.server.dto;



import java.util.ArrayList;
import java.util.List;


public class ListItemDto {

	private String id;
	private String name;
	private Boolean disabled;
	
	public ListItemDto(String id, String name) {
		this(id, name, false);
	}

	public ListItemDto(String id, String name, Boolean disabled) {
		this.setId(id);
		this.setName(name);
		this.setDisabled(disabled);
	}

	public ListItemDto(OptionDto item){
		this(item.getValue().toString(), item.getName(), item.containsKey("disabled") ? (Boolean) item.get("disabled") : false);
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

}
