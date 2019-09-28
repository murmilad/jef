package com.technology.jef.server.form;

import static com.technology.jef.server.WebServiceConstant.SERVICE_STATUS_OK;
import static com.technology.jef.server.serialize.SerializeConstant.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.technology.jef.server.dto.FormDto;
import com.technology.jef.server.dto.ListItemDto;
import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.dto.ResultDto;
import com.technology.jef.server.exceptions.ServiceException;
import com.technology.jef.server.form.parameters.FormParameters;
import com.technology.jef.server.form.parameters.Parameter;

public class Service<F extends FormFactory> {

	F factory = null;

	public Service(F factory) {
		this.factory = factory;
	} 

	private interface Handler {
		void handle(FormParameters formParameters, ResultDto result) throws ServiceException;
	}

	/**
	 * Получение параметров формы интерфейса консультанта
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param operatorId идентификатор пользователя интерфейса консультанта
	 * @param cityId идентификатор города интерфейса консультанта
	 * @param parameters TODO
	 * @return список групп и параметров формы
	 * @throws ServiceException
	 */

	public FormDto getFormData(Integer applicationId, String formApi, Integer operatorId, Integer cityId, Map<String, String> parameters)
			throws ServiceException {
		
		FormDto fromResult;
		
		Form form = factory.getForm(formApi);
		form.load(applicationId, operatorId, cityId);
		
		for(String parameterName : form.getFormData().getValues().keySet()) {
			if (!"form".equals(parameterName)) {
				form.getFormData().putAttributes(parameterName, form.getAttributes(parameterName, applicationId, operatorId, cityId));
			}
		}
		
		fromResult = new FormDto(SERVICE_STATUS_OK, form.getFormData());
		
		fromResult.setResult(checkInterface(applicationId, operatorId, cityId, formApi, form.getFormData().getValues()));

		return fromResult;
	}

	
	/**
	 * Сохранение параметров формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param cityId идентификатор города интерфейса консультанта
	 * @param parameters параметры, которые необходимо обработать
	 * 
	 * @return результат выполнения операции
	 * @throws ServiceServiceException
	 */

	public ResultDto setFormData(Integer applicationId, Integer cityId, Integer operatorId, String iPaddress, String parameters)
			throws ServiceException {

		ResultDto result = null;

		Map<String,String> sourceParametersMap = new HashMap<String,String>();
		Map<String, FormParameters> formsMap = new HashMap<String, FormParameters>();
		List<FormParameters> formsList = new LinkedList<FormParameters>();

		Pattern prefixPattern = Pattern.compile("^api_(\\w+)(?:"+GROUP_SEPARATOR+"(\\w+))?$");
		Matcher prefixMatcher;

		// создаем карту API по параметрам
		for (String parameter : parameters.split(PARAMETER_SEPARATOR)) {
			String[] nameValue = parameter.split(PARAMETER_NAME_VALUE_SEPARATOR);
			String name = nameValue.length > 0 ? nameValue[0] : "";
			String value = nameValue.length > 1 ? nameValue[1] : "";

			prefixMatcher = prefixPattern.matcher(name);
			
			if (prefixMatcher.matches()) {
				formsMap.put(String.valueOf(value), new FormParameters(String.valueOf(value)));
			}

			sourceParametersMap.put(name, value);
		}

		Pattern apiPattern = Pattern.compile("^(?:(api|parrent_api)_)?(\\w+)("+GROUP_SEPARATOR+"(\\w+))?$");
		Matcher apiMatcher;

		// заполняем карту API данными форм по параметрам
		for (String key : sourceParametersMap.keySet()) {
			apiMatcher = apiPattern.matcher(key);
			if (apiMatcher.matches()) {
				String apiName = sourceParametersMap.get("api_" + apiMatcher.group(2) + (apiMatcher.group(3) != null ? apiMatcher.group(3) : ""));
				if (apiName != null) {
					String currentForm = String.valueOf(apiName); 
					if ("parrent_api".equals(apiMatcher.group(1))) {
						if (!"".equals(sourceParametersMap.get(key))) {
							formsMap.get(currentForm).setParrentApi(String.valueOf(sourceParametersMap.get(key)));
						}
					} else if (apiMatcher.group(1) == null){
						formsMap.get(currentForm).addParameter(apiMatcher.group(2), sourceParametersMap.get(key), "1".equals(sourceParametersMap.get("required_" + key)), apiMatcher.group(4) != null ? apiMatcher.group(4) : "");
					}
				}
			}
		}

		// Формируем дерево вызовов API переберая карту
		for (String formKey : formsMap.keySet()) {
			if (formsMap.get(formKey).getParrentApi() == null) {
				formsList.add(formsMap.get(formKey));
			} else {
				if (formsMap.containsKey(formsMap.get(formKey).getParrentApi())) {
					formsMap.get(formsMap.get(formKey).getParrentApi()).addChildren(formsMap.get(formKey));
				} else {
					formsList.add(formsMap.get(formKey));
				}
			}
		}

		
		result = new ResultDto(SERVICE_STATUS_OK);

		// Проверяем параметры формы
		for (FormParameters parrentFormParameters : formsList) {
			operateFormParameters(parrentFormParameters, result, new Handler() {
				public void handle(FormParameters formParameters, ResultDto result) throws ServiceException {
					for (String formPrefix : formParameters.getParameters().keySet()) {
						result.appendResult(checkFormData(applicationId, operatorId, cityId, formPrefix, formParameters.getCurrentApi(), formParameters.getFormParameters(formPrefix), formParameters.getInputParameters(formPrefix)), !"".equals(formPrefix) 
								? GROUP_SEPARATOR + formPrefix
								: ""
						);
					}
				}
			});
		}

		// Сохраняем параметры формы в случае если нет ошибок на форме
		if (result.getErrors().getFormErrors().size() == 0 && result.getErrors().getParametersErrors().isEmpty()) {
			for (FormParameters parrentFormParameters : formsList) {
				operateFormParameters(parrentFormParameters, result, new Handler() {
					public void handle(FormParameters formParameters, ResultDto result) throws ServiceException {
						for (String formPrefix : formParameters.getParameters().keySet()) {
							saveFormData(applicationId, operatorId, iPaddress, formPrefix, formParameters.getCurrentApi(), formParameters.getInputParameters(formPrefix));
						}
					}
				});
			}
			
		}

		// Проверяем сущность, связанную с формой
		for (FormParameters parrentFormParameters : formsList) {
			operateFormParameters(parrentFormParameters, result, new Handler() {
				public void handle(FormParameters formParameters, ResultDto result) throws ServiceException {
					for (String formPrefix : formParameters.getParameters().keySet()) {
						result.appendResult(checkInterface(applicationId, operatorId, cityId, formParameters.getCurrentApi(), formParameters.getInputParameters(formPrefix)), !"".equals(formPrefix) 
								? GROUP_SEPARATOR + formPrefix
								: ""
						);
					}
				}
			});
		}

		return result;
	}		

	
	// Рекурсивная функция обхода дерева форм (для тех интерфейсов где требуется определенная последовательность проверок/сохранения форм)
	private void operateFormParameters(FormParameters formParameters, ResultDto result, Handler handler) throws ServiceException {
		handler.handle(formParameters, result);
		for (FormParameters childFormParameters : formParameters.getChildren()) {
			operateFormParameters(childFormParameters, result, handler);
		}
	}

	/**
	 * Проверка параметров формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameters параметры, которые необходимо обработать
	 * @param checkParametersMap 
	 * 
	 * @return результаты выполнения операции
	 * @throws ServiceException
	 */
	
	public ResultDto checkFormData(Integer applicationId, Integer operatorId, Integer cityId, String groupPrefix, String formApi, List<Parameter> parameters, Map<String, String> checkParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		Map<String,List<String>> parameterErrors = new HashMap<String,List<String>>(); 
		// Добавляем ошибки связанные с заполнением интерфейса 
		for (Parameter parameter : parameters) {
			List<String> result = form.checkParameter(parameter.getName(), parameter.getIsRequired(), applicationId, groupPrefix, checkParametersMap);
			if (result != null && result.size() > 0) {
				parameterErrors.put(parameter.getName(), result);
			}
		}

		List<String> formErrors = new LinkedList<String>();
		Map<String, List<String>> formParameterErrors = form.checkForm(applicationId, operatorId, groupPrefix, checkParametersMap);
		
		for (String parameterName: formParameterErrors.keySet()){
			if ("form".equals(parameterName) || !form.getParametersMap().containsKey(parameterName)) {
				formErrors.addAll(formParameterErrors.get(parameterName));
			} else {
				parameterErrors.put(parameterName, formParameterErrors.get(parameterName));
			}
		}

		return new ResultDto(formErrors != null ? formErrors : new LinkedList<String>(), parameterErrors);
	}

	/**
	 * Сохранение параметров формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param saveParametersMap 
	 * 
	 * @throws ServiceException
	 */

	public void saveFormData(Integer applicationId, Integer operatorId, String iPAddress, String groupPrefix, String formApi, Map<String, String> saveParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		form.saveForm(applicationId, operatorId, iPAddress, groupPrefix, saveParametersMap);
	}


	/**
	 * Получение списка элементов для параметра формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param operatorId идентификатор пользователя интерфейса консультанта
	 * @param cityId идентификатор города интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * 
	 * @return список элементов параметра формы
	 * @throws ServiceException
	 */


	public List<ListItemDto> getListData(Integer applicationId, String formApi, Integer operatorId, Integer cityId, String parameterName)
			throws ServiceException {
		
		
		
		Form form = factory.getForm(formApi);
		
		List<OptionDto> list = form.getList(applicationId, operatorId, cityId, parameterName);
		if (list == null) {
			 throw new ServiceException(form.getClass() + " getList(applicationId, operatorId, cityId, parameterName) function is not definded for parameter: '" + parameterName + "'");
		}
		return ListItemDto.asList(list);
	}

	/**
	 * Интерактивное получение списка элементов для параметра формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param operatorId идентификатор пользователя интерфейса консультанта
	 * @param cityId идентификатор города интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * 
	 * @return список элементов параметра формы
	 * @throws ServiceException
	 */

	public List<ListItemDto> getListData(Integer applicationId, String formApi, Integer operatorId, Integer cityId,
			String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);

		List<OptionDto> list = form.getList(applicationId, operatorId, cityId, parameterName);
		if (list == null) {
			 throw new ServiceException(form.getClass() + " getList(applicationId, operatorId, cityId, parameterName, parameters) function is not definded for parameter: '" + parameterName + "'");
		}
		
		return ListItemDto.asList(form.getList(applicationId, operatorId, cityId, parameterName, parameters));
	}


	/**
	 * Интерактивное получение значения параметра  формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param operatorId идентификатор пользователя интерфейса консультанта
	 * @param cityId идентификатор города интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * 
	 * @return значение параметра формы
	 * @throws ServiceException
	 */

	public String getValueData(Integer applicationId, String formApi, Integer operatorId, Integer cityId,
			String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);
		String valueData = form.getValue(applicationId, operatorId, cityId, parameterName, parameters);
		if (valueData == null) {
			 throw new ServiceException(form.getClass() + " getValue(applicationId, operatorId, cityId, parameterName, parameters) function is not definded for parameter: '" + parameterName + "'");
		}

		return valueData;
	}

	/**
	 * Интерактивное получение признака видимости параметра  формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param operatorId идентификатор пользователя интерфейса консультанта
	 * @param cityId идентификатор города интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * 
	 * @return Признак видимости параметра формы true видим false не видим
	 * @throws ServiceException
	 */

	public Boolean getIsVisible(Integer applicationId, String formApi, Integer operatorId, Integer cityId,
			String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);
		Boolean isVisible = form.isVisible(applicationId, operatorId, cityId, parameterName, parameters);

		if (isVisible == null) {
			 throw new ServiceException(form.getClass() + " isVisible(applicationId, operatorId, cityId, parameterName, parameters) function is not definded for parameter: '" + parameterName + "'");
		}

		return isVisible;
	}

	/**
	 * Интерактивное получение признака активности параметра  формы
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param operatorId идентификатор пользователя интерфейса консультанта
	 * @param cityId идентификатор города интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * 
	 * @return Признак активности параметра формы true активен false не активен
	 * @throws ServiceException
	 */

	public Boolean getIsActive(Integer applicationId, String formApi, Integer operatorId, Integer cityId,
			String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);
		Boolean isActive = form.isActive(applicationId, operatorId, cityId, parameterName, parameters);
		if (isActive == null) {
			 throw new ServiceException(form.getClass() + " isActive(applicationId, operatorId, cityId, parameterName, parameters) function is not definded for parameter: '" + parameterName + "'");
		}
		
		return isActive;
	}

	/**
	 * Проверка сущности, связанной с формой
	 * 
	 * @param applicationId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameters параметры, которые необходимо обработать
	 * @param checkParametersMap 
	 * 
	 * @return ошибки сущности
	 * @throws ServiceException
	 */

	public ResultDto checkInterface(Integer applicationId, Integer operatorId, Integer cityId, String formApi, Map<String, String> checkParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		Map<String,List<String>> parameterErrors = new HashMap<String,List<String>>(); 
		List<String> formErrors =  new LinkedList<String>();
		Map<String, List<String>> interfaceErrors = form.checkInterface(applicationId, operatorId, cityId, form.getFormData().getValues());

		for (String parameterName: interfaceErrors.keySet()){
			if ("form".equals(parameterName)) {
				formErrors.addAll(interfaceErrors.get(parameterName));
			} else {
				parameterErrors.put(parameterName, interfaceErrors.get(parameterName));
			}
		}

		
		return new ResultDto(formErrors != null ? formErrors : new LinkedList<String>(), parameterErrors);
	}


}
