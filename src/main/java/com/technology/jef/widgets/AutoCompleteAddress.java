package com.technology.jef.widgets;

import java.util.HashMap;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Виджет список выподающий на основе введенного текста
*/
public class AutoCompleteAddress extends Widget {

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
				
				return ("	$('#visible_${child_name}').val(''); \n" + 
						"	${child_name}_autocomplete_result = []; \n" + 
						"       $('#${child_name}').val('|'); \n"
						);
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

			//reg, real prefixes
			String addressPrefix = nameAPI.replaceFirst("([a-zA-Z]+_)[\\w]+", "$1");
			addressPrefix = nameAPI.equals(addressPrefix) ? "" :addressPrefix; 
			

			 parrent.add(Tag.Type.SCRIPT, 
																				("                            \n" + 
	"		var ${name}_autocomplete_result = [];                \n" + 
	"        $( document ).ready(function() {                            \n" + 
	"			var group_postfix='';                            \n" + 
	"			if (match = 'visible_${name}'.match(/_group_.*/)){                            \n" + 
	"				group_postfix = match[0]                            \n" + 
	"			}                            \n" + 
	"			$('#visible_${name}').on('input', function () {                            \n" + 
	"				// ручной ввод без выбора из списка                            \n" + 
	"				if ($(\"#visible_${name}\").val()){                            \n" + 
	"					$(\"#visible_\"+\"${name}\").addClass('warning');                            \n" + 
	"					$(\"#visible_\"+\"${name}\").attr('title', '${couldnt_find}');                            \n" + 
	"					if (\"visible_${name}\".indexOf('house')!==-1){ // номер дома без fias_code - получаем общегородской индекс                            \n" + 
	"						ajax({                            \n" + 
	"					      	      	url: '${service}get_list_interactive',                             \n" + 
	"							data: {                             \n" + 
	"								parameter_name: '${child_name_api}',                             \n" + 
	"								form_api: '${api}',                             \n" + 
	"								parameters: ${value_js},                         \n" + 
	"								rnd: Math.floor(Math.random() * 10000),                             \n" + 
	"							},                            \n" + 
	"							type: \"POST\",                            \n" + 
	"					        dataType: 'json',                         \n" + 
	"				           	contentType: 'application/x-www-form-urlencoded',                         \n" + 
	"						}, function (response) {              \n" + 
	"								$('#${group_prefix}'+'post_index'+group_postfix).val(response.value);                            \n" + 
	"								$('#visible_'+'${group_prefix}'+'post_index'+group_postfix).val(response.value);                            \n" + 
	"						});                            \n" + 
	"					}                            \n" + 
	"				}                            \n" + 
	"				$(\"#${name}\").val('|'+$(\"#visible_${name}\").val());                            \n" + 
	"			});             \n" + 
	"			if ($._data( $(\"#visible_${name}\")[0], \"events\" ).change) {             \n" + 
	"				jQuery.map(jQuery.grep($._data( $(\"#visible_${name}\")[0], \"events\" ).change, function( a ) {              \n" + 
	"					return a.handler.toString().indexOf('onChange') >= 0;              \n" + 
	"				}), function( a ) {              \n" + 
	"					$('#visible_${name}').bind('autoCompleteChange', a.handler);              \n" + 
	"					$('#visible_${name}').unbind('change', a.handler)              \n" + 
	"				});              \n" + 
	"			}             \n" + 
	"			$('#visible_${name}').autocomplete({        \n" + 
	"					serviceUrl: '${service}get_list_interactive',        \n" + 
	"					type: 'POST',        \n" + 
	"					paramName:'value_1',// основной параметр для поиска                          \n" + 
	"					minChars:0,                          \n" + 
	"					showNoSuggestionNotice: true,                          \n" + 
	"					noSuggestionNotice: '${couldnt_find}',                          \n" + 
	"					params:{			// доп параметры                          \n" + 
	"						parameter_name:'${name_api}',                            \n" + 
	"						form_api:'${api}',                            \n" + 
	"					},                          \n" + 
	"					preventBadQueries: false,                            \n" + 
	"					forceFixPosition: true,                            \n" + 
	"					deferRequestBy: 1000,                            \n" + 
	"					dataType: 'text',   \n" + 
	"					ajaxSettings: {  \n" + 
	"						dataType: 'json',   \n" + 
	"						contentType: 'application/x-www-form-urlencoded; charset=UTF-8',	  \n" + 
	"					},  \n" + 
	"					onSelect: function (suggestion) {                            \n" + 
	"							// ставим выбранное значение в hidden поле                            \n" + 
	"						$('#${name}').val((suggestion.data||'')+'|'+(suggestion.name||'')); // значение через разделитель                            \n" + 
	"						$('#visible_${name}').val(suggestion.name).change();                             \n" + 
	"						$('#visible_${name}').removeClass('warning');                            \n" + 
	"						$('#visible_${name}').attr('title', '');                            \n" + 
	"						if (\"visible_${name}\".indexOf('region_code')!==-1){                            \n" + 
	"								// для регион фед значения в город ставим значение региона                            \n" + 
	"								var FEDERAL_REGIONS = [                            \n" + 
	"								'0c5b2444-70a0-4932-980c-b4dc0d3f02b5', // Moscow                            \n" + 
	"								'c2deb16a-0330-4f05-821f-1d09c93331e6', // Spb                            \n" + 
	"								'6fdecb78-893a-4e3f-a5ba-aa062459463b', // Baykonur                            \n" + 
	"								'63ed1a35-4be6-4564-a1ec-0c51f7383314'  // Sevastopol                            \n" + 
	"								];                            \n" + 
	"								if (FEDERAL_REGIONS.indexOf(suggestion.data)!==-1){                            \n" + 
	"										$('#visible_'+'${group_prefix}'+'city_code'+group_postfix).val(suggestion.name);                            \n" + 
	"										$('#${group_prefix}'+'city_code'+group_postfix).val((suggestion.data||'')+'|'+(suggestion.name||'')); // значение через разделитель                            \n" + 
	"								}                            \n" + 
	"						}                            \n" + 
	"							if (\"visible_${name}\".indexOf('house')!==-1){                            \n" + 
	"								// для дома подтягиваются индекс строение и корпус                            \n" + 
	"                                				$('#${group_prefix}'+'building'+group_postfix).val(suggestion.building);                            \n" + 
	"				                            $('#${group_prefix}'+'section'+group_postfix).val(suggestion.block);                            \n" + 
	"								$('#${group_prefix}'+'block'+group_postfix).val(suggestion.block);                            \n" + 
	"                                				$('#${group_prefix}'+'post_index'+group_postfix).val(suggestion.post_index);                            \n" + 
	"								$('#visible_'+'${group_prefix}'+'building'+group_postfix).val(suggestion.building);                            \n" + 
	"								$('#visible_'+'${group_prefix}'+'section'+group_postfix).val(suggestion.block);                            \n" + 
	"								$('#visible_'+'${group_prefix}'+'block'+group_postfix).val(suggestion.block);                            \n" + 
	"								$('#visible_'+'${group_prefix}'+'post_index'+group_postfix).val(suggestion.post_index);                            \n" + 
	"							}                            \n" + 
	"			                     check_addr_field('real', 'visible_real_and_reg_equals');              \n" + 
	"						$('#visible_${name}').trigger('autoCompleteChange');              \n" + 
	"					},                            \n" + 
	"					onSearchError: function (query, jqXHR, textStatus, errorThrown) {    \n" + 
	"						$('#background_overlay_wait_${name}').hide();    \n" + 
	"						$('#message_box_wait_${name}').hide();    \n" + 
	"						showError(\"Error: \" + errorThrown, jqXHR.responseText + 'Parameters:' + query + '<br><br>');      \n" + 
	"					},    \n" + 
	"					transformResult: function(response) {       \n" + 
	"						var query = $('#visible_${name_api}'+group_postfix).val();                          \n" + 
	"						return {       \n" + 
	"							suggestions: $.map(response.data, function(dataItem) {  \n" + 
	"								var index = dataItem.name.toLowerCase().indexOf(query.toLowerCase());           \n" + 
	"								var visible_name =  dataItem.name.substr(0, index) + '<b>' + query + '</b>' + dataItem.name.substr(index+query.length, dataItem.name.length);           \n" + 
	"								if (\"visible_${name}\".indexOf('house')!==-1){                           \n" + 
	"									return { value: dataItem.name + (dataItem.block ? ' ${sector} '+dataItem.block : '') + (dataItem.building ? ' ${building} '+dataItem.building : ''),                           \n" + 
	"									        data: dataItem.id,                           \n" + 
	"											 post_index: dataItem.post_index,                           \n" + 
	"											 name: dataItem.name,        \n" + 
	"											 html: visible_name,        \n" + 
	"											 building:dataItem.building,                           \n" + 
	"											block: dataItem.block                           \n" + 
	"									};                           \n" + 
	"								}else{                           \n" + 
	"									return { value: dataItem.name, data: dataItem.id, name: dataItem.name, html: visible_name };                           \n" + 
	"								}                           \n" + 
	"							})                           \n" + 
	"						};       \n" + 
	"					},       \n" + 
	"					onSearchStart: function (params) {                            \n" + 
	"						if (!$('#visible_${name}').is(\":visible\")){ // динамический visible вызывает у элемента change :( приходится проверять видим элемент или нет                            \n" + 
	"							return false;                            \n" + 
	"						}                            \n" + 
	"						// если значение было заполнено ранее то прерываем поиск, все равно будет одна запись                            \n" + 
	"						var fias_code = $('#${name}').val(); fias_code = fias_code.substring(0, fias_code.indexOf('|'));                            \n" + 
	"						if($('#visible_${name}').val() && fias_code){                            \n" + 
	"							return false;                            \n" + 
	"						}                            \n" + 
	"						// динам параметры для формирования GET к ajax - сам запрос                            \n" + 
	"						params['parameters']='${name_api}${value_separator}' + $('#visible_${name_api}'+group_postfix).val();                        \n" + 
	"						// модифицируем params чтобы передать реальные значения параметров - parent                            \n" + 
	"						params['parameters'] += '${parameter_separator}' + ${value_js};                      \n" + 
	"						$('#background_overlay_wait_${name}').show();      \n" + 
	"						$('#message_box_wait_${name}').show();      \n" + 
	"					},      \n" + 
	"					onSearchComplete: function (query, suggestions) {      \n" + 
	"						$('#background_overlay_wait_${name}').hide();      \n" + 
	"						$('#message_box_wait_${name}').hide();      \n" + 
	"						if (suggestions.length==0){      \n" + 
	"							// ничего не нашли - очищаем значение в hidden поле и оставляем введенное пользователем значение      \n" + 
	"							$('#${name}').val('|'+$('#visible_${name}').val());      \n" + 
	"						}      \n" + 
	"					} ,    \n" + 
	"					formatResult:function (suggestion, currentValue) {            \n" + 
	"						return \"<div data-field='${name}' data-id='\"+suggestion.data+\"' data-name = '\"+suggestion.name +\"'> \" + suggestion.html + \"</div>\";            \n" + 
	"					},                            \n" + 
	"				});     \n" + 
	"			});                \n")
						.replace("${couldnt_find}", CurrentLocale.getInstance().getTextSource().getString("couldnt_find"))
						.replace("${sector}", CurrentLocale.getInstance().getTextSource().getString("sector"))
						.replace("${building}", CurrentLocale.getInstance().getTextSource().getString("building"))
						.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
						.replace("${parameter_separator}", PARAMETER_SEPARATOR)
						.replace("${value_js}", valueJS)
						.replace("${name}", name)
						.replace("${group_prefix}", addressPrefix)
						.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
						.replace("${name_api}", nameAPI)
						.replace("${service}", (String) generator.getAttribute(TagGenerator.Attribute.SERVICE))
						.replace("${child_name_api}", (String) generator.getAttribute(TagGenerator.Attribute.ID))

			);

			
			return parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "search");
				 put(Tag.Property.STYLE, "padding-right:0px;width:100%;");
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
				
				return  	" \n" + 
				"	$('#visible_${child_name}').val(data.value.split('|').pop()).blur(); \n" + 
				"	$('#${child_name}').val(data.value); \n"  
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
							" $(\"#visible_${parrent_name}\").on('blur', function(){\n" +
							"		onChange${parrent_name}_${child_name}_ct_ajax_list(this); \n" +
							" }); \n")
							.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
							.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
					);
					
					String bodyJS =
							("		function onChange${parrent_name}_${child_name}_ct_ajax_list(${parrent_name}List){      \n" + 
							"						$(\"#${child_name}\").trigger('cleanValue');       \n" + 
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
							" $(\"#visible_${parrent_name}\").change(function(){\n" +
							"		onChange${parrent_name}_${child_name}_ct_ajax_value(this);\n" +
							" }); \n")
							.replace("${parrent_name}", ((String)parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
							.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
					);

					
					String bodyJS =
							("		function onChange${parrent_name}_${child_name}_ct_ajax_value(${parrent_name}List){      \n" + 
							"						$(\"#${child_name}\").trigger('cleanValue');       \n" + 
							"		}      \n")
					.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
					.replace("${child_name}", name);

					return bodyJS;
				}


}
