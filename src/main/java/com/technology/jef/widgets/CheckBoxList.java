package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import static com.technology.jef.server.serialize.SerializeConstant.*;

/**
* Виджет группа выключателей
*/
public class CheckBoxList extends RadioSwitch {

	/**
	   * Метод возвращает тип виджета
	   * 
	   * ViewType.DOUBLE - С заголовком слева
	   * ViewType.SINGLE - Без заголовка слева  
	   * 
	   * @return тип виджета
	   */
		public ViewType getType () {
			return ViewType.SINGLE;
		}
		
		@Override
		public String getSetItemsJS() throws SAXException {
			
			String valueJS = getValueJS(null , "", null);
			
			return 					("			$(\"#background_overlay_wait_${name}\").show();          \n" + 
	"			$(\"#message_box_wait_${name}\").show();          \n" + 
	"			$(\"#input${name}\").trigger('lock');         \n" + 
	"			$(\"#visible_${name}\").attr(\"disabled\",\"disabled\");          \n" + 
	"			ajax({    \n" + 
	"					url: \"${service}\" + \"get_list\",   \n" + 
	"					data: { \n" + 
	"						form_api: \"${api}\", \n" + 
	"						parameter_name: \"${name_api}\", \n" + 
	"						parameters: ${value_js}, \n" + 
	"					},   \n" + 
	"					type: \"post\",   \n" + 
	"					dataType: \"json\",  \n" + 
	"					contentType: 'application/x-www-form-urlencoded'  \n" + 
	"				}, function( data ) {      \n" + 
	"					$(\"#visible_${name}\").empty();          \n" + 
	"					$(\"#visible_${name}\").removeAttr('disabled');          \n" + 
	"					${list_item_js}          \n" + 
	"					$(\"#background_overlay_wait_${name}\").hide();          \n" + 
	"					$(\"#message_box_wait_${name}\").hide();          \n" + 
	"					$(\"input#${name}\").trigger('unlock');         \n" + 
	"					$(\"#visible_${name}\").trigger('refresh');          \n" + 
	"					$(\"#visible_${name}\").unbind(\"focusin\");         \n" + 
	"					$(\"#visible_${name}\").find('input').styler({});    \n" + 
	"					$(\"#visible_${name}\").trigger('setCheckboxValue');    \n" + 
	"			});         \n").replace("${value_js}", valueJS);
		}

		@Override
		public Tag assembleTag(String name, TagGenerator generator) throws SAXException {
			
			Tag mainInput = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "fieldset_" + name);
				 put(Tag.Property.CLASS, "fieldset second_frames_border");
			}});

			Tag elementInput = mainInput
					.add(Tag.Type.LEGEND)
					.add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.FOR, "visible_" + name);
						 put(Tag.Property.CLASS, "widgets_label_color");
					}})
					.add(Tag.Type.DIV, (String) generator.getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.NAME, "span_" + name);
						 put(Tag.Property.STYLE, !"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED)) ? "color: rgb(170, 0, 0);" : "color: rgb(0, 0, 0);");
					}})
					.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
							 put(Tag.Property.CLASS, "styled widgets_height");
							put(Tag.Property.ID, "visible_" + name);
							put(Tag.Property.NAME, "visible_" + name);
							put(Tag.Property.CLASS, "display: block; border-width: 1px;");
					}});

			mainInput.add(Tag.Type.LINK, CurrentLocale.getInstance().getTextSource().getString("cleanup"), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "link_" + name);
				 put(Tag.Property.STYLE, "border-bottom: 1px dashed; cursor: pointer; margin: 0pt 2px 2px; text-align: left;");
				 put(Tag.Property.CLICK, "for(i=0,arr=document.getElementsByName('visible_" + name + "');i<arr.length;i++){arr[i].checked=false;};$('#" + name + "').val('');return false;");
			}});
			

			
			return elementInput;
		}

		@Override
		protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {
			element.add(Tag.Type.SCRIPT, 	(" \n" + 
					"	function fill_${name}_checks() { \n" + 
					"		$(\"input#${name}\").val(\"\"); \n" + 
					"		$(\"#visible_${name}\").trigger('refresh'); \n" +
					"		$(\":input[name^='visible_${name}']\").each( function(index, element){ \n" + 
					"			if ($( this ).prop(\"checked\")){ \n" + 
					"				$(\"input#${name}\").val($(\"input#${name}\").val() + ($(\"input#${name}\").val()? \"${list_separator}\" : \"\") + $( this ).attr('value')); \n" + 
					"			} \n" + 
					"		}); \n" + 
					"	} \n")
					.replace("${name}", name)
					.replace("${list_separator}", LIST_SEPARATOR));
			element.add(Tag.Type.SCRIPT, 		("  \n" + 
	"		$(\"#visible_${child_name}\").bind('setValue', function(event, value){      \n" + 
	"			value.split('${list_separator}').forEach(function callback(currentValue, index, array) {     \n" + 
	"				$('#visible_${child_name}' + currentValue).click();      \n" + 
	"				$('#visible_${child_name}' + currentValue).prop('checked', true).trigger('refresh');       \n" + 
	"				$('#visible_${child_name}' + currentValue).change();      \n" + 
	"				$(\"#visible_${child_name}\" + currentValue).trigger('refresh');       \n" + 
	"			});     \n" +
	"		});     \n")
	.replace("${list_separator}", LIST_SEPARATOR)
	.replace("${child_name}", name));

			return element;
		}
		
		@Override
		String getListItemJS() {
			return
						("							$.each(data.data, function(key, val) {     \n" + 
	"								var span_name = 'span_group_${name}' + val.id;    \n" + 
	"								$(\"<div/>\", {    \n" + 
	"									'id' : span_name,    \n" + 
	"									'style' : 'white-space:nowrap; padding-bottom:5px; float: left;',    \n" +
	"								}).appendTo(\"#visible_${name}\");    \n" + 
	"								var visible_name = 'visible_${name}' + val.id;    \n" + 
	"								$(\"<input/>\", {    \n" + 
	"									'id' : visible_name,    \n" + 
	"									'name' : 'visible_${name}',    \n" + 
	"									'value' : val.id,    \n" + 
	"									'type' : 'checkbox',    \n" + 
	"									'disabled' : $('input#${name}').attr('data-disabled') ? 'disabled' : false," + 
	"								}).appendTo(\"#\" + span_name);    \n" + 
	"								$(\"<label/>\", {    \n" + 
	"									html : val.name,    \n" + 
	"									'for' : visible_name,    \n" + 
	"									'style' : 'display: inline-block; padding:6px;',    \n" + 
	"									'class' : 'frist_text_color',    \n" + 
	"								}).appendTo(\"#\" + span_name);    \n" + 
	"								$('#' + visible_name).change( function(event){  \n" + 
	"									fill_${name}_checks(); \n" + 
	"								});  \n" + 
	"							});  \n" + 
	"							$(\"#visible_${name}\").find('input').styler({});            \n");
			//TODO Добавить возможность вызова привязанных событий при выборе каждого элемента 
		}

		@Override
			public String getSetActiveJS() {
				
				return 
				"		if (data.value) { \n " + 
				" 			if ($('#tr_${child_name}' ).prop('disabled')){  \n" + 
				"				$( \"[name='visible_${child_name}']\" ).each(function( index, element) { \n " + 
				"					$( element ).prop( \"disabled\", false); \n	" +
				"           		$( element ).find(\"input\").trigger('refresh');" +
				"				}); \n " +
				"           	$(\"#visible_${child_name}\").find(\"input\").trigger('refresh');" +
				" 				$( '#tr_${child_name}' ).prop( 'disabled', false);  \n" + 
				"           	$(\"#visible_${child_name}\").find(\"input\").trigger('refresh');" +
				" 			}                                                 \n" +
				"		} else { \n " +
				" 			if (!$('#tr_${child_name}' ).prop('disabled')){  \n" + 
				"				$( \"[name='visible_${child_name}']\" ).each(function( index, element) { \n " + 
				"					$( element ).prop( \"disabled\", true); \n	" +
				"				}); \n " +
				" 				$( '#tr_${child_name}' ).prop( 'disabled', true);  \n" + 
				" 			}                                                 \n" +
				"		} \n ";
			}

			@Override
			public String getSetInactiveJS() {
				
				return 
				"			$( \"[name='visible_${child_name}']\" ).each(function( index, element) { \n " + 
				"				$( element ).prop( \"disabled\", true); \n	" +
				"			}); \n " +
				"			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n ";
						
			}

			

			@Override
			public String getSetValueJS() {
				return  					("	if (isLoading) {    \n" + 
	"		$(\"#visible_${child_name}\").bind('setCheckboxValue', function(){      \n" + 
	"			$('input#${child_name}').trigger('setHiddenValue',[data.value]);     \n" + 
	"			data.value.split('${list_separator}').forEach(function callback(currentValue, index, array) {    \n" + 
	"				$('#visible_${child_name}' + currentValue).click();     \n" + 
	"				$('#visible_${child_name}' + currentValue).prop('checked', true).trigger('refresh');      \n" + 
	"				$('#visible_${child_name}' + currentValue).change();     \n" + 
	"				$(\"#visible_${child_name}\" + currentValue).trigger('refresh');      \n" + 
	"			});    \n" + 
	"			$('#visible_${child_name}').bind('change', function(){  \n" + 
	"				$('#${system_prefix}_changed_${child_name}').val('1')  \n" + 
	"			});  \n" + 
	"			$(\"#visible_${child_name}\").unbind('setCheckboxValue');      \n" + 
	"		});      \n" + 
	"	} else {   \n" + 
	"	$('input#${child_name}').val(data.value);     \n" + 
	"		data.value.split('${list_separator}').forEach(function callback(currentValue, index, array) {    \n" + 
	"			if ($(\"#visible_${child_name}\" + data.value).attr('disabled')) {  \n" + 
	"				$(\"input#${child_name}\").trigger('setUnlocked')   \n" + 
	"	 			$('#visible_${child_name}' + currentValue).click();     \n" + 
	"				$(\"input#${child_name}\").trigger('setLocked')  \n" + 
	"			} else {  \n" + 
	"	 			$('#visible_${child_name}' + currentValue).click();     \n" + 
	"			}  \n" + 
	"			$('#visible_${child_name}' + currentValue).prop('checked', true).trigger('refresh');      \n" + 
	"			$('#visible_${child_name}' + currentValue).change();     \n" + 
	"			$(\"#visible_${child_name}\" + currentValue).trigger('refresh');      \n" + 
	"		});    \n" + 
	"	}   \n")
				.replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX)
				.replace("${list_separator}", LIST_SEPARATOR);
			}


}
