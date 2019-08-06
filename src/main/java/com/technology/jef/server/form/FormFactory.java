package com.technology.jef.server.form;

import com.technology.jef.server.exceptions.ServiceException;

/**
 * Класс - фабрика контроллеров
 */

public abstract class FormFactory {

	/**
	   * Метод инициализации контроллеров форм
	   * 
	   * @param name имя контроллера
	   * @return экземпляр класса контроллера
	   */

		public abstract Form getForm(String name) throws ServiceException;
	
}
