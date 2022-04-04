package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

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
				// Вызываем change что бы связанные поля тоже отчистились
				return ("	$('#visible_${child_name}').val(''); \n" + 
						"	${child_name}_autocomplete_result = []; \n" + 
						"       $('input#${child_name}').val('|'); \n" +
						"	$('#visible_${child_name}').change();   \n" 
						);
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
		@Override
		public Tag assembleTag(String name, TagGenerator generator) throws SAXException {

			String[] ajax_parrent_list = (String[])generator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT);

			String prefix = (String) generator.getAttribute(TagGenerator.Attribute.PREFIX);
			String nameAPI = name.replace(prefix, "");
			String valueJS = getValueJS(generator, prefix, TagGenerator.Attribute.AJAX_LIST_PARRENT);

			//reg, real prefixes
			String addressPrefix = nameAPI.replaceFirst("([a-zA-Z]+_)[\\w]+", "$1");
			addressPrefix = nameAPI.equals(addressPrefix) ? "" :addressPrefix; 
			

			 parrent.add(Tag.Type.SCRIPT, 
																					("                             \n" + 
	"			$('#visible_${name}').on('input', function () {                             \n" + 
	"				// ручной ввод без выбора из списка                             \n" + 
	"				if ($(\"#visible_${name}\").val()){                             \n" + 
	"					$(\"#visible_\"+\"${name}\").addClass('warning_color');                             \n" + 
	"					$(\"#visible_\"+\"${name}\").attr('title', '${couldnt_find}');                             \n" + 
	"					if (\"visible_${name}\".indexOf('house')!==-1){ // номер дома без fias_code - получаем общегородской индекс                             \n" + 
	"						ajax({                             \n" + 
	"					      	      	url: '${service}get_list${interactive}',                              \n" + 
	"							data: {                              \n" + 
	"								parameter_name: '${child_name_api}',                              \n" + 
	"								form_api: '${api}',                              \n" + 
	"								parameters: ${value_js},                          \n" + 
	"								rnd: Math.floor(Math.random() * 10000),                              \n" + 
	"							},                             \n" + 
	"							type: \"POST\",                             \n" + 
	"					        dataType: 'json',                          \n" + 
	"				           	contentType: 'application/x-www-form-urlencoded',                          \n" + 
	"						}, function (response) {               \n" + 
	"								if (response.value) {                             \n" + 
	"									$('input#${address_prefix}'+'post_index${prefix}').val(response.value);                             \n" + 
	"									$('#visible_'+'${address_prefix}'+'post_index${prefix}').val(response.value);                             \n" + 
	"								}                             \n" + 
	"						});                             \n" + 
	"					}                             \n" + 
	"				}                             \n" + 
	"				$(\"input#${name}\").val('|'+$(\"#visible_${name}\").val());                             \n" + 
	"			});              \n" + 
	"			$('#visible_${name}').autocomplete({         \n" + 
	"					serviceUrl: '${service}get_list${interactive}',         \n" + 
	"					type: 'POST',         \n" + 
	"					paramName:'value_1',// основной параметр для поиска                           \n" + 
	"					zIndex:99,                                  \n" + 
	"					minChars:0,                           \n" + 
	"					showNoSuggestionNotice: true,                           \n" + 
	"					noSuggestionNotice: '${couldnt_find}',                           \n" + 
	"					params:{			// доп параметры                           \n" + 
	"						parameter_name:'${name_api}',                             \n" + 
	"						form_api:'${api}',                             \n" + 
	"					},                           \n" + 
	"					preventBadQueries: false,                             \n" + 
	"					forceFixPosition: true,                             \n" + 
	"					deferRequestBy: 1000,                             \n" + 
	"					dataType: 'text',    \n" + 
	"					ajaxSettings: {   \n" + 
	"						dataType: 'json',    \n" + 
	"						contentType: 'application/x-www-form-urlencoded; charset=UTF-8',	   \n" + 
	"					},   \n" + 
	"					onSelect: function (suggestion) {                             \n" + 
	"							// ставим выбранное значение в hidden поле                             \n" + 
	"						$('input#${name}').val((suggestion.data||'')+'|'+(suggestion.name||'')); // значение через разделитель                             \n" + 
	"						$('#visible_${name}').val(suggestion.name).change();                              \n" + 
	"						$('#visible_${name}').removeClass('warning_color');                             \n" + 
	"						$('#visible_${name}').attr('title', '');                             \n" + 
	"							if (\"visible_${name}\".indexOf('house')!==-1){                             \n" + 
	"								// для дома подтягиваются индекс строение и корпус                             \n" + 
	"                                				$('input#${address_prefix}'+'building${prefix}').val(suggestion.building);                             \n" + 
	"				                            $('input#${address_prefix}'+'section${prefix}').val(suggestion.block);                             \n" + 
	"								$('input#${address_prefix}'+'block${prefix}').val(suggestion.block);                             \n" + 
	"                                				$('input#${address_prefix}'+'post_index${prefix}').val(suggestion.post_index);                             \n" + 
	"								$('#visible_'+'${address_prefix}'+'building${prefix}').val(suggestion.building);                             \n" + 
	"								$('#visible_'+'${address_prefix}'+'section${prefix}').val(suggestion.block);                             \n" + 
	"								$('#visible_'+'${address_prefix}'+'block${prefix}').val(suggestion.block);                             \n" + 
	"								$('#visible_'+'${address_prefix}'+'post_index${prefix}').val(suggestion.post_index);                             \n" + 
	"							}                             \n" + 
	"			                     check_addr_field('real', 'visible_real_and_reg_equals');               \n" + 
	"						$('#visible_${name}').trigger('autoCompleteChange');               \n" + 
	"					},                             \n" + 
	"					onSearchError: function (query, jqXHR, textStatus, errorThrown) {     \n" + 
	"						$('#error_${name}').empty().hide();       \n" + 
	"						$('#background_overlay_wait_${name}').hide();     \n" + 
	"						$('#message_box_wait_${name}').hide();     \n" + 
	"						showError(\"Error: \" + errorThrown, jqXHR.responseText + 'Parameters:' + query + '<br><br>');       \n" + 
	"						$('#is_loading').val('0'); \n" +  
	"					},     \n" + 
	"					transformResult: function(response) {        \n" + 
	"						var query = $('#visible_${name_api}${prefix}').val();                           \n" + 
	"						var isHouse = (\"visible_${name}\".indexOf('house')!==-1);      \n" + 
	"						if (response.data.length > 200) {      \n" + 
	"							response.data[199] =  {name: '...', disabled: true};      \n" + 
	"						}      \n" + 
	"						$('#error_${name}').empty().hide();       \n" + 
	"						return {               \n" + 
	"							suggestions: response.data.slice(0, 200).filter(function(dataItem) {     \n" + 
	"								if (dataItem.error) {  \n" + 
	"									$('#error_${name}').show();       \n" + 
	"									$('#error_${name}').append('<span style=\"color:red\">' + dataItem.error + '</span>' );       \n" + 
	"									return false;       \n" + 
	"								}  \n" + 
	"							    return true; \n" + 
	"							}).map( function(dataItem) {         \n" + 
	"								var index = dataItem.name.toLowerCase().indexOf(query.toLowerCase());                   \n" + 
	"								var visible_name =  dataItem.name.substr(0, index) + '<b>' + query + '</b>' + dataItem.name.substr(index+query.length, dataItem.name.length);                   \n" + 
	"								if (isHouse){                                   \n" + 
	"									return { value: dataItem.name + (dataItem.block ? ' корп '+dataItem.block : '') + (dataItem.building ? ' стр '+dataItem.building : ''),                                   \n" + 
	"									        data: dataItem.id,                                   \n" + 
	"											 post_index: dataItem.post_index,                                   \n" + 
	"											 name: dataItem.name,                \n" + 
	"											 html: visible_name,                \n" + 
	"											 building:dataItem.building,                                   \n" + 
	"											block: dataItem.block,    \n" + 
	"											disabled: dataItem.disabled,                                   \n" + 
	"									};                                   \n" + 
	"								}else{                                   \n" + 
	"									return { value: dataItem.name, data: dataItem.id, name: dataItem.name, html: visible_name, disabled: dataItem.disabled, };                                   \n" + 
	"								}                                   \n" + 
	"							})                                   \n" + 
	"						};               \n" + 
	"					},        \n" + 
	"					onSearchStart: function (params) {                             \n" + 
	"						if (!$('#visible_${name}').is(\":visible\")){ // динамический visible вызывает у элемента change :( приходится проверять видим элемент или нет                             \n" + 
	"							return false;                             \n" + 
	"						}                             \n" + 
	"						if ($('#message_box_wait_${name}').is(\":visible\")){ // не вызывать поиск пока выполняется этот же запрос                                    \n" + 
	"							return false;                                    \n" + 
	"						}                                    \n" + 
	"						// если значение было заполнено ранее то прерываем поиск, все равно будет одна запись                             \n" + 
	"						var fias_code = $('input#${name}').val(); fias_code = fias_code.substring(0, fias_code.indexOf('|'));                             \n" + 
	"						if($('#visible_${name}').val() && fias_code){                             \n" + 
	"							return false;                             \n" + 
	"						}                             \n" + 
	"						// модифицируем params чтобы передать реальные значения параметров - parent                             \n" + 
	"						params['parameters'] = (${value_js}).replace('${name_api}${value_separator}|', '${name_api}${value_separator}');                       \n" + 
	"						$('#background_overlay_wait_${name}').show();       \n" + 
	"						$('#message_box_wait_${name}').show();       \n" + 
	"						$('#is_loading').val('1'); \n" +  
	"					},       \n" + 
	"					onSearchComplete: function (query, suggestions) {       \n" + 
	"						$('#background_overlay_wait_${name}').hide();       \n" + 
	"						$('#message_box_wait_${name}').hide();       \n" + 
	"						$('#is_loading').val('0'); \n" +  
	"						if (suggestions.length==0){       \n" + 
	"							// ничего не нашли - очищаем значение в hidden поле и оставляем введенное пользователем значение       \n" + 
	"							$('input#${name}').val('|'+$('#visible_${name}').val());       \n" + 
	"						}       \n" + 
	"					} ,     \n" + 
	"					formatResult:function (suggestion, currentValue) {             \n" + 
	"						if (suggestion.error) {  \n" + 
	"							$('#error_${name}').show();       \n" + 
	"							$('#error_${name}').append('<span style=\"color:red\">' + suggestion.error + '</span>' );       \n" + 
	"							return '';       \n" + 
	"						}  \n" + 
	"						if (suggestion.disabled) {  \n" + 
	"							return \"<div style='color:gray;' > \" + suggestion.html + \"</div>\";  \n" + 
	"						} else {  \n" + 
	"							return \"<div data-field='${name}' data-id='\"+suggestion.data+\"' data-name = '\"+suggestion.name +\"'> \" + suggestion.html + \"</div>\";                    \n" + 
	"						}  \n" + 
	"					},                             \n" + 
	"				});      \n")
						.replace("${couldnt_find}", CurrentLocale.getInstance().getTextSource().getString("couldnt_find"))
						.replace("${prefix}", (String) generator.getAttribute(TagGenerator.Attribute.PREFIX))
						.replace("${sector}", CurrentLocale.getInstance().getTextSource().getString("sector"))
						.replace("${building}", CurrentLocale.getInstance().getTextSource().getString("building"))
						.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
						.replace("${parameter_separator}", PARAMETER_SEPARATOR)
						.replace("${value_js}", valueJS)
						.replace("${name}", name)
						.replace("${address_prefix}", addressPrefix)
						.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
						.replace("${name_api}", nameAPI)
						.replace("${service}", (String) generator.getAttribute(TagGenerator.Attribute.SERVICE))
						.replace("${child_name_api}", (String) generator.getAttribute(TagGenerator.Attribute.ID))
						.replace("${interactive}", ajax_parrent_list.length > 0 ? "_interactive" : "")

			);

			parrent.add(Tag.Type.SPAN, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.CLASS, "combobox");
			}});
			
			return parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "search");
			     put(Tag.Property.STYLE, "padding-right:0px;width:100%;-webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;");
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
//				"				$(\"#visible_\"+name+group_postfix).removeClass('warning_color');  \n" + 
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

			element.add(Tag.Type.SCRIPT, 		("  \n" + 
					"		$(\"#visible_${child_name}\").bind('setValue', function(event, value){      \n" + 
					"				$('input#${child_name}').val(value.split('${name_value_separator}')[1]);        \n" + 
					"				$('#visible_${child_name}').val(value.split('${name_value_separator}')[0]);                        \n" + 
					"				$('#visible_${child_name}').trigger('autoCompleteChange');                        \n" + 
					"		});     \n")
					.replace("${name_value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
					.replace("${child_name}", name));

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
	"			$('input#${child_name}').trigger('setHiddenValue',[data.value]);   \n" + // установка значения идет первой потому что при вызове в дальнейшем метода change зависимые поля должны уже видеть изменение в текущем поле  
	"	$('#visible_${child_name}').val(data.value.split('|').pop()).blur().change().trigger('autoCompleteChange'); \n" + 
	"	if (data.value.split('|').shift() || !data.value.split('|').pop()) { \n" + 
	"		$('#visible_${child_name}').removeClass('warning_color');                              \n" + 
	"	} else  { \n" + 
	"		$('#visible_${child_name}').addClass('warning_color');                              \n" + 
	// XSS unescape for input fields
	"		if (data.value) {$('#visible_${child_name}').val(data.value.split('|').pop().replace(/&gt;/g, '>').replace(/&lt;/g, '<'));}  \n" + 
	"	}  \n" + 
	"	$('#visible_${child_name}').bind('change', function(){  \n" + 
	"		$('#${system_prefix}_changed_${child_name}').val('1')  \n" + 
	"	});  \n").replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX) 
				;
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
				


}
