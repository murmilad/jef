package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.Tag.Type;
import com.technology.jef.generators.TagGenerator.Handler;
import com.technology.jef.generators.TagGenerator.Name;
import com.technology.jef.widgets.Widget;

import static com.technology.jef.server.serialize.SerializeConstant.*;
import static com.technology.jef.server.WebServiceConstant.*;

/**
* Класс сборщика DOM модели уровня формы (страницы)
*/
public class ContainerGenerator extends TagGenerator {



	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {

		Tag container = dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.DATA_FORM_ID, (String) getAttribute(TagGenerator.Attribute.FORM_ID));
		     put(Tag.Property.ID, (String) getAttribute(TagGenerator.Attribute.ID) + "_container");
		     put(Tag.Property.NAME, (String) getAttribute(TagGenerator.Attribute.ID) + "_container");
			 put(Tag.Property.STYLE, "display: inline-block;");

		}});
		dom.add(Type.SCRIPT, 				("   \n" + 
	"	$( document ).ready(function() {  \n" + 
	"		execOrBindAfterAll(function(){   \n" + 
	"			loadPage('${form_id}.html?no_cache=' + Math.floor(Math.random() * 10000), getWindowParams(), 'get', '#${id}_container');   \n" + 
	"		});   \n" + 
	"	});  \n")
				.replace("${form_id}", (String) getAttribute(TagGenerator.Attribute.FORM_ID))
				.replace("${id}", (String) getAttribute(TagGenerator.Attribute.ID))
		);


		return container;
	}

	@Override
	public void onEndElement() throws SAXException {
	}

	@Override
	public Name getName() {
		return Name.CONTAINER;
	}

}
