package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

/**
* Виджет маленький переключатель
*/
public class RadioSwitchShort extends RadioSwitch {

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
	   * Метод формирования DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param parrent родительский тег в DOM модели
	   * @return DOM модель на текущем уровне
	   */
		public Tag assembleTag(String name, TagGenerator generator) {

			Tag elementInput = parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
							put(Tag.Property.ID, "visible_" + name);
							 put(Tag.Property.NAME, "visible_" + name);
							 put(Tag.Property.STYLE, "display: table-row;");
					}});			

			return elementInput;
		}



}
