package com.technology.jef.server.form;

import static com.technology.jef.server.WebServiceConstant.SERVICE_STATUS_OK;
import static com.technology.jef.server.serialize.SerializeConstant.*;
import static com.technology.jef.server.WebServiceConstant.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
			fromResult = getFormDto(id, null, formApi, parameters);
			fromResult.setGroups(groups);

		} else {
			fromResult = getFormDto(id, null, formApi, parameters);
		}

		

		return fromResult;
	}

	private FormDto getFormDto(Integer primaryId, Integer secondaryId, String formApi, Map<String, String> parameters)
			throws ServiceException {

		FormDto fromResult;

		Form form = factory.getForm(formApi);
		form.load(primaryId, secondaryId, parameters);

		form.getFormData().putValue("group_id", Objects.toString(secondaryId, ""));
		form.getFormData().putExtraValues(parameters);

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

	public ResultDto setFormData(Integer id, Map<String, String> parameters)
			throws ServiceException {

		ResultDto result = null;

		Map<String,String> allInputParametersMap = new HashMap<String,String>();
		Map<String, FormParameters> formsMap = new HashMap<String, FormParameters>();
		List<FormParameters> formsList = new LinkedList<FormParameters>();

		Pattern prefixPattern = Pattern.compile("^api_(\\w+?)(?:"+GROUP_SEPARATOR+"(\\w+))?$");
		Matcher prefixMatcher;

		// создаем карту API по параметрам
		for (String name : parameters.keySet()) {
			String value = parameters.get(name);

			prefixMatcher = prefixPattern.matcher(name);
			
			// добавляем в карту API пустой класс параметров 
			if (prefixMatcher.matches()) {
				formsMap.put(Objects.toString(value, ""), new FormParameters(Objects.toString(value, "")));
			} else if (!name.contains("api_") && !name.contains("required_")) {
				allInputParametersMap.put(name, parameters.get(name));
			}
		}

		Pattern apiPattern = Pattern.compile("^(?:(api|parrent_api)_)?(\\w+?)("+GROUP_SEPARATOR+"(\\w+))?$");
		Matcher apiMatcher;

		// заполняем карту API данными форм по параметрам
		for (String name : parameters.keySet()) {
			apiMatcher = apiPattern.matcher(name);
			if (apiMatcher.matches()) {

				// Если для текущего параметра есть параметр содержащий API
				String apiName = parameters.get("api_" + apiMatcher.group(2) + (apiMatcher.group(3) != null ? apiMatcher.group(3) : ""));
				if (apiName != null) { 

					// Имя текущего API
					String currentForm = Objects.toString(apiName, ""); 

					// Если текущий параметр содержит PARRENT_API
					if ("parrent_api".equals(apiMatcher.group(1))) { 
						if (!"".equals(parameters.get(name))) { // Если PARRENT_API не пуст
							formsMap.get(currentForm).setParrentApi(String.valueOf(parameters.get(name)));
						}
					
					// Если текущий параметр содержит значение поля формы
					} else if (apiMatcher.group(1) == null){
						String groupPrefix = apiMatcher.group(4) != null ? apiMatcher.group(4).replace(currentForm + "_", "") : "";
						formsMap.get(currentForm).addParameter(apiMatcher.group(2), parameters.get(name), "1".equals(parameters.get("required_" + name)), groupPrefix, allInputParametersMap);
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
						String secondaryId = parameters.get("group_id" + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
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
							String secondaryId = parameters.get("group_id" + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
							String action = parameters.get("action" + GROUP_SEPARATOR + formParameters.getCurrentApi() + "_" + groupPrefix);
							if (ACTION_DELETE.equals(action)) {
								deleteFormData(currentPrimaryId, secondaryId == null | "".equals(secondaryId) ? null : Integer.parseInt(secondaryId), formParameters.getCurrentApi());
							} else {
								newRecordId.set(0,  saveFormData(currentPrimaryId, secondaryId == null | "".equals(secondaryId) ? null : Integer.parseInt(secondaryId), formParameters.getCurrentApi(), formParameters.getInputParameters(groupPrefix)));
							}
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

		//Добавляем в результат данные о текущем ID
		result.setId(String.valueOf(newRecordId.get(0)));
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
	 * @return идентификатор базовой сущности
	 * 
	 * @throws ServiceException
	 */

	public Integer saveFormData(Integer primaryId, Integer secondaryId, String formApi, Map<String, String> saveParametersMap)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		return form.saveForm(primaryId, secondaryId, saveParametersMap);
	}

	/**
	 * Удаление групповой формы
	 * 
	 * @param primaryId идентификатор анкеты
	 * @param formApi идентификатор контроллера интерфейса консультанта
	 * 
	 * @throws ServiceException
	 */

	public void deleteFormData(Integer primaryId, Integer secondaryId, String formApi)
			throws ServiceException {
		
		Form form = factory.getForm(formApi);
		
		form.deleteForm(primaryId, secondaryId);
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


	public List<ListItemDto> getListData(Integer id, String formApi, String parameterName, Map<String, String> parameters)
			throws ServiceException {
		
		
		
		Form form = factory.getForm(formApi);
		
		return ListItemDto.asList(form.getList(id, parameterName, parameters));
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

	public List<ListItemDto> getInteractiveListData(Integer id, String formApi, String parameterName, Map<String, String> parameters) throws ServiceException {

		
		
		Form form = factory.getForm(formApi);

		
		return ListItemDto.asList(form.getInteractiveList(id, parameterName, parameters));
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
		form.getFormData().putExtraValues(checkParametersMap);
		
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

	/**
	 * Преобразование потока с картинкой в base64URI. Необходимо для реализации сервиса поддержки виджета Image в IE<10
	 * 
	 * @param inputStream Поток картинки
	 * @return Строка base64URI
	 * @throws ServiceException
	 */
	
	public String imageToBase64(InputStream inputStream) throws ServiceException {
        try {
        	ImageInputStream imageStream = ImageIO.createImageInputStream(inputStream);
        	Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageStream);

        	if (imageReaders.hasNext()) {
        	    ImageReader reader = (ImageReader) imageReaders.next();
        	    reader.setInput(imageStream);
        	    BufferedImage bufferedImage = reader.read(0);
        	    String formatName = reader.getFormatName();
        	    ByteArrayOutputStream byteaOutput = new ByteArrayOutputStream();
        	    Base64OutputStream base64Output = new Base64OutputStream(byteaOutput);
        	    ImageIO.write(bufferedImage, formatName, base64Output);
        	    String base64 = new String(byteaOutput.toByteArray());

        	    return "data:image/" + reader.getFormatName().toLowerCase() + ";base64," + base64.replaceAll("[\\r\\n]", "");
        	}
        	return "";
        	
        } catch(IOException e) {
        	throw new ServiceException(e.getMessage(),e);
        }

	}
}
