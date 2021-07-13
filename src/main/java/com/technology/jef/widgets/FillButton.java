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
public class FillButton extends Widget {
		@Override
		public ViewType getType() {
			// TODO Auto-generated method stub
			return ViewType.SINGLE;
		}

		@Override
		public String getSetValueJS() {
			return 	(" \n" + 
	" $('#${child_name}').val(data.value);  \n" + 
	" $('#visible_${child_name}').val(data.value);  \n" + 
	" $('#${child_name}').bind('fill', function(){   \n" +
	"    $('#visible_${child_name}').change();  \n" + 
	" });   \n");
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
			
			parrent.add(Tag.Type.SCRIPT, 	(" \n" + 
				"			$('#${name}').bind('fill', function(){ \n" + 
				"				$('#visible_${name}').change(); \n" + 
				"			}); \n")
				.replace("${name}", name));
			
			parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name + "_button");
				 put(Tag.Property.NAME, "visible_" + name + "_button");
				 put(Tag.Property.TYPE, "button");
				 put(Tag.Property.VALUE, (String) generator.getAttribute(TagGenerator.Attribute.NAME));
				 put(Tag.Property.STYLE, "margin-left:10px;margin-right:10px;width:93%;padding:0px !important;");
				 put(Tag.Property.CLASS, "first_color second_text_color interface_button widgets_height");
				 put(Tag.Property.CLICK, "$('#" + name + "').trigger('fill');$('#" + name + "').unbind('fill');");
			}});
			Tag elementInput = parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "hidden");
			}});
			return elementInput;
		}
}
