package com.technology.jef.generators;

import com.technology.jef.Tag;

/**
* Класс сборщика DOM модели уровня списка форм
*/
public class FormsGenerator extends TagGenerator {

	// Счетчик форм (страниц), содержащихся на текущем интерфейсе
	Integer formCount = 0;


	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	@Override
	public Tag generate(String qName) {

		
		addHandler(TagGenerator.Name.FORM, new Handler() {
			@Override
			public void handle(TagGenerator currentGenerator) {
				//Прибавляем количество форм (страниц) с каждым тегом FORM
				formCount++;
			}
		});

		return dom;
	}

	@Override
	public void onEndElement() {
		
	}
	
	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) {
		switch (attributeName) {
		case API:
			return !"".equals(super.getAttribute(TagGenerator.Attribute.API)) 
					? super.getAttribute(TagGenerator.Attribute.API)
					: this.getParrent().getAttribute(TagGenerator.Attribute.API);
		case FORM_COUNT:
			return formCount;
		case SERVICE:
			return (String) getParrent().getAttribute(TagGenerator.Attribute.SERVICE);
		default:
			return super.getAttribute(attributeName);
		}
		
	}

}
