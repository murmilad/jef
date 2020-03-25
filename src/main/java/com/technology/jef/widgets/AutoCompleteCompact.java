package com.technology.jef.widgets;

import java.util.HashMap;

import com.technology.jef.Tag;
import com.technology.jef.Tag.Property;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

/**
* Виджет список выподающий на основе введенного текста
*/
public class AutoCompleteCompact extends AutoCompleteEditable {
	@Override
	public ViewType getType () {
		return ViewType.SINGLE;
	}

	@Override
	public Tag assembleTag(String name, TagGenerator generator) {

			Tag input = super.assembleTag(name, generator);

			input.setProperty(Property.READONLY, "readonly");
			
			String caption = ((String) generator.getAttribute(TagGenerator.Attribute.NAME))
					.replace("\\n", "<br>");
			
			input.getParrent().add(Tag.Type.DIV, caption , new HashMap<Tag.Property, String>(){{
				put(Tag.Property.FOR,"visible_" + generator.getAttribute(TagGenerator.Attribute.ID) + generator.getAttribute(TagGenerator.Attribute.PREFIX)); 
			}});		
			return input;
	}


}
