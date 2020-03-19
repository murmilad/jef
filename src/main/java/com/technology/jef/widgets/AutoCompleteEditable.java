package com.technology.jef.widgets;

import java.util.HashMap;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Виджет список выподающий на основе введенного текста
*/
public class AutoCompleteEditable extends Widget {

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
		   * Метод возвращает функционал отчищающий значение элемента
		   * Применяемые переменные: 
		   * 	${child_name} - имя текущего (зависимого) элемента
		   * 
		   * @return код JavaScript
		   */
			public String getCleanValueJS() {
				
				return 		(" \n" + 
	"	$('#visible_${child_name}').val('---');   \n" + 
	"	$('input#${child_name}').val('');   \n" + 
	" ");
			}

	  /**
	   * Метод формирования DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param parrent родительский тег в DOM модели
	   * @return DOM модель на текущем уровне
	   */
		@Override
		public Tag assembleTag(String name, TagGenerator generator) {

			String[] ajax_parrent_list = (String[])generator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT);

			String prefix = (String) generator.getAttribute(TagGenerator.Attribute.PREFIX);
			String nameAPI = name.replace(prefix, "");
			String valueJS = getValueJS(ajax_parrent_list, prefix);


			parrent.add(Tag.Type.SCRIPT, 
															("                       \n" + 
	"			$('#visible_${name}').on('input', function () {                       \n" + 
	"				// ручной ввод без выбора из списка                       \n" + 
	"				if ($(\"#visible_${name}\").val()){                       \n" + 
	"					$(\"#visible_\"+\"${name}\").addClass('warning');                       \n" + 
	"					$(\"#visible_\"+\"${name}\").attr('title', '${couldnt_find}');                       \n" + 
	"				}                       \n" + 
	"			}); \n" + 
	"			$(document).ready(function() {        \n" + 
	"				if ($._data( $(\"#visible_${name}\")[0], \"events\" ).change) {        \n" + 
	"					jQuery.map(jQuery.grep($._data( $(\"#visible_${name}\")[0], \"events\" ).change, function( a ) {         \n" + 
	"						return a.handler.toString().indexOf('onChange') >= 0;         \n" + 
	"					}), function( a ) {         \n" + 
	"						$('#visible_${name}').bind('autoCompleteChange', a.handler);         \n" + 
	"						$('#visible_${name}').unbind('change', a.handler)         \n" + 
	"					});         \n" + 
	"				}     \n" + 
	"			}); \n" + 
	"			$('#visible_${name}').autocomplete({                       \n" + 
	"					serviceUrl: '${service}get_list${interactive}',         \n" + 
	"					type: 'POST',         \n" + 
	"					paramName:'value_1',// основной параметр для поиска                           \n" + 
	"					minChars:0,                           \n" + 
	"					showNoSuggestionNotice: true,                           \n" + 
	"					noSuggestionNotice: '${couldnt_find}',                           \n" + 
	"					params:{			// доп параметры                           \n" + 
	"						parameter_name:'${name_api}',                             \n" + 
	"						form_api:'${api}',                             \n" + 
	"					},                           \n" + 
	"					preventBadQueries: false,                             \n" + 
	"					forceFixPosition: true,                             \n" + 
	"					zIndex:99,                             \n" + 
	"					deferRequestBy: 1000,                             \n" + 
	"					dataType: 'text',    \n" + 
	"					ajaxSettings: {   \n" + 
	"						dataType: 'json',    \n" + 
	"						contentType: 'application/x-www-form-urlencoded; charset=UTF-8',	   \n" + 
	"					},   \n" + 
	"					onSelect: function (suggestion) {                       \n" + 
	"						// ставим выбранное значение в hidden поле                       \n" + 
	"						$('input#${name}').val(suggestion.data);       \n" + 
	"						$('#visible_${name}').val(suggestion.name).change();                       \n" + 
	"						$('#visible_${name}').removeClass('warning');                       \n" + 
	"						$('#visible_${name}').attr('title', '');                       \n" + 
	"						$('#visible_${name}').trigger('autoCompleteChange');         \n" + 
	"					},                       \n" + 
	"					onSearchError: function (query, jqXHR, textStatus, errorThrown) {     \n" + 
	"						$('#background_overlay_wait_${name}').hide();     \n" + 
	"						$('#message_box_wait_${name}').hide();     \n" + 
	"						$(\"input#${name}\").trigger('unlock');            \n" + 
	"						showError(\"Error: \" + errorThrown, jqXHR.responseText + 'Parameters:' + query + '<br><br>');       \n" + 
	"					},     \n" + 
	"					transformResult: function(response) {        \n" + 
	"						response.data.unshift({name: '---', id: ''})  \n" + 
	"						var query = $('#visible_${name_api}${prefix}').val();                           \n" + 
	"						return {        \n" + 
	"									suggestions: $.map(response.data, function(dataItem) {         \n" + 
	"										var index = dataItem.name.toLowerCase().indexOf(query.toLowerCase());            \n" + 
	"										var visible_name =  index >= 0 ? dataItem.name.substr(0, index) + '<b>' + query + '</b>' + dataItem.name.substr(index+query.length, dataItem.name.length) : dataItem.name;            \n" + 
	"										return { value: dataItem.name, data: dataItem.id, name: dataItem.name, html: visible_name};                          \n" + 
	"									})                       \n" + 
	"						};        \n" + 
	"					},        \n" + 
	"					onSearchStart: function (params) {                       \n" + 
	"						if (!$('#visible_${name}').is(\":visible\")){ // динамический visible вызывает у элемента change :( приходится проверять видим элемент или нет                       \n" + 
	"							return false;                       \n" + 
	"						}                       \n" + 
	"						if ($('#message_box_wait_${name}').is(\":visible\")){ // не вызывать поиск пока выполняется этот же запрос                               \n" + 
	"							return false;                               \n" + 
	"						}                               \n" + 
	"						// динам параметры для формирования GET к ajax - сам запрос                       \n" + 
	"						params['parameters']='${name_api}${value_separator}' + ($('#visible_${name_api}${prefix}').val() == '---' ? '' : $('#visible_${name_api}${prefix}').val());                    \n" + 
	"						// модифицируем params чтобы передать реальные значения параметров - parent                      \n" + 
	"						params['parameters'] += '${parameter_separator}' + ${value_js};           \n" + 
	"						$('#background_overlay_wait_${name}').show();       \n" + 
	"						$('#message_box_wait_${name}').show();       \n" + 
	"						$(\"input#${name}\").trigger('lock');            \n" + 
	"					},                       \n" + 
	"					onSearchComplete: function (query, suggestions) {       \n" + 
	"						$('#background_overlay_wait_${name}').hide();       \n" + 
	"						$('#message_box_wait_${name}').hide();       \n" + 
	"						$(\"input#${name}\").trigger('unlock');            \n" + 
	"						if (suggestions.length==0){       \n" + 
	"							// ничего не нашли - очищаем значение в hidden поле и оставляем введенное пользователем значение       \n" + 
	"							$('input#${name}').val('');          \n" + 
	"						}       \n" + 
	"					} ,     \n" + 
	"					formatResult:function (suggestion, currentValue) {             \n" + 
	"						return \"<div data-field='${name}' data-id='\"+suggestion.data+\"' data-name = '\"+suggestion.name +\"'> \" + suggestion.html + \"</div>\";             \n" + 
	"					},                             \n" + 
	"			});                       \n")
						.replace("${prefix}", (String) generator.getAttribute(TagGenerator.Attribute.PREFIX))
						.replace("${couldnt_find}", CurrentLocale.getInstance().getTextSource().getString("couldnt_find"))
						.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
						.replace("${parameter_separator}", PARAMETER_SEPARATOR)
						.replace("${value_js}", valueJS)
						.replace("${name}", name)
						.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
						.replace("${id}", name)
						.replace("${name_api}", nameAPI)
						.replace("${service}", (String) generator.getAttribute(TagGenerator.Attribute.SERVICE))
						.replace("${child_name_api}", (String) generator.getAttribute(TagGenerator.Attribute.ID))
						.replace("${interactive}", ajax_parrent_list.length > 0 ? "_interactive" : "")

			);

			
			Tag span = parrent.add(Tag.Type.SPAN, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.CLASS, "combobox");
			}});
			Tag input = span.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "search");
				 put(Tag.Property.STYLE, "padding-right:0px;width:100%;margin-top:1px;margin-bottom:1px;");
//				 put(Tag.Property.CHANGE, 	("var group_postfix='';  \n" + 
//				"			if (match = '${name}'.match(/_group_.*/)){  \n" + 
//				"				group_postfix = match[0]  \n" + 
//				"		}  \n" + 
//				"		// если есть дочернии то их чистим  \n" + 
//				"		var list = \"${children_list}\";  \n" + 
//				"		if (list != \"\") { \n" + 
//				"			$.each(list.split(','), function(i, name){  \n" + 
//				"				$(\"#\"+name+group_postfix).val('');  \n" + 
//				"				$(\"#visible_\"+name+group_postfix).val('');  \n" + 
//				"				$(\"#visible_\"+name+group_postfix).removeClass('warning');  \n" + 
//				"			}); \n" + 
//				"		}")
//					.replace("${children_list}", String.join(",", ((String[])generator.getAttribute(TagGenerator.Attribute.AJAX_VALUE_CHILD))))
//					.replace("${name}", name));
			}});
			span.add(Tag.Type.SPAN, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.TABINDEX, "-1");
				 put(Tag.Property.ID, "downarrow__" + name);
				 put(Tag.Property.CLASS, "downarrow downarrow_size downarrow_color");
				 put(Tag.Property.CLICK, "if ($(\"[data-field='${name}']:visible\").length === 0) {$('#visible_${name}').focus();} ".replace("${name}", name));
			}});
			
			return input;
		}

		/**
	   * Метод постобработки DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param element тег созданного элемента
	   * @return DOM модель на текущем уровне
	   */
		protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {


			return element;
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
				
				return  		("  \n" + 
	"	if (data.value != '') { \n" + 
	"		$('#visible_${child_name}').val(data.name).blur().change().trigger('autoCompleteChange');  \n" + 
	"		$('input#${child_name}').val(data.value);  \n" + 
	"	} else { \n" + 
	"		$('#visible_${child_name}').val('---').blur().change().trigger('autoCompleteChange');  \n" + 
	"		$('input#${child_name}').val('');  \n" + 
	"	} \n" + 
	"	$('#visible_${child_name}').bind('change', function(){ \n" + 
	"		$('#${system_prefix}_changed_${child_name}').val('1') \n" + 
	"	}); \n" 
	).replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX)
				;
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
							" $(\"#visible_${parrent_name}\").on('change', function(){\n" +
							"		onChange${parrent_name}_${child_name}_ct_ajax_list(this); \n" +
							" }); \n")
							.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
							.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
					);
					
					String bodyJS =
							("		function onChange${parrent_name}_${child_name}_ct_ajax_list(${parrent_name}List){      \n" + 
							"						$(\"input#${child_name}\").trigger('cleanValue');       \n" + 
							"		}      \n")
					.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
					.replace("${child_name}", name);

					return bodyJS;
				}
				
				/**
			   * Метод возвращает функционал влияющий на значение элемента 
			   * 
			   * @return код JavaScript
			   */
				public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
					String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
					String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);
					String valueJS = getValueJS((String[])currentGenerator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT), prefix);
					String name = ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(prefix);

					// Вызываем функцию связи в событиях родительского элемента
					// Пишем процедуру в DOM дочернего элемента для корректной обработки мулттиформ
					currentGenerator.getDom().add(Tag.Type.SCRIPT,
							(
							" $(\"#visible_${parrent_name}\").bind('change', function(){\n" +
							"		onChange${parrent_name}_${child_name}_ct_ajax_value(this);\n" +
							" }); \n")
							.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
							.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
					);

					
					String bodyJS =
							("		function onChange${parrent_name}_${child_name}_ct_ajax_value(${parrent_name}List){      \n" + 
							"						$(\"input#${child_name}\").trigger('cleanValue');       \n" + 
							"		}      \n")
					.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
					.replace("${child_name}", name);

					return bodyJS;
				}


}
