package com.technology.jef.generators;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.widgets.Widget;
import com.technology.jef.widgets.WidgetFactory;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

/**
* Класс сборщика DOM модели уровня формы
*/
public class ItemGenerator extends TagGenerator {

	private Widget widget = null;
	
	private Tag visibleRow;
	private Tag visibleTag;
	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {
		
		
		
		visibleTag = dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "div_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
		     put(Tag.Property.CLASS, "div_interface_element_horizontal interface_element_horizontal"
		    		 + ("1".equals(getAttribute(TagGenerator.Attribute.FIXED)) ? " block_height" : "") 
		    );
		     put(Tag.Property.STYLE, "position:relative;" + 
		    		 (hasAttribute(TagGenerator.Attribute.HEIGHT) ? ("height: " +  getAttribute(TagGenerator.Attribute.HEIGHT) + ";") : "") 
		    		 + (hasAttribute(TagGenerator.Attribute.WIDTH) ? "width:"+getAttribute(TagGenerator.Attribute.WIDTH)+";" : "width:100%;")
		     );
		}});

		visibleTag.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_box_wait_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
			 put(Tag.Property.NAME, "message_box_wait");
			 put(Tag.Property.CLASS, "message_box_loading background_color");
			}}).add(Tag.Type.DIV, CurrentLocale.getInstance().getTextSource().getString("loading"), new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "message_overlay_wait");
			 put(Tag.Property.CLASS, "message_overlay_loading");
			}});

		Tag element_table = visibleTag.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "table_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
		     put(Tag.Property.CLASS, "interface_element_horizontal");
		     put(Tag.Property.STYLE, 
		    		 (hasAttribute(TagGenerator.Attribute.HEIGHT) ? ("height: " +  getAttribute(TagGenerator.Attribute.HEIGHT) + ";") : "")  
		     );
		}});
		
		visibleRow = element_table
				.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.ID, "tr_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
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
		

		widget = WidgetFactory.getWidget((String) getAttribute(TagGenerator.Attribute.TYPE));
		
		Tag elementValue = new Tag(Tag.Type.DIV);
		switch (widget.getType()) {
		case DOUBLE: 
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
			break;
		case SINGLE: 
			elementValue = element_row.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.CLASS,"widgets_body widgets_body_float"); 
			     put(Tag.Property.STYLE, "display: block;");
			}});
			break;
		}
		
		elementValue.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, SYSTEM_PARAMETER_PREFIX + "_api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, SYSTEM_PARAMETER_PREFIX + "_api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.TYPE, "hidden"); 
		     put(Tag.Property.VALUE, (String) getAttribute(TagGenerator.Attribute.API)); 
		}});

		elementValue.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, SYSTEM_PARAMETER_PREFIX + "_parrent_api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, SYSTEM_PARAMETER_PREFIX + "_parrent_api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.TYPE, "hidden"); 
		     put(Tag.Property.VALUE, (String) getAttribute(TagGenerator.Attribute.PARRENT_API)); 
		}});

		elementValue.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, SYSTEM_PARAMETER_PREFIX + "_required_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, SYSTEM_PARAMETER_PREFIX + "_required_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.TYPE, "hidden"); 
		     put(Tag.Property.VALUE, (String) getAttribute(TagGenerator.Attribute.REQUIRED)); 
		}});

		elementValue.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, SYSTEM_PARAMETER_PREFIX + "_changed_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, SYSTEM_PARAMETER_PREFIX + "_changed_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.VALUE, "0"); 
		     put(Tag.Property.TYPE, "hidden"); 
		}});

		
		return widget.assembleTag(((String) getAttribute(TagGenerator.Attribute.ID)).concat((String) parrent.getAttribute(TagGenerator.Attribute.PREFIX)), this, elementValue);
	}


	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) throws SAXException {
		switch (attributeName) {
		case VISIBLE_ROW:
			return visibleRow;
		case VISIBLE_TAG:
			return visibleTag;
		case WIDGET:
			return widget;
		case SERVICE:
			return this.getParrent().getAttribute(TagGenerator.Attribute.SERVICE);
		case API:
			return !"".equals(super.getAttribute(TagGenerator.Attribute.API)) 
					? super.getAttribute(TagGenerator.Attribute.API)
					: this.getParrent().getAttribute(TagGenerator.Attribute.API);
		case PARRENT_API:
			return hasAttribute(TagGenerator.Attribute.PARRENT_API) 
					? super.getAttribute(TagGenerator.Attribute.PARRENT_API)
					: this.getParrent().getAttribute(TagGenerator.Attribute.PARRENT_API);
		case PREFIX:
			if (this.getParrent() != null) {
				return this.getParrent().getAttribute(TagGenerator.Attribute.PREFIX);
			} else {
				return "";
			}
		case AJAX_LIST_PARRENT:
		case AJAX_VALUE_PARRENT:
		case AJAX_ACTIVE_PARRENT:
		case AJAX_VISIBLE_PARRENT:
		case AJAX_VALUE_CHILD:
			if (!"".equals(super.getAttribute(attributeName))) {
				return ((String) super.getAttribute(attributeName)).split(",");
			} else {
				return new String[0];
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

}
