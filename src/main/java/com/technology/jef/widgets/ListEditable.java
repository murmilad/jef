package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет Выпадающий список с возможностью выбрать значение "Иное"
*/
public class ListEditable extends List {

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
			Tag mainInput = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.ID, "fieldset_" + name);
				put(Tag.Property.CLASS, "fieldset second_frames_border");
			}});

			mainInput
					.add(Tag.Type.LEGEND)
					.add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.FOR, "visible_" + name);
					}})
					.add(Tag.Type.SPAN, (String) generator.getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.NAME, "span_" + name);
						 put(Tag.Property.STYLE, !"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED)) ? "color: rgb(170, 0, 0);" : "color: rgb(0, 0, 0);");
					}});
					

			if (!"".equals(generator.getAttribute(TagGenerator.Attribute.HIDE_IF_EMPTY))) {
				mainInput.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.ID, "is_empty_" + name);
					put(Tag.Property.NAME, "is_empty_" + name);
					put(Tag.Property.TYPE, "hidden");
				}});
			}
			
			return getListBody(name, generator, mainInput);
		}
		
		protected Tag getListBody(String name, TagGenerator generator, Tag mainInput) throws SAXException {

			Tag styledDiv = mainInput.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.CLASS, "styled");
			}});
			
			Tag elementInput = styledDiv.add(Tag.Type.SELECT, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.ID, "visible_" + name);
				put(Tag.Property.NAME, "visible_" + name);
				put(Tag.Property.STYLE, "width: 100%;  -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;");
				put(Tag.Property.CHANGE, "showHideOtherValue" + name + "(this);");
			}});
	
			styledDiv.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.ID, "other_" + name);
				put(Tag.Property.NAME, "other_" + name);
				put(Tag.Property.TYPE, "text");
				put(Tag.Property.STYLE, "width: 100%;  -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;");
			}});
		
			if (!"".equals(generator.getAttribute(TagGenerator.Attribute.HIDE_IF_EMPTY))) {
				mainInput.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.ID, "is_empty_" + name);
					put(Tag.Property.NAME, "is_empty_" + name);
					put(Tag.Property.TYPE, "hidden");
				}});
			}
		
			elementInput.add(Tag.Type.SCRIPT, 
				("			function showHideOtherValue${name}(current){ \n" + 
				"				if (current.value == 'other') { \n" + 
				"					$('#other_${name}').css('display', 'inline'); \n" + 
				"				} else { \n" + 
				"					$('#other_${name}').css('display','none'); \n" + 
				"					$('#other_${name}').val(''); \n" + 
				"				} \n" + 
				"			} \n")
				.replace("${name}", name)
			);
			
			return elementInput;
		}
		
}
