package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

/**
* Виджет документ
*/
public class Document extends Widget {

	@Override
	public ViewType getType () {
		return ViewType.SINGLE;
	}

	@Override
	public String getSetValueJS() {
		return ("	                                                 \n" 
				+ "			$(\"#img_visible_${child_name}\").prop('src', data.value);   \n"
				+ "			$(\"#visible_${child_name}\").change();   \n"
				+ "			$(\"#${child_name}\").val(data.value); \n" 
				+ "	                                            \n");
	}

	@Override
		public Tag assembleTag(String name, TagGenerator generator) {
			

			Tag fieldset = parrent.add(Tag.Type.FIELDSET);
			
			fieldset.add(Tag.Type.LEGEND).add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.FOR, name);
			}}).add(Tag.Type.SPAN, (String) generator.getAttribute(TagGenerator.Attribute.NAME),  new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "span_" + name);
				 put(Tag.Property.STYLE, !"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED)) ? "color: rgb(170, 0, 0);" : "color: rgb(0, 0, 0);");
			}});
			
			Tag input = fieldset.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "div_header_visible_" + name);
				 put(Tag.Property.NAME, "div_header_visible_" + name);
			}});

			input.add(Tag.Type.IMG, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "img_visible_" + name);
				 put(Tag.Property.NAME, "img_visible_" + name);
				 put(Tag.Property.STYLE, "max-width: " + generator.getAttribute(TagGenerator.Attribute.WIDTH) +";");
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
				 put(Tag.Property.STYLE, "width:" + generator.getAttribute(TagGenerator.Attribute.WIDTH) + ";");
				 put(Tag.Property.ACCEPT, "image/jpg,image/jpeg,image/gif,image/bmp");
			}});

			parrent.add(Tag.Type.SCRIPT, 						("		var imgType_${name};  \n" + 
	"		var img_${name} = new Image();    \n" + 
	"		img_${name}.crossOrigin = 'Anonymous';    \n" + 
	"		img_${name}.onload = function () {      \n" + 
	"		  var canvas = document.createElement('canvas');      \n" + 
	"		  ctx = canvas.getContext('2d');      \n" + 
	"		  canvas.height = img_${name}.naturalHeight;      \n" + 
	"		  canvas.width = img_${name}.naturalWidth;      \n" + 
	"		  ctx.drawImage(img_${name}, 0, 0);      \n" + 
	"		  var uri = canvas.toDataURL(imgType_${name});      \n" + 
	"		  $('#${name}').val(uri);      \n" + 
	"                $('#img_visible_${name}').prop('src', uri);      \n" + 
	"		};      \n" + 
	"		function setImageOnChange_${name}(img){     \n" + 
	"			if (img) { \n" + 
	"				imgType_${name} = img.type;  \n" + 
	"				img_${name}.src = window.URL.createObjectURL(img);     \n" + 
	"			} \n" + 
	"		}      \n")
	.replace("${name}", name));

			
			
			return input;
		}

	@Override
	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {
		element.setProperty(Tag.Property.CHANGE, element.getProperty(Tag.Property.CHANGE) + "setImageOnChange_" + name + "($(this).prop('files')[0]);");

		return element;
	}

	@Override
	public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
		return "";
	}
}


