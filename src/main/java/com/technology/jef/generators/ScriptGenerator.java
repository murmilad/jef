package com.technology.jef.generators;

import com.technology.jef.Tag;

/**
* Класс сборщика DOM модели уровня скрипта
*/
public class ScriptGenerator extends TagGenerator {

	private Boolean hasEvent = false;
	
	  /**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	@Override
	public Tag generate(String qName) {

		Tag script;
		if (hasAttribute(TagGenerator.Attribute.TYPE)) {
			script = dom.add(Tag.Type.SCRIPT, "$( \"#"+ dom.getProperty(Tag.Property.NAME) + "\" ).bind( \"" + 
					((String) getAttribute(TagGenerator.Attribute.TYPE)).replaceAll(",", " ")
					+ "\", function(event, data) { \n");
			hasEvent = true;
		} else {
			script = dom.add(Tag.Type.SCRIPT, "");
		}

		return script;
	}

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	   * 
	   */
	@Override
	public void onEndElement() {
		if (hasEvent) {
			dom.setBody(dom.getBody() + "});\n");
		}
	}

	

}
