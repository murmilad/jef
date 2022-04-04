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
public class TabsGenerator extends TagGenerator {

	String firstTabFormId = null;
    
	@Override
	public Tag generate(String qName) throws SAXException {


		Tag container = dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS, "main_container");
		}});

		Tag tabs = container.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS, "tabs_container");
		}});
		
		Tag form = container.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "form_container");
		     put(Tag.Property.CLASS, "form_container");
		     put(Tag.Property.STYLE, "position: relative;");
		}});


		addHandler(TagGenerator.Name.TAB, new Handler()  {

			@Override
			public void handle(TagGenerator currentGenerator) throws SAXException {
				if (firstTabFormId == null) {
					firstTabFormId = (String) currentGenerator.getAttribute(TagGenerator.Attribute.FORM_ID);
				}
			}
			
		});

		return tabs;
	}


	@Override
	public void onEndElement() {
		if (firstTabFormId != null) {
			dom.add(Tag.Type.SCRIPT, 
																	("				$( document ).ready(function() {         \n" + 
	"					setTimeout(function(){ // IE support       \n" + 
	"						loadFormTab(getWindowParams()['form_id'] || '${form}');       \n" + 
	"					}, 300);       \n" + 
	"				});      \n" + 
	"				window.resourcesStackTabs = [];  //already loaded js 'js/jef.js'          \n" + 
	"				function loadFormTab(formId) {            \n" + 
	"			            $(\"#background_overlay_load_tab\").show();                                            \n" + 
	"			            $(\"#message_box_wait_tab\").show();                                            \n" + 
	"			            $(\"#message_box_wait_tab_load\").show();                                            \n" + 
	"						$('.tab').removeClass('current first_color');           \n" + 
	"						$('.tab').addClass('second_color');           \n" + 
	"						$( 'div[data-form-id=\"'+formId+'\"]' ).removeClass('second_color');           \n" + 
	"						$( 'div[data-form-id=\"'+formId+'\"]' ).addClass('current first_color');           \n" + 
	"					$.ajax({                 \n" + 
	"				       	url: formId + '.html?no_cache=' + Math.floor(Math.random() * 10000),                                         \n" + 
	"						data: getWindowParams(),                                        \n" + 
	"					       type: 'get',                                         \n" + 
	"					       dataType: 'text',                                         \n" + 
	"					}).done(function(data){                 \n" + 
	"						$(\"#form_container\").empty(); \n" +           
	"						$('script').each( function() {  \n" + 
	"							if($( this ).attr('src')) { \n" + 
	"								if ($( this ).attr('src').split('?')[0].indexOf(formId) < 0 ) { \n " +
	"									window.resourcesStackTabs.push($( this ).attr('src').split('?')[0]); \n" + 
	"								} \n " +
	"							} \n" + 
	"						}); \n" + 
	"						$('<div />', {  \n" + 
	"		    					'class': 'background_overlay_form_loading background_color',  \n" + 
	"		    					'id': 'background_overlay_load_tab',  \n" + 
	"		    					'name': 'background_overlay_load_tab'  \n" + 
	"						}).appendTo('#form_container');  \n" + 
	"						var $box = $('<div />', {  \n" + 
	"		    					'class': 'message_overlay_form_loading',  \n" + 
	"		    					'id': 'message_box_wait_tab',  \n" + 
	"		    					'name': 'message_box_wait_tab'  \n" + 
	"						}).appendTo('#form_container');  \n" + 
	"						$('<div />', {  \n" + 
	"		    					'class': 'message_box_form_loading messages_border messages_color',  \n" + 
	"		    					'id': 'message_box_wait_tab_load',  \n" + 
	"		    					'name': 'message_box_wait_tab_load',  \n" + 
	"						}).appendTo($box);  \n" + 
	"						var resourcesRegExp = /<\\s*script\\s+src\\s*=\\s*['\"](.+?\\.js)/g;      \n" + 
	"						var resourcesRegExpResult;      \n" + 
	"						var filtredData = data;      \n" + 
	"						while((resourcesRegExpResult = resourcesRegExp.exec(data)) !== null) {     \n" + 
	"							if ($.inArray(resourcesRegExpResult[1], window.resourcesStackTabs) > -1) {     \n" + 
	"								var replace = '<\\\\s*script\\\\s+src\\\\s*=\\\\s*[\\'\"]' + resourcesRegExpResult[1] + '.*?[\\'\"][\\\\s\\\\S]+?</script>'; \n" + 
	"								var re = new RegExp(replace,\"g\");    \n" + 
	"								filtredData = filtredData.replace(re, '');     \n" + 
	"								replace = 'src\\\\s*=\\\\s*[\\'\"]' + resourcesRegExpResult[1] + '.*?[\\'\"]';   \n" + 
	"								re = new RegExp(replace,\"g\");    \n" + 
	"								filtredData = filtredData.replace(re, '');     \n" + 
	"							}      \n" + 
	"						}  \n" + 
	"						window.ajaxRequestStack = [];                 \n" + 
	"						var ajaxRequestId = 0;                 \n" + 
	"        				window.ajaxPool = [];       \n" + 
	"        				window.ajaxXNR = [];       \n" + 
	"        				window.ajaxPool.hasSame = function(parameters) {       \n" + 
	"            				if (ajaxPool.indexOf(parameters) != -1) {      \n" + 
	" 								return true;      \n" + 
	"							}       \n" + 
	"							return false;       \n" + 
	"        				};       \n" + 
	"        				window.ajaxPool.delete	= function(parameters) {       \n" + 
	"            				var i = ajaxPool.indexOf(parameters);       \n" + 
	"							if (i > -1) ajaxPool.splice(i, 1);      \n" + 
	"        				};      \n" +
	"						$(\"#form_container\").append(filtredData);                     \n" + 
	"			        		$(\"#background_overlay_load_tab\").hide();                                            \n" + 
	"			        		$(\"#message_box_wait_tab\").hide();                                            \n" + 
	"			        		$(\"#message_box_wait_tab_load\").hide();                                            \n" + 
	"					}).fail(function(jqXHR, textStatus, errorThrown){                 \n" + 
	"						showError(\"Error: \" + errorThrown, jqXHR.responseText);                 \n" + 
	"					});                 \n" + 
	"				}            \n")
					.replace("${form}", firstTabFormId)
			);
		}
	}


	@Override
	public Name getName() {
		return Name.TABS;
	}

}
