package com.technology.jef.widgets;

import java.util.HashMap;

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
	   */
		public Tag assembleTag(String name, TagGenerator generator) {
			Tag tableForm = parrent.add(Tag.Type.TABLE, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.WIDTH, "100%");
				 put(Tag.Property.COLOR, "gray");
			}});
			
			tableForm
				.add(Tag.Type.TR)
				.add(Tag.Type.TD, (String) generator.getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
					put(Tag.Property.WIDTH, "100%");
					put(Tag.Property.STYLE, !"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED)) ? "color: rgb(170, 0, 0);" : "color: rgb(0, 0, 0);");
				}});
			
			
			
			
			Tag elementInput = tableForm
				.add(Tag.Type.TR)
				.add(Tag.Type.TD)
				.add(Tag.Type.TEXTAREA, new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.ID, "visible_" + name);
					 put(Tag.Property.NAME, "visible_" + name);
					 put(Tag.Property.ROWS, "3");
					 put(Tag.Property.STYLE, "height:80; width:100%;");
					 put(Tag.Property.KEYUP, !"".equals(generator.getAttribute(TagGenerator.Attribute.MAXLENGTH))
							 ? "TextThreshold(this, " + (String) generator.getAttribute(TagGenerator.Attribute.MAXLENGTH) + ");" 
							 : "TextThreshold(this, 256);"
							 );
				}});
			
			return elementInput;
		}
}
