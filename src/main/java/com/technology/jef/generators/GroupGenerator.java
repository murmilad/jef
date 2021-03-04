package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.widgets.Widget;

import static com.technology.jef.server.serialize.SerializeConstant.*;
import static com.technology.jef.server.WebServiceConstant.*;
/**
* Класс сборщика DOM модели уровня группы
*/
public class GroupGenerator extends TagGenerator {

	private Boolean isMultiplie = false;
	private Tag multilineParrentDOM;
	private Tag templateParrentGroup;
	private List<String> formItems = new LinkedList<String>();
	
	  /**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {
		Tag group = null;
		isMultiplie = "true".equals(getAttribute(TagGenerator.Attribute.IS_MULTIPLIE));
		if (isMultiplie) {
			isMultiplie = true;

			// То что не должно пойти в шаблон
			multilineParrentDOM = dom.add(Tag.Type.DIV).add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.ID, "place_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
			     put(Tag.Property.NAME, "place_" + GROUP_SEPARATOR +getAttribute(TagGenerator.Attribute.API));
			}});
			
			// Формируем шаблон для мультигрупп и возвращаем его для дочерних генераторов
			// Что бы дочерние элементы группы добавлялись имменно в шаблон
			templateParrentGroup = new Tag(Tag.Type.DIV);
			group = addFormGroup(templateParrentGroup, "<NUMBER>", (String) getAttribute(TagGenerator.Attribute.NAME));

			addHandler(TagGenerator.Name.FORM_ITEM, new Handler() {

				@Override
				public void handle(TagGenerator currentGenerator) throws SAXException {
					formItems.add((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				}
				
			});
//TODO			load params with prefix
		} else {
			group = addFormGroup(dom, (String) getAttribute(TagGenerator.Attribute.ID), (String) getAttribute(TagGenerator.Attribute.NAME));
		}
		
		return group;
	}
	
	  /**
	   * Метод добавляет тело формы  в DOM модель.
	   * 
	   * @param parrent текущий адрес в DOM модели
	   * @param name уникальное имя группы
	   * @param caption заголовок группы
	   * @return тег группы
	   */
	private Tag addFormGroup(Tag parrent, String name, String caption) {

		Tag tagFieldset = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "fildset_" + name);
			 put(Tag.Property.NAME, "fildset_" + name);
			 put(Tag.Property.CLASS, "fieldset second_frames_border");
			 put(Tag.Property.STYLE, "display: block; margin: 5px;");
		}});
		
		Tag tagCaption = tagFieldset.add(Tag.Type.LEGEND).add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.FOR, "fildset_" + name);
		}});
		
		tagCaption.add(Tag.Type.SPAN, "-", new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "span_control_" + name);
			 put(Tag.Property.ID, "span_control_" + name);
			 put(Tag.Property.CLASS, "interface_expand");
		}});

		tagCaption.add(Tag.Type.SCRIPT,(
					"var control_${name}_visible = true;\n" + 
					"$(\"#span_control_${name}\").click(function(){\n" +
					"	if (control_${name}_visible) {\n" +
					"		$(\"#span_control_${name}\").html(\"+\");\n" +
					"		$(\"#div_${name}\").css('display', 'none');\n" +
					"	} else {\n" +
					"		$(\"#span_control_${name}\").html(\"-\");\n" +
					"		$(\"#div_${name}\").css('display', 'block');\n" +
					"	}\n" +
					"	control_${name}_visible = !control_${name}_visible;\n" + 
					"});\n"
			).replace("${name}", name)
		);
		
		tagCaption.add(Tag.Type.SPAN, caption, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "span_" + name);
			 put(Tag.Property.ID, "span_" + name);
			 put(Tag.Property.CLASS, "third_text_color");
		}});
		
		tagFieldset = tagFieldset.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "div_" + name);
			 put(Tag.Property.ID, "div_" + name);
		}});
		

		return tagFieldset;
	}

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	 * @throws SAXException 
	   * 
	   */
	@Override
	public void onEndElement() throws SAXException {
		// если группа у нас расширяемая, то добавляем соответсвующие элементы правления
		if (isMultiplie) {
			Tag buttonDel = dom.add(Tag.Type.DIV,
				new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.STYLE, "width: 100%; display: inline-block;");
					 put(Tag.Property.CLASS, "div_interface_element_horizontal interface_element_horizontal");
				}}).add(Tag.Type.DIV,
						new HashMap<Tag.Property, String>(){{
						put(Tag.Property.STYLE, "position:relative; width: 100%;text-align: left; display: block;");
				}});
			buttonDel.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_button_del_<NUMBER>");
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_loading background_color");
				}}).add(Tag.Type.DIV, CurrentLocale.getInstance().getTextSource().getString("loading"), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "message_overlay_wait");
				 put(Tag.Property.CLASS, "message_overlay_loading");
				}});
			buttonDel.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.NAME, "button_del_<NUMBER>");
					put(Tag.Property.ID, "button_del_<NUMBER>");
					put(Tag.Property.CLASS, "interface_del_button buttons_color buttons_height");
					put(Tag.Property.TYPE, "button");
					put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("delete") + " " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll(CurrentLocale.getInstance().getTextSource().getString("multi_prefix") + "$", "").toLowerCase());
				}});
			
			dom.getParrent().add(Tag.Type.SCRIPT, 	(" \n" + 
	"	$(\"#button_del_<NUMBER>\").click(function(){  \n" + 
	"		$('#action<NUMBER>').val('${action}'); \n" + 
	"		$(\"#fildset_<NUMBER>\").remove();  \n" + 
	"			$(\"<input/>\", {    \n" + 
	"				'value': '${api}',     \n" + 
	"				'type': 'hidden',     \n" + 
	"				'id': '${system_prefix}_api_group_id<NUMBER>',     \n" + 
	"				'name': '${system_prefix}_api_group_id<NUMBER>',     \n" + 
	"			}).appendTo( \"#place_${multiplie_group_name}\" ); \n" + 
	"			$(\"<input/>\", {    \n" + 
	"				'value': '${parrent_api}',     \n" + 
	"				'type': 'hidden',     \n" + 
	"				'id': '${system_prefix}_parrent_api_group_id<NUMBER>',     \n" + 
	"				'name': '${system_prefix}_parrent_api_group_id<NUMBER>',     \n" + 
	"			}).appendTo( \"#place_${multiplie_group_name}\" ); \n" + 
	"		count_${multiplie_group_name}--; \n" +
	"						var parameters = ${value_js};  \n" + 
	"						parameters += (parameters ? '${parameter_separator}' : '') + 'group_count${value_separator}' + count_${multiplie_group_name};  \n" + 
	"						$('#id_add_joined_group_${joined_by}').val(''); \n " + 
	"						$('#name_add_joined_group_${joined_by}').val(''); \n " + 
	"						setButtonVisiblity${multiplie_group_name}('button_add', '${multiplie_group_name}', parameters);   \n" + 
	"						setButtonVisiblity${multiplie_group_name}('button_del', groupPrefix, parameters);   \n" + 
	"}); \n")
	.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
	.replace("${parameter_separator}", PARAMETER_SEPARATOR)
	.replace("${joined_by}", (String) getAttribute(TagGenerator.Attribute.JOINED_BY))
	.replace("${value_js}", Widget.getValueJS(null, "", null))
	.replace("${multiplie_group_name}", GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API))
	.replace("${api}", (String) getAttribute(TagGenerator.Attribute.API))
	.replace("${parrent_api}", (String) getAttribute(TagGenerator.Attribute.PARRENT_API))
	.replace("${action}", ACTION_DELETE)
	.replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX)

	
			) ;

			dom.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "group_action_<NUMBER>");
				 put(Tag.Property.ID, "group_action_<NUMBER>");
				 put(Tag.Property.CLASS, "interface_del_button buttons_color buttons_height");
				 put(Tag.Property.TYPE, "hidden");
				 put(Tag.Property.VALUE, "create");
			}});

			Tag buttonAdd = multilineParrentDOM.getParrent().add(Tag.Type.DIV,
					new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.STYLE, "width: 100%;");
					}}).add(Tag.Type.DIV,
							new HashMap<Tag.Property, String>(){{
							put(Tag.Property.STYLE, "position:relative; width: 100%;text-align: left; display: block;");
					}});
			buttonAdd.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_button_add_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_loading background_color");
				}}).add(Tag.Type.DIV, CurrentLocale.getInstance().getTextSource().getString("loading"), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "message_overlay_wait");
				 put(Tag.Property.CLASS, "message_overlay_loading");
				}});

			buttonAdd.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.ID, "button_add_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
					 put(Tag.Property.NAME, "button_add_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
					 put(Tag.Property.TYPE, "button");
					 put(Tag.Property.CLASS, "interface_add_button buttons_color buttons_height");
					 put(Tag.Property.STYLE, !"".equals(getAttribute(TagGenerator.Attribute.JOINED_BY)) ? "display: none" : "display: inline-block");
					 put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("add") + " " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll(CurrentLocale.getInstance().getTextSource().getString("multi_prefix") + "$", "").toLowerCase());
				}});


			
			

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "background_overlay_wait_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "background_overlay_wait");
				 put(Tag.Property.CLASS, "background_overlay_wait background_color");
			}});

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_wait messages_border messages_color");
			}}).add(Tag.Type.DIV, CurrentLocale.getInstance().getTextSource().getString("wait"), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "message_overlay_wait");
				 put(Tag.Property.CLASS, "message_overlay_wait messages_color");
			}});

		} else {

			dom.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "group_action_<NUMBER>");
				 put(Tag.Property.ID, "group_action_<NUMBER>");
				 put(Tag.Property.CLASS, "interface_del_button buttons_color buttons_height");
				 put(Tag.Property.TYPE, "hidden");
			}});
			
		}
	}


	  /**
	   * Метод получения атрибута текущего тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Содержимое атрибута
	 * @throws SAXException 
	   */
	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) throws SAXException {
		switch (attributeName) {
		case API:
			return !"".equals(super.getAttribute(TagGenerator.Attribute.API)) 
					? super.getAttribute(TagGenerator.Attribute.API)
					: this.getParrent().getAttribute(TagGenerator.Attribute.API);
		case PREFIX: // Хотим получить префикс для параметра групповых форм
			return isMultiplie ? "<NUMBER>" : "";
		case TEMPLATE: // Хотим получить шаблон для мультигруппы
			return templateParrentGroup;
		case SERVICE:
			return this.getParrent().getAttribute(TagGenerator.Attribute.SERVICE);
		case ITEMS:
			return formItems;
		default:
			return super.getAttribute(attributeName);
		}
	}

}
