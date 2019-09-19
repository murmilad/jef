package com.technology.jef.widgets.text;

import com.technology.jef.widgets.Text;

/**
* Виджет текст
*/
public class Inn extends Text {

	
	  /**
	   * Метод возвращает регулярное выражения для проверки элемента на странице
	   * 
	   * @return регулярное выражение
	   */
		@Override
		public String getInputRegexp() {
			
			return "([0-9]{10}|[0-9]{12})";
		}
		
}
