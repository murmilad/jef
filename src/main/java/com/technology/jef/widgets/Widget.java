package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Абстрактный класс для виджетов
*/
public abstract class Widget {

	  /**
	   * Перечисление типов виджета. 
	   * С заголовком слева
	   * Без заголовка слева  
	   */
		public enum ViewType {
			DOUBLE,
			SINGLE,
		}
	
	   /**
	   * Перечисление DOM моделей разных виджетов
	   */
		public static enum Type {
			TEXT,
			ANYTEXT,
			TEXTAREA,
			NUMBER,
			DATE,
			INN,
			EMAIL,
			MONEY,
			PHONE,
			HIDDEN,
			INFO,
			LABEL,
			POPUP_LIST,
			LIST,
			DOCUMENT,
			IMAGE,
			IMAGE_SHORT,
			IMAGE_WEBCAM,
			EDITABLE_LIST,
			SHORT_EDITABLE_LIST,
			SHORT_LIST,
			SHORT,
			FIND,
			SHORT_FIND,
			CHECKBOX,
			CHECKBOX_LIST,
			SHORT_CHECKBOX_LIST,
			RADIO_SWITCH,
			SHORT_RADIO_SWITCH,
			DADATA,
			AUTO_COMPLETE,
			AUTO_COMPLETE_ADDRESS,
			AUTO_COMPLETE_EDITABLE,
		}
		
		protected Tag parrent;
		
	  /**
	   * Метод возвращает тип виджета
	   * 
	   * ViewType.DOUBLE - С заголовком слева
	   * ViewType.SINGLE - Без заголовка слева  
	   * 
	   * @return тип виджета
	   */
		public ViewType getType () {
			return ViewType.DOUBLE;
		}
	
	  /**
	   * Метод возвращает регулярное выражения для проверки элемента на странице
	   * 
	   * @return регулярное выражение
	   */
		public String getInputRegexp () {
			return "";
		}
	
	  /**
	   * Метод формирования DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param parrent родительский тег в DOM модели
	   * @return DOM модель на текущем уровне
	   */
		public Tag assembleTag(String name, TagGenerator generator, Tag parrent) {
		
			this.setParrent(parrent);
	
			parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, name);
				 put(Tag.Property.NAME, name);
				 put(Tag.Property.TYPE, "hidden");
			}});
			
			Tag resultElement = assembleTag(name, generator);
			
			resultElement.add(Tag.Type.SCRIPT,			("	$(\"#${name}\").bind(\"setValueOnLoad\", function (event, data) {      \n" + 
					"		var isLoading = true;      \n" + 
					"		${setValueJS} \n" + 
					"		$(\"#${name}\").bind(\"cleanValue\", function(){ \n" + 
					"			if (formsWaitedToLoad === 0) { \n" + 
					"				${cleanValueJS} \n" +
					"			}	\n " +		
					"		}); \n" + 
					"		$(\"#${name}\").unbind(\"setValueOnLoad\");   \n" + 
					"	});      \n" + 
					"	$( document ).bind('setListOnLoad', function() {      \n" + 
					"		${setItemsJS}     \n" + 
					"	});      \n" + 
					"	function onChangeReadOnly${name}(current){       \n" + 
					"		if (typeof current.value != 'undefined'){  \n" + 
					"			$('#${name}').val(current.value);       \n" + 
					"		}  \n" + 
					"	}       \n" + 
					"	function onKeyDownReadOnly${name}(current){       \n" + 
					"		if(window.event.keyCode == 13){       \n" + 
					"			$('#visible_${name}').val(current.value).change();       \n" + 
					"		}       \n" + 
					"	}       \n")
					.replace("${cleanValueJS}", this.getCleanValueJS().replace("${child_name}", name))
					.replace("${setValueJS}", this.getSetValueJS().replace("${child_name}", name))
					.replace("${name}", name)
					.replace("${setItemsJS}",
							((String[]) generator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT)).length == 0 // Грузим списки только если нет зависимостей
								? this.getSetItemsJS()
									.replace("${list_item_js}", getListItemJS())
									.replace("${service}", (String) generator.getAttribute(TagGenerator.Attribute.SERVICE))
									.replace("${name}", name)
									.replace("${name_api}", name.replaceAll((String) generator.getAttribute(TagGenerator.Attribute.PREFIX),""))
									.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
								: "$(\"#visible_${name}\").trigger('setValue');".replace("${name}", name)
					)
			);
			
			postAssembleTag(name, generator, resultElement);
	
			return resultElement;
		}
	
	  /**
	   * Метод формирования DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @return DOM модель на текущем уровне
	   */
		protected abstract Tag assembleTag(String name, TagGenerator generator);

		/**
	   * Метод постобработки DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param element тег созданного элемента
	   * @return DOM модель на текущем уровне
	   */
		protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {

			element.setProperty(Tag.Property.BLUR, element.getProperty(Tag.Property.BLUR) + "onChangeReadOnly" + name + "(this);");
			element.setProperty(Tag.Property.CHANGE, element.getProperty(Tag.Property.CHANGE) + "onChangeReadOnly" + name + "(this);");
			element.setProperty(Tag.Property.KEYDOWN, element.getProperty(Tag.Property.KEYDOWN) + "onChangeReadOnly" + name + "(this);");
			 
			return element;
		}
	
	  /**
	   * Метод возвращает строки инициализации кода JavaScript
	   * 
	   * @return строки инициализации кода JavaScript
	   */
		public String getInitJS () {
			return "";
		}

	  /**
	   * Метод возвращает функционал заполняющий данными списочный элемент
	   * Применяемые переменные: 
	   * 	${name} - имя текущего (зависимого) элемента
	   * 	data.data - полученный от вебсервиса список (id, name, value)
	   * 
	   * @return код JavaScript
	   */
		String getListItemJS() {
			return ""; 
		}

		
	  /**
	   * Метод возвращает функционал устанавливающий список для выбора значения элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 	${api} - API для получения данных
	   * 	${list_item_js} - JS для добавления элемента для выбора
	   * 
	   * @return код JavaScript
	   */
		public String getSetItemsJS() {
			
			return "";
		}

		
	  /**
	   * Метод возвращает функционал устанавливающий значение элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 	val - JSON объект, содержащий возвращенное сервисом значение элемента
	   * 
	   * @return код JavaScript
	   */
		public String getSetValueJS() {
			
			return "$('#visible_${child_name}').val(data.value).change();";
		}

	  /**
	   * Метод возвращает функционал отчищающий значение элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 
	   * @return код JavaScript
	   */
		public String getCleanValueJS() {
			
			return "$('#visible_${child_name}').val('').change();";
		}


	  /**
	   * Метод возвращает функционал устанавливающий признак активности элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 	val - JSON объект, содержащий возвращенное сервисом значение элемента
	   * 
	   * @return код JavaScript
	   */
		public String getSetActiveJS() {
			
			return 
			"		if (val.value) { \n " + 
			"			$('#visible_${child_name}').prop( \"disabled\", false); \n " +
			"			$(\"#tr_${child_name}\" ).css('color', 'black'); \n "+
			"		} else { \n " +
			"			$('#visible_${child_name}').prop( \"disabled\", true); \n " +
			"			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n " +
			"		} \n ";
		}

	  /**
	   * Метод возвращает функционал снимающий признак активности элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 
	   * @return код JavaScript
	   */
		public String getSetInactiveJS() {
			
			return "$('#visible_${child_name}').prop( \"disabled\", false); \n " +
					"$(\"#tr_${child_name}\" ).css('color', 'black'); \n ";
		}

		/**
	    * Метод возвращает функционал влияющий на состав списочного элемента 
	   * 
	   * @return код JavaScript
	   */
		public String getListConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
			return "";
		}

		  /**
		   * Метод возвращает функционал влияющий на видимость элемента 
		   * 
		   * @return код JavaScript
		   */
			public String getVisibleConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {

				// Вызываем функцию связи в событиях родительского элемента
				// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
				currentGenerator.getDom().add(Tag.Type.SCRIPT,
						(
						" $(\"#visible_${parrent_name}\").change(function(){\n" +
						"		onChange${parrent_name}_${child_name}_ct_ajax_visible(this);\n" +
						" }); \n")
						.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				);
				
				String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
				String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);
				String valueJS = getValueJS((String[])currentGenerator.getAttribute(TagGenerator.Attribute.AJAX_VISIBLE_PARRENT), prefix);
//TODO Нужно убрать блок	ignoreEmptyJS создав евент выключения видимости поля но учесть при этом отчистку списочных полей	
				String ignoreEmptyJS =
						("			if (valueJS.match(/${force_ajax}${value_separator}(none)?(${parameter_separator}|$)/)) {\n" + 
								"				$('#tr_${child_name}').css(\"display\", 'none');\n" + 
								"				$('#visible_${child_name}').val('').change();\n" + 
								"				$('#${child_name}').attr('invisible', true);\n" + 
								"				$('#tr_${child_name}').trigger('refresh');\n" + 
								"				return;" + 
								"			}")
								.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
								.replace("${parameter_separator}", PARAMETER_SEPARATOR)
								.replace("${value_js}", valueJS)
								.replace("${child_name}", currentGenerator.getAttribute(TagGenerator.Attribute.ID) + prefix);

				if (!"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX))) {
					ignoreEmptyJS = ignoreEmptyJS.replace("${force_ajax}", "(?!" + (String) currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX) + ")");
				} else {
					ignoreEmptyJS = ignoreEmptyJS.replace("${force_ajax}", "");
				}
				
				String bodyJS = 
								("				function onChange${parrent_name}_${child_name}_ct_ajax_visible(${parrent_name}List){        \n" + 
							"					var valueJS = ${value_js};     \n" + 
							"					${ignore_empty_js}         \n" + 
							"					$('#${child_name}').attr('invisible', true);        \n" + 
							"					$(\"#visible_${parrent_name}\").attr(\"disabled\",\"disabled\");        \n" + 
							"					$(\"#visible_${parrent_name}\").trigger('refresh');        \n" + 
							"					$(\"#background_overlay_wait_${parrent_name}\").show();        \n" + 
							"	            	$(\"#message_box_wait_${parrent_name}\").show();        \n" + 
							"					if (!ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"]) {        \n" + 
							"						ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] = 0;        \n" + 
							"					}					++ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];        \n" + 
							"					if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {    \n" + 
							"						ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;    \n" + 
							"					}    \n" + 
							"					++ajax_is_child_blocked${prefix}[\"${child_name}\"];    \n" + 
							"					ajax({        \n" + 
							"			            	url: '${service}get_is_visible_interactive',        \n" + 
							"						data: {        \n" + 
							"							parameter_name:'${child_name_api}',        \n" + 
							"							form_api: '${api}',        \n" + 
							"							parameters: ${value_js},        \n" + 
							"							rnd: Math.floor(Math.random() * 10000),        \n" + 
							"						},       \n" + 
							"				            type: 'post',        \n" + 
							"				            dataType: 'json',        \n" + 
							"				            contentType: 'application/x-www-form-urlencoded',        \n" + 
							"						}, function (data) {  \n" + 
							"								if (data.value) {        \n" + 
							"									if ((document.getElementById && !document.all) || window.opera)        \n" + 
							"										$('#tr_${child_name}').css(\"display\",'table-row');        \n" + 
							"									else        \n" + 
							"										$('#tr_${child_name}').css(\"display\",'inline');        \n" + 
							"										$('#${child_name}').attr('invisible', false);        \n" + 
							"								} else {        \n" + 
							"									$('#tr_${child_name}').css(\"display\", 'none');        \n" + 
							"									$('#visible_${child_name}').val('').change();        \n" + 
							"									$('#${child_name}').attr('invisible', true);        \n" + 
							"								}        \n" + 
							"							$('#tr_${child_name}').trigger('refresh');        \n" + 
							"							--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];   \n" + 
							"							$(\"#visible_${child_name}\").trigger(\"set_find_result\");        \n" + 
							"							if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {        \n" + 
							"								$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');  \n" + 
							"								if (!$(\"#tr_${parrent_name}\" ).hasClass('disabled')) {  \n" + 
							"									$(\"#visible_${parrent_name}\").removeAttr('disabled');  \n" + 
							"								}  \n" + 
							"								$(\"#visible_${parrent_name}\").removeAttr('disabled');        \n" + 
							"								$(\"#background_overlay_wait_${parrent_name}\").hide();        \n" + 
							"			            				$(\"#message_box_wait_${parrent_name}\").hide();        \n" + 
							"							}        \n" + 
							"							--ajax_is_child_blocked${prefix}[\"${child_name}\"]; \n" + 
							"							if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) { \n" + 
							"            							$(\"#visible_${child_name}\").trigger( 'on_child_unblocked'); \n" + 
							"							} \n" + 
							"							$(\"#visible_${parrent_name}\").trigger('refresh');        \n" + 
							"							$(\"#visible_${child_name}\").trigger('refresh');        \n" + 
							"					});        \n" + 
							"				}        \n")
							.replace("${handler}", handler)
							.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
							.replace("${ignore_empty_js}", ignoreEmptyJS)
							.replace("${value_js}", valueJS)
							.replace("${prefix}", prefix)
							.replace("${api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.API))
							.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
							.replace("${child_name_api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
							.replace("${service}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.SERVICE));		
				
				return bodyJS;
			}
		
		  /**
		   * Метод возвращает функционал влияющий на доступность редактирования элемента 
		   * 
		   * @return код JavaScript
		   */
			public String getActiveConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
				String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
				String valueJS = getValueJS((String[])currentGenerator.getAttribute(TagGenerator.Attribute.AJAX_ACTIVE_PARRENT), prefix);

				// Вызываем функцию связи в событиях родительского элемента
				// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
				currentGenerator.getDom().add(Tag.Type.SCRIPT,
						(
						" $(\"#visible_${parrent_name}\").change(function(){\n" +
						"		onChange${parrent_name}_${child_name}_ct_ajax_active(this);\n" +
						" }); \n")
						.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				);

				return asyncRequestValueJS(currentGenerator, parrentGenerator, getSetInactiveJS(), getSetActiveJS(), valueJS, "active");
			}
			
		/**
	   * Метод возвращает функционал влияющий на значение элемента 
	   * 
	   * @return код JavaScript
	   */
		public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
			String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
			String valueJS = getValueJS((String[])currentGenerator.getAttribute(TagGenerator.Attribute.AJAX_VALUE_PARRENT), prefix);

			// Вызываем функцию связи в событиях родительского элемента
			// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
			currentGenerator.getDom().add(Tag.Type.SCRIPT,
					(
					" $(\"#visible_${parrent_name}\").change(function(){\n" +
					"		onChange${parrent_name}_${child_name}_ct_ajax_value(this);\n" +
					" }); \n")
					.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
					.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			);

			return asyncRequestValueJS(currentGenerator, parrentGenerator, getCleanValueJS(), getSetValueJS(), valueJS, "value");
		}
	
		protected String asyncRequestValueJS(TagGenerator currentGenerator, TagGenerator parrentGenerator, String initValueJS, String setValueJS, String valueJS, String requestType) {
			String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
			String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);

			String bodyJS = 
								("					function onChange${parrent_name}_${child_name}_ct_ajax_${request_type}(${parrent_name}List){      \n" + 
				"						var valueJS = ${value_js};    \n" + 
				"						var isLoading = false;    \n" + 
				"						if (valueJS.match(/${force_ajax}${value_separator}(none)?(${parameter_separator}|$)/)){ return };           \n" + 
				"						$(\"#${child_name}\").trigger('cleanValue');       \n" + 
				"						$(\"#visible_${child_name}\").trigger('refresh');      \n" + 
				"						$(\"#visible_${parrent_name}\").attr(\"disabled\",\"disabled\");      \n" + 
				"						$(\"#visible_${parrent_name}\").trigger('refresh');      \n" + 
				"						$(\"#background_overlay_wait_${parrent_name}\").show();      \n" + 
				"		            	$(\"#message_box_wait_${parrent_name}\").show();      \n" + 
				"						if (!ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"]) {      \n" + 
				"							ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] = 0;      \n" + 
				"						}      \n" + 
				"						++ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];      \n" + 
				"						if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {   \n" + 
				"							ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;   \n" + 
				"						}   \n" + 
				"						++ajax_is_child_blocked${prefix}[\"${child_name}\"];   \n" + 
				"						ajax({     \n" + 
				"				            	url: '${service}get_${request_type}_interactive',     \n" + 
				"							data: {     \n" + 
				"								parameter_name:'${child_name_api}',     \n" + 
				"								form_api: '${api}',     \n" + 
				"								parameters: valueJS,     \n" + 
				"								rnd: Math.floor(Math.random() * 10000),     \n" + 
				"							},     \n" + 
				"					            type: 'post',     \n" + 
				"					            dataType: 'json',     \n" + 
				"					            contentType: 'application/x-www-form-urlencoded',     \n" + 
				"						}, function (data) {  \n" + 
				"								var elements_present=0;      \n" + 
				"								elements_present=1;      \n" + 
				"								if (data.message) {      \n" + 
				"									$('#visible_${child_name}').css('color', 'red');      \n" + 
				"									$('#visible_${child_name}').html(data.item_error);      \n" + 
				"									${clean_value_js}      \n" + 
				"								} else {      \n" + 
				"									$('#visible_${child_name}').css('color', '');      \n" + 
				"									${set_value_js};      \n" + 
				"								}      \n" + 
				"								var ${child_name} = $('#tr_${child_name}');      \n" + 
				"								if (elements_present==0){      \n" + 
				"									if (\"${hide_if_empty}\"){      \n" + 
				"										${child_name}.css(\"display\", 'none');      \n" + 
				"										$('#is_empty_${child_name}').val(1);      \n" + 
				"									}else{      \n" + 
				"										if ($('#${child_name}').attr('invisible') == 'false') {      \n" + 
				"											if ((document.getElementById && !document.all) || window.opera)      \n" + 
				"												${child_name}.css(\"display\",'table-row');      \n" + 
				"											else      \n" + 
				"												${child_name}.css(\"display\",'inline');      \n" + 
				"										}      \n" + 
				"									}      \n" + 
				"								}else{      \n" + 
				"									if ($('#${child_name}').attr('invisible') == 'false') {Value      \n" + 
				"										if ((document.getElementById && !document.all) || window.opera)      \n" + 
				"											${child_name}.css(\"display\",'table-row');      \n" + 
				"										else      \n" + 
				"											${child_name}.css(\"display\",'inline');      \n" + 
				"									}      \n" + 
				"								}      \n" + 
				"								--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];      \n" + 
				"								$(\"#visible_${child_name}\").trigger('set_find_result');      \n" + 
				"								if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {      \n" + 
				"									if (!$(\"#tr_${parrent_name}\" ).hasClass('disabled')) {  \n" + 
				"										$(\"#visible_${parrent_name}\").removeAttr('disabled');  \n" + 
				"									}  \n" + 
				"									$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');      \n" + 
				"									$(\"#background_overlay_wait_${parrent_name}\").hide();      \n" + 
				"		            						$(\"#message_box_wait_${parrent_name}\").hide();      \n" + 
				"									$(\"#visible_${child_name}\").unbind('set_find_result');    \n" + 
				"								}      \n" + 
				"								--ajax_is_child_blocked${prefix}[\"${child_name}\"]; \n" + 
				"								if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) { \n" + 
				"            								$(\"#visible_${child_name}\").trigger( 'on_child_unblocked'); \n" + 
				"								} \n" + 
			
				"								$(\"#visible_${parrent_name}\").trigger('refresh');      \n" + 
				"								$(\"#visible_${child_name}\").trigger('refresh');      \n" + 
				"						});   \n" + 
				" }      \n")
			.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
			.replace("${parameter_separator}", PARAMETER_SEPARATOR)
			.replace("${force_ajax}", !"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX)) ? ("(?!" + (String) currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX) + ")") : "")
			.replace("${clean_value_js}", initValueJS)
			.replace("${set_value_js}", setValueJS)
			.replace("${handler}", handler)
			.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			.replace("${value_js}", valueJS)
			.replace("${prefix}", prefix)
			.replace("${hide_if_empty}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.HIDE_IF_EMPTY))
			.replace("${api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.API))
			.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			.replace("${child_name_api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
			.replace("${request_type}", requestType)
			.replace("${service}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.SERVICE));
			
			return bodyJS;
			
		}
			
		  /**
		   * Метод формирует значение поля на основе связных элементов  
		   * 
		   * @return код JavaScript
		   */
	
		public static String getValueJS(String[] parrentElements, String prefix) {
			String valueJS = "";
			
			for (Integer i = 0; i < parrentElements.length; i++) {
				String parrentElementName = parrentElements[i];
				valueJS = valueJS.concat("'${parrent_name_api_value_js}' + '${value_separator}' + $('#${parrent_name_value_js}').val()${divider}"
				.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
				.replace("${parrent_name_api_value_js}", parrentElementName)
				.replace("${parrent_name_value_js}", parrentElementName + prefix))
				.replace("${divider}",i < parrentElements.length-1 ? "+ '" + PARAMETER_SEPARATOR + "' +" : "");
				
			}
			valueJS = valueJS.concat(" \n" + 
					"	${increment_operation} Object.keys(uri_params).filter(function callback(currentValue, index, array) {  \n" + 
					"	    return currentValue != \"\"; \n" + 
					"	}).map(function callback(currentValue, index, array) {  \n" + 
					"	    return 'uri_' + currentValue + \"${value_separator}\"  + uri_params[currentValue]; \n" + 
					"	}).join(\"${parameter_separator}\") \n"
			).replace("${increment_operation}", "".equals(valueJS) ? "" : "+ '${parameter_separator}' + ")
			.replace("${parameter_separator}", PARAMETER_SEPARATOR)
			.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR);
			return valueJS;
		}
	
		public Tag getParrent() {
			return parrent;
		}
		
		public void setParrent(Tag parrent) {
			this.parrent = parrent;
		}
	
}