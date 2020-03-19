package com.technology.jef.widgets;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

import java.util.HashMap;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет Выпадающий список
*/
public class Info extends Widget {

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
			if (!"".equals(generator.getAttribute(TagGenerator.Attribute.HINT))) {
				parrent.add(Tag.Type.FONT, (String) generator.getAttribute(TagGenerator.Attribute.HINT), new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.TYPE, "text");
					 put(Tag.Property.COLOR, "gray");
				}});
			}
			
			Tag elementInput = parrent.add(Tag.Type.SPAN, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.STYLE, "padding: 1pt 5px; vertical-align: middle; text-align: left;");
			}});

			return elementInput;
		}

	  /**
	   * Метод возвращает функционал устанавливающий значение элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 	val - JSON объект, содержащий возвращенное сервисом значение элемента
	   * 
	   * @return код JavaScript
	   */
		public String getSetValueJS() {
			
			return 	(" \n" + 
	"	$('#visible_${child_name}').html(data.value).change(); \n" +
	"	$('#visible_${child_name}').bind('change', function(){ \n" + 
	"		$('#${system_prefix}_changed_${child_name}').val('1') \n" + 
	"	}); \n"  
					).replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
		}

	  /**
	   * Метод возвращает функционал отчищающий значение элемента
	   * Применяемые переменные: 
	   * 	${child_name} - имя текущего (зависимого) элемента
	   * 
	   * @return код JavaScript
	   */
		public String getCleanValueJS() {
			
			return 	(" \n" + 
	"	if ($('#visible_${child_name}').html() !== '') { \n" + 
	"		$('#visible_${child_name}').html('').change(); \n" + 
	"	} \n");
		}

}
