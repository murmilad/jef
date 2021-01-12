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
public class FileShort extends File {

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

			Tag downloadForm = input.add(Tag.Type.FORM, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "download_visible_" + name);
				 put(Tag.Property.NAME, "download_visible_" + name);
				 put(Tag.Property.METHOD, "post");
				 put(Tag.Property.ACTION, "rest/form/base64_to_image");
			}});

			downloadForm.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.TYPE, "hidden");
				 put(Tag.Property.ID, "base64_visible_" + name);
				 put(Tag.Property.NAME, "base64");
			}});
			downloadForm.add(Tag.Type.A, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "download_button_" + name);
				 put(Tag.Property.NAME, "download_button_" + name);
				 put(Tag.Property.HREF, "#");
				 put(Tag.Property.CLICK, "$('#download_visible_" + name + "').submit();");
			}}).add(Tag.Type.DIV,CurrentLocale.getInstance().getTextSource().getString("download"), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "download_button_div_" + name);
				 put(Tag.Property.NAME, "download_button_div_" + name);
			}});
			
			Tag submit =  input.add(Tag.Type.DIV,
					new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.ID, "div_submit_data_" + name);
					 put(Tag.Property.NAME, "div_submit_data_" + name);
				}});
			
			submit.add(Tag.Type.FONT, "<br>" + (String) generator.getAttribute(TagGenerator.Attribute.HINT), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.TYPE, "text");
				 put(Tag.Property.CLASS, "interface_hint");
			}});

			input = submit.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "file");
				 put(Tag.Property.WIDTH, "200");
				 put(Tag.Property.STYLE, "width:100%;");
				 put(Tag.Property.ACCEPT, !"".equals(generator.getAttribute(TagGenerator.Attribute.ACCEPT)) ? (String) generator.getAttribute(TagGenerator.Attribute.ACCEPT) : "");
				 put(Tag.Property.DATA_URL, (String) generator.getAttribute(TagGenerator.Attribute.SERVICE) + "image_to_base64");
				 put(Tag.Property.DATA_FILEBROWSETEXT, CurrentLocale.getInstance().getTextSource().getString("file_browse_text"));
				 put(Tag.Property.DATA_FILEPLACEHOLDER, CurrentLocale.getInstance().getTextSource().getString("file_placeholder"));
			}});
		
			
			return input;
		}

}
