package com.technology.jef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
 
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.technology.jef.generators.FormGenerator;
import com.technology.jef.generators.FormsGenerator;
import com.technology.jef.generators.GroupGenerator;
import com.technology.jef.generators.InterfaceGenerator;
import com.technology.jef.generators.ItemGenerator;
import com.technology.jef.generators.ScriptGenerator;
import com.technology.jef.generators.TabGenerator;
import com.technology.jef.generators.TabsGenerator;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.generators.TagGenerator.Name;

/**
 * Класс сборки DOM модели страницы.
 */
public class DOMGenerator {
	
	private String html = null;
	private String js = null;
	
	private String sourcePath;

	private String interfaceHandler;
	// Текущий адрес в собраном DOM
	private Stack<Tag> domPath = new Stack<Tag>();
	// Текущий адрес сборщиков DOM
	private Stack<TagGenerator> generatorPath = new Stack<TagGenerator>();
	
  /**
   * Конструктор
   * 
   * @param interfaceHandler уникальное имя интерфейса
   */
	public DOMGenerator(String interfaceHandler, String sourcePath) {
		this.interfaceHandler = interfaceHandler;
		this.sourcePath = sourcePath;
		this.domPath.add(new Tag(Tag.Type.HTML));
	}

  /**
   * Метод получает список XML файлов описания интерфейса.
   * 
   * @param path путь к директории, содержащей XML файлы описания интерфейса
   * @return список файлов описания интерфейса.
   */
	private List<String> getResourceFiles( String path )  {

		List<String> filenames = new LinkedList<String>();
		// Get current classloader
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream in = cl.getResourceAsStream( path );
		try {
			Enumeration<URL> en = getClass().getClassLoader().getResources("com/technology/jef");
			en.hasMoreElements();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader( new InputStreamReader( in ) );

		String resource;

	    try {
			while( (resource = br.readLine()) != null ) {
				filenames.add( resource );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      

	    return filenames;
	}	



  /**
   * Метод формирования DOM модели на основе XML представления
   * 
   * @return 
   */

	public void createDOM () {

//		for (String resource : getResourceFiles(RESOURCE_XML_FOLDER)) {
 
	        try {
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            SAXParser saxParser = factory.newSAXParser();
	 
	            // Здесь мы определили анонимный класс, расширяющий класс DefaultHandler до нужной нам функциональности
	            DefaultHandler handler = new DefaultHandler() {
	                String currentInterfaceQName = null;
	                Boolean isInterfaceCurrent = false;
	                
	 
	                // Метод вызывается когда SAXParser "натыкается" на начало тэга
	                @Override
	                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                		System.out.println(localName +"," + qName + "," + attributes.getValue("id"));

	                	// Нашли тег интерфейса
	                	if (qName.equals("interface")) {
	                		currentInterfaceQName = qName;
	                		// SAX пришел к иннтерфейсу, DOM которого мы хотим построить
	                		if (interfaceHandler.equals(attributes.getValue("id"))) {
	                			isInterfaceCurrent = true;
	                		}
	                	}
	                	if (isInterfaceCurrent) {
	                		// Формируем генератор в зависимости от атрибутов полученных SAX и добавляем его в адресный стек генераторов
	                		generatorPath.push(addTag(getCurrentGenerator() != null 
	                				? getCurrentGenerator().getDom(Name.valueOf(qName.toUpperCase()), attributes) 
	                				: getCurrentDomElement(),
	                		qName, attributes));
	                	}
	                }
	 
	                // SAX Подошол к концу текущего тега
	                @Override
	                public void endElement(String uri, String localName, String qName) throws SAXException {
	                	if (isInterfaceCurrent) {
	                		// Поднимаемся на уровень выше по адресному стеку DOM модели
	                		// Если мы пришли к завершению обработки интерфейса то не чистим адрес, 
	                		// чтобы при печати было откуда достать корневой Тег 
	                		Tag currentTag = null; 
	                		if (!qName.equals("interface")) {
	                			currentTag = domPath.pop();
	                		}
	                		// Поднимаемся на уровень выше по адресному стеку генераторов DOM
	                		TagGenerator generator = generatorPath.pop();
	                		if (generator != null) {
	                			// Вызываем метод завершения формирования DOM модели у генератора
	                			generator.onEndElement();
	                		}
	                	}
	                	// Если SAX подошел к концу тега текущего интерфейса
	                	if (qName.equals(currentInterfaceQName)) {
	                		isInterfaceCurrent = false;
	                	}
	                }
	                
	                // SAX собирает тело текущего тега
	                @Override
	                public void characters(char[] ch, int start, int length) throws SAXException {
	                	// Записываем в модель DOM тело текущего тега (чаще всего нужно для JS)
	                	if (getCurrentDomElement() != null) {
	                		getCurrentDomElement().setBody(getCurrentDomElement().getBody() + new String(ch, start, length));
	                	}
	                }
	                
	            };
	 
	            // Стартуем разбор методом parse, которому передаем наследника от DefaultHandler, который будет вызываться в нужные моменты
	            saxParser.parse(sourcePath, handler);
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
//		}
		
	    setHtml("<!DOCTYPE HTML>" 
	    		+ domPath.firstElement().getHTML());
		setJs(domPath.firstElement().getJS());
	}

  /**
   * Метод формирования генератора DOM модели на основе атрибутов тега
   * 
   * @param dom текущее положение в DOM модели
   * @param qName имя тега в XML представлении интерфейса
   * @param attributes атрибуты тега в XML представлении интерфейса
   * @return генератор DOM модели для текущего элемента в XML представлении интерфейса
   */

	private TagGenerator addTag(Tag dom, String qName, Attributes attributes) {
		TagGenerator generator = null;
		TagGenerator.Name name = TagGenerator.Name.valueOf(qName.toUpperCase());

		// Выбираем генератор DOM модели в зависимости от имени тега
		switch (name) {
		case FORMS:
			generator = new FormsGenerator();
			// Формируем DOM модель на уровне списка форм
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
			domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		case FORM:
			generator = new FormGenerator();
			// Формируем DOM модель на уровне формы
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		case GROUP:
			generator = new GroupGenerator();
			// Формируем DOM модель на уровне группы
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		case FORM_ITEM:
			generator = new ItemGenerator();
			// Формируем DOM модель на уровне элемента
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		case INTERFACE:
			generator = new InterfaceGenerator();
			// Формируем DOM модель на уровне элемента
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		case SCRIPT:
			generator = new ScriptGenerator();
			// Формируем DOM модель на уровне скрипта
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		case TAB:
			generator = new TabGenerator();
			// Формируем DOM модель на уровне таба
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		case TABS:
			generator = new TabsGenerator();
			// Формируем DOM модель на уровне контейнера табов
    		// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(generator.generate(dom, qName, attributes, getCurrentGenerator()));
			break;
		default:
			// Формируем DOM модель по умолчанию для нереализованных уровней
			// Добавляем ссылку на текущую позицию в DOM в адресный стек
    		domPath.push(dom.add(Tag.Type.DIV));
			generator = null;
		
		}

		// Вызываем объявленные для разных уровней генераторов события 
		for (TagGenerator currentGenerator: generatorPath){
			if (currentGenerator!= null) {
				for (TagGenerator.Handler callback : currentGenerator.gethHandlerList(name)) {
					callback.handle(generator);
				}
			}
		}
		
		return generator;
	}

	public Tag getCurrentDomElement() {

		return domPath.size() > 0 ? domPath.lastElement() : null;
	}

	public TagGenerator getCurrentGenerator() {

		return generatorPath.size() > 0 ? generatorPath.lastElement() : null;
	}

	
	public String getHtml() {
		return html;
	}
	
	public void setHtml(String html) {
		this.html = html;
	}
	
	public String getJs() {
		return js;
	}
	
	public void setJs(String js) {
		this.js = js;
	}
}
