package com.technology.jef.widgets;

import java.util.HashMap;
import java.util.Objects;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет поиск
*/
public class Find extends Widget {

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
		@Override
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
					}});			

			
			return getFindBody(name, generator, elementInput);
		}

		protected Tag getFindBody (String name, TagGenerator generator, Tag mainInput) throws SAXException {
			Tag row = mainInput
					.add(Tag.Type.TABLE, new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.STYLE, "width: 100%;");
					}})
					.add(Tag.Type.TR);
			
			row.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.STYLE, "width:40%;");
				}})
				.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.ID,"visible_" + name);
					 put(Tag.Property.NAME, "visible_" + name);
					 put(Tag.Property.TYPE, "text");
					 put(Tag.Property.STYLE, "width: 100%;");
					 put(Tag.Property.CLASS, "widget first_frames_border widgets_color widgets_height widgets_font");
					 put(Tag.Property.KEYDOWN, "if (window.event.keyCode == 13) {  event.returnValue = false; onClickFindButton" + name + "(this); event.preventDefault();}");
				}});

			row.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.STYLE, "width:10%;");
			}})
			.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "button_" + name);
				 put(Tag.Property.NAME, "button_" + name);
				 put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("find"));
				 put(Tag.Property.CLASS, "interface_button first_color second_text_color widgets_height");
				 put(Tag.Property.TYPE, "button");
				 put(Tag.Property.STYLE, "margin-left:10px;margin-right:10px;padding:0px !important;");
				 put(Tag.Property.CLICK, "onClickFindButton" + name + "(this);");
			}});

			Tag elementInput =  row.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.STYLE, "width:50%;");
			}});
			elementInput.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.CLASS, "styled");
						 put(Tag.Property.ID, "search_result_error_" + name);
						 put(Tag.Property.NAME, "search_result_error_" + name);
			}});
					
			elementInput = elementInput.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.CLASS, "styled");
			}})
			.add(Tag.Type.SELECT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "search_result_" + name);
				 put(Tag.Property.NAME, "search_result_" + name);
				 put(Tag.Property.STYLE, "float:left; visibility: hidden;width:100%;");
				 put(Tag.Property.CHANGE, "onChangeFindResult" + name + "(this);");
				 put(Tag.Property.CLASS, "widget first_frames_border widgets_color widgets_height widgets_font");
			}});
			

			String prefix = (String) generator.getAttribute(TagGenerator.Attribute.PREFIX);
			String nameAPI = name.replace(prefix, "");

			String valueJS = getValueJS(generator, prefix, TagGenerator.Attribute.AJAX_LIST_PARRENT);

			String[] ajax_parrent_list = (String[])generator.getAttribute(TagGenerator.Attribute.AJAX_LIST_PARRENT);

			String ajaxSearchJS = 
						("  \n" + 
	"					$(\"input#${name}\").on( \"clean_find_data\", function() {   \n" + 
	"						$(\"#search_result_${name}\")   \n" + 
	"							.empty()   \n" + 
	"							.val(\"\")   \n" + 
	"							.css({visibility: \"hidden\"})   \n" + 
	"						;   \n" + 
	"					});   \n" + 
	"					function onClickFindButton${name}(current){   \n" + 
	"						$(\"#${name}\").val($(\"#visible_${name}\").val());   \n" + 
	"						$(\"#search_result_${name}\").empty();   \n" + 
	"						$(\"#search_result_${name}\").attr(\"disabled\",\"disabled\");   \n" + 
	"						$(\"<option/>\", {'value': '', html: '${loading}'}).appendTo(\"#search_result_${name}\");   \n" + 
	"						$(\"#search_result_${name}\").trigger('refresh');   \n" + 
	"					ajax({      \n" + 
	"						url: \"${service}\" + \"get_list_interactive\",     \n" + 
	"						data: {   \n" + 
	"							form_api: \"${api}\",   \n" + 
	"							parameter_name: \"${name_api}\",   \n" + 
	"							parameters: ${value_js},   \n" + 
	"						},     \n" + 
	"						type: \"post\",     \n" + 
	"						dataType: \"json\",    \n" + 
	"						contentType: 'application/x-www-form-urlencoded'    \n" + 
	"					},function(data) {   \n" + 
	"								$(\"#search_result_${name}\").empty();   \n" + 
	"								$(\"<option/>\", {'value': '', 'disabled': 'disabled', 'selected': 'selected'}).appendTo(\"#search_result_${name}\");   \n" + 
	"								var errors = []; \n" + 
	"								$.each(data.data, function(key, val) {   \n" + 
	"									if (val.error) { \n" + 
	"										errors.push(val.error); \n" + 
	"									} \n" + 
	"									$(\"<option/>\", {'value': val.id, html: val.name}).appendTo(\"#search_result_${name}\");   \n" + 
	"							  	});   \n" + 
	"				  				if (errors.length > 0) { \n" + 
	"				  					$(\"#search_result_${name}\").empty(); \n" + 
	"				  					$(\"#search_result_${name}\").hide(); \n" + 
	"				  					$(\"#search_result_error_${name}\").html('<span style=\"color:red\">'+ errors.join('<br>')+'</span>'); \n" + 
	"				  				} else { \n" + 
	"				  					$(\"#search_result_${name}\").show(); \n" + 
	"				  					$(\"#search_result_error_${name}\").empty().hide(); \n" + 
	"				  				} \n" + 
	"								$(\"#search_result_${name}\").removeAttr('disabled');   \n" + 
	"								$(\"#search_result_${name}\").trigger('refresh');   \n" + 
	"								$(\"#search_result_${name}\").css({visibility: \"visible\"});   \n" + 
	"						   \n" + 
	"						});   \n" + 
	"					}   \n")
				.replace("${loading}", CurrentLocale.getInstance().getTextSource().getString("loading"))
				.replace("${value_js}", valueJS)
				.replace("${name}", name)
				.replace("${api}", (String) generator.getAttribute(TagGenerator.Attribute.API))
				.replace("${name_api}", nameAPI)
				.replace("${service}", (String) generator.getAttribute(TagGenerator.Attribute.SERVICE));

			
			
			
		
			
			
			String[] ajax_child_list = (String[])generator.getAttribute(TagGenerator.Attribute.AJAX_VALUE_CHILD);

			String callEventsJS = "";
			for (String parrentName: ajax_child_list) {
				
				String condition = "typeof onChange" + String.join("_" + parrentName + "__ct_ajax_value === 'function' || typeof onChange", ajax_child_list) + "_" + parrentName + "__ct_ajax_value == 'function'";

				callEventsJS = callEventsJS.concat(	(" \n" + 
				"			if (!(${condition})) { \n" + 
				"				$(\"#visible_${parrent_name}${prefix}\").trigger('set_find_result'); \n" + 
				"				$(\"#visible_${parrent_name}${prefix}\").trigger('on_parrent_unblocked'); \n" + 
				"				$(\"#visible_${parrent_name}${prefix}\").unbind('set_find_result'); \n" + 
				"			}  \n")
				.replace("${condition}", condition)
				.replace("${parrent_name}", parrentName)
				.replace("${prefix}", prefix));
			}
			
			
			
			// Добавляем листенеры для установки значений полей после загрузки списков
			
			String resultName = ajax_child_list[0];
			
			valueJS = 	(" \n" + 
			"		var set_result_listeners = ${set_result_listeners}; \n" + 
			"		$(\"#visible_${result_name}${prefix}\").trigger('setValue', [value_array[0]]); \n" + 
		
			"		\n")
			.replace("${set_result_listeners}", String.valueOf(ajax_child_list.length))
			.replace("${result_name}", resultName)
			.replace("${prefix}", prefix);
			
			
			
			Integer index = 0;
			for (String childName: ajax_child_list) {
				valueJS = valueJS.concat(
						(" \n" + 
	"				$(\"input#${result_name}${prefix}\").val(value_array[${i}]); \n" + 

	"				$(\"#visible_${result_name}${prefix}\").on( \"on_child_unblocked\", function() { \n" + 
	"					$(\"#visible_${result_name}${prefix}\").unbind('set_find_result'); \n" + 
	"				}); \n" + 

	"				$(\"#visible_${result_name}${prefix}\").on( \"after_load\", function() { \n"+
	"					$(\"#visible_${result_name}${prefix}\").trigger('set_find_result'); \n" + 
	"				}); \n" +
	
	"				$(\"#visible_${result_name}${prefix}\").on( \"set_find_result\", function() { \n"+
	"					$(\"#visible_${result_name}${prefix}\").trigger('setValue', [value_array[${i}]]); \n" + 
	"				}); \n"
	)
					.replace("${result_name}", childName)
					.replace("${prefix}", prefix)
					.replace("${i}", String.valueOf(index))
				);
				index++;
			}
			
			
			
			String setValueJS = 
				("			function onChangeFindResult${name}(current){ \n" + 
				"				var value_array = current.value.split(\"\\|\"); \n" + 
				"				${value_js} \n" + 
				"				${call_events_js} \n" + 
				"			} \n")
				.replace("${value_js}", valueJS)
				.replace("${call_events_js}", callEventsJS)
				.replace("${name}", name);			

			parrent.add(Tag.Type.SCRIPT, ajaxSearchJS + setValueJS);

			return elementInput;
		}

		/**
	   * Метод возвращает функционал влияющий на значение элемента 
	   * 
	   * @return код JavaScript
	   */
		public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
			// Заглушка для связных списков, расчитывающих на зависимость в поиске
			return "";
		}
}
