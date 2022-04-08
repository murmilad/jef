package com.technology.jef.generators;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator.Name;
import com.technology.jef.widgets.Widget;
import com.technology.jef.widgets.WidgetFactory;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

/**
* Класс сборщика DOM модели уровня элемента формы предварительного просмотра
*/
public class PreviewItemGenerator extends TagGenerator {

	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {
		
		
		
		Tag visibleTag = dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "div_preview_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
		     put(Tag.Property.CLASS, "div_interface_element_horizontal interface_element_horizontal"); 
		     put(Tag.Property.STYLE, "position:relative;" + 
		    		 (hasAttribute(TagGenerator.Attribute.HEIGHT) ? ("height: " +  getAttribute(TagGenerator.Attribute.HEIGHT) + ";") : "") 
		    		 + (hasAttribute(TagGenerator.Attribute.WIDTH) ? "width:"+getAttribute(TagGenerator.Attribute.WIDTH)+";" : "width:100%;")
		     );
		}});

		Tag element_table = visibleTag.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "table_preview_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
		     put(Tag.Property.CLASS, "interface_element_horizontal");
		     put(Tag.Property.STYLE, 
		    		 (hasAttribute(TagGenerator.Attribute.HEIGHT) ? ("height: " +  getAttribute(TagGenerator.Attribute.HEIGHT) + ";") : "")  
		     );
		}});
		
		Tag visibleRow = element_table
				.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.ID, "tr_preview_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
				    put(Tag.Property.CLASS, "block_height");
				    put(Tag.Property.STYLE, "display: block;");
				}});
				
		Tag element_row = visibleRow
				.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				    put(Tag.Property.CLASS, "interface_element_body");
				}})
				.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				     put(Tag.Property.CLASS, "interface_element_horizontal");
				}})
				.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				     put(Tag.Property.CLASS, "interface_element_horizontal");
				}});
		

		
		Tag elementValue = new Tag(Tag.Type.DIV);
		String caption = ((String) getAttribute(TagGenerator.Attribute.NAME))
			.replace("\\n", "<br>");

		element_row.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS,"widgets_header widgets_header_float"); 
		}})
			.add(Tag.Type.NOBR)
			.add(Tag.Type.LABEL, caption + ":", new HashMap<Tag.Property, String>(){{
				put(Tag.Property.FOR,"visible_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
				put(Tag.Property.CLASS, "widgets_label_color");
			}});
		elementValue = element_row.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS,"widgets_body widgets_body_float"); 
		}});

		elementValue.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "visible_preview_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, "visible_preview_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		}});

		elementValue.add(Tag.Type.SCRIPT, ("  \n" + 
				"	$(\"#visible_${name}\").bind('change', function(event){      \n" + 
				"			$('#visible_preview_${name}').html($('#visible_${name}').val() || $('[for=\"${name}' + $('input#${name}').val() + '\"]').html() || $('#visible_${name}').html());  \n" + 
				"	});     \n")
				.replace("${name}", ((String) getAttribute(TagGenerator.Attribute.ID)).concat((String) parrent.getAttribute(TagGenerator.Attribute.PREFIX)))
		);
//		"	$(\"#visible_${name}\").bind('setValue', function(event, data){      \n" + 
//		"			$('#visible_preview_${name}').html(data.name || data.value);  \n" + 
//		"	});     \n")
//		"				$('#visible_${child_name}').val(value.split('${name_value_separator}')[0]);                        \n" + 
//		"				$('#visible_${child_name}').trigger('autoCompleteChange');                        \n" + 
//		"		});     \n")
//		.replace("${name_value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)

		
		return elementValue;
	}

	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) throws SAXException {
		switch (attributeName) {
		case PREFIX:
			if (this.getParrent() != null) {
				return this.getParrent().getAttribute(TagGenerator.Attribute.PREFIX);
			} else {
				return "";
			}
		default:
			return super.getAttribute(attributeName);
		}
		
	}

	/**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	   * 
	   */
	@Override
	public void onEndElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Name getName() {
		return Name.PREVIEW_ITEM;
	}

}
