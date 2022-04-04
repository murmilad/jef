package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет многострочный текст
*/
public class TextArea extends Widget {

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
		public Tag assembleTag(String name, TagGenerator generator) throws SAXException {
			Tag tableForm = parrent.add(Tag.Type.TABLE, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.WIDTH, "100%");
				 put(Tag.Property.COLOR, "gray");
			}});
			
			tableForm
				.add(Tag.Type.TR)
				.add(Tag.Type.TD, (String) generator.getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
					put(Tag.Property.WIDTH, "100%");
					put(Tag.Property.FOR, "visible_" + name);
					put(Tag.Property.CLASS, "widgets_label_color second_font");
				}});
			
			
			
			
			Tag elementInput = tableForm
				.add(Tag.Type.TR)
				.add(Tag.Type.TD)
				.add(Tag.Type.TEXTAREA, new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.ID, "visible_" + name);
					 put(Tag.Property.NAME, "visible_" + name);
					 put(Tag.Property.ROWS, "3");
					 put(Tag.Property.STYLE, "height:80; width:100%; -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;");
				}});

			parrent.add(Tag.Type.SCRIPT, 	(" \n" + 
					"			$(\"#visible_${name}\").on(\"keyup\",function(event){ \n" + 
					"				if (event.delegateTarget.value.length >= ${thresh}) { \n" + 
					"					event.delegateTarget.value = event.delegateTarget.value.slice(0, ${thresh}); \n" + 
					"					alert('Количество введенных символов не должно превышать ' + ${thresh} + '!'); \n" + 
					"				} \n" + 
					"			}); \n")
					.replace("${name}", name)
					.replace("${thresh}", !"".equals(generator.getAttribute(TagGenerator.Attribute.MAXLENGTH)) ? (String) generator.getAttribute(TagGenerator.Attribute.MAXLENGTH) : "255"));
			
			return elementInput;
		}
}
