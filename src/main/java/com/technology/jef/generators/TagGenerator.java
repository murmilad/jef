package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.Tag;

/**
* Абстрактный класс сборщика DOM модели нижнего уровня
*/
public abstract class TagGenerator {

   /**
   * Cборщик DOM моделей указанного уровня (тег в XML описании интерфейса)
   */
	public enum Name {
		INTERFACE,
		FORMS,
		FORM,
		SCRIPT,
		GROUP,
		FORM_ITEM,
		TABS,
		TAB,
		CONTAINERS,
		CONTAINER,
	}

   /**
   * Атрибут элемента интерфейса (атрибут в XML описании интерфейса)
   */
	public enum Attribute {
		SERVICE,              // Адрес rest сервиса для получения/сохранения данных формы
		API,                  // Класс обработчик элемента в бэкэнде
		HEADER,               // Ссылка на произвольную шапку формы
		PARRENT_API,          // Класс обработчик элемента в бэкэнде, который должен быть сохранен перед текущим 
		REQUIRED,             // Поле является обязательным для заполнения
		ID,                 // Уникальное имя элемента в XML представлении интерфейса
		NAME,               // Заголовок группы/формы
		HEIGHT,               // Высота элемента интерфейса
		WIDTH,                // Ширина элемента интерфейса
		HINT,                 // Подсказка для пользователя
		TYPE,                 // Виджет элемента интерфейса
		MAXLENGTH,            // Максимальное количество вводимых символов
		IS_MULTIPLIE,         // Признак того, что группа является добавляемой
		FIXED,                // Признак того, что элемент интерфейса не будет уменьшаться при скрытии
		FORCE_AJAX,           // Признак того, что зависимые списки будут перезагружаться при изменении
		DEFAULT_IMAGE,        // Картинка по умолчанию
		                      // родительских несмотря на то, что не все родительские заполнены
		HANDLER,              
		AJAX_VISIBLE_PARRENT, // Список родительских элементов, влияющих на видимость
		AJAX_ACTIVE_PARRENT,  // Список родительских элементов, влияющих на возможность изменений
		AJAX_VALUE_PARRENT,   // Список родительских элементов, влияющих на значение
		AJAX_LIST_PARRENT,    // Список родительских элементов, влияющих на содержание списочных элементов
		AJAX_VALUE_CHILD,     // Список зависимых элементов, у корых будет проставляться результаты поиска
		HIDE_IF_EMPTY,        // Скрывать список, если он не содержит ни одного элемента 
		FORM_ID,              // ID формы которая будет загружена при переходе навигации 
		STYLE,                // Стиль

		VISIBLE_ROW,          // Исскуственное свойство для возврата DOM модели отображаемой строки с элементом (нужен чтобы скрыть Widget Hidden)
		VISIBLE_TAG,          // Исскуственное свойство для возврата DOM модели отображаемого тега с элементом (нужен чтобы скрыть Widget Hidden)
		PREFIX,               // Исскуственное свойство для возврата префикса групповой формы
		TEMPLATE,             // Иссукственное свойство для возврата шаблоно групповой формы
		WIDGET,               // Иссукственное свойство для возврата Виджета элемента
		FORM_COUNT,           // Иссукственное свойство для возврата количества форм (страниц) находящихся на текущем интерфейсе
		ITEMS,                // Иссукственное свойство для возврата списка параметров групповой формы
		ACCEPT,               // Доступные mimetype для виджета File
		JOINED_BY,            // Группы, объединенные по типу
		MAX_SIZE,             // Максимальный размер файла
		SRC,                  // Источник файла скрипта

	}

	public interface Handler  {
		public void handle(TagGenerator currentGenerator) throws SAXException;
	}

	HashMap<TagGenerator.Name, LinkedList<Handler>> handlers = new HashMap();

	/**
	* Родительский адрес в DOM модели 
	*/
	Tag dom;

	/**
	* Атрибуты в XML представлении интерфейса  
	*/
	Attributes attributes;
	
	/**
	* Генератор вышестоящего уровня (нужен для формирования событий JS)  
	*/
	TagGenerator parrent;
	
	/**
	* Имя генератора  
	*/
	Name name;

	  /**
	   * Метод получения имени генератора
	   * 
	   * @return Имя генератора
	   */
	public abstract Name getName();

	  /**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	public abstract Tag generate(String qName)  throws SAXException;

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	 * @throws SAXException 
	   * 
	   */
	public abstract void onEndElement() throws SAXException;
	
	  /**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param dom текущий адрес в DOM модели 
	   * @param qName имя тега в XML представлении интерфейса
	   * @param attributes атрибуты тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	public Tag generate(Tag dom, String qName, Attributes attributes, TagGenerator parrent) throws SAXException {
		this.dom = dom;
		this.attributes = new AttributesImpl(attributes);

		this.parrent = parrent;

		this.dom = generate(qName);
		return this.dom; 
	}

	  /**
	   * Метод получения текущего адреса DOM модели для данного уровня гернератора
	   * Позволяет получить нужный адрес в зависимости от атрибутов и имени тега в XML представлении
	   * Нужен для тех случаев, когда генерация дочерних элементов должна проводиться для специальных
	   * родителей DOM модели (группы)
	   * 
	   * @param name имя тега в XML представлении интерфейса
	   * @param attributes атрибуты тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	public Tag getDom(Name name, Attributes attributes)  throws SAXException{
		return dom;
	}

	public Tag getDom()  throws SAXException {
		return dom;
	}

	public void setDom(Tag dom)  throws SAXException {
		this.dom = dom;
	}

	public TagGenerator getParrent()  throws SAXException {
		return parrent;
	}

	public void setParrent(TagGenerator parrent)  throws SAXException {
		this.parrent = parrent;
	}
	
	  /**
	   * Метод получения атрибута текущего тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Содержимое атрибута
	   */
	public Object getAttribute(TagGenerator.Attribute attributeName)  throws SAXException {
		return  hasAttribute(attributeName) ? attributes.getValue(attributeName.toString().toLowerCase()) : "";
	}

	  /**
	   * Метод проверки наличия атрибута у тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Признак наличия (true атрибут есть; false атрибута нет)
	   */
	public Boolean hasAttribute(TagGenerator.Attribute attributeName)  throws SAXException {
		return  attributes.getIndex(attributeName.toString().toLowerCase()) >= 0;
	}


	  /**
	   * Метод получения списка событий, объявленных текущим генератором
	   * 
	   * @param name уровень генератора, на котором будет вызываться событие
	   * @return список событий
	   */
	public LinkedList<Handler> gethHandlerList(TagGenerator.Name name)  throws SAXException {
		return handlers.containsKey(name) ? handlers.get(name) : new LinkedList<Handler>(); 
	}
	

	  /**
	   * Метод объявления событий для уровней генераторов, выполняющихся 
	   * ниже по XML представлению интерфейса
	   * 
	   * @param name уровень генератора, на котором будет вызываться событие
	   */
	public void addHandler(TagGenerator.Name name, Handler callback)  throws SAXException {
		if (handlers.containsKey(name)) {
			handlers.get(name).add(callback);
		} else {
			handlers.put(name, new LinkedList<Handler>() {{
				add(callback);
			}});
		}
			
	}
}
