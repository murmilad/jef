package com.technology.jef.server.form;

import static com.technology.jef.server.serialize.SerializeConstant.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.technology.jef.CurrentLocale;
import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.dto.RecordDto;
import com.technology.jef.server.exceptions.ServiceException;
import com.technology.jef.server.form.Field.CheckListener;
import com.technology.jef.server.form.Field.GetAttributesListener;
import com.technology.jef.server.form.Field.GetListInteractiveListener;
import com.technology.jef.server.form.Field.GetListListener;
import com.technology.jef.server.form.Field.GetValueListener;
import com.technology.jef.server.form.Field.IsActiveListener;
import com.technology.jef.server.form.Field.IsRequiredListener;
import com.technology.jef.server.form.Field.IsVisibleListener;
import com.technology.jef.server.form.FormData.Attribute;
import com.technology.jef.server.form.parameters.Parameters;
import com.technology.jef.server.form.parameters.Value;

/**
* Абстрактный класс для контроллеров интерфейсов
*/

public abstract class Form {

	
	   /**
	   * Данные формы, загруженные из БД
	   */
		private com.technology.jef.server.form.FormData formData = new com.technology.jef.server.form.FormData();
	
		/**
		 * Метод загрузки данных формы из БД для групповых форм
		 * @param id идентификатор анкеты
		 * @param groupId идентификатор группы для групповых форм
		 * @param parameters 
		 * @throws ServiceException
		 */
		public abstract void load(String id, String secondaryId, Parameters parameters) throws ServiceException;

	
		/**
		 * Метод загрузки списочных элементов параметра формы
		 * @param primaryId идентификатор анкеты
		 * @param parameterName наименование параметра формы 
		 * @return список элементов параметра формы
		 * @throws ServiceException
		 */
		protected List<OptionDto> getList(String primaryId, String parameterName, Parameters parameters)
				throws ServiceException {
			List<OptionDto> list = new LinkedList<OptionDto>();

			parameters.put("id", new Value("id",Objects.toString(primaryId, "")));

			if (getFieldsMap().containsKey(parameterName)) {
				GetListListener listListener = getFieldsMap().get(parameterName).getGetListListener();
				if (listListener == null) {
					throw new ServiceException("listListener is not definded for parameter: '" + parameterName + "'");
				}
				list = listListener.handle(parameterName, parameters);
			} else {
				throw new ServiceException("Parameter '" + parameterName + "' is not defined in getFieldsMap method.");
			}
			
			return list;
		}


		/**
		 * Метод интерактивной загрузки списочных элементов параметра формы
		 * @param primaryId идентификатор анкеты
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие на состав возвращаемого списка 
		 * @return список элементов параметра формы
		 * @throws ServiceException
		 */
		protected  List<OptionDto> getInteractiveList(String primaryId, String parameterName, Parameters parameters) throws ServiceException {

			List<OptionDto> list = new LinkedList<OptionDto>();

			parameters.put("id", new Value("id",Objects.toString(primaryId, "")));

			if (getFieldsMap().containsKey(parameterName)) {
				GetListInteractiveListener listInteractiveListener = getFieldsMap().get(parameterName).getGetListInteractiveListener();
				if (listInteractiveListener == null) {
					throw new ServiceException("listInteractiveListener is not definded for parameter: '" + parameterName + "'");
				}
				list = listInteractiveListener.handle(parameterName, parameters);
			} else {
				throw new ServiceException("Parameter '" + parameterName + "' is not defined in getFieldsMap method.");
			}
			
			return list;
		}

		/**
		 * Метод интерактивной загрузки значения параметра формы
		 * @param primaryId идентификатор анкеты
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие значение параметра формы 
		 * @return значение параметра формы
		 * @throws ServiceException
		 */
		protected  String getValue(String primaryId, String parameterName, Parameters parameters) throws ServiceException {

			String value = "";
			
			parameters.put("id", new Value("id",Objects.toString(primaryId, "")));

			if (getFieldsMap().containsKey(parameterName)) {
				GetValueListener getValueListener = getFieldsMap().get(parameterName).getGetValueListener();
				if (getValueListener == null) {
					throw new ServiceException("getValueListener is not definded for parameter: '" + parameterName + "'");
				}
				value = getValueListener.handle(parameterName, parameters);
			} else {
				throw new ServiceException("Parameter '" + parameterName + "' is not defined in getFieldsMap method.");
			}

			return value;
		}


		/**
		 * Метод интерактивной загрузки признака видимости параметра формы
		 * @param primaryId идентификатор анкеты
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие признак видимости параметра формы
		 * @return признак видимости True видим, False не видим
		 * @throws ServiceException
		 */

		protected  Boolean isVisible(String primaryId, String parameterName, Parameters parameters) throws ServiceException {

			Boolean isVisible = null;
			
			parameters.put("id", new Value("id",Objects.toString(primaryId, "")));
			
			if (getFieldsMap().containsKey(parameterName)) {
				IsVisibleListener isVisibleListener = getFieldsMap().get(parameterName).getIsVisibleListener();
				if (isVisibleListener == null) {
					if ("button_add".equals(parameterName) || "button_del".equals(parameterName) ) { // Methods for buttons is optional
						isVisible = true; // Buttons is visible by default
					} else {
						throw new ServiceException("isVisibleListener is not definded for parameter: '" + parameterName + "'");
					}
				}
				isVisible = isVisibleListener.handle(parameterName, parameters);
			} else {
				if ("button_add".equals(parameterName) || "button_del".equals(parameterName) ) { // Methods for buttons is optional
					isVisible = true; // Buttons is visible by default
				} else {
					throw new ServiceException("Parameter '" + parameterName + "' is not defined in getFieldsMap method.");
				}
			}
			
			return isVisible;
		}

		/**
		 * Метод интерактивной загрузки признака активности параметра формы
		 * @param primaryId идентификатор анкеты
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие на признак активности параметра формы 
		 * @return признак активности True активен, False не активен
		 * @throws ServiceException
		 */
		protected  Boolean isActive(String primaryId, String parameterName, Parameters parameters) throws ServiceException {
			Boolean isActive = null;

			parameters.put("id", new Value("id",Objects.toString(primaryId, "")));

			if (getFieldsMap().containsKey(parameterName)) {
				IsActiveListener isActiveListener = getFieldsMap().get(parameterName).getIsActiveListener();
				if (isActiveListener == null) {
					throw new ServiceException("isActiveListener is not definded for parameter: '" + parameterName + "'");
				}
				isActive = isActiveListener.handle(parameterName, parameters);
			} else {
				throw new ServiceException("Parameter '" + parameterName + "' is not defined in getFieldsMap method.");
			}
			
			return isActive;
		}


		/**
		 * Метод проверки параметра формы, который вызывается непосредственно перед сохранением
		 * @param primaryId идентификатор анкеты
		 * @param secondaryId данные о группе (если она множимая)
		 * @param parameterName наименование параметра на форме
		 * @param isRequired 
		 * @param parameters Параметры для проверки 
		 * @return список ошибок
		 * @throws ServiceException
		 */
		protected  List<String> checkParameter(String primaryId, String secondaryId, Value parameter,
				Parameters parameters) throws ServiceException {

			Boolean isRequired = parameter.getIsRequired();
			List<String> errors = new LinkedList<String>();

			parameters.put("id", new Value("id", Objects.toString(primaryId, "")));
			
			if (getFieldsMap().containsKey(parameter.getName())) {
				CheckListener checkListener = getFieldsMap().get(parameter.getName()).getCheckListener();
				if (checkListener != null) {
					List<String> localErrors = checkListener.handle(parameter.getName(), parameters);
					if (localErrors != null) {
						errors = localErrors; 
					};
				}
			}

			if (getFieldsMap().containsKey(parameter.getName())) {
				IsRequiredListener isRequiredListener = getFieldsMap().get(parameter.getName()).getIsRequiredListener();
				if (isRequiredListener != null) {
					Boolean localRequired = (Boolean) isRequiredListener.handle(parameter.getName(), parameters);
					if (localRequired != null) {
						isRequired = localRequired; 
					};
				}
			}

			if (isRequired && ("".equals(parameters.getValue(parameter.getName())) || "|".equals(parameters.getValue(parameter.getName())))) {
				errors.add(CurrentLocale.getInstance().getTextSource().getString("required_parameter"));
			}

			return errors;
		}

		/**
		 * Метод получения атрибутов параметра формы (видимость, доступность к редактированию)
		 * @param parameterName наименование параметра на форме
		 * @param parameters 
		 * @param id идентификатор анкеты
		 * @return список атрибутов параметра
		 * @throws ServiceException
		 */
		protected Map<Attribute, Boolean> getAttributes(String parameterName, Parameters parameters, String id) throws ServiceException {

			Map<Attribute, Boolean> attributes = new HashMap<Attribute, Boolean>();
			if (getFieldsMap().containsKey(parameterName)) {
				GetAttributesListener getAttributesListener = getFieldsMap().get(parameterName).getGetAttributesListener();
				if (getAttributesListener != null) {
					attributes = getAttributesListener.handle(parameterName, parameters, id);
				}
			}
			
			return attributes;
		}

		
		/**
		 * Метод проверки всей формы, который вызывается непосредственно перед сохранением
		 * @param primaryId идентификатор анкеты
		 * @param secondaryId данные о группе (если она множимая)
		 * @param parameters Параметры для проверки 
		 * @return список ошибок
		 * @throws ServiceException
		 */
		public Map<String, List<String>> checkForm(String primaryId, String secondaryId, Parameters parameters)  throws ServiceException {
			
			return new HashMap<String, List<String>>();
		}

	   /**
	   * Карта соответствия параметров текущего контроллера
	   * @return карта параметров <наименование в интерфейсе> - <наименование в базе данных>
	   */
		abstract public Map<String, Field> getFieldsMap(); 

		public com.technology.jef.server.form.FormData getFormData() {
			return formData;
		}

		public void setFormData(RecordDto formData) throws ServiceException {
			
			Map<String,Field> parametersMap = getFieldsMap();
			
			for (String interfaceFieldName : parametersMap.keySet()) {
				if (parametersMap.containsKey(interfaceFieldName) && parametersMap.get(interfaceFieldName) != null) {
					if (parametersMap.get(interfaceFieldName).getDaoValueField() != null) {
						Object fieldValue = formData.get(parametersMap.get(interfaceFieldName).getDaoValueField());
						if (parametersMap.get(interfaceFieldName).getDaoNameField() != null 
								&& (
										fieldValue != null && !fieldValue.toString().contains(PARAMETER_NAME_VALUE_SEPARATOR)
										|| fieldValue == null 
								)
						){
							Object fieldName = formData.get(parametersMap.get(interfaceFieldName).getDaoNameField());
							this.formData.putValue(interfaceFieldName, new Value(interfaceFieldName, (fieldValue != null ? fieldValue : "") + PARAMETER_NAME_VALUE_SEPARATOR + (fieldName != null ? fieldName : "")));
						} else {
							this.formData.putValue(interfaceFieldName, new Value(interfaceFieldName, fieldValue != null ? fieldValue.toString() : ""));
						}
					} else {
						this.formData.putValue(interfaceFieldName, new Value(interfaceFieldName));
					}
				} else {
					throw new ServiceException("Undeclared parameter '" + interfaceFieldName + "' for interface: " + this.getClass());
				}
				
			}


		}

		public static String getItemOrSpace (String data, Integer index) {
			String result = "";
			if (data != null && !"".equals(data) && !FIAS_CODE_NAME_SEPARATOR.equals(data)) {
				result = data.split("\\" + FIAS_CODE_NAME_SEPARATOR).length < index+1
						? ""
						: data.split("\\" + FIAS_CODE_NAME_SEPARATOR)[index];
			}
			return result;
		}

		/**
		 * Метод сохранения формы
		 * @param primaryId идентификатор анкеты
		 * @param secondaryId данные о группе (если она множимая)
		 * @param parameters Параметры для сохранения 
		 * @return TODO

		 * @throws ServiceException
		 */

		abstract public String saveForm(String primaryId, String secondaryId, Parameters parameters)  throws ServiceException;

		/**
		 * Удаление групповой формы
		 * @param primaryId идентификатор анкеты
		 * @param secondaryId данные о группе (если она множимая)
		 * @param parametersMap 
		 * @return TODO

		 * @throws ServiceException
		 */

		public void deleteForm(String primaryId, String secondaryId, Parameters parametersMap)  throws ServiceException {};

		protected RecordDto mapDaoParameters(Parameters parameters) {

			return mapDaoParameters(parameters, new RecordDto());
		}

		protected RecordDto mapDaoParameters(Parameters parameters, RecordDto daoParameters) {

			
			Map<String,Field> parametersMap = getFieldsMap();
			for (String name : parametersMap.keySet()) {
				if (parametersMap.get(name).getDaoValueField() != null && parameters.get(name) != null) {
					daoParameters.put(parametersMap.get(name).getDaoValueField(), parameters.getValue(name));
				}
			}

			return daoParameters;
		}

		/**
		 * Общая проверка данных, связанных с формой
		 * 
		 * @param primaryId идентификатор анкеты
		 * @return результаты проверки данных
		 * @throws ServiceException
		 */
		public Map<String, List<String>> checkInterface(String primaryId, Parameters parameters)  throws ServiceException {
			
			return new HashMap<String, List<String>>();
		}

		/**
		 * Получение списка идентификаторов если форма являетсся групповой
		 * 
		 * @param primaryId идентификатор анкеты
		 * @param parameters 
		 * @return список идентификаторов групп
		 * @throws ServiceException
		 */
		public List<String> getGroups(String primaryId, Parameters parameters)  throws ServiceException {
			
			return null;
		}

		protected static Map<String, Field> addIsVisible(String fieldRegex, IsVisibleListener listener, Map<String, Field> fieldsMap) {

			
			for (String isVisibleField: fieldsMap.keySet()) {
				if (isVisibleField.matches(fieldRegex)) {
					IsVisibleListener isVisibleListener = fieldsMap.get(isVisibleField).getIsVisibleListener();
		
					if (isVisibleListener != null) {
						fieldsMap.get(isVisibleField).isVisibleListener((String parameterName, Parameters parameters) -> {
							return isVisibleListener.handle(parameterName, parameters) && listener.handle(parameterName, parameters);
						});
					} else {
						fieldsMap.get(isVisibleField).isVisibleListener((String parameterName, Parameters parameters) -> {
							return listener.handle(parameterName, parameters);
						});
					}
				}
			}

			
			return fieldsMap;
		}	

		protected static Map<String, Field> addIsRequired(String fieldRegex, IsRequiredListener listener, Map<String, Field> fieldsMap) {

			
			for (String isRequiredField: fieldsMap.keySet()) {
				if (isRequiredField.matches(fieldRegex)) {
		
					fieldsMap.get(isRequiredField).isRequiredListener((String parameterName, Parameters parameters) -> {
						return listener.handle(parameterName, parameters);
					});
				}
			}

			
			return fieldsMap;
		}	

		protected static Map<String, Field> addCheck(String fieldRegex, CheckListener listener, Map<String, Field> fieldsMap) {

			
			for (String checkField: fieldsMap.keySet()) {
				if (checkField.matches(fieldRegex)) {
					CheckListener checkListener = fieldsMap.get(checkField).getCheckListener();
		
					fieldsMap.get(checkField).checkListener((String parameterName, Parameters parameters) -> {

						List<String> result = new LinkedList<String>();
						if (checkListener != null) {
							result = checkListener.handle(parameterName, parameters);
						}
						for (String error : listener.handle(parameterName, parameters)) {
							result.add(error);
						}
						return  result;
					});
				}
			}

			
			return fieldsMap;
		}	

}
