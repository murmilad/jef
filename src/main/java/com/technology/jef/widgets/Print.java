package com.technology.jef.widgets;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

import java.util.HashMap;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет кнопка
*/
public class Print extends HTML {
		@Override
		public ViewType getType() {
			// TODO Auto-generated method stub
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
			parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.CLASS,"widgets_header widgets_header_float"); 
			}})
			.add(Tag.Type.NOBR)
			.add(Tag.Type.LABEL, "&nbsp;", new HashMap<Tag.Property, String>(){{
				put(Tag.Property.FOR,"visible_" + name); 
				put(Tag.Property.CLASS, "widgets_label_color");
			}});

			parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.CLASS,"widgets_body widgets_body_float"); 
			}}).add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name + "_button");
				 put(Tag.Property.NAME, "visible_" + name + "_button");
				 put(Tag.Property.TYPE, "button");
				 put(Tag.Property.VALUE, (String) generator.getAttribute(TagGenerator.Attribute.NAME));
				 put(Tag.Property.STYLE, "padding: 0px 5px; width: 100%; -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;");
				 put(Tag.Property.CLASS, "first_color second_text_color interface_button widgets_height");
				 put(Tag.Property.CLICK, "$(window.open().document.body).html($('#" + name + "_body').html());");
			}});

			Tag elementInput = parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, name + "_body");
				 put(Tag.Property.NAME, name + "_body");
				 put(Tag.Property.STYLE, "display: none;");
			}}).add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.VALUE, "0");
			}});
			
			parrent.add(Tag.Type.SCRIPT, getJsonToHtmlJs(name));
			return elementInput;
		}
			


}
