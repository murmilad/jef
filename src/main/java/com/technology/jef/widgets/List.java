package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Виджет Выпадающий список
*/
public class List extends Widget {

	  /**
	   * Метод возвращает тип виджета
	   * 
	   * ViewType.DOUBLE - С заголовком слева
	   * ViewType.SINGLE - Без заголовка слева  
	   * 
	   * @return тип виджета
	   */
		public ViewType getType () {
			return ViewType.SINGLE;
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
		public Tag assembleTag(String name, TagGenerator generator) throws SAXException {
			Tag mainInput = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.ID, "fieldset_" + name);
				put(Tag.Property.CLASS, "fieldset second_frames_border");
			}});

			Tag elementInput = mainInput
					.add(Tag.Type.LEGEND)
					.add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.FOR, "visible_" + name);
					}})
					.add(Tag.Type.SPAN, (String) generator.getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.NAME, "span_" + name);
						 put(Tag.Property.STYLE, !"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED)) ? "color: rgb(170, 0, 0);" : "color: rgb(0, 0, 0);");
					}})
					.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
							 put(Tag.Property.CLASS, "styled");
					}})
					.add(Tag.Type.SELECT, new HashMap<Tag.Property, String>(){{
						put(Tag.Property.ID, "visible_" + name);
						put(Tag.Property.NAME, "visible_" + name);
						put(Tag.Property.STYLE, "width: 100%;");
						put(Tag.Property.CLASS, "select first_text_color");
					}});
			
			if (!"".equals(generator.getAttribute(TagGenerator.Attribute.HIDE_IF_EMPTY))) {
				mainInput.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.ID, "is_empty_" + name);
					put(Tag.Property.NAME, "is_empty_" + name);
					put(Tag.Property.TYPE, "hidden");
				}});
			}

			return elementInput;
		}

		/**
		   * Метод возвращает функционал отчищающий значение элемента
		   * Применяемые переменные: 
		   * 	${child_name} - имя текущего (зависимого) элемента
		   * 
		   * @return код JavaScript
		   */
			public String getCleanValueJS() {
				
				return 			("							$(\"#visible_${child_name}\").empty();   \n" + 
	"							$(\"<option/>\", {'value': '', html: '---'}).appendTo(\"#visible_${child_name}\");   \n" + 
	"						  	if ($(\"#other_${child_name}\").length > 0) {   \n" + 
	"								$(\"<option/>\", {'value': 'other', html: '${other}', 'selected': $(\"#other_${child_name}\").value }).appendTo(\"#visible_${child_name}\");   \n" + 
	"						  	}   \n" +
	"							if ($('#visible_${child_name}').val() !== '') {  \n" + 
	"								$(\"#visible_${child_name}\").val('').change();   \n" + 
	"							}  \n")
						.replace("${other}", CurrentLocale.getInstance().getTextSource().getString("other"));
			}

	   /**
	   * Метод возвращает функционал влияющий на состав списочного элемента 
	   * 
	   * @return код JavaScript
	 * @throws SAXException 
	   */
		public String getListConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
			String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
			String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);
			String valueJS = getValueJS(currentGenerator, prefix, TagGenerator.Attribute.AJAX_LIST_PARRENT);
			String name = ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(prefix);

			// Вызываем функцию связи в событиях родительского элемента
			// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
			currentGenerator.getDom().add(Tag.Type.SCRIPT,
					(
					" $(\"#visible_${parrent_name}\").bind('change', function(){\n" +
					"		onChange${parrent_name}_${child_name}_ct_ajax_list(this);\n" +
					" }); \n")
					.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
					.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			);
			
			String bodyJS =
					("					function onChange${parrent_name}_${child_name}_ct_ajax_list(${parrent_name}List){           \n" + 
			"					if (!ajax_is_parrent_blocked${prefix}[\"${child_name}\"]) { // прерывание циклических зависимостей  \n" + 
			"						var valueJS = ${value_js}; \n" + 
			"						$(\"input#${child_name}\").trigger('cleanValue');       \n" + 
			"						if (valueJS.match(/${force_ajax}${value_separator}(none|${fias_code_name_separator})?(${parameter_separator}|$)/)){ return };           \n" + 
			"						$(\"#visible_${child_name}\").unbind(\"focusin\");          \n" + 
			"						$(\"#visible_${child_name}\").focusin( function() {          \n" + 
			"							var value = $(\"#visible_${child_name}\").val(); \n" + 
			"							$(\"#visible_${child_name}\").empty();           \n" + 
			"							$(\"<option/>\", {'value': '', html: '${loading}'}).appendTo(\"#visible_${child_name}\");           \n" + 
			"							$(\"#visible_${child_name}\").trigger('refresh');           \n" + 
			"							$(\"#background_overlay_wait_${parrent_name}\").show();           \n" + 
			"            				$(\"#message_box_wait_${parrent_name}\").show();           \n" + 
			"							$(\"input#${parrent_name}\").trigger('lock');         \n" + 
			"							if (!ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"]) {           \n" + 
			"								ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] = 0;           \n" + 
			"							}           \n" + 
			"							++ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];          \n" + 
			"							if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {      \n" + 
			"								ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;      \n" + 
			"							}      \n" + 
			"							++ajax_is_child_blocked${prefix}[\"${child_name}\"];      \n" + 
			"							ajax({        \n" + 
			"					            	url: '${service}get_list_interactive',        \n" + 
			"								data: {        \n" + 
			"									parameter_name:'${child_name_api}',        \n" + 
			"									form_api: '${api}',        \n" + 
			"									parameters: valueJS,        \n" + 
			"									rnd: Math.floor(Math.random() * 10000),        \n" + 
			"								},       \n" + 
			"						            	type: 'post',        \n" + 
			"						           	dataType: 'json',        \n" + 
			"				            			contentType: 'application/x-www-form-urlencoded',        \n" + 
			"							}, function (data) {  \n" + 
			"									$(\"#visible_${child_name}\").unbind(\"focusin\");          \n" + 
			"									$(\"#visible_${child_name}\").empty();           \n" + 
			"									${list_item_js}           \n" + 
			"									var ${child_name} = $('#tr_${child_name}');           \n" + 
			"									if (data.data && data.data.length > 0){           \n" + 
			"										if (\"${hide_if_empty}\"){           \n" + 
			"											${child_name}.css(\"display\", 'none');           \n" + 
			"											$('#visible_${child_name}').val('');           \n" + 
			"											$('input#${child_name}').val('');           \n" + 
			"											$('#is_empty_${child_name}').val(1);           \n" + 
			"										}else{           \n" + 
			"											if ($('input#${child_name}').attr('invisible') == 'false') {           \n" + 
			"												if ((document.getElementById && !document.all) || window.opera)           \n" + 
			"													${child_name}.css(\"display\",'table-row');           \n" + 
			"												else           \n" + 
			"													${child_name}.css(\"display\",'inline');           \n" + 
			"											}           \n" + 
			"										}           \n" + 
			"									}else{           \n" + 
			"										if ($('input#${child_name}').attr('invisible') == 'false') {           \n" + 
			"											if ((document.getElementById && !document.all) || window.opera)           \n" + 
			"												${child_name}.css(\"display\",'table-row');           \n" + 
			"											else           \n" + 
			"												${child_name}.css(\"display\",'inline');           \n" + 
			"										}           \n" + 
			"									}           \n" + 
			"									--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];           \n" + 
			"									$(\"#visible_${child_name}\").trigger('set_find_result');           \n" + 
			"									if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {           \n" + 
			"										$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');           \n" + 
			"										$(\"#background_overlay_wait_${parrent_name}\").hide();           \n" + 
			"					      	      		$(\"#message_box_wait_${parrent_name}\").hide();           \n" + 
			"										$(\"input#${parrent_name}\").trigger('unlock');         \n" + 
			"									}           \n" + 
			"									--ajax_is_child_blocked${prefix}[\"${child_name}\"];    \n" + 
			"									if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) {    \n" + 
			"					            				$(\"#visible_${child_name}\").trigger( 'on_child_unblocked');    \n" + 
			"									}    \n" + 
			"									$(\"#visible_${parrent_name}\").trigger('refresh');           \n" + 
			"									$(\"#visible_${child_name}\").trigger('refresh');           \n" + 
			"									if (value) {   \n" + 
			"										$(\"#visible_${child_name}\").val(value).change();   \n" + 
			"									}   \n" + 
			"							});          \n" + 
			"						});          \n" + 
			"					}          \n" + 
			"				}           \n")
			.replace("${loading}", CurrentLocale.getInstance().getTextSource().getString("loading"))
			.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
			.replace("${parameter_separator}", PARAMETER_SEPARATOR)
			.replace("${fias_code_name_separator}", "\\" + FIAS_CODE_NAME_SEPARATOR)
			.replace("${force_ajax}", !"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX)) 
					? ("(^|:p:)(?!${child_name_api}|" + ((String) currentGenerator
							.getAttribute(TagGenerator.Attribute.FORCE_AJAX)).replace(",", "|") + ")\\w*")
					: "(^|:p:)(?!${child_name_api})\\w*")
			.replace("${value_js}", valueJS)
			.replace("${list_item_js}", getListItemJS().replace("${name}", name))
			.replace("${handler}", handler)
			.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			.replace("${value_js}", valueJS)
			.replace("${prefix}", prefix)
			.replace("${api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.API))
			.replace("${child_name}", name)
			.replace("${child_name_api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
			.replace("${hide_if_empty}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.HIDE_IF_EMPTY))
			.replace("${service}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.SERVICE));

			return bodyJS;
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
			return
							("		$(\"<option/>\", {'value': '', html: '---'}).appendTo(\"#visible_${name}\");   \n" + 
	"		$.each(data.data, function(key, val) {   \n" + 
	"			$(\"<option/>\", {'value': val.id, html: val.name, 'selected': val.value }).appendTo(\"#visible_${name}\");   \n" + 
	"			if (val.value) {  \n" + 
	"				value = val.id  \n" + 
	"			}  \n" + 
	"		});   \n" + 
	"		if ($(\"#other_${name}\").length > 0) {   \n" + 
	"			$(\"<option/>\", {'value': 'other', html: '${other}', 'selected': $(\"#other_${name}\").value }).appendTo(\"#visible_${name}\");   \n" + 
	"		}  \n" + 
	"		$(\"#visible_${name}\").click(); \n")
							.replace("${other}", CurrentLocale.getInstance().getTextSource().getString("other")); 
		}

	  /**
	   * Метод возвращает функционал устанавливающий список для выбора значения элемента
	   * Применяемые переменные: 
	   * 	${name} - имя текущего (зависимого) элемента
	   * 	${api} - API для получения данных
	   * 	${list_item_js} - JS для добавления элемента для выбора
	   * 
	   * @return код JavaScript
	 * @throws SAXException 
	   */
		public String getSetItemsJS() throws SAXException {

			String valueJS = getValueJS(null , "", null);
			
			return 			("	$(\"#visible_${name}\").focusin( function() {     \n" + 
							"			$(\"#background_overlay_wait_${name}\").show();        \n" + 
							"			$(\"#message_box_wait_${name}\").show();        \n" + 
							"			$(\"input#${name}\").trigger('lock');         \n" + 
							"			$(\"#visible_${name}\").attr(\"disabled\",\"disabled\");        \n" + 
							"			$(\"#visible_${name}\").addClass(\"second_color\");        \n" + 

							"			ajax({    \n" + 
							"					url: \"${service}\" + \"get_list\",   \n" + 
							"					data: { \n" + 
							"						form_api: \"${api}\", \n" + 
							"						parameter_name: \"${name_api}\", \n" + 
							"						parameters: ${value_js}, \n" + 
							"					},   \n" + 
							"					type: \"post\",   \n" + 
							"					dataType: \"json\",  \n" + 
							"					contentType: 'application/x-www-form-urlencoded'  \n" + 
							"				}, function( data ) {    \n" + 
							"					var value = $(\"#visible_${name}\").val();  \n" + 
							"					$(\"#visible_${name}\").empty();        \n" + 
							"					${list_item_js}        \n" + 
							"					$(\"#visible_${name}\").removeAttr('disabled');        \n" + 
							"					$(\"#visible_${name}\").removeClass(\"second_color\");        \n" + 
							"					$(\"#background_overlay_wait_${name}\").hide();        \n" + 
							"					$(\"#message_box_wait_${name}\").hide();        \n" + 
							"					$(\"input#${name}\").trigger('unlock');         \n" + 
							"					$(\"#visible_${name}\").trigger('refresh');        \n" + 
							"					$(\"#visible_${name}\").unbind(\"focusin\");       \n" + 
							"					if (value) {    \n" + 
							"						$(\"#visible_${name}\").val(value);    \n" + 
							"						$(\"input#${name}\").val(value);    \n" + 
							"					}    \n" + 
							"					$(\"#visible_${name}\").find('input').styler({});  \n" + 
							"			});       \n" + 
							"	});     \n").replace("${value_js}", valueJS);
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
				// Добавить условие для загрузки зависимых списков (пока работает только для незаполненных ранее)
				return  	("	if (isLoading) {  \n" + 
							"		$(\"<option/>\", {'value': '', html: '---'}).appendTo(\"#visible_${child_name}\");    \n" + 
							"		$(\"<option/>\", {'value': data.value, html: data.name, 'selected': true }).appendTo(\"#visible_${child_name}\");    \n" + 
							"		$(\"#visible_${child_name}\").trigger('refresh');  \n" + 
							"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
							"		$('#visible_${child_name}').bind('change', function(){ \n" + 
							"			$('#${system_prefix}_changed_${child_name}').val('1') \n" + 
							"		}); \n" + 
							"	} else { \n" + 
							"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
							"	} \n").replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
			}

}
