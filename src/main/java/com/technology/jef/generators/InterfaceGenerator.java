package com.technology.jef.generators;

import java.util.HashMap;

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
	   */
	@SuppressWarnings("serial")
	@Override
	public Tag generate(String qName) {
		
		Tag head = dom.add(Tag.Type.HEAD);
		head.add(Tag.Type.META, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.HTTPEQUIV, "pragma");
		     put(Tag.Property.CONTENT, "no-cache");
		}});
		head.add(Tag.Type.META, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.HTTPEQUIV, "X-UA-Compatible");
		     put(Tag.Property.CONTENT, "IE=edge");
		}});
		head.add(Tag.Type.META, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.HTTPEQUIV, "Content-Type");
		     put(Tag.Property.CONTENT, "text/html; charset=windows-1251");
		}});
		head.add(Tag.Type.LINK, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.REL, "stylesheet");
		     put(Tag.Property.TYPE, "text/css");
		     put(Tag.Property.HREF, "css/jquery.formstyler.css?v=2.0.0");
		}});
		head.add(Tag.Type.LINK, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.REL, "stylesheet");
		     put(Tag.Property.TYPE, "text/css");
		     put(Tag.Property.HREF, "css/styles.css?v=2.12.1");
		}});
		head.add(Tag.Type.SCRIPT, ("	$.ajaxSetup({scriptCharset: \"windows-1251\" , contentType: \"application/json; charset=windows-1251\"});  \n" + 
					"	$.fn.isBound = function(type) {  \n" + 
					"		    var data = $._data($(this).get(0), \"events\")[type];  \n" + 
					"		    if (data === undefined || data.length === 0) {  \n" + 
					"		        return false;  \n" + 
					"		    }  \n" + 
					"		    return true;  \n" + 
					"	};")
			, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/jquery-3.2.1.min.js");
		}});
		head.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/jquery.autocomplete.js");
		}});
		
		head.add(Tag.Type.SCRIPT, 
										(" $.support.cors = true;     \n" +  // Кросс-домены
	"		(function($) {       \n" + 
	"		    $(function() {       \n" + 
	"		        $('input').styler();       \n" + 
	"		    })       \n" + 
	"		})(jQuery)        \n" + 
	"	function showError (header, message){     \n" + 
	"		$(\"#background_overlay_error\").show();            \n" + 
	"   		$(\"#message_box_error\").show();            \n" + 
	"		$(\"#message_header_error\").html(header);     \n" + 
	"		$(\"#message_body_error\").html(message);     \n" + 
	"	}      \n" + 
	"	function deleteAllCookies() {    \n" + 
	"	    var cookies = document.cookie.split(\";\");    \n" + 
	"	    for (var i = 0; i < cookies.length; i++) {    \n" + 
	"	        var cookie = cookies[i];    \n" + 
	"	        var eqPos = cookie.indexOf(\"=\");    \n" + 
	"	        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;    \n" + 
	"	        document.cookie = name + \"=;expires=Thu, 01 Jan 1970 00:00:00 GMT\";    \n" + 
	"	    }    \n" + 
	"	}    \n" + 
	"	function operateResult (result, callback) {   \n" + 
	"		if (result.status_code == 2) {   \n" + 
	"			if (result.error.error_code == 2) {   \n" + 
	"				window.location.replace(\"/?_auth=logout\");   \n" + 
	"				deleteAllCookies();   \n" + 
	"			} else {   \n" + 
	"				showError(result.error.error_description, result.error.error_message);   \n" + 
	"			}   \n" + 
	"		} else if (result.status_code == 1) {   \n" + 
	"			if (callback) {   \n" + 
	"				callback(result, this);   \n" + 
	"			}   \n" + 
	"		}   \n" + 
	"	}   \n" + 
	"	function ajax(parameters, callback) {  \n" + 
	"		$.ajax(  \n" + 
	"			parameters  \n" + 
	"		).done(function(data){  \n" + 
	"			operateResult(data, callback);  \n" + 
	"		}).fail(function(jqXHR, textStatus, errorThrown){  \n" + 
	"			showError(\"Error: \" + errorThrown, jqXHR.responseText);  \n" + 
	"		});  \n" + 
	"	}  \n" + 

	"	function getJSON(url, parameters, callback){ \n" + 
	"		$.getJSON( \n" + 
	"			url, \n" + 
	"			parameters \n" + 
	"		).done(function(data){  \n" + 
	"			operateResult(data, callback);  \n" + 
	"		}).fail(function(jqXHR, textStatus, errorThrown){  \n" + 
	"			showError(\"Error: \" + errorThrown, jqXHR.responseText);  \n" + 
	"		});  \n" + 
	"	} \n"),		
			new HashMap<Tag.Property, String>(){{
		    put(Tag.Property.TYPE, "text/javascript");
		    put(Tag.Property.SRC, "js/jquery.formstyler.js?v=2.0.0");
		}});
		
		Tag body = dom.add(Tag.Type.BODY);


		Tag div = body.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			put(Tag.Property.ID, "wrapper");
		}}).add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "main_content");
		     put(Tag.Property.CLASS, "block");
		}});
		if (!"".equals(getAttribute(TagGenerator.Attribute.HEADER))) {
			div.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.ID, "header");
			}}).add(Tag.Type.SCRIPT, 		
					(" \n" + 
					"	$( document ).ready(function() {  \n" + 
					"		$('#header').load(\"${header_uri}\");  \n" + 
					"	}); \n")
					.replace("${header_uri}",(String) getAttribute(TagGenerator.Attribute.HEADER))
			);
		}

		div.add(Tag.Type.H1).add(Tag.Type.DIV, (String) getAttribute(TagGenerator.Attribute.NAME));

		Tag content = div.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS, "content");
		     put(Tag.Property.STYLE, "padding: 5px;");
		}});
		// Добавляем сообщение об ошибке при выполнении запроса
		content.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "background_overlay_error");
			 put(Tag.Property.NAME, "background_overlay_error");
			 put(Tag.Property.CLASS, "background_overlay_error");
		}});

		Tag errorBox = content.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_box_error");
			 put(Tag.Property.NAME, "message_box_error");
			 put(Tag.Property.CLASS, "message_box_error");
		}});
		
		errorBox.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_header_error");
			 put(Tag.Property.NAME, "message_header_error");
			 put(Tag.Property.CLASS, "message_header_error");
		}});

		errorBox.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_body_error");
			 put(Tag.Property.NAME, "message_body_error");
			 put(Tag.Property.CLASS, "message_body_error");
		}});
		
		errorBox.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_buttons_error");
			 put(Tag.Property.NAME, "message_buttons_error");
			 put(Tag.Property.CLASS, "message_buttons_error");
		}}).add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_error_button");
			 put(Tag.Property.TYPE, "button");
			 put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("return"));
			 put(Tag.Property.CLASS, "interface_button");
			 put(Tag.Property.CLICK, "location.reload();");
		}});
		
		return content;
	}

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	   * 
	   */
	@Override
	public void onEndElement() {
		// Добавляем загрузку сгенерированного JS для текущего интерфейса 
		dom.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.TYPE, "text/javascript");
		     put(Tag.Property.SRC, "js/" + getAttribute(TagGenerator.Attribute.ID) + ".js");
		}});
	}
	
}
