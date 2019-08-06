package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

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
	   */
		public Tag assembleTag(String name, TagGenerator generator) {
			Tag mainInput = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "fieldset_" + name);
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
				
				return 		("							$(\"#visible_${child_name}\").empty();  \n" + 
						"							$(\"<option/>\", {'value': 'none', html: '---'}).appendTo(\"#visible_${child_name}\");  \n" + 
						"						  	if ($(\"#other_${child_name}\").length > 0) {  \n" + 
						"								$(\"<option/>\", {'value': 'other', html: 'Иное', 'selected': $(\"#other_${child_name}\").value }).appendTo(\"#visible_${child_name}\");  \n" + 
						"						  	}  \n" + 
						"							$(\"#visible_${child_name}\").val('').change();  \n"
						);
			}

	   /**
	   * Метод возвращает функционал влияющий на состав списочного элемента 
	   * 
	   * @return код JavaScript
	   */
		public String getListConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
			String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
			String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);
			String valueJS = getValueJS((String[])currentGenerator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT), prefix);
			String name = ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(prefix);

			// Вызываем функцию связи в событиях родительского элемента
			// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
			currentGenerator.getDom().add(Tag.Type.SCRIPT,
					(
					" $(\"#visible_${parrent_name}\").change(function(){\n" +
					"		onChange${parrent_name}_${child_name}_ct_ajax_list(this);\n" +
					" }); \n")
					.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
					.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			);
			
			String bodyJS =
					("					function onChange${parrent_name}_${child_name}_ct_ajax_list(${parrent_name}List){           \n" + 
			"						var valueJS = ${value_js}; \n" + 
			"						$(\"#${child_name}\").trigger('cleanValue');       \n" + 
			"						if (valueJS.match(/${force_ajax}:p:(none)?(:i:|$)/)){ return };           \n" + 
			"						$(\"#visible_${child_name}\").unbind(\"focusin\");          \n" + 
			"						$(\"#visible_${child_name}\").focusin( function() {          \n" + 
			"							var value = $(\"#visible_${child_name}\").val(); \n" + 
			"							$(\"#visible_${child_name}\").empty();           \n" + 
			"							$(\"<option/>\", {'value': 'none', html: 'Загрузка...'}).appendTo(\"#visible_${child_name}\");           \n" + 
			"							$(\"#visible_${child_name}\").trigger('refresh');           \n" + 
			"							$(\"#background_overlay_wait_${parrent_name}\").show();           \n" + 
			"		            				$(\"#message_box_wait_${parrent_name}\").show();           \n" + 
			"							$(\"#visible_${parrent_name}\").attr(\"disabled\",\"disabled\");           \n" + 
			"							$(\"#visible_${parrent_name}\").trigger('refresh');           \n" + 
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
			"									application_id: $(\"#id\").val(),        \n" + 
			"									form_api: '${api}',        \n" + 
			"									form_id:$(\"#form_id\").val(),        \n" + 
			"									parameters: valueJS,        \n" + 
			"									city_id: params.city_id,                \n" + 
			"									application_id: params.application_id,                \n" + 
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
			"											$('#visible_${child_name}').val('none');           \n" + 
			"											$('#${child_name}').val('none');           \n" + 
			"											$('#is_empty_${child_name}').val(1);           \n" + 
			"										}else{           \n" + 
			"											if ($('#${child_name}').attr('invisible') == 'false') {           \n" + 
			"												if ((document.getElementById && !document.all) || window.opera)           \n" + 
			"													${child_name}.css(\"display\",'table-row');           \n" + 
			"												else           \n" + 
			"													${child_name}.css(\"display\",'inline');           \n" + 
			"											}           \n" + 
			"										}           \n" + 
			"									}else{           \n" + 
			"										if ($('#${child_name}').attr('invisible') == 'false') {           \n" + 
			"											if ((document.getElementById && !document.all) || window.opera)           \n" + 
			"												${child_name}.css(\"display\",'table-row');           \n" + 
			"											else           \n" + 
			"												${child_name}.css(\"display\",'inline');           \n" + 
			"										}           \n" + 
			"									}           \n" + 
			"									--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];           \n" + 
			"									$(\"#visible_${child_name}\").trigger('set_find_result');           \n" + 
			"									if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {           \n" + 
			"										if (!$(\"#tr_${parrent_name}\" ).hasClass('disabled')) {     \n" + 
			"											$(\"#visible_${parrent_name}\").removeAttr('disabled');     \n" + 
			"										}     \n" + 
			"										$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');           \n" + 
			"										$(\"#background_overlay_wait_${parrent_name}\").hide();           \n" + 
			"					      	      				$(\"#message_box_wait_${parrent_name}\").hide();           \n" + 
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
			"					}           \n")
			.replace("${force_ajax}", !"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX)) ? ("(?!" + (String) currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX) + ")") : "")
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
							("		$(\"<option/>\", {'value': 'none', html: '---'}).appendTo(\"#visible_${name}\");   \n" + 
	"		$.each(data.data, function(key, val) {   \n" + 
	"			$(\"<option/>\", {'value': val.id, html: val.name, 'selected': val.value }).appendTo(\"#visible_${name}\");   \n" + 
	"			if (val.value) {  \n" + 
	"				value = val.id  \n" + 
	"			}  \n" + 
	"		});   \n" + 
	"		if ($(\"#other_${name}\").length > 0) {   \n" + 
	"			$(\"<option/>\", {'value': 'other', html: 'Иное', 'selected': $(\"#other_${name}\").value }).appendTo(\"#visible_${name}\");   \n" + 
	"		}  \n" + 
	"		$(\"#visible_${name}\").click(); \n"); 
		}

	  /**
	   * Метод возвращает функционал устанавливающий список для выбора значения элемента
	   * Применяемые переменные: 
	   * 	${name} - имя текущего (зависимого) элемента
	   * 	${api} - API для получения данных
	   * 	${list_item_js} - JS для добавления элемента для выбора
	   * 
	   * @return код JavaScript
	   */
		public String getSetItemsJS() {
			
			return 			("	$(\"#visible_${name}\").focusin( function() {     \n" + 
							"			$(\"#background_overlay_wait_${name}\").show();        \n" + 
							"			$(\"#message_box_wait_${name}\").show();        \n" + 
							"			$(\"#visible_${name}\").attr(\"disabled\",\"disabled\");        \n" + 
							"			params['form_api'] = \"${api}\";    \n" + 
							"			params['parameter_name'] = \"${name}\";    \n" + 
							"			ajax({    \n" + 
							"					url: \"${service}\" + \"get_list\",   \n" + 
							"					data:  params,   \n" + 
							"					type: \"post\",   \n" + 
							"					dataType: \"json\",  \n" + 
							"					contentType: 'application/x-www-form-urlencoded'  \n" + 
							"				}, function( data ) {    \n" + 
							"					var value = $(\"#visible_${name}\").val();  \n" + 
							"					$(\"#visible_${name}\").empty();        \n" + 
							"					${list_item_js}        \n" + 
							"					$(\"#visible_${name}\").removeAttr('disabled');        \n" + 
							"					$(\"#background_overlay_wait_${name}\").hide();        \n" + 
							"					$(\"#message_box_wait_${name}\").hide();        \n" + 
							"					$(\"#visible_${name}\").trigger('refresh');        \n" + 
							"					$(\"#visible_${name}\").unbind(\"focusin\");       \n" + 
							"					if (value) {    \n" + 
							"						$(\"#visible_${name}\").val(value);    \n" + 
							"						$(\"#${name}\").val(value);    \n" + 
							"					}    \n" + 
							"					$(\"#visible_${name}\").find('input').styler({});  \n" + 
							"			});       \n" + 
							"	});     \n");
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
							"		$(\"<option/>\", {'value': 'none', html: '---'}).appendTo(\"#visible_${child_name}\");    \n" + 
							"		$(\"<option/>\", {'value': data.value, html: data.name, 'selected': true }).appendTo(\"#visible_${child_name}\");    \n" + 
							"		$(\"#visible_${child_name}\").trigger('refresh');  \n" + 
							"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
							"	} else { \n" + 
							"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
							"	} \n");
			}

}
