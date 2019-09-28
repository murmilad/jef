package com.technology.jef.server.dto;



import java.util.ArrayList;
import java.util.List;


public class ListItemDto {

	private String id;
	private String name;
	
	public ListItemDto(String id, String name) {
		this.setId(id);
		this.setName(name);
	}
	
	public ListItemDto(OptionDto item){
		this(item.getValue().toString(), item.getName());
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

}
