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
		 * @param parameters 
		 * @throws ServiceException
		 */
		public abstract void load(Integer id, Integer groupId, Map<String, String> parameters) throws ServiceException;

	
		/**
		 * Метод загрузки списочных элементов параметра формы
		 * @param primaryId идентификатор анкеты
		 * @param parameterName наименование параметра формы 
		 * @return список элементов параметра формы
		 * @throws ServiceException
		 */
		protected List<OptionDto> getList(Integer primaryId, String parameterName, Map<String, String> parameters)
				throws ServiceException {
			List<OptionDto> list = new LinkedList<OptionDto>();

			parameters.put("id", Objects.toString(primaryId, ""));

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
		protected  List<OptionDto> getInteractiveList(Integer primaryId, String parameterName, Map<String, String> parameters) throws ServiceException {

			List<OptionDto> list = new LinkedList<OptionDto>();

			parameters.put("id", Objects.toString(primaryId, ""));

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
		protected  String getValue(Integer primaryId, String parameterName, Map<String, String> parameters) throws ServiceException {

			String value = "";
			
			parameters.put("id", Objects.toString(primaryId, ""));

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

		protected  Boolean isVisible(Integer primaryId, String parameterName, Map<String, String> parameters) throws ServiceException {

			Boolean isVisible = null;
			
			parameters.put("id", Objects.toString(primaryId, ""));
			
			if (getFieldsMap().containsKey(parameterName)) {
				IsVisibleListener isVisibleListener = getFieldsMap().get(parameterName).getIsVisibleListener();
				if (isVisibleListener == null) {
					throw new ServiceException("isVisibleListener is not definded for parameter: '" + parameterName + "'");
				}
				isVisible = isVisibleListener.handle(parameterName, parameters);
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

			parameters.put("id", Objects.toString(primaryId, ""));

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
		protected  List<String> checkParameter(Integer primaryId, Integer secondaryId, String parameterName, Boolean isRequired,
				Map<String, String> parameters) throws ServiceException {

			List<String> errors = new LinkedList<String>();

			parameters.put("id", Objects.toString(primaryId, ""));
			
			if (getFieldsMap().containsKey(parameterName)) {
				CheckListener checkListener = getFieldsMap().get(parameterName).getCheckListener();
				if (checkListener != null) {
					List<String> localErrors = checkListener.handle(parameterName, parameters);
					if (localErrors != null) {
						errors = localErrors; 
					};
				}
			}

			if (getFieldsMap().containsKey(parameterName)) {
				IsRequiredListener isRequiredListener = getFieldsMap().get(parameterName).getIsRequiredListener();
				if (isRequiredListener != null) {
					Boolean localRequired = (Boolean) isRequiredListener.handle(parameterName, parameters);
					if (localRequired != null) {
						isRequired = localRequired; 
					};
				}
			}

			if (isRequired && "".equals(parameters.get(parameterName))) {
				errors.add(CurrentLocale.getInstance().getTextSource().getString("required_parameter"));
			}

			return errors;
		}

		/**
		 * Метод получения атрибутов параметра формы (видимость, доступность к редактированию)
		 * @param parameterName наименование параметра на форме
		 * @param id идентификатор анкеты
		 * @return список атрибутов параметра
		 * @throws ServiceException
		 */
		protected Map<Attribute, Boolean> getAttributes(String parameterName, Integer id) throws ServiceException {

			Map<Attribute, Boolean> attributes = new HashMap<Attribute, Boolean>();
			if (getFieldsMap().containsKey(parameterName)) {
				GetAttributesListener getAttributesListener = getFieldsMap().get(parameterName).getGetAttributesListener();
				if (getAttributesListener != null) {
					attributes = getAttributesListener.handle(parameterName, id);
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
		
							this.formData.putValue(interfaceFieldName, value != null ? value.toString() : "");
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

		abstract public Integer saveForm(Integer primaryId, Integer secondaryId, Map<String, String> parameters)  throws ServiceException;

		/**
		 * Удаление групповой формы
		 * @param primaryId идентификатор анкеты
		 * @param secondaryId данные о группе (если она множимая)
		 * @param parametersMap 
		 * @return TODO

		 * @throws ServiceException
		 */

		public void deleteForm(Integer primaryId, Integer secondaryId, Map<String, String> parametersMap)  throws ServiceException {};

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
		 * @param parameters 
		 * @return список идентификаторов групп
		 * @throws ServiceException
		 */
		public List<String> getGroups(Integer primaryId, Map<String, String> parameters)  throws ServiceException {
			
			return null;
		}

}
