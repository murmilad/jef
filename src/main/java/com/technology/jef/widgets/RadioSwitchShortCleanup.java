package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет маленький переключатель
*/
public class RadioSwitchShortCleanup extends RadioSwitch {

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
							put(Tag.Property.STYLE, "border-width: 1px; display: table-cell;");
					}});			

			elementInput.add(Tag.Type.LINK, CurrentLocale.getInstance().getTextSource().getString("cleanup"),
					new HashMap<Tag.Property, String>() {
						{
							put(Tag.Property.ID, "link_" + name);
							put(Tag.Property.STYLE,
									"border-bottom: 1px dashed; cursor: pointer; margin: 0pt 2px 2px; text-align: left;");
							put(Tag.Property.CLICK, "$(\"[name='visible_" + name
									+ "']\").each(function(index, item){ $(this).prop(\"checked\", false).trigger('refresh');});$('#"
									+ name + "').val('');return false;");
						}
					});
			return elementInput;
		}



}
