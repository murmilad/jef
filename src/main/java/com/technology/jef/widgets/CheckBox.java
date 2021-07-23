package com.technology.jef.widgets;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

import java.util.HashMap;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет выключатель
*/
public class CheckBox extends Widget {

	@Override
	public ViewType getType () {
		return ViewType.DOUBLE;
	}
	

	@Override
	public Tag assembleTag(String name, TagGenerator generator) {
		
		Tag elementInput = parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.CLASS, "widgets_height");
			 put(Tag.Property.STYLE, "margin: 1px;");
		}}).add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "visible_" + name);
			 put(Tag.Property.NAME, "visible_" + name);
			 put(Tag.Property.TYPE, "checkbox");
		}});
		
		return elementInput;
	}

	@Override
	public String getSetValueJS() {
		
		return 		(" \n" + 
	"	$('#visible_${child_name}').prop('checked', data.value === 'true' || data.value == '1');  \n" + 
	"	$('#visible_${child_name}').change(); \n" + 
	"	$('input#${child_name}').val(data.value === '1' ? 1 : 0); \n"+
	"	$('#visible_${child_name}').bind('change', function(){ \n" + 
	"		$('#${system_prefix}_changed_${child_name}').val('1') \n" + 
	"	}); \n" 
	).replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
	}

	@Override
	public String getCleanValueJS() {
		
		return 	(" \n" + 
	"	$('#visible_${child_name}').prop('checked', false); \n" + 
	"	$('input#${child_name}').val(0); \n")
	;
	}

	@Override
	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {
		element.add(Tag.Type.SCRIPT, 	(" \n" + 
			"			$('#visible_${name}').bindFirst('change', function(event){  \n" + 
			"				onChangeReadOnly${name}({value: event.delegateTarget.checked ? 1 : 0});  \n" + 
			"			});  \n")
			.replace("${name}", name)
		);
		

		return element;
	}
}
