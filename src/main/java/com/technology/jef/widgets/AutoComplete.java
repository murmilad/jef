package com.technology.jef.widgets;

import com.technology.jef.Tag;
import com.technology.jef.Tag.Property;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет список выподающий на основе введенного текста
*/
public class AutoComplete extends AutoCompleteEditable {

		@Override
		public Tag assembleTag(String name, TagGenerator generator) {

			Tag input = super.assembleTag(name, generator);

			input.setProperty(Property.READONLY, "readonly");
			
			return input;
		}


}
