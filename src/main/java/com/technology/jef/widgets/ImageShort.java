package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

/**
* Виджет картинка
*/
public class ImageShort extends Image {

	@Override
	public ViewType getType () {
		return ViewType.DOUBLE;
	}

	@Override
		public Tag assembleTag(String name, TagGenerator generator) {
			
			
			Tag input =  parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "div_header_visible_" + name);
				 put(Tag.Property.NAME, "div_header_visible_" + name);
			}});

			input.add(Tag.Type.IMG, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "img_visible_" + name);
				 if (!"".equals(generator.getAttribute(TagGenerator.Attribute.DEFAULT_IMAGE))) {
					 put(Tag.Property.SRC, (String) generator.getAttribute(TagGenerator.Attribute.DEFAULT_IMAGE));
				 }
				 put(Tag.Property.ONERROR, "this.onerror=null; this.src='" + generator.getAttribute(TagGenerator.Attribute.DEFAULT_IMAGE) + "'");
				 put(Tag.Property.NAME, "img_visible_" + name);
				 put(Tag.Property.STYLE, "max-width: " + generator.getAttribute(TagGenerator.Attribute.WIDTH) +";");
			}});
			
			parrent.add(Tag.Type.FONT, "<br>" + (String) generator.getAttribute(TagGenerator.Attribute.HINT), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.TYPE, "text");
				 put(Tag.Property.CLASS, "interface_hint");
			}});

			input = input.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "file");
				 put(Tag.Property.WIDTH, "200");
				 put(Tag.Property.STYLE, "width:100%;");
				 put(Tag.Property.ACCEPT, "image/jpg,image/jpeg,image/gif,image/bmp");
			}});
		
			
			return input;
		}

}
