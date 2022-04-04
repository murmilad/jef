package com.technology.jef.server.form;

import static com.technology.jef.server.WebServiceConstant.SERVICE_STATUS_OK;
import static com.technology.jef.server.serialize.SerializeConstant.*;
import static com.technology.jef.server.WebServiceConstant.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.codec.binary.Base64OutputStream;
//import org.apache.log4j.Logger;

import com.technology.jef.server.dto.FormDto;
import com.technology.jef.server.dto.FormErrorDto;
import com.technology.jef.server.dto.ListItemDto;
import com.technology.jef.server.dto.OptionDto;
import com.technology.jef.server.dto.ParameterDto;
import com.technology.jef.server.dto.ResultDto;
import com.technology.jef.server.exceptions.ServiceException;
import com.technology.jef.server.form.parameters.FormParameters;
import com.technology.jef.server.form.parameters.Parameters;
import com.technology.jef.server.form.parameters.Value;

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
	 * @param id идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameters параметры формы
	 * @return список групп и параметров формы
	 * @throws ServiceException
	 */

	public FormDto getFormDto(String id, String formApi, Map<String, String> parameters)
			throws ServiceException {

		FormDto fromResult = null;

		Form form = factory.getForm(formApi);
		form.setSuperGroupId(parameters.get("super_group_id"));
		// Если реализован метод getGroups, содержащий superFormId то вызываем его, иначе вызываем без указанного параметра
		List<String> groupIdList = form.getGroups(id, stringMapToValueMap(parameters));

		if (groupIdList != null) {
			List<FormDto> groups = new LinkedList<FormDto>();
			for (String groupId: groupIdList) {
				groups.add(getFormDto(id, "".equals(groupId) ? null : groupId, formApi, parameters));
			}
			fromResult = getFormDto(id, null, formApi, parameters);
			fromResult.setGroups(groups);

		} else {
			fromResult = getFormDto(id, null, formApi, parameters);
		}
		fromResult.setResult(checkInterface(id, formApi, stringMapToValueMap(parameters)));

		

		return fromResult;
	}

	private FormDto getFormDto(String primaryId, String groupId, String formApi, Map<String, String> parameters)
			throws ServiceException {

		FormDto fromResult;

		Parameters valueParameters = stringMapToValueMap(parameters);
		
		Form form = factory.getForm(formApi);
		form.setSuperGroupId(parameters.get("super_group_id"));
		form.setGroupId(groupId);

		form.load(primaryId, groupId, valueParameters);

		if (groupId != null) {
			form.getFormData().putValue("group_id", new Value("group_id", groupId));
		}
		form.getFormData().putExtraValues(valueParameters);

		fromResult = new FormDto(SERVICE_STATUS_OK);

		for(String parameterName : new HashSet<String>(form.getFieldsMap().keySet()) {{
				if (groupId != null) {
					add("group_id");
				}}}) {
			if (!"form".equals(parameterName)) {
				fromResult.putParameter(parameterName, new ParameterDto(form.getFormData().getValues().containsKey(parameterName) ? form.getFormData().getValues().get(parameterName).getValue() : "", form.getAttributes(parameterName, form.getFormData().getValues(), primaryId)));
			}
		}


		return fromResult;
	}



	public Parameters stringMapToValueMap(Map<String, String> parameters) {
		Parameters result = new Parameters();

		parameters.keySet().forEach(name -> 
			result.put(name, new Value(
					name
					, parameters.get(name)
					, "1".equals(parameters.get(SYSTEM_PARAMETER_PREFIX + "_required_" + name))
					, "1".equals(parameters.get(SYSTEM_PARAMETER_PREFIX + "_changed_" + name))
					, parameters.get("visible_" + name)))
		);
		
		return result;
	}

	/**
	 * Сохранение параметров формы
	 * 
	 * @param id идентификатор 
	 * @param parameters параметры, которые необходимо обработать
	 * @return результат выполнения операции
	 * @throws ServiceServiceException
	 */

	public ResultDto setFormData(String id, Map<String, String> parameters)
			throws ServiceException {

		ResultDto result = null;

		Parameters allInputParametersMap = new Parameters();
		Map<String, FormParameters> formsMap = new HashMap<String, FormParameters>();
		List<FormParameters> formsList = new LinkedList<FormParameters>();

		Pattern prefixPattern = Pattern.compile("^"+SYSTEM_PARAMETER_PREFIX+"_api_(\\w+?)(?:(?:"+GROUP_SEPARATOR+"\\w+?)*(?:"+GROUP_SEPARATOR+"(\\w+)))?$");
		Matcher prefixMatcher;

		// создаем карту API по параметрам
		for (String name : parameters.keySet()) {
			String value = parameters.get(name);

			prefixMatcher = prefixPattern.matcher(name);

			
			// добавляем в карту API пустой класс параметров 
			if (prefixMatcher.matches()) {
				formsMap.put(Objects.toString(value, ""), new FormParameters(Objects.toString(value, "")));
			} else if (!name.contains(SYSTEM_PARAMETER_PREFIX + "_parrent_api_") && !name.contains(SYSTEM_PARAMETER_PREFIX + "_api_") && !name.contains(SYSTEM_PARAMETER_PREFIX + "_required_") && !name.contains(SYSTEM_PARAMETER_PREFIX + "_changed_")) {
				allInputParametersMap.put(name, new Value(
						name
						, parameters.get(name)
						, "1".equals(parameters.get(SYSTEM_PARAMETER_PREFIX + "_required_" + name))
						, "1".equals(parameters.get(SYSTEM_PARAMETER_PREFIX + "_changed_" + name))
						, parameters.get("visible_" + name)));
			}
		}

		
	
		
		Pattern apiPattern = Pattern.compile("^(?:"+SYSTEM_PARAMETER_PREFIX+"_(api|parrent_api)_)?(\\w+?)(?:((?:"+GROUP_SEPARATOR+"\\w+?)*)("+GROUP_SEPARATOR+"(\\w+)))?$");
		Matcher apiMatcher;

		// заполняем карту API данными форм по параметрам
		for (String name : parameters.keySet()) {
			apiMatcher = apiPattern.matcher(name);
			if (apiMatcher.matches()) {

				String groupPath   = apiMatcher.group(3) != null ? apiMatcher.group(3): "";

				// Если для текущего параметра есть параметр содержащий API
				String apiName = parameters.get(SYSTEM_PARAMETER_PREFIX + "_api_" + apiMatcher.group(2) + groupPath + (apiMatcher.group(4) != null ? apiMatcher.group(4) : ""));
				if (apiName != null) { 

					// Имя текущего API
					String currentForm = Objects.toString(apiName, ""); 
					String groupPrefix = apiMatcher.group(5) != null ? apiMatcher.group(5).replaceAll(currentForm + "_", "") : "";

					if (!"".equals(groupPrefix) && !groupPrefix.matches("^\\d+$")) { // Если префикс не цифра то это простая группа, вложенная в мультигруппу
						groupPath = GROUP_SEPARATOR + groupPrefix;
						groupPrefix = "";
					}

					// Если текущий параметр содержит PARRENT_API
					if ("parrent_api".equals(apiMatcher.group(1))) { 
						if (!"".equals(parameters.get(name))) { // Если PARRENT_API не пуст
							formsMap.get(currentForm).setParrentApi(String.valueOf(parameters.get(name)));
						}

						//Logger.getLogger(FormDto.class).info("groupPath " + groupPath + " name '" + name  + "' 1 '" + apiMatcher.group(1)+ "' 2 '" + apiMatcher.group(2)+ "' 3 '" + apiMatcher.group(3)+ "' 4 '" + apiMatcher.group(4)+ "' 5 '" + apiMatcher.group(5));
						Pattern groupPattern = Pattern.compile(GROUP_SEPARATOR+"(\\w+)_(\\d+)$");
						Matcher groupMatcher = groupPattern.matcher(groupPath);

						if (groupMatcher.matches()) { // Если группа вложена в мультигруппу
							
							formsMap.get(currentForm).setSuperApi(groupMatcher.group(1));
						}

					
					// Если текущий параметр содержит значение поля формы
					} else if (apiMatcher.group(1) == null){
							
						allInputParametersMap.get(name).setName(apiMatcher.group(2));
						formsMap.get(currentForm).addParameter(apiMatcher.group(2), allInputParametersMap.get(name), groupPath, groupPrefix, allInputParametersMap);
					}
				}
			}
		}

		// Формируем дерево вызовов API переберая карту
		for (String formKey : formsMap.keySet()) {

			if (formsMap.get(formKey).getSuperApi() != null ) {
				if (formsMap.containsKey(formsMap.get(formKey).getSuperApi())) {
					formsMap.get(formsMap.get(formKey).getSuperApi()).addChildren(formsMap.get(formKey));
				} else {
					formsList.add(formsMap.get(formKey));
				}
			} else if (formsMap.get(formKey).getParrentApi() != null){
				if (formsMap.containsKey(formsMap.get(formKey).getParrentApi())) {
					formsMap.get(formsMap.get(formKey).getParrentApi()).addChildren(formsMap.get(formKey));
				} else {
					formsList.add(formsMap.get(formKey));
				}
			} else {
				formsList.add(formsMap.get(formKey));
			}
		}

		
		result = new ResultDto(SERVICE_STATUS_OK);
		Boolean release = true; 
		// Проверяем параметры формы
		for (FormParameters parrentFormParameters : formsList) {
			operateFormParameters(parrentFormParameters, result, (FormParameters formParameters, ResultDto currentResult) -> {
				for (String groupPath : formParameters.getParameters().keySet()) {
					for (String groupPrefix : formParameters.getParameters().get(groupPath).keySet()) {
//TODO Поскольку параметры могут быть в группе, вложенной в другую группу, их нужно обработать отдельно
						String groupId = parameters.get("group_id" + groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
						String superGroupId = parameters.get("group_id" + groupPath);
						currentResult.appendResult(
								checkFormData(
									id,
									groupId == null | "".equals(groupId) 
										? null
										: groupId,
									superGroupId == null | "".equals(superGroupId) 
										? null
										: superGroupId,
									formParameters.getCurrentApi(),
									formParameters.getFormParameters(groupPath, groupPrefix),
									formParameters.getInputParameters(groupPath, groupPrefix)
								),
								!"".equals(groupPrefix) 
									? groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix
									: !"".equals(groupPath)
										? groupPath
										: ""
						);
					}
				}
			});
		}

		release = result.getErrors().getFormErrors().size() == 0 && result.getErrors().getParametersErrors().isEmpty();
		
		if (release) {
			for (FormParameters parrentFormParameters : formsList) {
				operateFormParameters(parrentFormParameters, result, (FormParameters formParameters, ResultDto currentResult) -> {
					for (String groupPath : formParameters.getParameters().keySet()) {
						for (String groupPrefix :  
								formParameters.getParameters().get(groupPath).keySet().stream()
								.sorted((f1, f2) -> f2.compareTo(f1)).collect(Collectors.toList())
								// Удаляем в обратном порядке, поскольку бывает удаление не только по ID но и по Order
						) {
							

									String groupId = parameters.get("group_id" + groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
									String action = parameters.get("action" + groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
									String superGroupId = parameters.get("group_id" + groupPath);

									//Logger.getLogger(FormDto.class).info("DELETE action" + groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix + " groupId:" + groupId + " action:" + action);
									if (ACTION_DELETE.equals(action)) {
										if (groupId != null && !"".equals(groupId)) {
											try {
												deleteFormData(id, groupId, superGroupId, formParameters.getCurrentApi(), parameters);
											} catch (ServiceException e) {
												throw new RuntimeException(e);
											}
										}
									}
						}
					}
				});
			}
			
		}

		// Сохраняем параметры формы в случае если нет ошибок на форме
		if (release) {
			for (FormParameters parrentFormParameters : formsList) {
				operateFormParameters(parrentFormParameters, result, (FormParameters formParameters, ResultDto currentResult) -> {
					
					for (String groupPath : formParameters.getParameters().keySet()) {
						for (String groupPrefix :  
								formParameters.getParameters().get(groupPath).keySet().stream()
								.sorted((f1, f2) -> f1.compareTo(f2)).collect(Collectors.toList())
						) {

							String groupId = parameters.getOrDefault("group_id" + groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix, "");
							String action = parameters.get("action" + groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
							String superGroupId = parameters.getOrDefault("group_id" + groupPath, "");

							if (!ACTION_DELETE.equals(action)) {
								String newGroupId = saveFormData(id 
											, (groupId == null || "".equals(groupId)) 
												? null 
												: groupId
											, (superGroupId == null || "".equals(superGroupId)) 
												? null 
												: superGroupId
											, formParameters.getCurrentApi()
											, formParameters.getInputParameters(groupPath, groupPrefix)
								);


								// если новая запись, то возможно groupId понадобится дочерним группам или подгруппам
								if ("".equals(groupId)
										|| groupId.matches("^.+" + JOINED_GROUP_PARAMETER_SEPARATOR + "$")
										|| groupId.matches("^.+" + JOINED_GROUP_PARAMETER_SEPARATOR + "$")) { 
									parameters.put("group_id" + groupPath + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix, newGroupId);
								}
							}
						}
					}
				});
			}
			
		}

		// Проверяем сущность, связанную с формой
		for (FormParameters parrentFormParameters : formsList) {
			operateFormParameters(parrentFormParameters, result, (FormParameters formParameters, ResultDto currentResult) -> {
				for (String groupPath : formParameters.getParameters().keySet()) {
					for (String groupPrefix : formParameters.getParameters().get(groupPath).keySet()) {
						currentResult.appendResult(checkInterface(id, formParameters.getCurrentApi(), formParameters.getInputParameters(groupPath, groupPrefix)), !"".equals(groupPrefix) 
								? groupPath + GROUP_SEPARATOR +  formParameters.getCurrentApi() + "_" + groupPrefix
								: ""
						);
					}
				}
			});
		}

		//Добавляем в результат данные о текущем ID
		result.setId(String.valueOf(id));
		result.setRelease(release);
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
	 * @param primaryId идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameters параметры, которые необходимо обработать
	 * @param checkParametersMap 
	 * @return результаты выполнения операции
	 * @throws ServiceException
	 */
	
	public ResultDto checkFormData(String primaryId, String groupId, String superGroupId, String formApi, List<Value> parameters, Parameters checkParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		form.setGroupId(groupId);
		form.setSuperGroupId(superGroupId);
		
		Map<String,List<FormErrorDto>> parameterErrors = new HashMap<String,List<FormErrorDto>>(); 
		// Добавляем ошибки связанные с заполнением интерфейса 
		for (Value parameter : parameters) {
			List<String> result = form.checkParameter(primaryId, groupId, parameter, checkParametersMap);
			if (result != null && result.size() > 0) {
				
				parameterErrors.put(parameter.getName(), result.stream().map(
						error -> new FormErrorDto(error, parameter.getName())).collect(Collectors.toList()));
			}
		}

		List<FormErrorDto> formErrors = new LinkedList<FormErrorDto>();
		Map<String, List<String>> formParameterErrors = form.checkForm(primaryId, groupId, checkParametersMap);
		
		for (String parameterName: formParameterErrors.keySet()){
			if ("form".equals(parameterName) || !form.getFieldsMap().containsKey(parameterName)) {
				formErrors.addAll(formParameterErrors.get(parameterName).stream().map(
						error -> error.indexOf(ERROR_TEXT_FIELD_CODE_SEPARATOR) > 0
							? new FormErrorDto(error.split(ERROR_TEXT_FIELD_CODE_SEPARATOR)[0], error.split(ERROR_TEXT_FIELD_CODE_SEPARATOR)[1], error.split(ERROR_TEXT_FIELD_CODE_SEPARATOR)[2]) 
							: new FormErrorDto(error)
						).collect(Collectors.toList()));
			} else {
				parameterErrors.put(parameterName, formParameterErrors.get(parameterName).stream().map(
						error -> new FormErrorDto(error, parameterName)).collect(Collectors.toList()));
			}
		}

		return new ResultDto(formErrors != null ? formErrors : new LinkedList<FormErrorDto>(), parameterErrors);
	}

	/**
	 * Сохранение параметров формы
	 * 
	 * @param primaryId идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param saveParametersMap 
	 * @return идентификатор базовой сущности
	 * 
	 * @throws ServiceException
	 */

	public String saveFormData(String primaryId, String groupId, String superGroupId, String formApi, Parameters saveParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		form.setGroupId(groupId);
		form.setSuperGroupId(superGroupId);
		
		return form.saveForm(primaryId, groupId, saveParametersMap);
	}

	/**
	 * Удаление групповой формы
	 * 
	 * @param primaryId идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * 
	 * @throws ServiceException
	 */

	public void deleteFormData(String primaryId, String groupId, String superGroupId, String formApi, Map<String, String> parametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		form.setGroupId(groupId);
		form.setSuperGroupId(superGroupId);
		
		form.deleteForm(primaryId, groupId, stringMapToValueMap(parametersMap));
	}


	/**
	 * Получение списка элементов для параметра формы
	 * 
	 * @param id идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @return список элементов параметра формы
	 * @throws ServiceException
	 */


	public List<ListItemDto> getListData(String id, String formApi, String parameterName, Map<String, String> parameters)
			throws ServiceException {
		
		List<ListItemDto> result = new LinkedList<ListItemDto>();
		
		for (String api : formApi.split(",")) {
			Form form = factory.getForm(api);
			form.setSuperGroupId(parameters.get("super_group_id"));
			for (ListItemDto item : ListItemDto.asList(form.getList(id, parameterName, stringMapToValueMap(parameters)))) {
				result.add(item);
			}
			
		}
		
		return result;
	}

	/**
	 * Интерактивное получение списка элементов для параметра формы
	 * 
	 * @param id идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return список элементов параметра формы
	 * @throws ServiceException
	 */

	public List<ListItemDto> getInteractiveListData(String id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		List<ListItemDto> result = new LinkedList<ListItemDto>();
		
		for (String api : formApi.split(",")) {
			Form form = factory.getForm(api);
			form.setSuperGroupId(parameters.get("super_group_id"));
			for (ListItemDto item : ListItemDto.asList(form.getInteractiveList(id, parameterName, stringMapToValueMap(parameters)))) {
				result.add(item);
			}
			
		}
		
		return result;
	}


	/**
	 * Интерактивное получение значения параметра  формы
	 * 
	 * @param id идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return значение параметра формы
	 * @throws ServiceException
	 */

	public String getValueData(String id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);
		form.setSuperGroupId(parameters.get("super_group_id"));

		return form.getValue(id, parameterName, stringMapToValueMap(parameters));
	}

	/**
	 * Интерактивное получение признака видимости параметра  формы
	 * 
	 * @param id идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return Признак видимости параметра формы true видим false не видим
	 * @throws ServiceException
	 */

	public Boolean getIsVisible(String id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);
		form.setSuperGroupId(parameters.get("super_group_id"));

		return form.isVisible(id, parameterName, stringMapToValueMap(parameters));
	}

	/**
	 * Интерактивное получение признака активности параметра  формы
	 * 
	 * @param id идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param parameterName наименование параметра на форме
	 * @param parameters параметры, влияющие на список элементов
	 * @return Признак активности параметра формы true активен false не активен
	 * @throws ServiceException
	 */

	public Boolean getIsActive(String id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);
		form.setSuperGroupId(parameters.get("super_group_id"));

		
		return form.isActive(id, parameterName, stringMapToValueMap(parameters));
	}

	/**
	 * Проверка сущности, связанной с формой
	 * 
	 * @param id идентификатор 
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * @param checkParametersMap 
	 * @param parameters параметры, которые необходимо обработать
	 * @return ошибки сущности
	 * @throws ServiceException
	 */

	public ResultDto checkInterface(String id, String formApi, Parameters checkParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		form.getFormData().putExtraValues(checkParametersMap);
		
		Map<String,List<FormErrorDto>> parameterErrors = new HashMap<String,List<FormErrorDto>>(); 
		List<FormErrorDto> formErrors =  new LinkedList<FormErrorDto>();
		Map<String, List<String>> interfaceErrors = form.checkInterface(id, form.getFormData().getValues());

		for (String parameterName: interfaceErrors.keySet()){
			if ("form".equals(parameterName)) {
				formErrors.addAll(interfaceErrors.get(parameterName).stream().map(
						error -> error.indexOf(ERROR_TEXT_FIELD_CODE_SEPARATOR) > 0
						? new FormErrorDto(error.split(ERROR_TEXT_FIELD_CODE_SEPARATOR)[0], error.split(ERROR_TEXT_FIELD_CODE_SEPARATOR)[1], error.split(ERROR_TEXT_FIELD_CODE_SEPARATOR)[2], false) 
						: new FormErrorDto(error) {{setBlock(false);}}
					).collect(Collectors.toList()));
			} else {
				parameterErrors.put(parameterName, interfaceErrors.get(parameterName).stream().map(
						error -> new FormErrorDto(error, parameterName){{setBlock(false);}}).collect(Collectors.toList()));
			}
		}

		
		return new ResultDto(formErrors != null ? formErrors : new LinkedList<FormErrorDto>(), parameterErrors);
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

	/**
	 * Преобразование потока с картинкой в base64URI. Необходимо для реализации сервиса поддержки виджета Image в IE<10
	 * 
	 * @param inputStream Поток картинки
	 * @return Строка base64URI
	 * @throws ServiceException
	 */
	
	public String imageToBase64(InputStream inputStream, String mimeType) throws ServiceException {
		try {
		    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		    int nRead;
		    byte[] data = new byte[1024];
		    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
		        buffer.write(data, 0, nRead);
		    }
		 
		    buffer.flush();

		    String encoded = Base64.getEncoder().encodeToString(buffer.toByteArray());

    	    return "data:" + mimeType + ";base64," + encoded;
		} catch (IOException e) {
        	throw new ServiceException(e.getMessage(),e);
		}
	    
	}

	/**
	 * Преобразование картинки в base64URI в бинарные данные. Необходимо для реализации сервиса поддержки виджета  в IE<10
	 * 
	 * @param inputStream Поток картинки
	 * @return Строка base64URI
	 * @throws ServiceException
	 */
	
	public byte[] base64ToImage(String inputStream) throws ServiceException {
		return Base64.getDecoder().decode(inputStream);
	}

	
	/**
	 * Десериализация объектов
	 * 
	 * @param list сериализованный Map
	 * @return десериализованный Map
	 */
	
	public static Map<String,String> listToMap(String list) throws ServiceException {
		Map<String,String> parametersMap = new HashMap<String,String>();

		// XSS Filtering
		list = list.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		
		for (String parameter: list.split(PARAMETER_SEPARATOR)) {
			Pattern pattern = Pattern.compile("(.*)"+PARAMETER_NAME_VALUE_SEPARATOR+"(.*)");
			Matcher matcher = pattern.matcher(parameter);
			if (matcher.matches()) {
				parametersMap.put(matcher.group(1), matcher.group(2));
			}
		}
		return parametersMap;
		
	}

}
