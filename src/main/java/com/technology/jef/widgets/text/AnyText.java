package com.technology.jef.widgets.text;

import com.technology.jef.widgets.Text;

/**
* Виджет текст
*/
public class AnyText extends Text {

	
	  /**
	   * Метод возвращает регулярное выражения для проверки элемента на странице
	   * 
	   * @return регулярное выражение
	   */
		@Override
		public String getInputRegexp() {
			
			return ".*";
		}
		
}
