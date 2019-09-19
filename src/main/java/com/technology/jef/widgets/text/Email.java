package com.technology.jef.widgets.text;

import com.technology.jef.widgets.Text;

/**
* Виджет текст
*/
public class Email extends Text {

	
	  /**
	   * Метод возвращает регулярное выражения для проверки элемента на странице
	   * 
	   * @return регулярное выражение
	   */
		@Override
		public String getInputRegexp() {
			
			return "([\\\\w-]+\\\\.)*[\\\\w-]+\\\\@([0-9a-zA-Z-]{1,}\\\\.){1,}[0-9a-zA-Z-]{2,6}";
		}
		
}
