package com.technology.jef;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



/**
 * Класс элемента DOM модели страницы.
 */
public class Tag {

   /**
   * Перечисление поддерживаемых  имен HTML тегов DOM модели страницы.  
   */
	public enum Type {
		H1,
		H2,
		H3,
		H4,
		BR,
		META,
		HTML,
		BODY,
		HEAD,
		SCRIPT,
		STYLE,
		IMG,
		DIV,
		TR,
		A,
		UL,
		FONT,
		FORM,
		SPAN,
		NOBR,
		LABEL,
		INPUT,
		LEGEND,
		TABLE,
		TD,
		SELECT,
		FIELDSET,
		LI,
		LINK,
		OPTION,
		TEXTAREA,
		IFRAME,
	}

   /**
   * Перечисление поддерживаемых атрибутов HTML тегов DOM модели страницы.  
   */
	public enum Property {
		ID,
		FOR,
		SRC,
		ALT,
		NAME,
		HREF,
		TYPE,
		ROWS,
		VALUE,
		WIDTH,
		STYLE,
		ACCEPT,
		CLASS,
		COLOR,
		METHOD,
		ACTION,
		ONERROR,
		CHECKED,
		BGCOLOR,
		COLSPAN,
		ENCTYPE,
		SELECTED,
		DISABLED,
		READONLY,
		PLACEHOLDER,
		AUTOCOMPLETE,
		TABINDEX,
		SIZE,
		MAXLENGTH,
		ALIGN,
		HEIGHT,
		INVISIBLE,
		REL,
		HTTPEQUIV,
		CONTENT,
		TARGET,
		ACTI,
		DATA_URL,
		DATA_FORM_ID,

		CHARSET,
		CHANGE,
		CLICK,
		BLUR,
		KEYUP,
		KEYDOWN,
		SUBMIT,
		FOCUS,
		KEYPRESS,
		MOUSEDOWN,
		DELETE
	}

   /**
   * Перечисление поддерживаемых атрибутов HTML тегов DOM модели страницы.
   * которые содержат JS  
   */
	private static List<Property> ACTION_PROPERTIES = new LinkedList<Property>(){{
	    add(Tag.Property.CHANGE);
	    add(Tag.Property.CLICK);
		add(Tag.Property.BLUR);
		add(Tag.Property.KEYUP);
		add(Tag.Property.KEYDOWN);
		add(Tag.Property.SUBMIT);
		add(Tag.Property.FOCUS);
		add(Tag.Property.KEYPRESS);
		add(Tag.Property.MOUSEDOWN);
		add(Tag.Property.DELETE);
	}};

	private static List<Type> EXPANDED_TAGS = new LinkedList<Type>(){{
	    add(Tag.Type.TEXTAREA);
	}};

   /**
   * Перечисление HTML тегов DOM модели страницы,
   * которые могут содержать тело
   */
	private static List<Type> CONTAINER_TAGS = new LinkedList<Type>(){{
		add(Tag.Type.H1);
		add(Tag.Type.H2);
		add(Tag.Type.H3);
		add(Tag.Type.H4);
		add(Tag.Type.BR);
		add(Tag.Type.IMG);
		add(Tag.Type.DIV);
		add(Tag.Type.TR);
		add(Tag.Type.A);
		add(Tag.Type.UL);
		add(Tag.Type.FONT);
		add(Tag.Type.FORM);
		add(Tag.Type.SPAN);
		add(Tag.Type.NOBR);
		add(Tag.Type.LABEL);
		add(Tag.Type.LEGEND);
		add(Tag.Type.TABLE);
		add(Tag.Type.TD);
		add(Tag.Type.SELECT);
		add(Tag.Type.FIELDSET);
		add(Tag.Type.LI);
		add(Tag.Type.OPTION); 
		add(Tag.Type.TEXTAREA);
		add(Tag.Type.SCRIPT);
	}};
	
	
	private Type type;
	private String body_before = "";
	private String body = "";
	private Tag parrent;
	private List<Tag> children  = new LinkedList<Tag>();
	private Map<Property, String> property;

  /**
   * Конструктор
   * 
   * @param type имя тега
   */
	public Tag(Type type) {
		this(type, null, null, null, new HashMap<Property, String>());
	}

  /**
   * Конструктор
   * 
   * @param type имя тега
   * @param parrent тег родитель
   */
	public Tag(Type type, Tag parrent) {
		this(type, null, null, parrent, new HashMap<Property, String>());
	}

	  /**
	   * Конструктор
	   * 
	   * @param type имя тега
	   * @param properties атрибуты тега
	   */
	public Tag(Type type, Map<Property, String> properties) {
		this(type, null, null, null, properties);
	}

	/**
	   * Конструктор
	   * 
	   * @param type имя тега
	   * @param parrent тег родитель
	   * @param properties атрибуты тега
	   */
	public Tag(Type type, Tag parrent, Map<Property, String> properties) {
		this(type, null, null, parrent, properties);
	}


	  /**
	   * Конструктор
	   * 
	   * @param type имя тега
	   * @param body тело тега
	   * @param parrent тег родитель
	   * @param properties атрибуты тега
	   */
	public Tag(Type type, String body, Tag parrent, Map<Property, String> properties) {
		this(type, null, body, parrent, properties);
	}

	  /**
	   * Конструктор
	   * 
	   * @param type имя тега
	   * @param body_before тело тега, которое будет выводится до дочерних тегов
	   * @param body тело тега
	   * @param parrent тег родитель
	   * @param properties атрибуты тега
	   */
	public Tag(Type type, String body_before, String body, Tag parrent, Map<Property, String> properties) {
		this.setType(type);
		this.setBody_before(body_before);
		this.setBody(body);
		this.setParrent(parrent);
		this.setProperties(properties);
	}
	

	  /**
	   * Метод добавляет дочерний тег
	   * 
	   * @param type имя тега
	   * @return добавленный тег
	   */
	public Tag add(Type type) {
		return add(type, null, null, new HashMap<Property, String>());
	}

	  /**
	   * Метод добавляет дочерний тег в начало
	   * 
	   * @param type имя тега
	   * @return добавленный тег
	   */
	public Tag unshift(Type type) {
		return unshift(type, null, null, new HashMap<Property, String>());
	}

	  /**
	   * Метод добавляет дочерний тег
	   * 
	   * @param type имя тега
	   * @param body тело тега
	   * @return добавленный тег
	   */
	public Tag add(Type type, String body) {
		return add(type, null, body, new HashMap<Property, String>());
	}

	  /**
	   * Метод добавляет дочерний тег  в начало
	   * 
	   * @param type имя тега
	   * @param body тело тега
	   * @return добавленный тег
	   */
	public Tag unshift(Type type, String body) {
		return unshift(type, null, body, new HashMap<Property, String>());
	}

	
	  /**
	   * Метод добавляет дочерний тег
	   * 
	   * @param type имя тега
	   * @param properties атрибуты тега
	   * @return добавленный тег
	   */
	public Tag add(Type type, Map<Property, String> properties) {
		return add(type, null, null, properties);
	}

	  /**
	   * Метод добавляет дочерний тег в начало
	   * 
	   * @param type имя тега
	   * @param properties атрибуты тега
	   * @return добавленный тег
	   */
	public Tag unshift(Type type, Map<Property, String> properties) {
		return unshift(type, null, null, properties);
	}

	  /**
	   * Метод добавляет дочерний тег
	   * 
	   * @param type имя тега
	   * @param body тело тега
	   * @param properties атрибуты тега
	   * @return добавленный тег
	   */
	public Tag add(Type type, String body, Map<Property, String> properties) {
		return add(type, null, body, properties);
	}

	/**
	   * Метод добавляет дочерний тег в начало
	   * 
	   * @param type имя тега
	   * @param body тело тега
	   * @param properties атрибуты тега
	   * @return добавленный тег
	   */
	public Tag unshift(Type type, String body, Map<Property, String> properties) {
		return unshift(type, null, body, properties);
	}
		

	  /**
	   * Метод добавляет дочерний тег в начало
	   * 
	   * @param type имя тега
	   * @param body_before тело тега, которое будет выводится до дочерних тегов
	   * @param body тело тега
	   * @param properties атрибуты тега
	   * @return добавленный тег
	   */
	public Tag unshift(Type type, String body_before, String body, Map<Property, String> properties) {
		Tag newTag = new Tag(type, body_before, body, this, properties);

		this.children.add(0,newTag);

		return newTag;
	}
	  /**
	   * Метод добавляет дочерний тег
	   * 
	   * @param type имя тега
	   * @param body_before тело тега, которое будет выводится до дочерних тегов
	   * @param body тело тега
	   * @param properties атрибуты тега
	   * @return добавленный тег
	   */
	public Tag add(Type type, String body_before, String body, Map<Property, String> properties) {
		Tag newTag = new Tag(type, body_before, body, this, properties);

		this.children.add(newTag);

		return newTag;
	}

	  /**
	   * Метод получения имени тега
	   * 
	   * @return имя тега
	   */
	public Type getType() {
		return type;
	}

	  /**
	   * Метод установки имени тега
	   * 
	   * @param имя тега
	   */
	public void setType(Type type) {
		this.type = type;
	}

	  /**
	   * Метод получения тела тега
	   * 
	   * @return тела тега, которое будет выводиться до дочерних тегов
	   */
	public String getBody_before() {
		return body_before;
	}

	  /**
	   * Метод установки тела тега
	   * 
	   * @param тело тега, которое будет выводиться до дочерних тегов
	   */
	public void setBody_before(String body_before) {
		if (body_before != null) {
			this.body_before = body_before;
		}
	}

	  /**
	   * Метод получения тела тега
	   * 
	   * @return тело тега
	   */
	public String getBody() {
		return body;
	}

	  /**
	   * Метод установки тела тега
	   * 
	   * @param тела тега
	   */
	public void setBody(String body) {
		if (body != null) {
			this.body = body;
		}
	}

	  /**
	   * Метод получения родительского тега
	   * 
	   * @return родительский тег
	   */
	public Tag getParrent() {
		return parrent;
	}

	  /**
	   * Метод установки родительского тега
	   * 
	   * @param родительского тега
	   */
	public void setParrent(Tag parrent) {
		this.parrent = parrent;
	}

	  /**
	   * Метод получения атрибутов тега
	   * 
	   * @return атрибуты тега
	   */
	public Map<Property, String> getProperties() {
		return property;
	}

	  /**
	   * Метод установки атрибутов тега
	   * 
	   * @param списоа атрибутов тега
	   */
	public void setProperties(Map<Property, String> property) {
		this.property = property;
	}

	  /**
	   * Метод получения атрибута тега
	   * 
	   * @param key имя атрибута тега
	   * @return атрибут тега
	   */
	public String getProperty(Property key) {

		String property = this.property.get(key);

		return property != null ? property : "";
	}

	  /**
	   * Метод установки атрибута тега
	   * 
	   * @param key имя атрибута тега
	   * @param value значение атрибута тега
	   */
	public void setProperty(Property key,  String value) {
		this.property.put(key, value);
	}

	  /**
	   * Метод удаления атрибута тега
	   * 
	   * @param key имя атрибута тега
	   */
	public void removeProperty(Property key) {
		this.property.remove(key);
	}

	  /**
	   * Метод получения элементов, стоящих на уровень ниже
	   * 
	   * @param список элементов, стоящих на уровень ниже
	   */
	public List<Tag> getChildren() {
		return children;
	}

	  /**
	   * Метод получения HTML представления тега
	   * 
	   * @return HTML представление тега
	   */
	public String getHTML() {
		String html = "";
		if (!Type.SCRIPT.equals(type) || !"".equals(getProperty(Tag.Property.SRC))) {
			html = "\n<" + type.toString().toLowerCase();
			
			for (Property propertyKey: property.keySet()) {
				if (!ACTION_PROPERTIES.contains(propertyKey)) {
					
					html = html.concat(" " + (propertyKey == Property.HTTPEQUIV ? "http-equiv" :  propertyKey.toString().toLowerCase().replaceAll("_", "-")) + "=\"" + property.get(propertyKey) + "\"");
				}
			}
			
			if (EXPANDED_TAGS.contains(type)) {
				html = html.concat(">\n");
				html = html.concat(body);
				
				html = html.concat("</" + type.toString().toLowerCase() + ">");
				if (children.size() > 0) {
					for(Tag child: children) {
						html = html.concat(child.getHTML());
					}
				}
			} else if (
					children.size() > 0
					|| !"".equals(body)
					|| !"".equals(body_before)
					|| CONTAINER_TAGS.contains(type)
			) {
				html = html.concat(">\n");
				html = html.concat(body_before);

				if (children.size() > 0) {
					for(Tag child: children) {
						html = html.concat(child.getHTML());
					}
				}
				html = html.concat(body);
				html = html.concat("\n</" + type.toString().toLowerCase() + ">");
			} else if (Type.INPUT.equals(type)) {
				html = html.concat(">");
			} else {
				html = html.concat("/>");
			}
		}
		
		return html;
	}

	  /**
	   * Метод получения связанного JS представления тега
	   * 
	   * @return связанное с JS HTML представление тега
	   */
	public String getJS() {
		String js = "";

		if (children.size() > 0) {
			for(Tag child: children) {
				js = js.concat(child.getJS());
			}
		}
		if (Type.SCRIPT.equals(type) && !"".equals(body)) {
			js = js.concat(body);
		}

		for (Property propertyKey: property.keySet()) {
			if (ACTION_PROPERTIES.contains(propertyKey)) {
				String value = property.get(propertyKey)
						.replaceAll("\\(\\s*this\\s*\\)", "(event.delegateTarget, event)")
						.replaceAll("this", "event.delegateTarget");
				
				js = js.concat("$(\"#" + property.get(Tag.Property.ID) + "\").on(\"" + (propertyKey == Property.HTTPEQUIV ? "http-equiv" :  propertyKey.toString().toLowerCase()) + "\",function(event){\n"
					+ "	" + value + "\n"
					+"});");
			}
		}

		return js;
	}

	public Tag locateUp(Type type) {
		if (this.getType().equals(type)) {
			return this;
		} else {
			Tag parrent = getParrent();
			if (parrent != null) {
				return getParrent().locateUp(type);
			} else {
				return null;
			}
		}
	}

	public Tag locateDown(Type type) {
		if (this.getType().equals(type)) {
			return this;
		} else {
			for (Tag child : getChildren()) {
				if (child.getType().equals(type)) {
					return child;
				} else {
					return child.locateDown(type);
				}
			}
		}
		return null;
	}

}

