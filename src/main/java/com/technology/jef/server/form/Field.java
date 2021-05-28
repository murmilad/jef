package com.technology.jef.server.form;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.exceptions.ServiceException;
import com.technology.jef.server.form.FormData.Attribute;
import com.technology.jef.server.form.parameters.Parameters;
import com.technology.jef.server.form.parameters.Value;

public class Field {

	// Listeners
	
	@FunctionalInterface
	public interface GetValueListener {
		public abstract String handle (String parameterName, Parameters parameters) throws ServiceException;

	}
	private GetValueListener getValueListener = null;
	public void getValueListener(GetValueListener listener) {
		getValueListener = listener;
	}
	public GetValueListener getGetValueListener() {
		return getValueListener;
	}


	@FunctionalInterface
	public interface IsActiveListener {
		public abstract Boolean handle (String parameterName, Parameters parameters) throws ServiceException;

	}
	private IsActiveListener isActiveListener = null;
	public void isActiveListener(IsActiveListener listener) {
		isActiveListener = listener;
	}
	public IsActiveListener getIsActiveListener() {
		return isActiveListener;
	}

	
	@FunctionalInterface
	public interface IsVisibleListener {
		public abstract Boolean handle (String parameterName, Parameters parameters) throws ServiceException;

	}
	private IsVisibleListener isVisibleListener = null;
	public void isVisibleListener(IsVisibleListener listener) {
		isVisibleListener = listener;
	}
	public IsVisibleListener getIsVisibleListener() {
		return isVisibleListener;
	}

	
	@FunctionalInterface
	public interface GetListListener {
		public abstract List<OptionDto> handle (String parameterName, Parameters parameters) throws ServiceException;

	}
	private GetListListener getListListener = null;
	public void getListListener(GetListListener listener) {
		getListListener = listener;
	}
	public GetListListener getGetListListener() {
		return getListListener;
	}
	

	@FunctionalInterface
	public interface GetListInteractiveListener {
		public abstract List<OptionDto> handle (String parameterName, Parameters parameters) throws ServiceException;

	}
	private GetListInteractiveListener getListInteractiveListener = null;
	public void getListInteractiveListener(GetListInteractiveListener listener) {
		getListInteractiveListener = listener;
	}
	public GetListInteractiveListener getGetListInteractiveListener() {
		return getListInteractiveListener;
	}

	
	@FunctionalInterface
	public interface IsRequiredListener {
		public abstract Boolean handle (String parameterName, Parameters parameters) throws ServiceException;

	}
	private IsRequiredListener isRequiredListener = null;
	public void isRequiredListener(IsRequiredListener listener) {
		isRequiredListener = listener;
	}
	public IsRequiredListener getIsRequiredListener() {
		return isRequiredListener;
	}

	
	@FunctionalInterface
	public interface CheckListener {
		public abstract List<String> handle (String parameterName, Parameters parameters) throws ServiceException;

	}
	private CheckListener checkListener = null;
	public void checkListener(CheckListener listener) {
		checkListener = listener;
	}
	public CheckListener getCheckListener() {
		return checkListener;
	}


	/**
	 * Метод получения атрибутов параметра формы (видимость, доступность к редактированию)
	 * @param parameterName наименование параметра на форме
	 * @param id идентификатор анкеты
	 * @return список атрибутов параметра
	 * @throws ServiceException
	 */
	
	@FunctionalInterface
	public interface GetAttributesListener {
		public abstract Map<Attribute, Boolean> handle (String parameterName, Parameters parameters, String primaryId) throws ServiceException;

	}
	private GetAttributesListener getAttributesListener = null;
	public void getAttributesListener(GetAttributesListener listener) {
		getAttributesListener = listener;
	}
	public GetAttributesListener getGetAttributesListener() {
		return getAttributesListener;
	}

	
	
	private String daoValueField = null;
	private String daoNameField = null;

	public Field(String daoValueField, String daoNameField) {
		setDaoNameField(daoNameField);
		setDaoValueField(daoValueField);
	}
	
	public Field(String daoValueField) {
		setDaoValueField(daoValueField);
	}

	public Field() {
		setDaoValueField(null);
	}

	public String getDaoValueField() {
		return daoValueField;
		
	}

	public void setDaoValueField(String daoValueField) {
		this.daoValueField = daoValueField;
	}
	public String getDaoNameField() {
		return daoNameField;
	}
	public void setDaoNameField(String daoNameField) {
		this.daoNameField = daoNameField;
	}

}
