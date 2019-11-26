package com.technology.jef.widgets;

import java.util.HashMap;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Виджет всплывающий список
*/
public class PopUpList extends List {

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
				
				return 	("							$(\"#visible_${child_name}\").empty();  \n" + 
						"							$(\"#fake_visible_${child_name}\").val('Не выбрано');  \n" + 
						"							$( \"#popup_${child_name}\" ).remove();  \n" + 
						"							$(\"#${child_name}\").val('');  \n"); 
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
			Tag styledDiv = parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.CLASS, "styled");
			}});

			styledDiv.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "fake_visible_" + name);
				 put(Tag.Property.NAME, "fake_visible_" + name);
				 put(Tag.Property.TYPE, "text");
				 put(Tag.Property.READONLY, "readonly");
				 put(Tag.Property.STYLE, "width:100%;cursor: default;");
				 put(Tag.Property.CLICK, "onPopWindowvisible_" + name + "(this);");
			}});

			Tag elementInput = styledDiv.add(Tag.Type.SELECT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.STYLE, "display: none;");
			}});


			return elementInput;
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
			
			String ignoreEmptyJS = 
					("			if ((valueJS).match(/${force_ajax}${value_separator}(none)?(${parameter_separator}|$)/)) {\n" + 
							"						$(\"#${child_name}\").trigger('cleanValue');       \n" + 
							"							return; \n" + 
							"						} \n")
							.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
							.replace("${parameter_separator}", PARAMETER_SEPARATOR)
							.replace("${child_name}", currentGenerator.getAttribute(TagGenerator.Attribute.ID) + prefix);

			if (!"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX))) {
				ignoreEmptyJS = ignoreEmptyJS.replace("${force_ajax}", "(?!" + (String) currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX) + ")");
			} else {
				ignoreEmptyJS = ignoreEmptyJS.replace("${force_ajax}", "");
			}

			String bodyJS = super.getListConnectJS(currentGenerator, parrentGenerator);
			bodyJS += 
								("					 \n" + 
	"					// реагирование на получение фокуса           \n" + 
	"					function onPopWindowvisible_${child_name}(){           \n" + 
	"						var valueJS = ${value_js};         \n" + 
	"						${ignore_empty_js}           \n" + 
	"						// сохраняем предыдущее значение           \n" + 
	"						var previous_text = $(\"#fake_visible_${child_name}\").val();           \n" + 
	"						var previous_id = $(\"#${child_name}\").val();           \n" + 
	"						$(\"#fake_visible_${child_name}\").attr(\"disabled\",\"disabled\");           \n" + 
	"						$(\"#visible_${child_name}\").empty();           \n" + 
	"						$(\"<option/>\", {'value': '', html: '${loading}'}).appendTo(\"#visible_${child_name}\");           \n" + 
	"						$(\"#fake_visible_${child_name}\").val('${loading}');           \n" + 
	"						//$(\"#visible_${child_name}\").trigger('refresh');           \n" + 
	"						$(\"#background_overlay_wait_${parrent_name}\").show();           \n" + 
	"				            	$(\"#message_box_wait_${parrent_name}\").show();           \n" + 
	"						$(\"#visible_${parrent_name}\").attr(\"disabled\",\"disabled\");           \n" + 
	"						$(\"#visible_${parrent_name}\").trigger('refresh');           \n" + 
	"						if (!ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"]) {           \n" + 
	"							ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] = 0;           \n" + 
	"						}           \n" + 
	"						++ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];           \n" + 
	"						if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {        \n" + 
	"							ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;        \n" + 
	"						}        \n" + 
	"						++ajax_is_child_blocked${prefix}[\"${child_name}\"];        \n" + 
	"						ajax({           \n" + 
	"				            	url: '${service}get_list_interactive',           \n" + 
	"							data: {           \n" + 
	"								parameter_name:'${child_name_api}',           \n" + 
	"								form_api: '${api}',           \n" + 
	"								parameters: valueJS,           \n" + 
	"								no_cache: Math.floor(Math.random() * 10000),           \n" + 
	"							},          \n" + 
	"					            type: 'post',           \n" + 
	"					            dataType: 'json',           \n" + 
	"				           		contentType: 'application/x-www-form-urlencoded',           \n" + 
	"						}, function (data) {   \n" + 
	"								$(\"#visible_${child_name}\").empty();           \n" + 
	"								$(\"#fake_visible_${child_name}\").val('${not_selected}');           \n" + 
	"								// создаем слой           \n" + 
	"								$(\"#fake_visible_${child_name}\").after(\"<div id='popup_${child_name}' class='popup'><p id='column1'></p><p id='column2'></p></div>\");           \n" + 
	"								$(\"#popup_${child_name}\").prepend(\"<h3>${select} \"+$('label[for=\"visible_${child_name}\"]').html()+\"</h3>\");           \n" + 
	"								$(\"#popup_${child_name}\").prepend(\"<div id='popup_${child_name}_close' class='popup_close'>${close}</div>\");           \n" + 
	"								var i = 0;           \n" + 
	"								$(\"<option/>\", {'value': '', html: '---'}).appendTo(\"#visible_${child_name}\");           \n" + 
	"								$.each(data.data, function(key, val) {           \n" + 
	"									$(\"<option/>\", {'value': val.id, html: val.name, 'selected': val.value }).appendTo(\"#visible_${child_name}\");           \n" + 
	"									i += 1;           \n" + 
	"									var bold = val.id==previous_id;   \n" + 
	"									var values = val.name.split(\"${value_separator}\"); \n" + 
	"									if (i % 2 == 0) {           \n" + 
	"										$(\"#column1\").append('<br><br><span class=\"popuplist_header\" id=\"popupelementid_'+val.id+'\"><u>'+(bold?'<b>':'')+values[0]+(bold?'</b>':'') + '</u>' + (values.length > 1 ? values[1] : '') +'</span>');      \n" + 
	"									}else{      \n" + 
	"										$(\"#column2\").append('<br><br><span class=\"popuplist_header\" id=\"popupelementid_'+val.id+'\"><u>'+(bold?'<b>':'')+values[0]+(bold?'</b>':'') + '</u>' + (values.length > 1 ? values[1] : '') +'</span>');      \n" + 
	"									}           \n" + 
	"							  	});           \n" + 
	"								if (i==0){           \n" + 
	"									// удаляем динам окно           \n" + 
	"									$( \"#popup_${child_name}\" ).remove();           \n" + 
	"									$(\"#fake_visible_${child_name}\").val('${list_empty}');           \n" + 
	"									$(\"#${child_name}\").val('');           \n" + 
	"									if (!$(\"#tr_${parrent_name}\" ).hasClass('disabled')) {     \n" + 
	"										$(\"#visible_${parrent_name}\").removeAttr('disabled');     \n" + 
	"									}     \n" + 
	"									$(\"#fake_visible_${child_name}\").removeAttr('disabled');           \n" + 
	"									--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];           \n" + 
	"									$(\"#visible_${child_name}\").trigger('set_find_result');           \n" + 
	"									if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {           \n" + 
	"										if (!$(\"#tr_${parrent_name}\" ).hasClass('disabled')) {    \n" + 
	"											$(\"#visible_${parrent_name}\").removeAttr('disabled');    \n" + 
	"										}    \n" + 
	"										$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');           \n" + 
	"										$(\"#background_overlay_wait_${parrent_name}\").hide();           \n" + 
	"			            						$(\"#message_box_wait_${parrent_name}\").hide();           \n" + 
	"									}           \n" + 
	"									$(\"#visible_${parrent_name}\").trigger('refresh');           \n" + 
	"									//$(\"#visible_${child_name}\").trigger('refresh');           \n" + 
	"									return;           \n" + 
	"								}           \n" + 
	"								$(\"#fake_visible_${child_name}\").removeAttr('disabled');           \n" + 
	"								--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];           \n" + 
	"								$(\"#visible_${child_name}\").trigger('set_find_result');           \n" + 
	"								if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {           \n" + 
	"									if (!$(\"#tr_${parrent_name}\" ).hasClass('disabled')) {   \n" + 
	"										$(\"#visible_${parrent_name}\").removeAttr('disabled');   \n" + 
	"									}   \n" + 
	"									$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');           \n" + 
	"									$(\"#background_overlay_wait_${parrent_name}\").hide();           \n" + 
	"		            						$(\"#message_box_wait_${parrent_name}\").hide();           \n" + 
	"								}           \n" + 
	"								--ajax_is_child_blocked${prefix}[\"${child_name}\"];  \n" + 
	"								if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) {  \n" + 
	"            								$(\"#visible_${child_name}\").trigger( 'on_child_unblocked');  \n" + 
	"								}  \n" + 
	"								$(\"#visible_${parrent_name}\").trigger('refresh');           \n" + 
	"								//$(\"#visible_${child_name}\").trigger('refresh');           \n" + 
	"								// показываем фон           \n" + 
	"								$(\".overlay\").show();           \n" + 
	"								// вешаем обработчик динамически на click на каждый элемент списка popuplist_header           \n" + 
	"								$(document).one(\"click\", \".popuplist_header\", function() {           \n" + 
	"									 // из id извлекаем значение и выводим           \n" + 
	"									 var id_value = $(this).attr('id');           \n" + 
	"									 var arr = id_value.split('_');           \n" + 
	"									 $(\"#fake_visible_${child_name}\").val($(this).find(\"u\").text());           \n" + 
	"									 $(\"#visible_${child_name}\").val(arr[1]);           \n" + 
	"									 $(\"#${child_name}\").val(arr[1]);           \n" + 
	"									 // закрываем фон           \n" + 
	"									 $(\".overlay\").hide();           \n" + 
	"									 // удаляем динам окно           \n" + 
	"									 $(\"#popup_${child_name}\" ).remove();           \n" + 
	"									 // посылаем событие на обновление           \n" + 
	"									 $(\"#visible_${child_name}\").change();           \n" + 
	"									 $(\"#visible_${child_name}\").trigger('refresh');           \n" + 
	"								});           \n" + 
	"								// вешаем обработчик динамически на click на закрыть           \n" + 
	"								$(document).one(\"click\", \".popup_close\", function() {           \n" + 
	"									// закрываем фон           \n" + 
	"									$(\".overlay\").hide();           \n" + 
	"									// удаляем динам окно           \n" + 
	"									$(\"#popup_${child_name}\").remove();           \n" + 
	"									// восстанавливаем предыдущий выбор           \n" + 
	"									$(\"#fake_visible_${child_name}\").val(previous_text);           \n" + 
	"									$(\"#${child_name}\").val(previous_id);           \n" + 
	"									$(\"#visible_${child_name}\").val(previous_id);           \n" + 
	"									// посылаем событие на обновление           \n" + 
	"									//$(\"#visible_${child_name}\").change();           \n" + 
	"								});           \n" + 
	"						});           \n" + 
	"					}           \n")
			.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
			.replace("${select}", CurrentLocale.getInstance().getTextSource().getString("select"))
			.replace("${close}", CurrentLocale.getInstance().getTextSource().getString("close"))
			.replace("${list_empty}", CurrentLocale.getInstance().getTextSource().getString("list_empty"))
			.replace("${loading}", CurrentLocale.getInstance().getTextSource().getString("loading"))
			.replace("${not_selected}", CurrentLocale.getInstance().getTextSource().getString("not_selected"))
			.replace("${handler}", handler)
			.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
			.replace("${ignore_empty_js}", ignoreEmptyJS)
			.replace("${value_js}", valueJS)
			.replace("${prefix}", prefix)
			.replace("${api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.API))
			.replace("${child_name}", name)
			.replace("${child_name_api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
			.replace("${service}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.SERVICE));
					

			return bodyJS;
		}
		
		
		public String getSetValueJS() {
			// Добавить условие для загрузки зависимых списков (пока работает только для незаполненных ранее)
			return  	("	if (isLoading) {  \n" + 
						"		$(\"<option/>\", {'value': '', html: '---'}).appendTo(\"#visible_${child_name}\");    \n" + 
						"		$(\"<option/>\", {'value': data.value, html: data.name, 'selected': true }).appendTo(\"#visible_${child_name}\");    \n" + 
						"		$(\"#visible_${child_name}\").trigger('refresh');  \n" + 
						"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
						"		$(\"#fake_visible_${child_name}\").val(data.name);  \n" + 
						"	} else { \n" + 
						"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
						"	} \n");
		}

}