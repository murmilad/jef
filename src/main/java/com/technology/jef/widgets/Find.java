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
					 put(Tag.Property.ID, name);
					 put(Tag.Property.NAME, name);
					 put(Tag.Property.TYPE, "text");
					 put(Tag.Property.STYLE, "width: 100%;");
					 put(Tag.Property.KEYDOWN, "if (window.event.keyCode == 13) {  event.returnValue = false; onClickFindButton" + name + "(this); event.preventDefault();}");
				}});

			row.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.STYLE, "width:40%;");
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
	
			String valueTemplateJS = 
				("					$(\"#search_result_${name}\").attr(\"disabled\",\"disabled\"); \n" + 
				"					${sub_list_js} \n")
				.replace("${name}", name);

			String[] ajax_child_list = (String[])generator.getAttribute(TagGenerator.Attribute.AJAX_VALUE_CHILD);
			
			
			String readyToUnbindJS = "ajax_is_parrent_blocked" + prefix + "[\"" + 
					String.join(prefix + "\"] == 0 && ajax_is_parrent_blocked" + prefix + "[\"", ajax_child_list) +
					"\"] == 0";
			String unbindAllListenersJS = "$(\"#visible_" + 
					String.join(prefix + "\").unbind('set_find_result');\n\t\t\t\t\t\t\t$(\"#visible_", ajax_child_list) +
					"\").unbind('set_find_result');";
			String unbindAllUnbindListenersJS ="$(\"#visible_" + 
					String.join(prefix + prefix + "\").unbind('on_parrent_unblocked');\n\t\t\t\t\t\t\t$(\"#visible_", ajax_child_list) +
					"\").unbind('on_parrent_unblocked');";

			String unbindJS = "";
			for (String childName: ajax_child_list) {
				unbindJS = unbindJS.concat(
					("						$(\"#visible_${result_name}${prefix}\").on( \"on_parrent_unblocked\", function() { \n" + 
					"							if (${redy_to_unbind}) { \n" + 
					"								${unbind_all_listeners}; \n" + 
					"								${unbind_all_unbund_listeners}; \n" + 
					"								$(\"#search_result_${name}\").removeAttr('disabled'); \n" + 
					"							} \n" + 
					"						}); \n")
					.replace("${result_name}", childName)
					.replace("${prefix}", prefix)
					.replace("${name}", name)
					.replace("${redy_to_unbind}", readyToUnbindJS)
					.replace("${unbind_all_listeners}", unbindAllListenersJS)
					.replace("${unbind_all_unbund_listeners}", unbindAllUnbindListenersJS)
				);
			}
			
			// Добавляем листенеры для установки значений полей после загрузки списков
			
			for (Integer i = 0; i < ajax_parrent_list.length; i++) {
				String subList = 
					("						if (value_array[${index}] == \"\") { \n" + 
					"							value_array[${index}] = \"\" \n" + 
					"						} \n" + 
				
					"						$(\"#visible_${result_name}${prefix}\").val(value_array[${index}]); \n" + 
					"						$(\"#visible_${result_name}${prefix}\").change(); \n")
					.replace("${result_name}", ajax_parrent_list[i])
					.replace("${prefix}", prefix)
					.replace("${index}", Objects.toString(i, ""));

				if (i+1 < ajax_parrent_list.length) {
					subList =
						("							if (value_array[${index}] == \"\") { \n" + 
						"								value_array[${index}] = \"\" \n" + 
						"							} \n" + 
					
						"							$(\"#visible_${child_result_name}${prefix}\").on( \"set_find_result\", function() { \n" + 
						"								${sub_list_js} \n" + 
						"							}); \n" + 
					
						"							$(\"#visible_${result_name}${prefix}\").val(value_array[${index}]); \n" + 
						"							$(\"#visible_${result_name}${prefix}\").change(); \n")
						.replace("${result_name}", ajax_parrent_list[i])
						.replace("${prefix}", prefix)
						.replace("${index}", Objects.toString(i, ""))
						.replace("${child_result_name}", ajax_parrent_list[i+1]);
				}
				
				String tabs = "";
				for (int j = 0; j < i; j++)
					tabs = tabs.concat("\t\t");
				subList = subList.replaceAll("\n", "\n" + tabs);
				valueTemplateJS = valueTemplateJS.replace("${sub_list_js}", subList);
			}
		
			String setValueJS = 
				("			function onChangeFindResult${name}(current){ \n" + 
				"				var value_array = current.value.split(\",\"); \n" + 
				"				${unbind_js} \n" + 
				"				${value_js} \n" + 
				"			} \n")
				.replace("${value_js}", valueTemplateJS)
				.replace("${unbind_js}", unbindJS)
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
