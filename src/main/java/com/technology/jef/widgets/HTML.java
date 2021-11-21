package com.technology.jef.widgets;

import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

import java.util.HashMap;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет кнопка
*/
public class HTML extends Widget {
		@Override
		public ViewType getType() {
			// TODO Auto-generated method stub
			return ViewType.SINGLE;
		}


	
	  /**
	   * Метод формирования DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param parrent родительский тег в DOM модели
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
		public Tag assembleTag(String name, TagGenerator generator) throws SAXException {

			Tag elementInput = parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
			}});
			parrent.add(Tag.Type.SCRIPT, 			("			json_${name} = '${value}';  \n" + 
	"			$( document ).ready(function() {  \n" + 
	"				$('#${visible_name}').change(function() {  \n" + 
	"					var val = $(this).val();  \n" + 
	"					if (val != null && val != '') {  \n" + 
	"						$('#${visible_name}').empty();  \n" + 
	"						var jsonList = jQuery.parseJSON(val); \n" + 
	"						if (jsonList.error){ \n" + 
	"							$('#${visible_name}').append(jsonList.error); \n" + 
	"						} else {  \n" + 
	"							for (var i = 0; i < jsonList.length; i++) {  \n" + 
	"								$('#${visible_name}').append($('<table id=\"'+i+'_${visible_name}\"/>').css(\"border-collapse\", \"collapse\").css(\"border\", \"1px solid black\"));  \n" + 
	"								if (jsonList[i].type == 'table') {  \n" + 
	"									buildHtmlTable(jsonList[i].body, '#'+i+'_${visible_name}');  \n" + 
	"								} else if (jsonList[i].type == 'list'){  \n" + 
	"									buildHtmlList(jsonList[i].body, '#'+i+'_${visible_name}');  \n" + 
	"								}  \n" + 
	"								$('#${visible_name}').append($('<br/>'));  \n" + 
	"							} \n" + 
	"						} \n" +
	"					}  \n" + 
	"				    return 0;  \n" + 
	"  				});  \n" + 
	"			});  \n" + 
	"			// Builds the HTML Table out of json.  \n" + 
	"			function buildHtmlList(json, selector) {  \n" + 
	"				for (var i = 0; i < json.length; i++) {  \n" + 
	"					var row$ = $('<tr/>');  \n" + 
	"					row$.append($('<td/>').html(json[i].name).css(\"border-bottom\", \"1px solid black\"));  \n" + 
	"					row$.append($('<td/>').html(json[i].value).css(\"border-bottom\", \"1px solid black\"));  \n" + 
	"					$(selector).append(row$);  \n" + 
	"				}  \n" + 
	"			}  \n" + 
	"			// Builds the HTML Table out of json.  \n" + 
	"			function buildHtmlTable(json, selector) {  \n" + 
	"				var columns = addAllColumnHeaders(json, selector);  \n" + 
	"				for (var i = 0; i < json.length; i++) {  \n" + 
	"					var row$ = $('<tr/>');  \n" + 
	"					for (var colIndex = 0; colIndex < columns.length; colIndex++) {  \n" + 
	"						var cellValue = json[i][columns[colIndex]];  \n" + 
	"						if (cellValue == null) cellValue = \"\";  \n" + 
	"						row$.append($('<td/>').html(cellValue).css(\"border-left\", \"1px solid black\").css(\"border-right\", \"1px solid black\").css(\"text-align\", \"center\").css(\"padding\", \"2px\"));  \n" + 
	"					}  \n" + 
	"					$(selector).append(row$);  \n" + 
	"				}  \n" + 
	"			} // Adds a header row to the table and returns the set of columns.  \n" + 
	"			// Need to do union of keys from all records as some records may not contain  \n" + 
	"			// all records.  \n" + 
	"			function addAllColumnHeaders(json, selector) {  \n" + 
	"				var columnSet = [];  \n" + 
	"				var headerTr$ = $('<tr/>');  \n" + 
	"				for (var i = 0; i < json.length; i++) {  \n" + 
	"					var rowHash = json[i];  \n" + 
	"					for (var key in rowHash) {  \n" + 
	"						if ($.inArray(key, columnSet) == -1) {  \n" + 
	"							columnSet.push(key);  \n" + 
	"							headerTr$.append($('<th/>').html(key).css(\"border\", \"1px solid black\").css(\"border-bottom\", \"2px solid black\").css(\"text-align\", \"center\").css(\"padding\", \"2px\"));  \n" + 
	"						}  \n" + 
	"					}  \n" + 
	"				}  \n" + 
	"				$(selector).append(headerTr$);  \n" + 
	"				return columnSet;  \n" + 
	"			}  \n")
	.replace("${visible_name}", "visible_" + name)
	.replace("${name}", name)
	.replace("${value}", ""));
			return elementInput;
		}
}
