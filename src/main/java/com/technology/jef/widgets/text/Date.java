package com.technology.jef.widgets.text;

import com.technology.jef.widgets.Text;

/**
* Виджет текст
*/
public class Date extends Text {

	
	  /**
	   * Метод возвращает регулярное выражения для проверки элемента на странице
	   * 
	   * @return регулярное выражение
	   */
		@Override
		public String getInputRegexp() {
			
			return "(0[1-9]|[12][0-9]|3[01])\\\\.(0[1-9]|1[012])\\\\.(19|20)[0-9]{2}";
		}
		
}
