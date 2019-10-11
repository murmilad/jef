package com.technology.jef.server.form;

import static com.technology.jef.server.serialize.SerializeConstant.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.dto.RecordDto;
import com.technology.jef.server.exceptions.ServiceException;
import com.technology.jef.server.form.FormData.Attribute;

/**
* Абстрактный класс для контроллеров интерфейсов
*/

public abstract class Form {

	
	   /**
	   * Данные формы, загруженные из БД
	   */
		private FormData formData = new FormData();
	
		/**
		 * Метод загрузки данных формы из БД для групповых форм
		 * @param id идентификатор анкеты
		 * @param groupId идентификатор группы для групповых форм
		 * @throws ServiceException
		 */
		public abstract void load(Integer id, Integer groupId) throws ServiceException;

	
		/**
		 * Метод загрузки списочных элементов параметра формы
		 * @param primaryId идентификатор анкеты
		 * @param parameterName наименование параметра формы 
		 * @return список элементов параметра формы
		 * @throws ServiceException
		 */
		protected List<OptionDto> getList(Integer primaryId, String parameterName)
				throws ServiceException {
			List<OptionDto> list = new LinkedList<OptionDto>();

			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("id", String.valueOf(primaryId));

			if (getFieldsMap().containsKey(parameterName)) {
				list = getFieldsMap().get(parameterName).getListHandler(parameterName, parameters);
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
		protected  List<OptionDto> getList(Integer primaryId, String parameterName, Map<String, String> parameters) throws ServiceException {

			List<OptionDto> list = new LinkedList<OptionDto>();

			parameters.put("id", String.valueOf(primaryId));

			if (getFieldsMap().containsKey(parameterName)) {
				list = getFieldsMap().get(parameterName).getListInteractiveHandler(parameterName, parameters);
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
		protected  String getValue(Integer primaryId, String parameterName, Map<String, String> parameters) throws ServiceException {

			String value = "";
			
			parameters.put("id", String.valueOf(primaryId));

			if (getFieldsMap().containsKey(parameterName)) {
				value = getFieldsMap().get(parameterName).getValueHandler(parameterName, parameters);
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

		protected  Boolean isVisible(Integer primaryId, String parameterName, Map<String, String> parameters) throws ServiceException {

			Boolean isVisible = null;
			
			parameters.put("id", String.valueOf(primaryId));
			
			if (getFieldsMap().containsKey(parameterName)) {
				isVisible = getFieldsMap().get(parameterName).isVisibleHandler(parameterName, parameters);
			} else {
				throw new ServiceException("Parameter '" + parameterName + "' is not defined in getFieldsMap method.");
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
		protected  Boolean isActive(Integer primaryId, String parameterName, Map<String, String> parameters) throws ServiceException {
			Boolean isActive = null;

			parameters.put("id", String.valueOf(primaryId));

			if (getFieldsMap().containsKey(parameterName)) {
				isActive = getFieldsMap().get(parameterName).isActiveHandler(parameterName, parameters);
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
		protected  List<String> checkParameter(Integer primaryId, Integer secondaryId, String parameterName, Boolean isRequired,
				Map<String, String> parameters) throws ServiceException {

			List<String> errors = new LinkedList<String>();

			parameters.put("id", String.valueOf(primaryId));
			
			if (getFieldsMap().containsKey(parameterName)) {
				List<String> localErrors = getFieldsMap().get(parameterName).checkHandler(parameterName, parameters);
				if (localErrors != null) {
					errors = localErrors; 
				};
			}

			if (getFieldsMap().containsKey(parameterName)) {
				Boolean localRequired = (Boolean) getFieldsMap().get(parameterName).isRequiredHandler(parameterName, parameters);
				if (localRequired != null) {
					isRequired = localRequired; 
				};
			}

			if (isRequired && "".equals(parameters.get(parameterName))) {
				errors.add("Значение обязательного параметра не введено");
			}

			return errors;
		}


		
		/**
		 * Метод проверки всей формы, который вызывается непосредственно перед сохранением
		 * @param primaryId идентификатор анкеты
		 * @param secondaryId данные о группе (если она множимая)
		 * @param parameters Параметры для проверки 
		 * @return список ошибок
		 * @throws ServiceException
		 */
		public Map<String, List<String>> checkForm(Integer primaryId, Integer secondaryId, Map<String, String> parameters)  throws ServiceException {
			
			return new HashMap<String, List<String>>();
		}

	   /**
	   * Карта соответствия параметров текущего контроллера
	   * @return карта параметров <наименование в интерфейсе> - <наименование в базе данных>
	   */
		abstract public Map<String, Field> getFieldsMap(); 

		public FormData getFormData() {
			return formData;
		}

		public void setFormData(RecordDto formData) throws ServiceException {
			
			Map<String,Field> parametersMap = getFieldsMap();
			
			for (String interfaceFieldName : parametersMap.keySet()) {
				if (parametersMap.containsKey(interfaceFieldName) && parametersMap.get(interfaceFieldName) != null) {
					if (parametersMap.get(interfaceFieldName).getFieldName() != null) {
						Object fieldValue = formData.get(parametersMap.get(interfaceFieldName).getFieldName());
		
						Pattern pattern = Pattern.compile("^(.+)_id$");
						Matcher matcher = pattern.matcher(parametersMap.get(interfaceFieldName).getFieldName());
		
						if (matcher.find() && formData.containsKey(matcher.group(1) + "_name")) {
							String value = PARAMETER_NAME_VALUE_SEPARATOR;
							if (formData.get(matcher.group(1) + "_name") != null) {
								value = formData.get(parametersMap.get(interfaceFieldName).getFieldName()) +  PARAMETER_NAME_VALUE_SEPARATOR + formData.get(matcher.group(1) + "_name");
							} else if (formData.get(matcher.group(1) + "_other") != null) {
								value = "other" + PARAMETER_NAME_VALUE_SEPARATOR + "Иное";
							}
		
							this.formData.putValue(interfaceFieldName, value);
						} else {
						
							this.formData.putValue(interfaceFieldName, fieldValue != null ? fieldValue.toString() : "");
						}
					}
				} else {
					throw new ServiceException("Undeclared parameter '" + interfaceFieldName + "' for interface: " + this.getClass());
				}
				
			}


		}

		public static String getItemOrSpace (String data, Integer index) {
			String result = "";
			if (data != null && !"".equals(data) && !FIAS_CODE_NAME_SEPARATOR.equals(data)) {
				result = data.split("\\" + FIAS_CODE_NAME_SEPARATOR)[index];
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

		abstract public Integer saveForm(Integer primaryId, Integer secondaryId, String iPAddress, Map<String, String> parameters)  throws ServiceException;

		/**
		 * Удаление групповой формы
		 * @param primaryId идентификатор анкеты
		 * @param secondaryId данные о группе (если она множимая)
		 * @return TODO

		 * @throws ServiceException
		 */

		public void deleteForm(Integer primaryId, Integer secondaryId, String iPAddress)  throws ServiceException {};

		protected RecordDto mapDaoParameters(Map<String, String> parameters) {

			RecordDto daoParameters = new RecordDto();
			
			Map<String,Field> parametersMap = getFieldsMap();
			for (String name : parametersMap.keySet()) {
				if (parametersMap.get(name).getFieldName() != null) {
					daoParameters.put(parametersMap.get(name).getFieldName(), parameters.get(name));
				}
			}

			return daoParameters;
		}

		/**
		 * Метод получения атрибутов параметра формы (видимость, доступность к редактированию)
		 * @param parameterName наименование параметра на форме
		 * @param id идентификатор анкеты
		 * @return список атрибутов параметра
		 * @throws ServiceException
		 */
		public Map<Attribute, Boolean> getAttributes(String parameterName, Integer id) {

			return new HashMap<Attribute, Boolean>();
		}


		/**
		 * Общая проверка данных, связанных с формой
		 * 
		 * @param primaryId идентификатор анкеты
		 * @return результаты проверки данных
		 * @throws ServiceException
		 */
		public Map<String, List<String>> checkInterface(Integer primaryId, Map<String,String> parameters)  throws ServiceException {
			
			return new HashMap<String, List<String>>();
		}

		/**
		 * Получение списка идентификаторов если форма являетсся групповой
		 * 
		 * @param primaryId идентификатор анкеты
		 * @return список идентификаторов групп
		 * @throws ServiceException
		 */
		public List<String> getGroups(Integer primaryId)  throws ServiceException {
			
			return null;
		}

}
