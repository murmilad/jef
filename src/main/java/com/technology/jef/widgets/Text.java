package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет текст
*/
public class Text extends Widget {

	
	  /**
	   * Метод возвращает регулярное выражения для проверки элемента на странице
	   * 
	   * @return регулярное выражение
	   */
		@Override
		public String getInputRegexp() {
			
			return "[\\\\wА-Яа-яЁё -.№:@\\\\/\\\\\\\\]+";
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
			
			Tag elementInput = parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "text");
				 put(Tag.Property.MAXLENGTH, (String) generator.getAttribute(TagGenerator.Attribute.MAXLENGTH));
				 put(Tag.Property.AUTOCOMPLETE, "off");
				 put(Tag.Property.PLACEHOLDER, (String) generator.getAttribute(TagGenerator.Attribute.HINT));
				 put(Tag.Property.STYLE, "width: 99%;");
				 put(Tag.Property.CHANGE, !"".equals(getInputRegexp())
						 ? "if ($(this).val() && !($(this).val().match(\"^" + getInputRegexp() + "$\"))) { alert(\"Введенные данные \\'\" + $(this).val() + \"\\' не соответствуют формату.\"); $(this).val(\"\").change().attr( \"class\", \"error\").trigger(\"refresh\").focus(); return false;} else {$(this).attr( \"class\", \"\").trigger(\"refresh\")}" 
						 : ""
				);
				 put(Tag.Property.MOUSEDOWN, "setTimeout(function() {$('#visible_" + name + "').focus();}, 100);");
			}});
			
			return elementInput;
		}
}
