package com.technology.jef.generators;

import java.util.HashMap;
import java.util.Random;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;

/**
* Класс сборщика DOM модели уровня интерфейса
*/
public class InterfaceGenerator extends TagGenerator {

	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@SuppressWarnings("serial")
	@Override
	public Tag generate(String qName) throws SAXException {
		
		Tag head = dom.add(Tag.Type.HEAD);
		head.add(Tag.Type.META, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.HTTPEQUIV, "content-type");
		     put(Tag.Property.CONTENT, "text/html; charset=UTF-8");
		}});
		head.add(Tag.Type.META, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.HTTPEQUIV, "pragma");
		     put(Tag.Property.CONTENT, "no-cache");
		}});
		head.add(Tag.Type.META, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.HTTPEQUIV, "X-UA-Compatible");
		     put(Tag.Property.CONTENT, "IE=edge");
		}});
		head.add(Tag.Type.LINK, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.REL, "stylesheet");
		     put(Tag.Property.TYPE, "text/css");
		     put(Tag.Property.HREF, "css/jquery.formstyler.css?no_cache=" + new Random().nextInt(10000));
		}});

		if (!"".equals((String) getAttribute(TagGenerator.Attribute.NAME))) {
			String styleCSS =  (String) getAttribute(TagGenerator.Attribute.STYLE);
			head.add(Tag.Type.LINK, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.REL, "stylesheet");
			     put(Tag.Property.TYPE, "text/css");
			     put(Tag.Property.HREF, "css/" + (!"".equals(styleCSS) ? styleCSS : "default_scheme.css") + "?no_cache=" + new Random().nextInt(10000));
			}});
		}

		head.add(Tag.Type.LINK, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.REL, "stylesheet");
		     put(Tag.Property.TYPE, "text/css");
		     put(Tag.Property.HREF, "css/jef.css?no_cache=" + new Random().nextInt(10000));
		}});

		head.add(Tag.Type.SCRIPT, 	("	$.ajaxSetup({scriptCharset: \"utf-8\" , contentType: \"application/json; charset=utf-8\"});   \n" + 
			"	$.fn.isBound = function(type) {   \n" + 
			"		    var data = $._data($(this).get(0), \"events\")[type];   \n" + 
			"		    if (data === undefined || data.length === 0) {   \n" + 
			"		        return false;   \n" + 
			"		    }   \n" + 
			"		    return true;   \n" + 
			"	}; \n" + 
			"	$.fn.bindFirst = function(name, fn) { \n" + 
			"	    // bind as you normally would \n" + 
			"	    // don't want to miss out on any jQuery magic \n" + 
			"	    this.on(name, fn); \n" + 
		
			"	    // Thanks to a comment by @Martin, adding support for \n" + 
			"	    // namespaced events too. \n" + 
			"	    this.each(function() { \n" + 
			"	        var handlers = $._data(this, 'events')[name.split('.')[0]]; \n" + 
			"	        // take out the handler we just inserted from the end \n" + 
			"	        var handler = handlers.pop(); \n" + 
			"	        // move it at the beginning \n" + 
			"	        handlers.splice(0, 0, handler); \n" + 
			"	    }); \n" + 
			"	}; \n")
			, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/jquery-3.2.1.min.js");
		}});
		head.add(Tag.Type.SCRIPT, 		("  \n" + 
	"	if (!window.isTouchpadLoaded) { \n" + 
	"		window.isTouchpadLoaded = true; \n" + 
	"		$(document).on('mousedown', '.autocomplete-suggestion', function (e) { //touchpad support  \n" + 
	"			$(e.target).click();  \n" + 
	"		}); \n" + 
	"	}  \n")
			,new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/jquery.autocomplete.js?no_cache=" + new Random().nextInt(10000));
		}});
		
		head.add(Tag.Type.SCRIPT, (
				"		(function($) {                \n" + 
				"		    $(function() {                \n" + 
				"		        $('input').styler();                \n" + 
				"		    })                \n" + 
				"		})(jQuery)        \n" + 
				"	window.ajaxRequestStack = [];                 \n" + 
				"	var ajaxRequestId = 0;                 \n" + 
				"        window.ajaxPool = [];       \n" + 
				"        window.ajaxXNR = [];       \n" + 
				"        window.ajaxPool.hasSame = function(parameters) {       \n" + 
				"            	if (ajaxPool.indexOf(parameters) != -1) {      \n" + 
				" 					return true;      \n" + 
				"				}       \n" + 
				"				return false;       \n" + 
				"        };       \n" + 
				"        window.ajaxPool.delete	= function(parameters) {       \n" + 
				"            	var i = ajaxPool.indexOf(parameters);       \n" + 
				"				if (i > -1) ajaxPool.splice(i, 1);      \n" + 
				"        };      \n"
		
		),
			new HashMap<Tag.Property, String>(){{
			    put(Tag.Property.TYPE, "text/javascript");
			    put(Tag.Property.SRC, "js/jquery.formstyler.js?no_cache=" + new Random().nextInt(10000));
		}});

		head.add(Tag.Type.SCRIPT,	
				new HashMap<Tag.Property, String>(){{
				    put(Tag.Property.TYPE, "text/javascript");
				    put(Tag.Property.SRC, "js/jef.js?no_cache=" + new Random().nextInt(10000));
			}});

		Tag body = dom.add(Tag.Type.BODY, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.CLASS, "body first_font background_color");
		}});


		Tag div = body.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			put(Tag.Property.ID, "wrapper");
		    put(Tag.Property.CLASS, "wrapper" + (!"".equals((String) getAttribute(TagGenerator.Attribute.NAME))? " work_area_width" : ""));
		}}).add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "main_content");
		     put(Tag.Property.CLASS, "block");
		}});
		if (!"".equals(getAttribute(TagGenerator.Attribute.HEADER))) {
			div.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.ID, "header");
				put(Tag.Property.CLASS, "work_area_width");
			}}).add(Tag.Type.SCRIPT, 		
					(" \n" + 
					"	$( document ).ready(function() {  \n" + 
					"		$('#header').load(\"${header_uri}?no_cache=\" + Math.floor(Math.random() * 10000));  \n" + 
					"	}); \n")
					.replace("${header_uri}",(String) getAttribute(TagGenerator.Attribute.HEADER))
			);
		}

		if (!"".equals((String) getAttribute(TagGenerator.Attribute.NAME))) {
			div.add(Tag.Type.H1, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.CLASS, "block_h1 header_height header_font_size header_line_height");
			}}).add(Tag.Type.DIV, (String) getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.CLASS, "block_h1_div header_height header_color");
			}});
		}
		Tag content = div.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS, !"".equals((String) getAttribute(TagGenerator.Attribute.NAME)) ? "content first_frames_border" : "");
		     put(Tag.Property.STYLE, "padding: 5px;");
		}});
		// Добавляем сообщение об ошибке при выполнении запроса
		content.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "background_overlay_error");
			 put(Tag.Property.NAME, "background_overlay_error");
			 put(Tag.Property.CLASS, "background_overlay_error background_color");
		}});

		Tag errorBox = content.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_box_error");
			 put(Tag.Property.NAME, "message_box_error");
			 put(Tag.Property.CLASS, "message_box_error error_border messages_color");
		}});
		
		errorBox.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_header_error");
			 put(Tag.Property.NAME, "message_header_error");
			 put(Tag.Property.CLASS, "message_header_error messages_color");
		}});

		errorBox.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_body_error");
			 put(Tag.Property.NAME, "message_body_error");
			 put(Tag.Property.CLASS, "message_body_error messages_color");
		}});
		
		errorBox.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_buttons_error");
			 put(Tag.Property.NAME, "message_buttons_error");
			 put(Tag.Property.CLASS, "message_buttons_error messages_color");
		}}).add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_error_button");
			 put(Tag.Property.TYPE, "button");
			 put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("return"));
			 put(Tag.Property.CLASS, "interface_button first_color second_text_color");
			 put(Tag.Property.CLICK, "location.reload();");
		}});
		
		return content;
	}

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	 * @throws SAXException 
	   * 
	   */
	@Override
	public void onEndElement() throws SAXException {
		// Добавляем загрузку сгенерированного JS для текущего интерфейса 
		dom.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/" + getAttribute(TagGenerator.Attribute.ID) + ".js?no_cache=" + new Random().nextInt(10000));
		}});
	}
	
}
