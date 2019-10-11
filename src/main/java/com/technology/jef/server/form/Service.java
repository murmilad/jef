package com.technology.jef.server.form;

import static com.technology.jef.server.WebServiceConstant.SERVICE_STATUS_OK;
import static com.technology.jef.server.serialize.SerializeConstant.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.technology.jef.server.dto.FormDto;
import com.technology.jef.server.dto.ListItemDto;
import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.dto.ParameterDto;
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
	 * @param id идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameters TODO
	 * @return список групп и параметров формы
	 * @throws ServiceException
	 */

	public FormDto getFormDto(Integer id, String formApi, Map<String, String> parameters)
			throws ServiceException {

		FormDto fromResult = null;

		Form form = factory.getForm(formApi);
		List<String> groupIdList = form.getGroups(id);

		if (groupIdList != null) {
			List<FormDto> groups = new LinkedList<FormDto>();
			for (String secondaryId: groupIdList) {
				groups.add(getFormDto(id, "".endsWith(secondaryId) ? null : Integer.parseInt(secondaryId), formApi, parameters));
			}
			fromResult = new FormDto(SERVICE_STATUS_OK, groups);

		} else {
			fromResult = getFormDto(id, null, formApi, parameters);
		}

		

		return fromResult;
	}

	private FormDto getFormDto(Integer primaryId, Integer secondaryId, String formApi, Map<String, String> parameters)
			throws ServiceException {

		FormDto fromResult;

		Form form = factory.getForm(formApi);
		form.load(primaryId, secondaryId);

		form.getFormData().putValue("group_id", String.valueOf(secondaryId));

		fromResult = new FormDto(SERVICE_STATUS_OK);

		for(String parameterName : form.getFormData().getValues().keySet()) {
			if (!"form".equals(parameterName)) {
				fromResult.putParameter(parameterName, new ParameterDto(form.getFormData().getValues().get(parameterName), form.getAttributes(parameterName, primaryId)));
			}
		}

		fromResult.setResult(checkInterface(primaryId, formApi, form.getFormData().getValues()));

		return fromResult;
	}



	/**
	 * Сохранение параметров формы
	 * 
	 * @param id идентификатор анкеты
	 * @param parameters параметры, которые необходимо обработать
	 * @return результат выполнения операции
	 * @throws ServiceServiceException
	 */

	public ResultDto setFormData(Integer id, String iPaddress, String parameters)
			throws ServiceException {

		ResultDto result = null;

		Map<String,String> sourceParametersMap = new HashMap<String,String>();
		Map<String, FormParameters> formsMap = new HashMap<String, FormParameters>();
		List<FormParameters> formsList = new LinkedList<FormParameters>();

		Pattern prefixPattern = Pattern.compile("^api_(\\w+?)(?:"+GROUP_SEPARATOR+"(\\w+))?$");
		Matcher prefixMatcher;

		// создаем карту API по параметрам
		for (String parameter : parameters.split(PARAMETER_SEPARATOR)) {
			String[] nameValue = parameter.split(PARAMETER_NAME_VALUE_SEPARATOR);
			String name = nameValue.length > 0 ? nameValue[0] : "";
			String value = nameValue.length > 1 ? nameValue[1] : "";

			prefixMatcher = prefixPattern.matcher(name);
			
			// добавляем в карту API пустой класс параметров 
			if (prefixMatcher.matches()) {
				formsMap.put(String.valueOf(value), new FormParameters(String.valueOf(value)));
			}

			// собираем параметры и мета-параметры в один большой хэш
			sourceParametersMap.put(name, value);
		}

		Pattern apiPattern = Pattern.compile("^(?:(api|parrent_api)_)?(\\w+?)("+GROUP_SEPARATOR+"(\\w+))?$");
		Matcher apiMatcher;

		// заполняем карту API данными форм по параметрам
		for (String key : sourceParametersMap.keySet()) {
			apiMatcher = apiPattern.matcher(key);
			if (apiMatcher.matches()) {

				// Если для текущего параметра есть параметр содержащий API
				String apiName = sourceParametersMap.get("api_" + apiMatcher.group(2) + (apiMatcher.group(3) != null ? apiMatcher.group(3) : ""));
				if (apiName != null) { 

					// Имя текущего API
					String currentForm = String.valueOf(apiName); 

					// Если текущий параметр содержит PARRENT_API
					if ("parrent_api".equals(apiMatcher.group(1))) { 
						if (!"".equals(sourceParametersMap.get(key))) { // Если PARRENT_API не пуст
							formsMap.get(currentForm).setParrentApi(String.valueOf(sourceParametersMap.get(key)));
						}
					
					// Если текущий параметр содержит значение поля формы
					} else if (apiMatcher.group(1) == null){
						String groupPrefix = apiMatcher.group(4) != null ? apiMatcher.group(4).replace(currentForm + "_", "") : "";
						formsMap.get(currentForm).addParameter(apiMatcher.group(2), sourceParametersMap.get(key), "1".equals(sourceParametersMap.get("required_" + key)), groupPrefix);
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
			operateFormParameters(parrentFormParameters, result, (FormParameters formParameters, ResultDto currentResult) -> {
					for (String groupPrefix : formParameters.getParameters().keySet()) {
						String secondaryId = sourceParametersMap.get("group_id" + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
						currentResult.appendResult(checkFormData(id, secondaryId == null | "".equals(secondaryId) ? null : Integer.parseInt(secondaryId), formParameters.getCurrentApi(), formParameters.getFormParameters(groupPrefix), formParameters.getInputParameters(groupPrefix)), !"".equals(groupPrefix) 
								? GROUP_SEPARATOR +  formParameters.getCurrentApi() + "_" + groupPrefix
								: ""
						);
					}
			});
		}

		// Сохраняем параметры формы в случае если нет ошибок на форме
		List<Integer> newRecordId = new LinkedList<Integer>() {{ add(id); }};
		if (result.getErrors().getFormErrors().size() == 0 && result.getErrors().getParametersErrors().isEmpty()) {
			for (FormParameters parrentFormParameters : formsList) {
				operateFormParameters(parrentFormParameters, result, (FormParameters formParameters, ResultDto currentResult) -> {
						for (String groupPrefix : formParameters.getParameters().keySet()) {
							Integer currentPrimaryId = newRecordId.get(0);
							String secondaryId = sourceParametersMap.get("group_id" + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
							newRecordId.set(0,  saveFormData(currentPrimaryId, secondaryId == null | "".equals(secondaryId) ? null : Integer.parseInt(secondaryId), iPaddress, formParameters.getCurrentApi(), formParameters.getInputParameters(groupPrefix)));
						}
				});
			}
			
		}

		// Проверяем сущность, связанную с формой
		for (FormParameters parrentFormParameters : formsList) {
			operateFormParameters(parrentFormParameters, result, (FormParameters formParameters, ResultDto currentResult) -> {
					for (String groupPrefix : formParameters.getParameters().keySet()) {
						currentResult.appendResult(checkInterface(id, formParameters.getCurrentApi(), formParameters.getInputParameters(groupPrefix)), !"".equals(groupPrefix) 
								? GROUP_SEPARATOR +  formParameters.getCurrentApi() + "_" + groupPrefix
								: ""
						);
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
	 * @param primaryId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameters параметры, которые необходимо обработать
	 * @param checkParametersMap 
	 * @return результаты выполнения операции
	 * @throws ServiceException
	 */
	
	public ResultDto checkFormData(Integer primaryId, Integer secondaryId, String formApi, List<Parameter> parameters, Map<String, String> checkParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		Map<String,List<String>> parameterErrors = new HashMap<String,List<String>>(); 
		// Добавляем ошибки связанные с заполнением интерфейса 
		for (Parameter parameter : parameters) {
			List<String> result = form.checkParameter(primaryId, secondaryId, parameter.getName(), parameter.getIsRequired(), checkParametersMap);
			if (result != null && result.size() > 0) {
				parameterErrors.put(parameter.getName(), result);
			}
		}

		List<String> formErrors = new LinkedList<String>();
		Map<String, List<String>> formParameterErrors = form.checkForm(primaryId, secondaryId, checkParametersMap);
		
		for (String parameterName: formParameterErrors.keySet()){
			if ("form".equals(parameterName) || !form.getFieldsMap().containsKey(parameterName)) {
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
	 * @param primaryId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param saveParametersMap 
	 * @return TODO
	 * 
	 * @throws ServiceException
	 */

	public Integer saveFormData(Integer primaryId, Integer secondaryId, String iPAddress, String formApi, Map<String, String> saveParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		return form.saveForm(primaryId, secondaryId, iPAddress, saveParametersMap);
	}


	/**
	 * Получение списка элементов для параметра формы
	 * 
	 * @param id идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @return список элементов параметра формы
	 * @throws ServiceException
	 */


	public List<ListItemDto> getListData(Integer id, String formApi, String parameterName)
			throws ServiceException {
		
		
		
		Form form = factory.getForm(formApi);
		
		return ListItemDto.asList(form.getList(id, parameterName));
	}

	/**
	 * Интерактивное получение списка элементов для параметра формы
	 * 
	 * @param id идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return список элементов параметра формы
	 * @throws ServiceException
	 */

	public List<ListItemDto> getListData(Integer id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);

		
		return ListItemDto.asList(form.getList(id, parameterName, parameters));
	}


	/**
	 * Интерактивное получение значения параметра  формы
	 * 
	 * @param id идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return значение параметра формы
	 * @throws ServiceException
	 */

	public String getValueData(Integer id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);

		return form.getValue(id, parameterName, parameters);
	}

	/**
	 * Интерактивное получение признака видимости параметра  формы
	 * 
	 * @param id идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return Признак видимости параметра формы true видим false не видим
	 * @throws ServiceException
	 */

	public Boolean getIsVisible(Integer id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);

		return form.isVisible(id, parameterName, parameters);
	}

	/**
	 * Интерактивное получение признака активности параметра  формы
	 * 
	 * @param id идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return Признак активности параметра формы true активен false не активен
	 * @throws ServiceException
	 */

	public Boolean getIsActive(Integer id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);
		
		return form.isActive(id, parameterName, parameters);
	}

	/**
	 * Проверка сущности, связанной с формой
	 * 
	 * @param id идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param checkParametersMap 
	 * @param parameters параметры, которые необходимо обработать
	 * @return ошибки сущности
	 * @throws ServiceException
	 */

	public ResultDto checkInterface(Integer id, String formApi, Map<String, String> checkParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		Map<String,List<String>> parameterErrors = new HashMap<String,List<String>>(); 
		List<String> formErrors =  new LinkedList<String>();
		Map<String, List<String>> interfaceErrors = form.checkInterface(id, form.getFormData().getValues());

		for (String parameterName: interfaceErrors.keySet()){
			if ("form".equals(parameterName)) {
				formErrors.addAll(interfaceErrors.get(parameterName));
			} else {
				parameterErrors.put(parameterName, interfaceErrors.get(parameterName));
			}
		}

		
		return new ResultDto(formErrors != null ? formErrors : new LinkedList<String>(), parameterErrors);
	}

	  public static void setListData(String listString, SetListHandler setListHandler, ClearListHandler clearListHandler) throws ServiceException {
		  	clearListHandler.handle();
			for (String id: listString.split("\\" + LIST_SEPARATOR)) {
				if (!"".equals(id)) {
					setListHandler.handle(id);
				}
			}
	  }


	public static String getListData(GetListHandler getListHandler) throws ServiceException {
		
		List<OptionDto> list = getListHandler.handle();

		return String.join(LIST_SEPARATOR, list.stream().map((OptionDto item) -> item.getValue().toString()).collect(Collectors.toList()));
	}

}
