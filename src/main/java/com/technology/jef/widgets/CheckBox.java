package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

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
		
		Tag elementInput = parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "visible_" + name);
			 put(Tag.Property.NAME, "visible_" + name);
			 put(Tag.Property.TYPE, "checkbox");
		}});
		
		return elementInput;
	}

	@Override
	public String getSetValueJS() {
		
		return 		(" \n" + 
	"	$('#visible_${child_name}').prop('checked', data.value === '1' ? true : false);  \n" + 
	"	$('#visible_${child_name}').change(); \n" + 
	"	$('#${child_name}').val(data.value); \n");
	}

	@Override
	public String getCleanValueJS() {
		
		return "$('#visible_${child_name}').prop('checked', false);";
	}

	@Override
	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {
		element.setProperty(Tag.Property.CHANGE, element.getProperty(Tag.Property.CHANGE) + "onChangeReadOnly" + name + "({value: this.checked ? 1 : 0});");

		return element;
	}
}
