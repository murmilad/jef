package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет текст
*/
public class Hidden extends Widget {

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
			
			((Tag) generator.getAttribute(TagGenerator.Attribute.VISIBLE_ROW)).setProperty(Tag.Property.STYLE, "display: none;");
			((Tag) generator.getAttribute(TagGenerator.Attribute.VISIBLE_TAG)).setProperty(Tag.Property.STYLE, "display: none;");
			

			Tag elementInput = parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "hidden");
			}});
			
			return elementInput;
		}
}
