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
public class Image extends Widget {

	@Override
	public ViewType getType () {
		return ViewType.SINGLE;
	}

	@Override
	public String getSetValueJS() {
		return ("	if (isLoading) {                                                 \n" 
				+ "		$(\"#visible_${child_name}\").bind('setValue', function(){    \n"
				+ "			$(\"#img_visible_${child_name}\").props('src', '/photos/'+data.value+'?nocache=' +  Math.floor(Math.random() * 100)).click();   \n"
				+ "			$(\"#visible_${child_name}\").change();   \n"
				+ "			$(\"#${child_name}\").val(data.value); \n" 
				+ "			                                                        \n" 
				+ "		});                                                         \n"
				+ "	} else {                                                        \n" 
				+ "		$(\"#img_visible_${child_name}\").props('src', '/photos/'+data.value+'?nocache=' +  Math.floor(Math.random() * 100)).click();   \n"
				+ "		$(\"#visible_${child_name}\").change();   \n"
				+ "		$(\"#${child_name}\").val(data.value);   \n" 
				+ "	}                                            \n");
	}

	@Override
		public Tag assembleTag(String name, TagGenerator generator) {
			
			Tag fieldset = parrent.add(Tag.Type.FIELDSET);
			
			Tag input = fieldset.add(Tag.Type.LEGEND);
			
			input = input.add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.FOR, name);
			}});
			
			input = input.add(Tag.Type.SPAN, (String) generator.getAttribute(TagGenerator.Attribute.NAME),  new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "span_" + name);
				 put(Tag.Property.STYLE, !"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED)) ? "color: rgb(170, 0, 0);" : "color: rgb(0, 0, 0);");
			}});
			
			input = input.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "div_header_visible_" + name);
				 put(Tag.Property.NAME, "div_header_visible_" + name);
			}});
			input.add(Tag.Type.IMG, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "img_visible_" + name);
				 put(Tag.Property.NAME, "img_visible_" + name);
			}});
			
			input.add(Tag.Type.FONT, (String) generator.getAttribute(TagGenerator.Attribute.HINT), new HashMap<Tag.Property, String>(){{
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

	@Override
	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {
		element.setProperty(Tag.Property.CHANGE, element.getProperty(Tag.Property.CHANGE) + "onChangeReadOnly" + name + "({value: this.value.replace(/.+[\\\\\\/]+/, \"\")});");

		return element;
	}

	@Override
	public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
		return "";
	}
}
