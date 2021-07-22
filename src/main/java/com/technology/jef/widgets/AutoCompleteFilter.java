package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.generators.TagGenerator.Attribute;

import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Виджет список выподающий на основе введенного текста
*/
public class AutoCompleteFilter extends AutoComplete {

	@Override
	public String getCleanValueJS() {
			
			return 		(" \n" + 
"	$('#visible_${child_name}').val('');   \n" + 
"	$('input#${child_name}').val('');   \n" + 
" ");
		}
	@Override
	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {
		element.removeProperty(Tag.Property.READONLY);
		element.getParrent().add(Tag.Type.SCRIPT,	(" \n" + 
	"			$('#visible_${name}').on('focus', function(){  \n" + 
	"				$('#visible_${name}').on('firstedit', function(){  \n" + 
	"					if ($('#visible_${name}').val() === '---') { \n" + 
	"						$('#visible_${name}').val('');  \n" + 
	"					} \n" + 
	"					$('#visible_${name}').unbind('firstedit');  \n" + 
	"				});  \n" + 
	"			});  \n" + 
	"			$('#visible_${name}').on('keydown', function(){  \n" + 
	"				$('#visible_${name}').trigger('firstedit');  \n" + 
	"			});  \n")
				.replace("${name}", name)
		);
		
		return super.postAssembleTag(name, generator, element);
	}

	@Override
	public String getValueJS(TagGenerator currentGenerator, String prefix, Attribute parrentType) throws SAXException {
			
		String name = ((String)currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat((String)currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX));
		String nameAPI = name.replace(prefix, "");
			

			return "'filter_" + nameAPI + PARAMETER_NAME_VALUE_SEPARATOR + "' + ($('#visible_" + name + "').val() ? $('#visible_" + name + "').val() : '---') + '" + PARAMETER_SEPARATOR + "' + " + super.getValueJS(currentGenerator, prefix, parrentType);
	}
}
