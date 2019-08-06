package com.technology.jef.generators;

import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;

import com.technology.jef.Tag;

/**
* Класс сборщика DOM модели уровня группы
*/
public class GroupGenerator extends TagGenerator {

	private Boolean isMultiplie = false;
	private Tag multilineParrentDOM;
	private Tag templateParrentGroup;
	
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
			     put(Tag.Property.ID, "place_" + getAttribute(TagGenerator.Attribute.ID));
			     put(Tag.Property.NAME, "place_" + getAttribute(TagGenerator.Attribute.ID));
			}});
			
			// Формируем шаблон для мультигрупп и возвращаем его для дочерних генераторов
			// Что бы дочерние элементы группы добавлялись имменно в шаблон
			templateParrentGroup = new Tag(Tag.Type.DIV);
			group = addFormGroup(templateParrentGroup, "<NUMBER>", (String) getAttribute(TagGenerator.Attribute.NAME));

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
				 put(Tag.Property.VALUE, "удалить " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll("ы$", ""));
			}});
			
			dom.add(Tag.Type.SCRIPT, (""
					+ "$(\"#button_del_<NUMBER>\").click(function(){ \n"
						+ "$(\"#fildset_<NUMBER>\").remove(); \n"
						+ "count_${multiplie_group_name}--;\n"
//TODO Добавить ограничение по количеству добавляемых групп
						+ "$( \"#place_${multiplie_group_name}\" ).trigger( \"on_add\" );\n"
					+"});"
					).replace("${multiplie_group_name}", (String) getAttribute(TagGenerator.Attribute.ID))
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
				 put(Tag.Property.ID, "button_add_" + getAttribute(TagGenerator.Attribute.ID));
				 put(Tag.Property.NAME, "button_add_" + getAttribute(TagGenerator.Attribute.ID));
				 put(Tag.Property.TYPE, "button");
				 put(Tag.Property.CLASS, "interface_add_button");
				 put(Tag.Property.STYLE, "display: inline-block");
				 put(Tag.Property.VALUE, "добавить " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll("ы$", ""));
			}});
			

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "background_overlay_wait_" + getAttribute(TagGenerator.Attribute.ID));
				 put(Tag.Property.NAME, "background_overlay_wait");
				 put(Tag.Property.CLASS, "background_overlay_wait");
			}});

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_" + getAttribute(TagGenerator.Attribute.ID));
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_wait");
			}}).add(Tag.Type.DIV, "Подождите...", new HashMap<Tag.Property, String>(){{
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
		default:
			return super.getAttribute(attributeName);
		}
	}

}
