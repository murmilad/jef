package com.technology.jef.widgets.text;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Text;

/**
* Виджет текст
*/
public class ReadOnly extends Text {

	
	  /**
	   * Метод возвращает регулярное выражения для проверки элемента на странице
	   * 
	   * @return регулярное выражение
	   */
		@Override
		public String getInputRegexp() {
			
			return ".*";
		}
		
		@Override
		public Tag assembleTag(String name, TagGenerator generator) {
			
			Tag inputText = super.assembleTag(name, generator);
			
			inputText.setProperty(Tag.Property.DISABLED, "disabled");
			
			return inputText;
		}
		
}
