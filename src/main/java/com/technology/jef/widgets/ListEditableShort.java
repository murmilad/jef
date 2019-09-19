package com.technology.jef.widgets;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет Выпадающий список с возможностью выбрать значение "Иное" малый
*/
public class ListEditableShort extends ListEditable {

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
			
			return getListBody(name, generator, parrent);
		}		
}
