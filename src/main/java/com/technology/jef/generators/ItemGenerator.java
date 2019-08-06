package com.technology.jef.generators;

import java.util.HashMap;

import com.technology.jef.Tag;
import com.technology.jef.widgets.Widget;
import com.technology.jef.widgets.WidgetFactory;

/**
* Класс сборщика DOM модели уровня формы
*/
public class ItemGenerator extends TagGenerator {

	private Widget widget = null;
	
	private Tag visibleRow;
	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	@Override
	public Tag generate(String qName) {
		
		
		// Добавляем сообщение "Ожидайте...", которое будет показываться при загрузке связных списков
		dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "background_overlay_wait_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
			 put(Tag.Property.NAME, "background_overlay_wait");
			 put(Tag.Property.CLASS, "background_overlay_wait");
		}});

		dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_box_wait_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
			 put(Tag.Property.NAME, "message_box_wait");
			 put(Tag.Property.CLASS, "message_box_wait");
		}}).add(Tag.Type.DIV, "Подождите...", new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "message_overlay_wait");
			 put(Tag.Property.CLASS, "message_overlay_wait");
		}});
		
		Tag element = dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "div_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
		     put(Tag.Property.CLASS, "interface_element_horizontal");
		     put(Tag.Property.STYLE, 
		    		 (hasAttribute(TagGenerator.Attribute.HEIGHT) ? ("height: " +  getAttribute(TagGenerator.Attribute.HEIGHT) + ";") : "") 
		    		 + ("true".equals(getAttribute(TagGenerator.Attribute.FIXED)) ? "height: 45px;" : "") 
		    		 + (hasAttribute(TagGenerator.Attribute.WIDTH) ? "" : "width:100%;")
		     );
		}});

		Tag element_table = element.add(Tag.Type.TABLE, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "table_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
		     put(Tag.Property.CLASS, "interface_element_horizontal");
		     put(Tag.Property.STYLE, 
		    		 (hasAttribute(TagGenerator.Attribute.HEIGHT) ? ("height: " +  getAttribute(TagGenerator.Attribute.HEIGHT) + ";") : "") 
		    		 + ("true".equals(getAttribute(TagGenerator.Attribute.FIXED)) ? ("width: " + getAttribute(TagGenerator.Attribute.WIDTH) + ";") : "width:100%;") 
		     );
		}});
		
		visibleRow = element_table
				.add(Tag.Type.TR, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.ID, "tr_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX));
				}});
				
		Tag element_row = visibleRow
				.add(Tag.Type.TD)
				.add(Tag.Type.TABLE, new HashMap<Tag.Property, String>(){{
				     put(Tag.Property.CLASS, "interface_element_horizontal");
				     put(Tag.Property.STYLE, 
				    		 (hasAttribute(TagGenerator.Attribute.WIDTH) ? ("width: " + getAttribute(TagGenerator.Attribute.WIDTH) + ";") : "width:100%;") 
				     );
				}})
				.add(Tag.Type.TR, new HashMap<Tag.Property, String>(){{
				     put(Tag.Property.CLASS, "interface_element_horizontal");
				}});
		

		widget = WidgetFactory.getWidget((String) getAttribute(TagGenerator.Attribute.TYPE));
		
		Tag elementValue = new Tag(Tag.Type.DIV);
		switch (widget.getType()) {
		case DOUBLE: 
			String caption = ((String) getAttribute(TagGenerator.Attribute.NAME))
				.replace("\\n", "<br>");

			element_row.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.STYLE,"padding: 1pt 5px; vertical-align: middle; text-align: left;"); 
			}})
			.add(Tag.Type.NOBR)
			.add(Tag.Type.LABEL, caption + ":", new HashMap<Tag.Property, String>(){{
				put(Tag.Property.FOR,"visible_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
			}});
			elementValue = element_row.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.STYLE,"vertical-align: middle; width: 100%;"); 
			}});
			break;
		case SINGLE: 
			elementValue = element_row.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.STYLE,"vertical-align: middle; width: 100%;"); 
			     put(Tag.Property.COLSPAN, "2"); 
			}});
			break;
		}
		
		elementValue.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, "api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.TYPE, "hidden"); 
		     put(Tag.Property.VALUE, (String) getAttribute(TagGenerator.Attribute.API)); 
		}});

		elementValue.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "parrent_api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, "parrent_api_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.TYPE, "hidden"); 
		     put(Tag.Property.VALUE, (String) getAttribute(TagGenerator.Attribute.PARRENT_API)); 
		}});

		elementValue.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "required_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.NAME, "required_" + getAttribute(TagGenerator.Attribute.ID) + getAttribute(TagGenerator.Attribute.PREFIX)); 
		     put(Tag.Property.TYPE, "hidden"); 
		     put(Tag.Property.VALUE, (String) getAttribute(TagGenerator.Attribute.REQUIRED)); 
		}});

		return widget.assembleTag(((String) getAttribute(TagGenerator.Attribute.ID)).concat((String) parrent.getAttribute(TagGenerator.Attribute.PREFIX)), this, elementValue);
	}


	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) {
		switch (attributeName) {
		case VISIBLE_ROW:
			return visibleRow;
		case WIDGET:
			return widget;
		case SERVICE:
			return this.getParrent().getAttribute(TagGenerator.Attribute.SERVICE);
		case API:
			return !"".equals(super.getAttribute(TagGenerator.Attribute.API)) 
					? super.getAttribute(TagGenerator.Attribute.API)
					: this.getParrent().getAttribute(TagGenerator.Attribute.API);
		case PARRENT_API:
			return !"".equals(super.getAttribute(TagGenerator.Attribute.PARRENT_API)) 
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
