package com.technology.jef.widgets;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xml.sax.SAXException;
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
			DATETIME,
			INN,
			EMAIL,
			MONEY,
			PHONE,
			HIDDEN,
			INFO,
			LABEL,
			POPUP_LIST,
			LIST,
			FILE,
			FILE_SHORT,
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
			SHORT_RADIO_SWITCH_CLEANUP,
			DADATA,
			DADATAJOB,
			AUTO_COMPLETE,
			AUTO_COMPLETE_ADDRESS,
			AUTO_COMPLETE_FILTER,
			AUTO_COMPLETE_EDITABLE_COMPACT,
			AUTO_COMPLETE_EDITABLE,
			TEXT_READ_ONLY,
			FILL_BUTTON,
			BUTTON,
			PRINT,
			HTML,
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
	 * @throws SAXException 
	   */
		public Tag assembleTag(String name, TagGenerator generator, Tag parrent) throws SAXException {
		
			this.setParrent(parrent);
	
			parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, name);
				 put(Tag.Property.NAME, name);
				 put(Tag.Property.TYPE, "hidden");
			}});
			
			Tag resultElement = assembleTag(name, generator);
			resultElement.add(Tag.Type.SCRIPT,								("     \n" + 
	"	$('input#visible_${name}').on('focusin', function () {   \n" + 
	"		$('input#visible_${name}').addClass('widgets_focused');  \n" + 
	"	});    \n" + 
	"	$('input#visible_${name}').on('focusout', function () {   \n" + 
	"		$('input#visible_${name}').removeClass('widgets_focused');  \n" + 
	"	});    \n" + 
	"	$('input#${name}').on('setLocked', function () {    \n" + 
	"			var data = {value: 0};     \n" + 
	"           $(\"[for='visible_${name}']\").addClass('widgets_label_color_disabled');	\n" +
	"           $(\"[for='visible_${name}']\").removeClass('widgets_label_color');	\n" +
	"			${set_active_js}     \n" + 
	"	});     \n" + 
	"	$('input#${name}').on('setUnlocked', function () {     \n" + 
	"			var data = {value: 1};     \n" + 
	"           $(\"[for='visible_${name}']\").addClass('widgets_label_color');	\n" +
	"           $(\"[for='visible_${name}']\").removeClass('widgets_label_color_disabled');	\n" +
	"			${set_active_js}   \n" + 
	"	});     \n")
				.replace("${set_active_js}", getSetActiveJS().replace("${child_name}", name))
				.replace("${name}", name)
			); 

			if ("text".equals(resultElement.getProperty(Tag.Property.TYPE)) || "search".equals(resultElement.getProperty(Tag.Property.TYPE)) || Tag.Type.TEXTAREA.equals(resultElement.getType()) ) {
				resultElement.setProperty(Tag.Property.CLASS, resultElement.getProperty(Tag.Property.CLASS) + "widget first_frames_border widgets_color widgets_height widgets_font");
			}

			for (String parrentName : Stream.of( 
					(String[])generator.getAttribute(TagGenerator.Attribute.AJAX_VISIBLE_PARRENT),
					(String[])generator.getAttribute(TagGenerator.Attribute.AJAX_ACTIVE_PARRENT),
					(String[])generator.getAttribute(TagGenerator.Attribute.AJAX_VALUE_PARRENT),
					(String[])generator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT)
			).flatMap(Stream::of).toArray(String[]::new)) {
				resultElement.add(Tag.Type.SCRIPT,							("    \n" + 
	"	$('input#${name}').on('lock', function () {   \n" + 
	"		$('input#${parrent_name}').trigger('setLocked');  \n" + 
	"	});    \n" + 
	"	$('input#${name}').on('unlock', function () {    \n" + 
	"		if (!$('input#${parrent_name}').attr('data-disabled')) {  \n" + 
	"			$('input#${parrent_name}').trigger('setUnlocked');  \n" + 
	"		} \n" + 
	"	});    \n" + 
	"	$('input#${parrent_name}').on('lock', function () {   \n" + 
	"		$('input#${parrent_name}').trigger('setLocked');  \n" + 
	"		$('input#${name}').trigger('setLocked');  \n" + 
	"	});    \n" + 
	"	$('input#${parrent_name}').on('unlock', function () {    \n" + 
	"		if (!$('input#${parrent_name}').attr('data-disabled')) {  \n" + 
	"			$('input#${parrent_name}').trigger('setUnlocked');  \n" + 
	"		} \n" + 
	"		if (!$('input#${name}').attr('data-disabled')) {  \n" + 
	"			$('input#${name}').trigger('setUnlocked');  \n" + 
	"		} \n" + 
	"	});    \n")
					.replace("${set_active_js}", getSetActiveJS().replace("${child_name}", name))
					.replace("${parrent_name}", parrentName + (String) generator.getAttribute(TagGenerator.Attribute.PREFIX))
					.replace("${name}", name)
				); 
				
			}
			
			resultElement.add(Tag.Type.SCRIPT,				("	$(\"input#${name}\").bind(\"setValueOnLoad\", function (event, data) {       \n" + 
	"		var isLoading = true;       \n" + 
	"		${setValueJS}  \n" + 
	"		$(\"input#${name}\").bind(\"cleanValue\", function(){  \n" + 
	"			if (!isFormLoading) {  \n" + 
	"				${cleanValueJS}  \n" + 
	"			}	 \n" + 
	" 		});  \n" + 
	"	});       \n" + 
	"	$('input#${name}').bind('setHiddenValue',function(event, value){ \n" + 
	"		${setHiddenValueJS}  \n" + 
	"	});       \n" + 
	"	$( '#form_id' ).bind('setListOnLoad_${api}${prefix}', function() {       \n" + 
	"		${setItemsJS}      \n" + 
	"	});       \n" + 
	"	function onChangeReadOnly${name}(current){        \n" + 
	"		if (typeof current.value != 'undefined'){   \n" + 
	"			$('input#${name}').val(current.value || $( current ).attr('value') || $( current ).prop('value') );       //IE9 support \n" + 
	"		}   \n" + 
	"	}        \n" + 
	"	function onKeyDownReadOnly${name}(current){        \n" + 
	"		if(window.event.keyCode == 13){ \n" + 
	"			$('#visible_${name}').trigger('press_enter', [current]);        \n" + 
	"			$('#visible_${name}').val($( current ).attr('value') || $( current ).prop('value')).change();       //IE9 support \n" + 
	"		}        \n" + 
	"	}        \n")
					.replace("${cleanValueJS}", this.getCleanValueJS().replace("${child_name}", name))
					.replace("${setValueJS}", this.getSetValueJS().replace("${child_name}", name))
					.replace("${setHiddenValueJS}",this.getSetHiddenValueJS().replace("${child_name}", name))
					.replace("${name}", name)
					.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
					.replace("${prefix}", (String) generator.getAttribute(TagGenerator.Attribute.PREFIX))
					.replace("${setItemsJS}",
							((String[]) generator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT)).length == 0 // Грузим списки только если нет зависимостей
								? this.getSetItemsJS()
									.replace("${list_item_js}", getListItemJS())
									.replace("${service}", (String) generator.getAttribute(TagGenerator.Attribute.SERVICE))
									.replace("${name}", name)
									.replace("${name_api}", name.replaceAll((String) generator.getAttribute(TagGenerator.Attribute.PREFIX),""))
									.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
								: ""
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
	 * @throws SAXException 
	   */
		protected abstract Tag assembleTag(String name, TagGenerator generator) throws SAXException;

		/**
	   * Метод постобработки DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param element тег созданного элемента
	   * @return DOM модель на текущем уровне
	   */
		protected Tag postAssembleTag(String name, TagGenerator generator, Tag element)  throws SAXException{

			element.add(Tag.Type.SCRIPT, 		("  \n" + 
				"			$('#visible_${name}').bindFirst('blur', function(event){   \n" + 
				"				onChangeReadOnly${name}(event.delegateTarget);   \n" + 
				"			});   \n" + 
				"			$('#visible_${name}').bindFirst('change', function(event){   \n" + 
				"				onChangeReadOnly${name}(event.delegateTarget);   \n" + 
				"			});   \n" + 
				"			$('#visible_${name}').bindFirst('keydown', function(event){   \n" + 
				"				onChangeReadOnly${name}(event.delegateTarget);   \n" + 
				"			});   \n")
				.replace("${name}", name)
				);
			element.add(Tag.Type.SCRIPT, 		("  \n" + 
					"		$(\"#visible_${child_name}\").bind('setValue', function(event, value){      \n" + 
					"			$('#visible_${child_name}').val(value);  \n" + 
					"			$('#visible_${child_name}').change(); \n" + 
					
					// XSS unescape for input fields
					"			if (value) {$('#visible_${child_name}').val(value.replace(/&gt;/g, '>').replace(/&lt;/g, '<'));}  \n" + 

					"			$('input#${child_name}').val(value); \n"+
					"		});     \n")
					.replace("${name_value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
					.replace("${child_name}", name));
			 
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
	 * @throws SAXException 
	   */
		public String getSetItemsJS() throws SAXException {
			
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
			
			return 	(" \n" + 
	"	$('#visible_${child_name}').val(data.value).change(); \n" + 
	// XSS unescape for input fields
	"	if (data.value) {$('#visible_${child_name}').val(data.value.replace(/&gt;/g, '>').replace(/&lt;/g, '<'));}  \n" + 
	"	$('#visible_${child_name}').bind('change', function(){ \n" + 
	"		$('#${system_prefix}_changed_${child_name}').val('1') \n" + 
	"	}); \n" + 
	" \n").replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
		}

	  /**
	   * Метод возвращает функционал отчищающий значение элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 
	   * @return код JavaScript
	   */
		public String getCleanValueJS() {
			// Вызываем change что бы связанные поля тоже отчистились
			return 	(" \n" +
					"	if ($('#visible_${child_name}').val() !== '') {  \n" + 
					"		$('#visible_${child_name}').val(''); \n" + 
					"		$('#visible_${child_name}').change();   \n" + 
					"	}  \n" + 
					" \n");
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
			"		if (data.value) { \n " + 
			"			$('#visible_${child_name}').prop( \"disabled\", false); \n " +
			" 			$( '#tr_${child_name}' ).prop( 'disabled', false);  \n" + 
			"           $(\"#visible_${child_name}\").trigger('refresh');" +
			"		} else { \n " +
			"			$('#visible_${child_name}').prop( \"disabled\", true); \n " +
			" 			$( '#tr_${child_name}' ).prop( 'disabled', true);  \n" + 
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
					" $( '#tr_${child_name}' ).prop( 'disabled', false); \n ";
		}

		/**
	    * Метод возвращает функционал влияющий на состав списочного элемента 
	   * 
	   * @return код JavaScript
		 * @throws SAXException 
	   */
		public String getListConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
			return "";
		}

		  /**
		   * Метод возвращает функционал влияющий на видимость элемента 
		   * 
		   * @return код JavaScript
		 * @throws SAXException 
		   */
			public String getVisibleConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {

				// Вызываем функцию связи в событиях родительского элемента
				// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
				currentGenerator.getDom().add(Tag.Type.SCRIPT,
						(
						" $(\"#visible_${parrent_name}\").bind('change', function(){\n" +
						"		onChange${parrent_name}_${child_name}_ct_ajax_visible(this);\n" +
						" }); \n")
						.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				);
				
				String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
				String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);
				String valueJS = getValueJS(currentGenerator, prefix, TagGenerator.Attribute.AJAX_VISIBLE_PARRENT);
//TODO Нужно убрать блок	ignoreEmptyJS создав евент выключения видимости поля но учесть при этом отчистку списочных полей	
				String ignoreEmptyJS =
						("			if (valueJS.match(/${force_ajax}${value_separator}(none|${fias_code_name_separator})?(${parameter_separator}|$)/)) {\n" + 
								"				$('#tr_${child_name}').hide();\n" + 
								"				${clean_value_js}\n" + 
								"				$('input#${child_name}').attr('invisible', true);\n" + 
								"				$('#tr_${child_name}').trigger('refresh');\n" + 
								"				return;" + 
								"			}")
								.replace("${clean_value_js}", getCleanValueJS())
								.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
								.replace("${parameter_separator}", PARAMETER_SEPARATOR)
								.replace("${fias_code_name_separator}", "\\" + FIAS_CODE_NAME_SEPARATOR)
								.replace("${value_js}", valueJS)
								.replace("${child_name}", currentGenerator.getAttribute(TagGenerator.Attribute.ID) + prefix);

				if (!"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX))) {
					ignoreEmptyJS = ignoreEmptyJS.replace("${force_ajax}", "(^|:p:)(?!" + currentGenerator.getAttribute(TagGenerator.Attribute.ID)  +"|" + ((String) currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX)).replace(",", "|") + ")\\w*");
				} else {
					ignoreEmptyJS = ignoreEmptyJS.replace("${force_ajax}", "(^|:p:)(?!" + currentGenerator.getAttribute(TagGenerator.Attribute.ID)  + ")\\w*");
				}
				
				String bodyJS = 
									("				function onChange${parrent_name}_${child_name}_ct_ajax_visible(${parrent_name}List){         \n" + 
	"				if (!ajax_is_parrent_blocked${prefix}[\"${child_name}\"] || !ajax_is_child_blocked${prefix}[\"${parrent_name}\"]) { // прерывание циклических зависимостей  \n" + 
	"					var valueJS = ${value_js};      \n" + 
	"					${ignore_empty_js}          \n" + 
	"					$('input#${child_name}').attr('invisible', true);         \n" + 
	"					$(\"#background_overlay_wait_${parrent_name}\").show();         \n" + 
	"	            	$(\"#message_box_wait_${parrent_name}\").show();         \n" + 
	"					$(\"input#${parrent_name}\").trigger('lock');          \n" + 
	"					if (!ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"]) {         \n" + 
	"						ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] = 0;         \n" + 
	"					}					++ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];         \n" + 
	"					if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {     \n" + 
	"						ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;     \n" + 
	"					}     \n" + 
	"					++ajax_is_child_blocked${prefix}[\"${child_name}\"];     \n" + 
	"					ajax({         \n" + 
	"			            	url: '${service}get_is_visible_interactive',         \n" + 
	"						data: {         \n" + 
	"							parameter_name:'${child_name_api}',         \n" + 
	"							form_api: '${api}',         \n" + 
	"							parameters: ${value_js},         \n" + 
	"							rnd: Math.floor(Math.random() * 10000),         \n" + 
	"						},        \n" + 
	"				            type: 'post',         \n" + 
	"				            dataType: 'json',         \n" + 
	"				            contentType: 'application/x-www-form-urlencoded',         \n" + 
	"						}, function (data) {   \n" + 
	"								if (data.value) {         \n" + 
	"									$('#tr_${child_name}').show();         \n" + 
	"									$('#tr_preview_${child_name}').show();         \n" + 
	"									$('input#${child_name}').attr('invisible', false);         \n" + 
	"								} else {         \n" + 
	"									$('#tr_${child_name}').hide();         \n" + 
	"									$('#tr_preview_${child_name}').hide();         \n" + 
	"									${clean_value_js}         \n" + 
	"									$('input#${child_name}').attr('invisible', true);         \n" + 
	"								}         \n" + 
	"							$('#tr_${child_name}').trigger('refresh');         \n" + 
	"							--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];    \n" + 
	"							$(\"#visible_${child_name}\").trigger(\"after_load\");         \n" + 
	"							if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {         \n" + 
	"								$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');   \n" + 
	"								$(\"#background_overlay_wait_${parrent_name}\").hide();         \n" + 
	"	            				$(\"#message_box_wait_${parrent_name}\").hide();         \n" + 
	"								$(\"input#${parrent_name}\").trigger('unlock');          \n" + 
	"							}         \n" + 
	"							--ajax_is_child_blocked${prefix}[\"${child_name}\"];  \n" + 
	"							if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) {  \n" + 
	"            							$(\"#visible_${child_name}\").trigger( 'on_child_unblocked');  \n" + 
	"							}  \n" + 
	"							$(\"#visible_${parrent_name}\").trigger('refresh');         \n" + 
	"							$(\"#visible_${child_name}\").trigger('refresh');         \n" + 
	"					});         \n" + 
	"				}         \n" + 
	"			}         \n")
							.replace("${clean_value_js}", getCleanValueJS())
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
		 * @throws SAXException 
		   */
			public String getActiveConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
				String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
				String valueJS = getValueJS(currentGenerator, prefix, TagGenerator.Attribute.AJAX_ACTIVE_PARRENT);

				// Вызываем функцию связи в событиях родительского элемента
				// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
				currentGenerator.getDom().add(Tag.Type.SCRIPT,
						(
						" $(\"#visible_${parrent_name}\").bind('change', function(){\n" +
						"		onChange${parrent_name}_${child_name}_ct_ajax_is_active(this);\n" +
						" }); \n")
						.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				);

				return asyncRequestValueJS(currentGenerator, parrentGenerator, getSetInactiveJS(), 
							(" \n" + 
							"	if ($('input#${child_name}').attr('data-disabled') !== 'attribute') { \n" + 
							"		${set_active_js} \n" + 
							"		if (data.value) { \n" + 
							"           $(\"[for='visible_${name}']\").addClass('widgets_label_color');	\n" +
							"           $(\"[for='visible_${name}']\").removeClass('widgets_label_color_disabled');	\n" +
							"			$('input#${child_name}').removeAttr('data-disabled');  \n" + 
							"		} else { \n" + 
							"           $(\"[for='visible_${name}']\").addClass('widgets_label_color_disabled');	\n" +
							"           $(\"[for='visible_${name}']\").removeClass('widgets_label_color');	\n" +
							"			$('input#${child_name}').attr('data-disabled', 'interactive'); \n" + 
							"		} \n" + 
							"	} \n").replace("${set_active_js}", getSetActiveJS())
					, valueJS
					, "is_active"
				) ;
			}
			
		/**
	   * Метод возвращает функционал влияющий на значение элемента 
	   * 
	   * @return код JavaScript
		 * @throws SAXException 
	   */
		public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
			String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
			String valueJS = getValueJS(currentGenerator, prefix, TagGenerator.Attribute.AJAX_VALUE_PARRENT);

			// Вызываем функцию связи в событиях родительского элемента
			// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
			currentGenerator.getDom().add(Tag.Type.SCRIPT,
						(" \n" + 

	" $(\"#visible_${parrent_name}\").bind('change', function(){ \n" + 
	"	if (!window.isFormLoading) {  // dont call change connected value when form is load \n" + 
	"		onChange${parrent_name}_${child_name}_ct_ajax_value(this); \n" + 
	"	} \n" + 
	" });  \n")
					.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
					.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			);

			return asyncRequestValueJS(currentGenerator, parrentGenerator, getCleanValueJS(), getSetValueJS(), valueJS, "value");
		}
	
		protected String asyncRequestValueJS(TagGenerator currentGenerator, TagGenerator parrentGenerator, String initValueJS, String setValueJS, String valueJS, String requestType) throws SAXException {
			String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
			String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);

			String bodyJS = 
									("				function onChange${parrent_name}_${child_name}_ct_ajax_${request_type}(${parrent_name}List){       \n" + 
	"				if (!ajax_is_parrent_blocked${prefix}[\"${child_name}\"] || !ajax_is_child_blocked${prefix}[\"${parrent_name}\"]) { // прерывание циклических зависимостей  \n" + 
	"						var valueJS = ${value_js};     \n" + 
	"						var isLoading = false;     \n" + 
	"						if (valueJS.match(/${force_ajax}${value_separator}(none|${fias_code_name_separator})?(${parameter_separator}|$)/)){            \n" + 
	"							${clean_before_change}  \n" + 
	"							return;  \n" + 
	"						}  \n" + 
	"						$(\"#visible_${child_name}\").trigger('refresh');       \n" + 
	"						$(\"#background_overlay_wait_${parrent_name}\").show();       \n" + 
	"		            			$(\"#message_box_wait_${parrent_name}\").show();       \n" + 
	"						$(\"input#${parrent_name}\").trigger('lock');          \n" + 
	"						if (!ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"]) {       \n" + 
	"							ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] = 0;       \n" + 
	"						} \n" + 
	"						++ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];       \n" + 
	"						if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {    \n" + 
	"							ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;    \n" + 
	"						}    \n" + 
	"						++ajax_is_child_blocked${prefix}[\"${child_name}\"];    \n" + 
	"						ajax({      \n" + 
	"				            	url: '${service}get_${request_type}_interactive',      \n" + 
	"							data: {      \n" + 
	"								parameter_name:'${child_name_api}',      \n" + 
	"								form_api: '${api}',      \n" + 
	"								parameters: valueJS,      \n" + 
	"								rnd: Math.floor(Math.random() * 10000),      \n" + 
	"							},      \n" + 
	"					            type: 'post',      \n" + 
	"					            dataType: 'json',      \n" + 
	"					            contentType: 'application/x-www-form-urlencoded',      \n" + 
	"						}, function (data) {   \n" + 
	"								var elements_present=0;       \n" + 
	"								elements_present=1;       \n" + 
	"								if (data.message) {       \n" + 
	"									$('#visible_${child_name}').css('color', 'red');       \n" + 
	"									$('#visible_${child_name}').html(data.item_error);       \n" + 
	"									${clean_value_js}       \n" + 
	"								} else {       \n" + 
	"									$('#visible_${child_name}').css('color', '');       \n" + 
	"									${set_value_js};       \n" + 
	"								}       \n" + 
	"								var ${child_name} = $('#tr_${child_name}');       \n" + 
	"								if (elements_present==0){       \n" + 
	"									if (\"${hide_if_empty}\"){       \n" + 
	"										${child_name}.hide();       \n" + 
	"										$('#is_empty_${child_name}').val(1);       \n" + 
	"									}else{       \n" + 
	"										if ($('input#${child_name}').attr('invisible') == 'false') {       \n" + 
	"											${child_name}.show();       \n" + 
	"										}       \n" + 
	"									}       \n" + 
	"								}else{       \n" + 
	"									if ($('input#${child_name}').attr('invisible') == 'false') {       \n" + 
	"										${child_name}.show();       \n" + 
	"									}       \n" + 
	"								}       \n" + 
	"								--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];       \n" + 
	"								$(\"#visible_${child_name}\").trigger('after_load');       \n" + 
	"								if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {       \n" + 
	"									$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');       \n" + 
	"									$(\"#background_overlay_wait_${parrent_name}\").hide();       \n" + 
	"		            				$(\"#message_box_wait_${parrent_name}\").hide();       \n" + 
	"									$(\"input#${parrent_name}\").trigger('unlock');          \n" + 
	"									$(\"#visible_${child_name}\").unbind('after_load');     \n" + 
	"								}       \n" + 
	"								--ajax_is_child_blocked${prefix}[\"${child_name}\"];  \n" + 
	"								if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) {  \n" + 
	"            								$(\"#visible_${child_name}\").trigger( 'on_child_unblocked');  \n" + 
	"								}  \n" + 
	"								$(\"#visible_${parrent_name}\").trigger('refresh');       \n" + 
	"								$(\"#visible_${child_name}\").trigger('refresh'); \n" + 
	"						});    \n" + 
	"					} \n" + 
	" }       \n")
			.replace("${clean_before_change}", "value".equals(requestType) ? "$(\"input#${child_name}\").trigger('cleanValue');" : "")
			.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
			.replace("${parameter_separator}", PARAMETER_SEPARATOR)
			.replace("${fias_code_name_separator}", "\\" + FIAS_CODE_NAME_SEPARATOR)
			.replace("${force_ajax}", 
					!"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX)) 
							? ("(^|:p:)(?!${child_name_api}|" + ((String) currentGenerator
									.getAttribute(TagGenerator.Attribute.FORCE_AJAX)).replace(",", "|") + ")\\w*")
							: "(^|:p:)(?!${child_name_api})\\w*")
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
		 * @throws SAXException 
		   */
	
		public String getValueJS(TagGenerator currentGenerator, String prefix, TagGenerator.Attribute parrentType) throws SAXException {

			String valueJS = "";

			if (currentGenerator != null) {
				LinkedList<String> elements = new LinkedList<String>();
				elements.addAll(Arrays.stream((String[])currentGenerator.getAttribute(parrentType)).map(parameter -> SYSTEM_PARAMETER_PREFIX + "_required_" +  parameter).collect(Collectors.toList()));
				elements.add(SYSTEM_PARAMETER_PREFIX + "_required_" +  currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				elements.addAll(Arrays.stream((String[])currentGenerator.getAttribute(parrentType)).map(parameter -> SYSTEM_PARAMETER_PREFIX + "_changed_" +  parameter).collect(Collectors.toList()));
				elements.add(SYSTEM_PARAMETER_PREFIX + "_changed_" +  currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				elements.addAll(Arrays.stream((String[])currentGenerator.getAttribute(parrentType)).map(parameter -> "visible_" +  parameter).collect(Collectors.toList()));
				Collections.addAll(elements, (String[])currentGenerator.getAttribute(parrentType));
				elements.add("visible_" +  currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				elements.add((String)currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				
				valueJS = String.join("+ '" + PARAMETER_SEPARATOR + "' +", elements.stream().map(elementName -> 
					"'${parrent_name_api_value_js}' + '${value_separator}' + $('input#${parrent_name_value_js}').val()"
							.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
							.replace("${parrent_name_api_value_js}", elementName)
							.replace("${parrent_name_value_js}", elementName + prefix)).collect(Collectors.toList()));
				
				
				//Получаем айди супергруппы если она есть
				Pattern pathPattern = Pattern.compile("^((?:"+GROUP_SEPARATOR+"\\w+?)*)("+GROUP_SEPARATOR+"(\\w+))$");
				Matcher pathMatcher = pathPattern.matcher(prefix);

				if (pathMatcher.matches() && pathMatcher.group(1) != null) {
					valueJS +=  PARAMETER_SEPARATOR + "super_group_id" + PARAMETER_NAME_VALUE_SEPARATOR + "$('#group_id" + pathMatcher.group(1) + "').val()";
				}

					
			}

			valueJS = valueJS.concat(" \n" + 
					"	${increment_operation} Object.keys(getWindowParams()).filter(function callback(currentValue, index, array) {  \n" + 
					"	    return currentValue != \"\"; \n" + 
					"	}).map(function callback(currentValue, index, array) {  \n" + 
					"	    return 'uri_' + currentValue + \"${value_separator}\"  + getWindowParams()[currentValue]; \n" + 
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

		public String getSetHiddenValueJS() {
			return 	"$('input#${child_name}').val(value); \n";
		}
	
}
