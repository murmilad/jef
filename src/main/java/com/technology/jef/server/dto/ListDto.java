package com.technology.jef.server.dto;

import java.util.List;

public class ListDto<D> extends StatusDto {

	private List<D> data;
	
	public ListDto(Integer status_code, List<D> data) {
		super(status_code);

		this.setData(data);
	}

	public ListDto(ErrorDto error) {
		super(error);
	}

	public List<D> getData() {
		return data;
	}

	public void setData(List<D> data) {
		this.data = data;
	}

}
