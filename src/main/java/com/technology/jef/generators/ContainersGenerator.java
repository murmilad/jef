package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator.Handler;
import com.technology.jef.widgets.Widget;

import static com.technology.jef.server.serialize.SerializeConstant.*;
import static com.technology.jef.server.WebServiceConstant.*;

/**
* Класс сборщика DOM модели уровня контейнера табов
*/
public class ContainersGenerator extends TagGenerator {

	String firstTabFormId = null;
    
	@Override
	public Tag generate(String qName) throws SAXException {

		
		Tag container = dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		}});
		

		return container;
	}

	@Override
	public void onEndElement() throws SAXException {
		// TODO Auto-generated method stub
		
	}


}
