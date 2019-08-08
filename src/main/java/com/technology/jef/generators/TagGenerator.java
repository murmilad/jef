package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import org.xml.sax.Attributes;
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
	}

   /**
   * Атрибут элемента интерфейса (атрибут в XML описании интерфейса)
   */
	public enum Attribute {
		REDIRECT,             // Адрес по которому будет осуществлен переход при успешном сохранении формы
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
		                      // родительских несмотря на то, что не все родительские заполнены
		HANDLER,              
		AJAX_VISIBLE_PARRENT, // Список родительских элементов, влияющих на видимость
		AJAX_ACTIVE_PARRENT,  // Список родительских элементов, влияющих на возможность изменений
		AJAX_VALUE_PARRENT,   // Список родительских элементов, влияющих на значение
		AJAX_LIST_PARRENT,    // Список родительских элементов, влияющих на содержание списочных элементов
		AJAX_VALUE_CHILD,     // Список зависимых элементов, у корых будет проставляться результаты поиска
		HIDE_IF_EMPTY,        // Скрывать список, если он не содержит ни одного элемента 

		VISIBLE_ROW,          // Исскуственное свойство для возврата DOM модели отображаемой строки с элементом (нужен чтобы скрыть Widget Hidden)
		PREFIX,               // Исскуственное свойство для возврата префикса групповой формы
		TEMPLATE,             // Иссукственное свойство для возврата шаблоно групповой формы
		WIDGET,               // Иссукственное свойство для возврата Виджета элемента
		FORM_COUNT,           // Иссукственное свойство для возврата количества форм (страниц) находящихся на текущем интерфейсе

	}

	public interface Handler {
		public void handle(TagGenerator currentGenerator);
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
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	public abstract Tag generate(String qName);

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	   * 
	   */
	public abstract void onEndElement();
	
	  /**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param dom текущий адрес в DOM модели 
	   * @param qName имя тега в XML представлении интерфейса
	   * @param attributes атрибуты тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	public Tag generate(Tag dom, String qName, Attributes attributes, TagGenerator parrent) {
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
	public Tag getDom(Name name, Attributes attributes) {
		return dom;
	}

	public Tag getDom() {
		return dom;
	}

	public void setDom(Tag dom) {
		this.dom = dom;
	}

	public TagGenerator getParrent() {
		return parrent;
	}

	public void setParrent(TagGenerator parrent) {
		this.parrent = parrent;
	}
	
	  /**
	   * Метод получения атрибута текущего тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Содержимое атрибута
	   */
	public Object getAttribute(TagGenerator.Attribute attributeName) {
		return  hasAttribute(attributeName) ? attributes.getValue(attributeName.toString().toLowerCase()) : "";
	}

	  /**
	   * Метод проверки наличия атрибута у тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Признак наличия (true атрибут есть; false атрибута нет)
	   */
	public Boolean hasAttribute(TagGenerator.Attribute attributeName) {
		return  attributes.getIndex(attributeName.toString().toLowerCase()) >= 0;
	}


	  /**
	   * Метод получения списка событий, объявленных текущим генератором
	   * 
	   * @param name уровень генератора, на котором будет вызываться событие
	   * @return список событий
	   */
	public LinkedList<Handler> gethHandlerList(TagGenerator.Name name) {
		return handlers.containsKey(name) ? handlers.get(name) : new LinkedList<Handler>(); 
	}
	

	  /**
	   * Метод объявления событий для уровней генераторов, выполняющихся 
	   * ниже по XML представлению интерфейса
	   * 
	   * @param name уровень генератора, на котором будет вызываться событие
	   */
	public void addHandler(TagGenerator.Name name, Handler callback) {
		if (handlers.containsKey(name)) {
			handlers.get(name).add(callback);
		} else {
			handlers.put(name, new LinkedList<Handler>() {{
				add(callback);
			}});
		}
			
	}
}
