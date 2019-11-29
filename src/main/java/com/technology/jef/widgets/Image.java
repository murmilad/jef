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
				 put(Tag.Property.DATA_URL, (String) generator.getAttribute(TagGenerator.Attribute.SERVICE) + "image_to_base64");
			}});

			return input;
		}

	@Override
	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {

		parrent.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/jquery.ui.widget.js");
		}});

		parrent.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/jquery.iframe-transport.js");
		}});

		parrent.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/jquery.fileupload.js");
		}});

		parrent.add(Tag.Type.SCRIPT, 																("         \n" + 
	"	$(function () {       \n" + 
	"	    $('#visible_${name}').fileupload({    \n" + 
	"		type: 'POST',   \n" + 
	"		dataType: 'text',   \n" + 
	"		paramName: 'file',    \n" + 
	"		cache : false,   \n" + 
	"		contentType : false,   \n" + 
	"		processData : false,  \n" + 
	"	    submit: function (e, data) {       \n" + 
	"			if ( window.FileReader) {     \n" + 
	"				if (data.files && data.files[0]) {     \n" + 
	"					var reader = new FileReader();     \n" + 
	"					reader.onload = function(e) {     \n" + 
	"						$('#img_visible_${name}').attr('src', e.target.result);     \n" + 
	"						$('#${name}').val(e.target.result);     \n" + 
	"					}     \n" + 
	"					reader.readAsDataURL(data.files[0]);     \n" + 
	"					return false;    \n" + 
	"				}     \n" + 
	"			}     \n" + 
	"	        },       \n" + 
	"		success : function(data, textStatus, jqXHR) {   \n" + 
	"			$('#img_visible_${name}').attr('src', data); \n" + 
	"			$('#${name}').val(data);     \n" + 
	"		},   \n" + 
	"		error : function(jqXHR, textStatus, errorThrown) {   \n" + 
	"			showError(\"Error: \" +  textStatus + \" \"+ errorThrown, jqXHR.responseText);    \n" + 
	"		}   \n" + 
	"	    });       \n" + 
	"	}); \n")
		.replace("${name}", name));
		
		return element;
	}

	@Override
	public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
		return "";
	}
}
