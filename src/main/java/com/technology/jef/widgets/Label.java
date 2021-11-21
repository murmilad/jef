package com.technology.jef.widgets;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет заголовок
*/
public class Label extends Widget {

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
		@Override
		public Tag assembleTag(String name, TagGenerator generator) throws SAXException {
			if (!"".equals(generator.getAttribute(TagGenerator.Attribute.NAME))) {
				parrent.add(Tag.Type.SPAN, generator.getAttribute(TagGenerator.Attribute.NAME) + ":", new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.STYLE, "padding:5px;");
				}});
			} else {
				parrent.setProperty(Tag.Property.STYLE, "text-align: center; vertical-align: center;height: 30px;");
			}
			
			parrent.add(Tag.Type.SPAN, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
			}});
	
			return parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "hidden_" + name);
				 put(Tag.Property.NAME, "hidden_" + name);
				 put(Tag.Property.TYPE, "hidden");
			}});
		}

	  /**
	   * Метод возвращает функционал устанавливающий значение элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 	val - JSON объект, содержащий возвращенное сервисом значение элемента
	   * 
	   * @return код JavaScript
	   */
		@Override
		public String getSetValueJS() {
			
			return 
					("	$('#visible_${child_name}').html(data.value); \n" +
					"	$('input#${child_name}').trigger('setHiddenValue',[data.value]); \n" + 
					"	$('#hidden_${child_name}').val(data.value).change(); \n"+
			"	$('#visible_${child_name}').bind('change', function(){ \n" + 
			"		$('#${system_prefix}_changed_${child_name}').val('1') \n" + 
			"	}); \n").replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
		}

	  /**
	   * Метод возвращает функционал отчищающий значение элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 
	   * @return код JavaScript
	   */
		@Override
		public String getCleanValueJS() {
			
			return
					"	$('#visible_${child_name}').html(''); \n" +
					"	$('input#${child_name}').val(''); \n" + 
					"	$('#hidden_${child_name}').val('').change(); \n";
		}

	
}
