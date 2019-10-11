package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;

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
	   */
	@Override
	public Tag generate(String qName) {
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
				public void handle(TagGenerator currentGenerator) {
					formItems.add((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				}
				
			});
//TODO			load params with prefix
		} else {
			group = addFormGroup(dom, GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API), (String) getAttribute(TagGenerator.Attribute.NAME));
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
		}});
		
		tagFieldset = tagFieldset.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "div_" + name);
			 put(Tag.Property.ID, "div_" + name);
		}});
		

		return tagFieldset;
	}

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	   * 
	   */
	@Override
	public void onEndElement() {
		// если группа у нас расширяемая, то добавляем соответсвующие элементы правления
		if (isMultiplie) {
//TODO Добавить возможность управления видимостью кнопки "удалить"
			dom.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "button_del_<NUMBER>");
				 put(Tag.Property.ID, "button_del_<NUMBER>");
				 put(Tag.Property.CLASS, "interface_del_button");
				 put(Tag.Property.TYPE, "button");
				 put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("delete") + " " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll(CurrentLocale.getInstance().getTextSource().getString("multi_prefix") + "$", ""));
			}});
			
			dom.add(Tag.Type.SCRIPT, 	(" \n" + 
	"	$(\"#button_del_<NUMBER>\").click(function(){  \n" + 
	"		$('#action<NUMBER>').val('${action}'); \n" + 
	"		$(\"#fildset_<NUMBER>\").remove();  \n" + 
	"			$(\"<input/>\", {    \n" + 
	"				'value': '${api}',     \n" + 
	"				'type': 'hidden',     \n" + 
	"				'id': 'api_group_id<NUMBER>',     \n" + 
	"				'name': 'api_group_id<NUMBER>',     \n" + 
	"			}).appendTo( \"#place_${multiplie_group_name}\" ); \n" + 
	"			$(\"<input/>\", {    \n" + 
	"				'value': '${parrent_api}',     \n" + 
	"				'type': 'hidden',     \n" + 
	"				'id': 'parrent_api_group_id<NUMBER>',     \n" + 
	"				'name': 'parrent_api_group_id<NUMBER>',     \n" + 
	"			}).appendTo( \"#place_${multiplie_group_name}\" ); \n" + 
	"		count_${multiplie_group_name}--; \n" +
	"}); \n")
	.replace("${multiplie_group_name}", GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API))
	.replace("${api}", (String) getAttribute(TagGenerator.Attribute.API))
	.replace("${parrent_api}", (String) getAttribute(TagGenerator.Attribute.PARRENT_API))
	.replace("${action}", ACTION_DELETE)
	
			);

			dom.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "group_action_<NUMBER>");
				 put(Tag.Property.ID, "group_action_<NUMBER>");
				 put(Tag.Property.CLASS, "interface_del_button");
				 put(Tag.Property.TYPE, "hidden");
				 put(Tag.Property.VALUE, "create");
			}});
			
//TODO Добавить ограничение на видимость кнопок "добавить" для групп
			multilineParrentDOM.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "button_add_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "button_add_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.TYPE, "button");
				 put(Tag.Property.CLASS, "interface_add_button");
				 put(Tag.Property.STYLE, "display: inline-block");
				 put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("add") + " " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll(CurrentLocale.getInstance().getTextSource().getString("multi_prefix") + "$", ""));
			}});
			

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "background_overlay_wait_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "background_overlay_wait");
				 put(Tag.Property.CLASS, "background_overlay_wait");
			}});

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_wait");
			}}).add(Tag.Type.DIV, CurrentLocale.getInstance().getTextSource().getString("wait"), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "message_overlay_wait");
				 put(Tag.Property.CLASS, "message_overlay_wait");
			}});

		} else {

			dom.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "group_action_<NUMBER>");
				 put(Tag.Property.ID, "group_action_<NUMBER>");
				 put(Tag.Property.CLASS, "interface_del_button");
				 put(Tag.Property.TYPE, "hidden");
			}});
			
		}
	}

	  /**
	   * Метод получения текущего адреса DOM модели для данного уровня гернератора
	   * Позволяет получить нужный адрес в зависимости от атрибутов и имени тега в XML представлении
	   * Нужен для тех случаев, когда генерация дочерних элементов должна проводиться для специальных
	   * родителей DOM модели (группы)
	   * 
	   * @param name имя тега в XML представлении интерфейса
	   * @param attributes атрибуты тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	public Tag getDom(Name name, Attributes attributes) {

		// Выбираем адрес DOM модели в зависимости от имени тега
		switch (name) {
		// Если дочерний генератор формирует скрипт, то пусть он записывается 
		// не в шаблон группы а в ее родительский тег
		case SCRIPT:
			return multilineParrentDOM;
		default:
			return dom;
		}
		
	}

	  /**
	   * Метод получения атрибута текущего тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Содержимое атрибута
	   */
	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) {
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