package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;

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
	public Tag generate(String qName) {


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


		form.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_box_wait_tab");
			 put(Tag.Property.NAME, "message_box_wait");
			 put(Tag.Property.CLASS, "message_box_loading background_color");
			 put(Tag.Property.STYLE, "display: none;");
		}}).add(Tag.Type.DIV, CurrentLocale.getInstance().getTextSource().getString("loading"), new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "message_overlay_wait");
			 put(Tag.Property.CLASS, "message_overlay_loading");
		}});

		addHandler(TagGenerator.Name.TAB, new Handler() {

			@Override
			public void handle(TagGenerator currentGenerator) {
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
															("				$( document ).ready(function() {       \n" + 
	"					setTimeout(function(){ // IE support     \n" + 
	"						loadForm(uri_params['form_id'] || '${form}');     \n" + 
	"					}, 300);     \n" + 
	"				});    \n" + 
	"				window.resourcesStack = [];          \n" + 
	"				function loadForm(formId) {          \n" + 
	"			            $(\"#message_box_wait_tab\").show();                                          \n" + 
	"						$('.tab').removeClass('current first_color');         \n" + 
	"						$('.tab').addClass('second_color');         \n" + 
	"						$( 'div[data-form-id=\"'+formId+'\"]' ).removeClass('second_color');         \n" + 
	"						$( 'div[data-form-id=\"'+formId+'\"]' ).addClass('current first_color');         \n" + 
	"					$.ajax({               \n" + 
	"				       	url: formId + '.html?no_cache=' + Math.floor(Math.random() * 10000),                                       \n" + 
	"						data: uri_params,                                      \n" + 
	"					       type: 'get',                                       \n" + 
	"					       dataType: 'text',                                       \n" + 
	"					}).done(function(data){               \n" + 
	"						$(\"#form_container\").empty();         \n" + 
	"						var resourcesRegExp = /<\\s*script\\s+src\\s*=\\s*['\"](.+?)\\.js/g;    \n" + 
	"						var resourcesRegExpResult;    \n" + 
	"						var filtredData = data;    \n" + 
	"						while((resourcesRegExpResult = resourcesRegExp.exec(data)) !== null) {   \n" + 
	"							if ($.inArray(resourcesRegExpResult[1], window.resourcesStack) > -1) {   \n" + 
	"								var replace = '<\\\\s*script\\\\s+src\\\\s*=\\\\s*[\\'\"]' + resourcesRegExpResult[1] + '\\\\.js.*?[\\'\"]\\\\s*>\\\\s*?</script>';  \n" + 
	"								var re = new RegExp(replace,\"g\");  \n" + 
	"								filtredData = filtredData.replace(re, '');   \n" + 
	"								replace = 'src\\\\s*=\\\\s*[\\'\"]' + resourcesRegExpResult[1] + '\\\\.js.*?[\\'\"]';  \n" + 
	"								re = new RegExp(replace,\"g\");  \n" + 
	"								filtredData = filtredData.replace(re, '');   \n" + 
	"							} else { \n" + 
	"								if (resourcesRegExpResult[1].indexOf(formId) < 0) {   \n" + 
	"									window.resourcesStack.push(resourcesRegExpResult[1])   \n" + 
	"								} \n" + 
	"							}    \n" + 
	"						}    \n" + 
	"						$(\"#form_container\").append(filtredData);                   \n" + 
	"				           	$(\"#message_box_wait_tab\").hide();                                          \n" + 
	"					}).fail(function(jqXHR, textStatus, errorThrown){               \n" + 
	"						showError(\"Error: \" + errorThrown, jqXHR.responseText);               \n" + 
	"					});               \n" + 
	"				}          \n")
					.replace("${form}", firstTabFormId)
			);
		}
	}

}
