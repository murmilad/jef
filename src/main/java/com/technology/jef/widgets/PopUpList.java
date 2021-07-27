package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Виджет всплывающий список
*/
public class PopUpList extends Widget {

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
						"							$(\"#fake_visible_${child_name}\").val('${not_selected}');  \n" + 
						"							$( \"#popup_${child_name}\" ).remove();  \n" + 
						"							$(\"input#${child_name}\").val('');  \n")
						.replace("${not_selected}", CurrentLocale.getInstance().getTextSource().getString("not_selected")); 
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

			generator.getDom().add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "overlay_" + name);
				 put(Tag.Property.NAME, "overlay_" + name);
				 put(Tag.Property.CLASS, "background_overlay_form_loading background_color");
				 put(Tag.Property.STYLE, "top: 0; left:0; display: none; background: rgba(255, 255, 255, 0.7)");
			}});			
			
			Tag styledDiv = parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.CLASS, "styled");
			}});

			Tag elementInput = styledDiv.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "fake_visible_" + name);
				 put(Tag.Property.NAME, "fake_visible_" + name);
				 put(Tag.Property.TYPE, "text");
				 put(Tag.Property.READONLY, "readonly");
				 put(Tag.Property.STYLE, "width:100%;cursor: default;");
			}});

			styledDiv.add(Tag.Type.SELECT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.STYLE, "display: none;");
			}});
			String prefix = (String) generator.getAttribute(TagGenerator.Attribute.PREFIX);
			String handler = (String) generator.getAttribute(TagGenerator.Attribute.HANDLER);
			String valueJS = getValueJS(generator, prefix, TagGenerator.Attribute.AJAX_LIST_PARRENT);

			String bodyJS = 
					("					  \n" + 
" 				$(\"#fake_visible_${child_name}\").bind('click', function(){\n" +
"				// реагирование на получение фокуса            \n" + 
"						$(\"#overlay_${child_name}\").show();       \n" + 
"						var valueJS = ${value_js};          \n" + 
"						// сохраняем предыдущее значение            \n" + 
"						var previous_text = $(\"#fake_visible_${child_name}\").val();            \n" + 
"						var previous_id = $(\"input#${child_name}\").val();            \n" + 
"						$(\"#fake_visible_${child_name}\").attr(\"disabled\",\"disabled\");            \n" + 
"						$(\"#visible_${child_name}\").empty();            \n" + 
"						$(\"<option/>\", {'value': '', html: '${loading}'}).appendTo(\"#visible_${child_name}\");            \n" + 
"						$(\"#fake_visible_${child_name}\").val('${loading}');            \n" + 
"						//$(\"#visible_${child_name}\").trigger('refresh');            \n" + 
"						if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {         \n" + 
"							ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;         \n" + 
"						}         \n" + 
"						++ajax_is_child_blocked${prefix}[\"${child_name}\"];         \n" + 
"						ajax({            \n" + 
"				            	url: '${service}get_list_interactive',            \n" + 
"							data: {            \n" + 
"								parameter_name:'${child_name_api}',            \n" + 
"								form_api: '${api}',            \n" + 
"								parameters: valueJS,            \n" + 
"								no_cache: Math.floor(Math.random() * 10000),            \n" + 
"							},           \n" + 
"					            type: 'post',            \n" + 
"					            dataType: 'json',            \n" + 
"				           		contentType: 'application/x-www-form-urlencoded',            \n" + 
"						}, function (data) {    \n" + 
"								$(\"#visible_${child_name}\").empty();            \n" + 
"								$(\"#fake_visible_${child_name}\").val('${not_selected}');            \n" + 
"								// создаем слой            \n" + 
"								$(\"#fake_visible_${child_name}\").after(\"<div id='popup_${child_name}' class='popup background_color first_frames_border'><p id='column1'></p><p id='column2'></p></div>\");            \n" + 
"								$(\"#popup_${child_name}\").prepend(\"<h3>${select} \"+$('label[for=\"visible_${child_name}\"]').html()+\"</h3>\");            \n" + 
"								$(\"#popup_${child_name}\").prepend(\"<div id='popup_${child_name}_close' class='popup_close buttons_height buttons_color'>${close}</div>\");            \n" + 
"								var i = 0;            \n" + 
"								$(\"<option/>\", {'value': '', html: '---'}).appendTo(\"#visible_${child_name}\");            \n" + 
"								$.each(data.data, function(key, val) {            \n" + 
"									$(\"<option/>\", {'value': val.id, html: val.name, 'selected': val.value }).appendTo(\"#visible_${child_name}\");            \n" + 
"									i += 1;            \n" + 
"									var bold = val.id==previous_id;    \n" + 
"									var values = val.name.split(\"${value_separator}\");  \n" + 
"									if (i % 2 == 0) {            \n" + 
"										$(\"#column1\").append('<br><div class=\"popuplist_header\" id=\"popupelementid_'+val.id+'\"><u  class=\"url_color\">'+(bold?'<b>':'')+values[0]+(bold?'</b>':'') + '</u>' + (values.length > 1 ? values[1] : '') +'</div>');       \n" + 
"									}else{       \n" + 
"										$(\"#column2\").append('<br><div class=\"popuplist_header\" id=\"popupelementid_'+val.id+'\"><u  class=\"url_color\">'+(bold?'<b>':'')+values[0]+(bold?'</b>':'') + '</u>' + (values.length > 1 ? values[1] : '') +'</div>');       \n" + 
"									}            \n" + 
"							  	});            \n" + 
"								if (i==0){            \n" + 
"									// удаляем динам окно            \n" + 
"									$( \"#popup_${child_name}\" ).remove();            \n" + 
"									$(\"#fake_visible_${child_name}\").val('${list_empty}');            \n" + 
"									$(\"input#${child_name}\").val('');            \n" + 
"									$(\"#fake_visible_${child_name}\").removeAttr('disabled');            \n" + 
"									$(\"#visible_${child_name}\").trigger('after_load');            \n" + 
"									return;            \n" + 
"								}            \n" + 
"								$(\"#fake_visible_${child_name}\").removeAttr('disabled');            \n" + 
"								$(\"#visible_${child_name}\").trigger('after_load');            \n" + 
"								--ajax_is_child_blocked${prefix}[\"${child_name}\"];   \n" + 
"								if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) {   \n" + 
"            								$(\"#visible_${child_name}\").trigger( 'on_child_unblocked');   \n" + 
"								}   \n" + 
"								//$(\"#visible_${child_name}\").trigger('refresh');            \n" + 
"								// показываем фон            \n" + 
"								$(\".overlay\").show();            \n" + 
"								$('.popuplist_header').hover( \n" + 
"									function() { \n" + 
"										$( this ).addClass( \"second_color\" ); \n" + 
"									}, function() { \n" + 
"										$( this ).removeClass( \"second_color\" ); \n" + 
"									} \n" + 
"								);\n" +
"								// вешаем обработчик динамически на click на каждый элемент списка popuplist_header            \n" + 
"								$(document).one(\"click\", \".popuplist_header\", function() {            \n" + 
"									 // из id извлекаем значение и выводим            \n" + 
"									 var id_value = $(this).attr('id');            \n" + 
"									 var arr = id_value.split('_');            \n" + 
"									 $(\"#fake_visible_${child_name}\").val($(this).find(\"u\").text());            \n" + 
"									 $(\"#visible_${child_name}\").val(arr[1]);            \n" + 
"									 $(\"input#${child_name}\").val(arr[1]);            \n" + 
"									 // закрываем фон            \n" + 
"									 $(\".overlay\").hide();            \n" + 
"									 // удаляем динам окно            \n" + 
"									 $(\"#popup_${child_name}\" ).remove();            \n" + 
"									 // посылаем событие на обновление            \n" + 
"									 $(\"#visible_${child_name}\").change();            \n" + 
"									 $(\"#visible_${child_name}\").trigger('refresh');            \n" + 
"									 $(\"#overlay_${child_name}\").hide();       \n" + 
"								});            \n" + 
"								// вешаем обработчик динамически на click на закрыть            \n" + 
"								$(document).one(\"click\", \".popup_close\", function() {            \n" + 
"									// закрываем фон            \n" + 
"									$(\".overlay\").hide();            \n" + 
"									// удаляем динам окно            \n" + 
"									$(\"#popup_${child_name}\").remove();            \n" + 
"									// восстанавливаем предыдущий выбор            \n" + 
"									$(\"#fake_visible_${child_name}\").val(previous_text);            \n" + 
"									$(\"input#${child_name}\").val(previous_id);            \n" + 
"									$(\"#visible_${child_name}\").val(previous_id);            \n" + 
"									 $(\"#overlay_${child_name}\").hide();       \n" + 
"									// посылаем событие на обновление            \n" + 
"									//$(\"#visible_${child_name}\").change();            \n" + 
"								});            \n" + 
"						});            \n" + 
"					});            \n" + 
"	\n")
.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
.replace("${select}", CurrentLocale.getInstance().getTextSource().getString("select"))
.replace("${close}", CurrentLocale.getInstance().getTextSource().getString("close"))
.replace("${list_empty}", CurrentLocale.getInstance().getTextSource().getString("list_empty"))
.replace("${loading}", CurrentLocale.getInstance().getTextSource().getString("loading"))
.replace("${not_selected}", CurrentLocale.getInstance().getTextSource().getString("not_selected"))
.replace("${handler}", handler)
.replace("${value_js}", valueJS)
.replace("${prefix}", prefix)
.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
.replace("${child_name}", name)
.replace("${child_name_api}", (String) generator.getAttribute(TagGenerator.Attribute.ID))
.replace("${service}", (String) generator.getAttribute(TagGenerator.Attribute.SERVICE));

			parrent.add(Tag.Type.SCRIPT, bodyJS);
			
			return elementInput;
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

//		  /**
//		   * Метод возвращает функционал устанавливающий признак активности элемента
//		   * Применяемые переменные: 
//		   * 	${child_name} - имя текущего (зависимого) элемента
//		   * 	val - JSON объект, содержащий возвращенное сервисом значение элемента
//		   * 
//		   * @return код JavaScript
//		   */
//			public String getSetActiveJS() {
//				
//				return 
//				"		if (data.value) { \n " + 
//				"			$('#fake_visible_${child_name}').prop( \"disabled\", false); \n " +
//				"			$(\"#tr_${child_name}\" ).css('color', 'black'); \n "+
//				"           $(\"#fake_visible_${child_name}\").trigger('refresh');" +
//				"		} else { \n " +
//				"			$('#fake_visible_${child_name}').prop( \"disabled\", true); \n " +
//				"			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n " +
//				"		} \n ";
//			}
//
//		  /**
//		   * Метод возвращает функционал снимающий признак активности элемента
//		   * Применяемые переменные: 
//		   * 	${child_name} - имя текущего (зависимого) элемента
//		   * 
//		   * @return код JavaScript
//		   */
//			public String getSetInactiveJS() {
//				
//				return "$('#fake_visible_${child_name}').prop( \"disabled\", false); \n " +
//						"$(\"#tr_${child_name}\" ).css('color', 'black'); \n ";
//			}
//
			
		public String getSetItemsJS() throws SAXException {

			
			return 			"";
		}

		
		public String getSetValueJS() {
			// Добавить условие для загрузки зависимых списков (пока работает только для незаполненных ранее)
			return  	("	if (isLoading) {  \n" + 
						"		$(\"<option/>\", {'value': '', html: '---'}).appendTo(\"#visible_${child_name}\");    \n" + 
						"		$(\"<option/>\", {'value': data.value, html: data.name, 'selected': true }).appendTo(\"#visible_${child_name}\");    \n" + 
						"		$(\"#visible_${child_name}\").trigger('refresh');  \n" + 
						"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
						"		$(\"#fake_visible_${child_name}\").val(data.name);  \n" + 
						"		$('#visible_${child_name}').bind('change', function(){ \n" + 
						"			$('#${system_prefix}_changed_${child_name}').val('1') \n" + 
						"		}); \n" + 
						"	} else { \n" + 
						"		$(\"input#${child_name}\").val(data.value);           \n" + 
						"		$(\"#fake_visible_${child_name}\").val(data.name);           \n" + 
						"		$(\"#visible_${child_name}\").val(data.value).change(); \n" + 
						"	} \n"
					).replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
		}

		public String getSetActiveJS() {
			
			return 
			"		if (data.value) { \n " + 
			"			$('#visible_${child_name}').prop( \"disabled\", false); \n " +
			"			$('#fake_visible_${child_name}').prop( \"disabled\", false); \n " +
			"			$(\"#tr_${child_name}\" ).css('color', 'black'); \n "+
			"           $(\"#visible_${child_name}\").trigger('refresh');" +
			"		} else { \n " +
			"			$('#visible_${child_name}').prop( \"disabled\", true); \n " +
			"			$('#fake_visible_${child_name}').prop( \"disabled\", true); \n " +
			"			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n " +
			"		} \n ";
		}
		protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) throws SAXException {

			element.add(Tag.Type.SCRIPT, 		("  \n" + 
					"		$(\"#visible_${child_name}\").bind('setValue', function(event, value){      \n" + 
					"				$('input#${child_name}').val(value.split('${name_value_separator}')[1]);        \n" + 
					"				$('#visible_${child_name}').val(value.split('${name_value_separator}')[0]).change();                        \n" + 
					"		});     \n")
					.replace("${name_value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
					.replace("${child_name}", name));

			return element;
		}
}
