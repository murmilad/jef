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
		 * Метод загрузки данных формы из БД
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * @throws ServiceException
		 */
		abstract public void load(Integer applicationId, Integer operatorId, Integer cityId) throws ServiceException;

		/**
		 * Метод загрузки списочных элементов параметра формы
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * @param parameterName наименование параметра формы 
		 * @return список элементов параметра формы
		 * @throws ServiceException
		 */
		public List<OptionDto> getList(Integer applicationId, Integer operatorId, Integer cityId, String parameterName) throws ServiceException{
			// TODO Auto-generated method stub
			return null;
		};

		/**
		 * Метод интерактивной загрузки списочных элементов параметра формы
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие на состав возвращаемого списка 
		 * @return список элементов параметра формы
		 * @throws ServiceException
		 */
		public List<OptionDto> getList(Integer applicationId, Integer operatorId, Integer cityId,String parameterName, Map<String,String> parameters) throws ServiceException{
			// TODO Auto-generated method stub
			return null;
		};

		/**
		 * Метод интерактивной загрузки значения параметра формы
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие значение параметра формы 
		 * @return значение параметра формы
		 * @throws ServiceException
		 */
		 public String getValue(Integer applicationId, Integer operatorId, Integer cityId, String parameterName, Map<String,String> parameters) throws ServiceException{
			// TODO Auto-generated method stub
			return null;
		};

		/**
		 * Метод интерактивной загрузки признака видимости параметра формы
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие признак видимости параметра формы
		 * @return признак видимости True видим, False не видим
		 * @throws ServiceException
		 */
		public Boolean isVisible(Integer applicationId, Integer operatorId, Integer cityId, String parameterName, Map<String,String> parameters) throws ServiceException{
			// TODO Auto-generated method stub
			return null;
		};

		/**
		 * Метод интерактивной загрузки признака активности параметра формы
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * @param parameterName наименование параметра формы 
		 * @param parameters Параметры, влияющие на признак активности параметра формы 
		 * @return признак активности True активен, False не активен
		 * @throws ServiceException
		 */
		public Boolean isActive(Integer applicationId, Integer operatorId, Integer cityId, String parameterName, Map<String,String> parameters) throws ServiceException{
			// TODO Auto-generated method stub
			return null;
		};
		
		/**
		 * Метод проверки параметра формы, который вызывается непосредственно перед сохранением
		 * @param parameterName наименование параметра на форме
		 * @param isRequired 
		 * @param applicationId идентификатор анкеты
		 * @param groupPrefix данные о группе (если она множимая)
		 * @param parameters Параметры для проверки 
		 * @return список ошибок
		 * @throws ServiceException
		 */
		public List<String> checkParameter(String parameterName, Boolean isRequired, Integer applicationId, String groupPrefix,
				Map<String, String> parameters)  throws ServiceException {

			List<String> errors = new LinkedList<String>();

			if (isRequired && "".equals(parameters.get(parameterName))) {
				errors.add("значение обязательного параметра не введено");
			}
			
			return errors;
		};

		
		/**
		 * Метод проверки всей формы, который вызывается непосредственно перед сохранением
		 * @param applicationId идентификатор анкеты
		 * @param groupPrefix данные о группе (если она множимая)
		 * @param parameters Параметры для проверки 
		 * @return список ошибок
		 * @throws ServiceException
		 */
		public Map<String, List<String>> checkForm(Integer applicationId, Integer operatorId, String groupPrefix, Map<String, String> parameters)  throws ServiceException {
			
			return new HashMap<String, List<String>>();
		}

	   /**
	   * Карта соответствия параметров текущего контроллера
	   * @return карта параметров <наименование в интерфейсе> - <наименование в базе данных>
	   */
		abstract public Map<String,String> getParametersMap(); 

		public FormData getFormData() {
			return formData;
		}

		public void setFormData(RecordDto formData) {
			
			Map<String,String> parametersMap = getParametersMap();
			
			for (String interfaceFieldName : parametersMap.keySet()) {
				Object fieldValue = formData.get(parametersMap.get(interfaceFieldName));

				Pattern pattern = Pattern.compile("^(.+)_id$");
				Matcher matcher = pattern.matcher(parametersMap.get(interfaceFieldName));

				if (matcher.find() && formData.containsKey(matcher.group(1) + "_name")) {
					String value = PARAMETER_NAME_VALUE_SEPARATOR;
					if (formData.get(matcher.group(1) + "_name") != null) {
						value = formData.get(parametersMap.get(interfaceFieldName)) +  PARAMETER_NAME_VALUE_SEPARATOR + formData.get(matcher.group(1) + "_name");
					} else if (formData.get(matcher.group(1) + "_other") != null) {
						value = "other" + PARAMETER_NAME_VALUE_SEPARATOR + "Иное";
					}

					this.formData.putValue(interfaceFieldName, value);
				} else {
				
					this.formData.putValue(interfaceFieldName, fieldValue != null ? fieldValue.toString() : "");
				}
				
			}


		}

		public static String getItemOrSpace (String data, Integer index) {
			String result = "";
			if (!FIAS_CODE_NAME_SEPARATOR.equals(data)) {
				result = data.split("\\" + FIAS_CODE_NAME_SEPARATOR)[index];
			}
			return result;
		}

		/**
		 * Метод сохранения формы
		 * @param applicationId идентификатор анкеты
		 * @param groupPrefix данные о группе (если она множимая)
		 * @param parameters Параметры для сохранения 

		 * @throws ServiceException
		 */

		abstract public void saveForm(Integer applicationId, Integer operatorId, String iPAddress, String groupPrefix, Map<String, String> parameters)  throws ServiceException;
		
		protected RecordDto mapDaoParameters(Map<String, String> parameters) {

			RecordDto daoParameters = new RecordDto();
			
			Map<String,String> parametersMap = getParametersMap();
			for (String name : parametersMap.keySet()) {
				daoParameters.put(parametersMap.get(name), parameters.get(name));
			}

			return daoParameters;
		}

		/**
		 * Метод получения атрибутов параметра формы (видимость, доступность к редактированию)
		 * @param parameterName наименование параметра на форме
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * 
		 * @return список атрибутов параметра
		 * @throws ServiceException
		 */
		public Map<Attribute, Boolean> getAttributes(String parameterName, Integer applicationId, Integer operatorId,
				Integer cityId) {

			return new HashMap<Attribute, Boolean>();
		}


		/**
		 * Общая проверка данных, связанных с формой
		 * 
		 * @param applicationId идентификатор анкеты
		 * @param operatorId идентификатор пользователя интерфейса консультанта
		 * @param cityId идентификатор города интерфейса консультанта
		 * 
		 * @return результаты проверки данных
		 * @throws ServiceException
		 */
		public Map<String, List<String>> checkInterface(Integer applicationId, Integer operatorId, Integer cityId, Map<String,String> parameters)  throws ServiceException {
			
			return new HashMap<String, List<String>>();
		}

}
