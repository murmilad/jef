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
import com.technology.jef.widgets.Widget;

import static com.technology.jef.server.serialize.SerializeConstant.*;
import static com.technology.jef.server.WebServiceConstant.*;

/**
* Класс сборщика DOM модели уровня формы (страницы)
*/
public class TabGenerator extends TagGenerator {

	Boolean hasOnClick = false;
    
	HashMap<String, TagGenerator> connectedElements = new HashMap<String, TagGenerator>();
	List<TagGenerator> multiplieGroups = new LinkedList<TagGenerator>();
	HashMap<String, String> formInterfaceApiMap = new HashMap<String, String>();


	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {

		Tag tab = dom.add(Tag.Type.DIV, (String) getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.DATA_FORM_ID, (String) getAttribute(TagGenerator.Attribute.FORM_ID));
		     put(Tag.Property.ID, (String) getAttribute(TagGenerator.Attribute.ID) + "_tab");
		     put(Tag.Property.NAME, (String) getAttribute(TagGenerator.Attribute.ID) + "_tab");
		     put(Tag.Property.CLASS, "tab second_color");
		}});
		tab.add(Type.SCRIPT, 	("					$( \"#${name}_tab\" ).hover(  \n" + 
	"						function() {  \n" + 
	"							$( this ).addClass( \"first_color\");  \n" + 
	"							$( this ).removeClass( \"second_color\" );  \n" + 
	"						}, function() {  \n" + 
	"							if (!$( this ).hasClass( \"current\")) { \n" + 
	"								$( this ).addClass( \"second_color\");  \n" + 
	"								$( this ).removeClass( \"first_color\" );  \n" + 
	"							} \n" + 
	"						}  \n" + 
	"					);  \n")
				.replace("${name}", (String) getAttribute(TagGenerator.Attribute.ID))
		);

		addHandler(TagGenerator.Name.SCRIPT, new Handler() {
			@Override
			public void handle(TagGenerator currentGenerator) throws SAXException {
				if ("click".equals(((String)currentGenerator.getAttribute(TagGenerator.Attribute.TYPE)).toLowerCase())) {
					hasOnClick = true;
				}
			}
		});

		return tab;
	}

	@Override
	public void onEndElement() throws SAXException {
		if (!hasOnClick) {
			dom.add(Tag.Type.SCRIPT, 
						("                         \n" + 
	"					$(\"#${name}_tab\").on(\"click\", function(event){                        \n" + 
	"						loadForm('${form}');  \n" + 
	"					});                                 \n" + 
	"					\n")
			.replace("${form}", (String) getAttribute(TagGenerator.Attribute.FORM_ID))
			.replace("${name}", (String) getAttribute(TagGenerator.Attribute.ID))
			);
		}
		
	}
}
