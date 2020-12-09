package com.technology.jef.widgets;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

import java.util.HashMap;

import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

/**
* Виджет картинка
*/
public class File extends Widget {

	@Override
	public String getSetActiveJS() {
		
		return 
		"		if (data.value) { \n " + 
		"			$('#visible_${child_name}').prop( \"disabled\", false); \n " +
		"			$(\"#tr_${child_name}\" ).css('color', 'black'); \n "+
		"			$('#div_header_visible${child_name}').show(); \n " +
		"			$('#div_submit_data_${child_name}').show(); \n " +
		"           $(\"#visible_${child_name}\").trigger('refresh');" +
		"		} else { \n " +
		"			$('#div_header_visible${child_name}').hide(); \n " +
		"			$('#div_submit_data_${child_name}').hide(); \n " +
		"			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n " +
		"		} \n ";
	}
	
	@Override
	public ViewType getType () {
		return ViewType.SINGLE;
	}

	@Override
	public String getSetValueJS() {
		return 					("	                                                      \n" + 
	"	var fileTypeMatcher = data.value.match(/data:([a-zA-Z]*)\\/([a-zA-Z]*);base64,/i);    \n" + 
	"	if (!fileTypeMatcher || fileTypeMatcher[1] == 'image') {         \n" + 
	"		$('#img_visible_${child_name}').show();   \n" + 
	"		$('#download_visible_${child_name}').hide();   \n" + 
	"		$('#img_visible_${child_name}').attr('src', data.value);   \n" + 
	"	} else {   \n" + 
	"		$('#download_button_${child_name}').attr('class', 'download_button download_button_'+fileTypeMatcher[2]);   \n" + 
	"		$('#download_visible_${child_name}').show();   \n" + 
	"		$('#img_visible_${child_name}').hide();   \n" + 
	"		$('#base64_visible_${child_name}').val(data.value);   \n" + 
	"	} \n" + 
	"	$(\"input#${child_name}\").val(data.value);      \n" + 
	"	if (isLoading) {    \n" + 
	"		$(\"#visible_${child_name}\").change();   \n" + 
	"		$('#visible_${child_name}').bind('change', function(){      \n" + 
	"			$('#${system_prefix}_changed_${child_name}').val('1')      \n" + 
	"		});      \n" + 
	"		$('#visible_${name}').bindFirst('change', function(event){      \n" + 
	"			onChangeReadOnly${name}(event.delegateTarget);      \n" + 
	"		});      \n" + 
	"	} else {   \n" + 
	"		$(\"#visible_${child_name}\").change();   \n" + 
	"	}   \n")
	.replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
	}

	@Override
		public Tag assembleTag(String name, TagGenerator generator) {
			

			Tag fieldset = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.CLASS, "fieldset second_frames_border");
			}});
			
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
			downloadForm.add(Tag.Type.A, CurrentLocale.getInstance().getTextSource().getString("download"), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "download_button_" + name);
				 put(Tag.Property.NAME, "download_button_" + name);
				 put(Tag.Property.HREF, "#");
				 put(Tag.Property.CLICK, "$('#download_visible_" + name + "').submit();");
			}});
			
			Tag submit =  input.add(Tag.Type.DIV,
				new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "div_submit_data_" + name);
				 put(Tag.Property.NAME, "div_submit_data_" + name);
			}});

			submit.add(Tag.Type.FONT, (String) generator.getAttribute(TagGenerator.Attribute.HINT), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.TYPE, "text");
				 put(Tag.Property.CLASS, "interface_hint");
			}});

			input = submit.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "file");
				 put(Tag.Property.WIDTH, "200");
				 put(Tag.Property.STYLE, "width:" + generator.getAttribute(TagGenerator.Attribute.WIDTH) + ";");
				 put(Tag.Property.ACCEPT, !"".equals(generator.getAttribute(TagGenerator.Attribute.ACCEPT)) ? (String) generator.getAttribute(TagGenerator.Attribute.ACCEPT) : "");
				 put(Tag.Property.DATA_URL, (String) generator.getAttribute(TagGenerator.Attribute.SERVICE) + "image_to_base64");
				 put(Tag.Property.DATA_FILEBROWSETEXT, CurrentLocale.getInstance().getTextSource().getString("file_browse_text"));
				 put(Tag.Property.DATA_FILEPLACEHOLDER, CurrentLocale.getInstance().getTextSource().getString("file_placeholder"));
				 
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

		parrent.add(Tag.Type.SCRIPT, 																		("           \n" + 
	"	$(function () {         \n" + 
	"	    $('#visible_${name}').fileupload({      \n" + 
	"		type: 'POST',     \n" + 
	"		dataType: 'text',     \n" + 
	"		paramName: 'file',      \n" + 
	"		cache : false,     \n" + 
	"		contentType : false,     \n" + 
	"		processData : false,    \n" + 
	"	        submit: function (e, data) {         \n" + 
	"			if ( window.FileReader) {       \n" + 
	"				if (data.files && data.files[0]) {       \n" + 
	"					var reader = new FileReader();       \n" + 
	"					reader.onload = function(e) {  \n" + 
	"						var fileTypeMatcher = e.target.result.match(/data:([a-zA-Z]*)\\/([a-zA-Z]*);base64,/i);  \n" + 
	"						if (fileTypeMatcher[1] == 'image') {       \n" + 
	"							$('#img_visible_${name}').show(); \n" + 
	"							$('#download_visible_${name}').hide(); \n" + 
	"							$('#img_visible_${name}').attr('src', e.target.result); \n" + 
	"						} else { \n" + 
	"							$('#download_button_${name}').attr('class', 'download_button download_button_'+fileTypeMatcher[2]); \n" + 
	"							$('#download_visible_${name}').show(); \n" + 
	"							$('#img_visible_${name}').hide(); \n" + 
	"							$('#base64_visible_${name}').val(e.target.result); \n" + 
	"						}       \n" + 
	"						$('input#${name}').val(e.target.result);       \n" + 
	"					}       \n" + 
	"					reader.readAsDataURL(data.files[0]);       \n" + 
	"					return false;      \n" + 
	"				}       \n" + 
	"			}       \n" + 
	"	        },         \n" + 
	"		success : function(data, textStatus, jqXHR) {     \n" + 
	"			$('#visible_${name}').change();   \n" + 
	"			$('#img_visible_${name}').attr('src', data);   \n" + 
	"			$('input#${name}').attr('value', data);       \n" + 
	"		},     \n" + 
	"		error : function(jqXHR, textStatus, errorThrown) {     \n" + 
	"			showError(\"Error: \" +  textStatus + \" \"+ errorThrown, jqXHR.responseText);      \n" + 
	"		}     \n" + 
	"	    });         \n" + 
	"	});   \n")
		.replace("${name}", name));

		return element;
	}

	@Override
	public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
		return "";
	}
}
