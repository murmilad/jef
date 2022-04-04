package com.technology.jef.generators;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator.Name;

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
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {

		Tag script;
		if (hasAttribute(TagGenerator.Attribute.TYPE)) {
			script = dom.add(Tag.Type.SCRIPT, "$( \"#"+ dom.getProperty(Tag.Property.NAME) + "\" ).bind( \"" + 
					((String) getAttribute(TagGenerator.Attribute.TYPE)).replaceAll(",", " ")
					+ "\", function(event, data) { \n", new HashMap<Tag.Property, String>(){{
						if (!"".equals(getAttribute(TagGenerator.Attribute.SRC))) {
							put(Tag.Property.SRC, (String) getAttribute(TagGenerator.Attribute.SRC));
							put(Tag.Property.TYPE, "text/javascript");
						}

					}});
			hasEvent = true;
		} else {
			script = dom.add(Tag.Type.SCRIPT, "", new HashMap<Tag.Property, String>(){{
				if (!"".equals(getAttribute(TagGenerator.Attribute.SRC))) {
					put(Tag.Property.SRC, (String) getAttribute(TagGenerator.Attribute.SRC));
					put(Tag.Property.TYPE, "text/javascript");
				}

			}});
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
			dom.setBody(dom.getBody() + " });\n"); // event.stopPropagation(); отмена поиска аналогичных эвентов в родительских элементах
		}
	}

	@Override
	public Name getName() {
		return Name.SCRIPT;
	}


}
