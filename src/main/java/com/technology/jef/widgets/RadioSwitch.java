package com.technology.jef.widgets;

import static com.technology.jef.server.serialize.SerializeConstant.FIAS_CODE_NAME_SEPARATOR;
import static com.technology.jef.server.serialize.SerializeConstant.LIST_SEPARATOR;
import static com.technology.jef.server.serialize.SerializeConstant.PARAMETER_NAME_VALUE_SEPARATOR;
import static com.technology.jef.server.serialize.SerializeConstant.PARAMETER_SEPARATOR;
import static com.technology.jef.server.serialize.SerializeConstant.SYSTEM_PARAMETER_PREFIX;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
 * Виджет переключатель
 */
public class RadioSwitch extends List {

	@Override
	public ViewType getType() {
		return ViewType.SINGLE;
	}

	@Override
	public String getCleanValueJS() {

		return 	("							    \n" + 
	"							$(\"[name='visible_${child_name}']\").each(function(index, item){  \n" + 
	"								$(this).prop(\"checked\", false).trigger('refresh');  \n" + 
	"							});  \n" + 
	"							$('input#${child_name}').val('');  \n");
	}

	@Override
	public Tag assembleTag(String name, TagGenerator generator) throws SAXException {

		Tag mainInput = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>() {
			{
				put(Tag.Property.ID, "fieldset_" + name);
				put(Tag.Property.CLASS, "fieldset second_frames_border");
			}
		});

		Tag elementInput = mainInput.add(Tag.Type.LEGEND).add(Tag.Type.LABEL, new HashMap<Tag.Property, String>() {
			{
				put(Tag.Property.FOR, "visible_" + name);
				put(Tag.Property.CLASS, "widgets_label_color");
			}
		}).add(Tag.Type.SPAN, (String) generator.getAttribute(TagGenerator.Attribute.NAME),
				new HashMap<Tag.Property, String>() {
					{
						put(Tag.Property.NAME, "span_" + name);
						put(Tag.Property.STYLE,
								!"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED))
										? "color: rgb(170, 0, 0);"
										: "color: rgb(0, 0, 0);");
					}
				}).add(Tag.Type.DIV, new HashMap<Tag.Property, String>() {
					{
						put(Tag.Property.ID, "visible_" + name);
						put(Tag.Property.NAME, "visible_" + name);
						put(Tag.Property.STYLE, "display: table-cell; border-width: 1px;");
					}
				});
		elementInput.add(Tag.Type.DIV, new HashMap<Tag.Property, String>() {
			{
				put(Tag.Property.ID, "visible_container_" + name);
				put(Tag.Property.NAME, "visible_container_" + name);
			}
		});
		mainInput.add(Tag.Type.A, CurrentLocale.getInstance().getTextSource().getString("cleanup"),
				new HashMap<Tag.Property, String>() {
					{
						put(Tag.Property.ID, "link_" + name);
						put(Tag.Property.STYLE,
								"border-bottom: 1px dashed; cursor: pointer; margin: 0pt 2px 2px; text-align: left;display: block;");
						put(Tag.Property.CLICK, "$('#"+ name+"').val(''); $(\"input[name^='visible_" + name + "']\").each(function(index, item){ if ($( item ).prop(\"checked\")) {$( item ).prop('checked',false).trigger('refresh').trigger('update');  }}); $('#visible_"+ name+"').change();");
								
					}
				});

		return elementInput;
	}

	@Override
	String getListItemJS() {
		return 		("		$.each(data.data, function(key, val) {    \n" + 
	"			var visible_name = 'visible_${name}' + val.id;    \n" + 
	"			$(\"<input/>\", {    \n" + 
	"				'value': val.id,     \n" + 
	"				'checked': val.value,     \n" + 
	"				'type': 'radio',     \n" + 
	"				'id': visible_name,     \n" + 
	"				'name': 'visible_${name}',     \n" + 
	"				'disabled' : $('input#${name}').attr('data-disabled') ? 'disabled' : false ," +
	"			}).appendTo(\"#visible_container_${name}\");    \n" + 
	"			$(\"<label/>\", {     \n" + 
	"				style: 'margin:6px;',     \n" + 
	"				html: val.name,     \n" + 
	"				'for': visible_name,     \n" + 
	"				'class': 'frist_text_color',     \n" + 
	"			}).appendTo(\"#visible_container_${name}\");   \n" + 
	"			$('#' + visible_name).bindFirst('click', function(event){ \n" + 
	"				$('#' + visible_name + '-styler').addClass('checked').trigger('refresh'); \n"+
	"				onChangeReadOnly${name}(event.delegateTarget);  \n" + 
	"			});  \n" + 
	"			$('#' + visible_name).bindFirst('change', function(event){  \n" + 
	"				$('#' + visible_name + '-styler').addClass('checked').trigger('refresh'); \n"+
	"				onChangeReadOnly${name}(event.delegateTarget);  \n" +
	"//				$('.checked[name=\"visible_${name}\"]').removeClass('checked'); \n"+
	"			});  \n" + 
	"           $(\"#visible_${name}\" + val.id).styler({});  \n" + 
	"		});    \n")
	;
//TODO Добавить возможность вызова привязанных событий при выборе каждого элемента 
	}

	@Override
	public String getSetItemsJS() throws SAXException {

		String valueJS = getValueJS(null , "", null);

		return 	("			$(\"#background_overlay_wait_${name}\").show();          \n" + 
	"			$(\"#message_box_wait_${name}\").show();          \n" + 
	"			$(\"input#${name}\").trigger('lock');         \n" + 
	"			$( '#form_id' ).bind('setRadioValue', function() { //С помощью триггера сортирую вызовы установки значения радио в порядке формы , чтобы избежать ненужной проверки циклических зависимостей\n" + 
	"				$(\"#visible_${name}\").trigger('setRadioValue');    \n" + 
	"			});       \n" + 
	"			document.setRadioValueCount = !document.setRadioValueCount ? 1 : document.setRadioValueCount+1; \n" +
	"			$(\"#visible_${name}\").attr(\"disabled\",\"disabled\");         \n" + 
	"				ajax({                                        \n" + 
	"						url: \"${service}\" + \"get_list\",   \n" + 
	"						data: {  \n" + 
	"							form_api: \"${api}\",  \n" + 
	"							parameter_name: \"${name_api}\",  \n" + 
	"							parameters: ${value_js},  \n" + 
	"						},    \n" + 
	"						type: \"post\",     \n" + 
	"						dataType: \"json\",    \n" + 
	"						contentType: 'application/x-www-form-urlencoded'    \n" + 
	"					}, function( data ) {      \n" + 
	"						$(\"#visible_container_${name}\").empty();          \n" + 
	"						$(\"#visible_${name}\").removeAttr('disabled');         \n" + 
	"						${list_item_js}          \n" + 
	"						$(\"#visible_${name}\").trigger('refresh');          \n" + 
	"						if (--document.setRadioValueCount == 0) { \n" +
	"							$( '#form_id' ).trigger('setRadioValue');       \n" + 
	"							$( '#form_id' ).unbind('setRadioValue');       \n" + 
	"						} \n" +
	"						$(\"#background_overlay_wait_${name}\").hide();          \n" + 
	"						$(\"#message_box_wait_${name}\").hide();          \n" + 
	"						$(\"input#${name}\").trigger('unlock');         \n" + 
	"			});     \n").replace("${value_js}", valueJS);
	}

	@Override
	public String getSetActiveJS() {

		return 	(" \n" + 
	"		if (data.value) {  \n" + 
	" 			if ($('#tr_${child_name}' ).prop('disabled')){  \n" + 
	" 				$( '#tr_${child_name}' ).prop( 'disabled', false);  \n" + 
	" 				$( \"[name='visible_${child_name}']\" ).each(function( index, element) {  \n" + 
	" 					$( element ).prop( \"disabled\", false).trigger('refresh');  \n" + 
	"				}); \n" + 
	"				$(\"#tr_${child_name}\" ).css('color', 'black'); \n" + 
	" 			}                                                 \n" +
	"			if ($('input#${child_name}').val()) { //ie9 support                                           \n" + 
	" 				$('#visible_${child_name}' + $('input#${child_name}').val()).prop('checked', true).trigger('refresh');   \n" + 
	"			}  \n" + 
	" 		} else {                                                 \n" + 
	" 			if (!$('#tr_${child_name}' ).prop('disabled')){  \n" + 
	" 				$( \"[name='visible_${child_name}']\" ).each(function( index, element) {  \n" + 
	" 					$( element ).prop( \"disabled\", true).trigger('refresh');  \n" + 
	"				});                                          \n" + 
	" 				$( '#tr_${child_name}' ).prop( 'disabled', true);  \n" + 
	" 				$(\"#tr_${child_name}\" ).css('color', 'lightgray');  \n" + 
	" 			}                                                 \n" +
	" 		}  ;                                               \n");
	}

	@Override
	public String getSetInactiveJS() {

		return "			$( \"[name='visible_${child_name}']\" ).each(function( index, element) { \n "
				+ "				$( element ).prop( \"disabled\", true); \n	" + "			}); \n "
				+ "			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n ";

	}

	@Override
	public String getSetValueJS() {
		return 		("	if (isLoading) {                                                   \n" + 
	"		$(\"#visible_${child_name}\").bind('setRadioValue', function(){      \n" + 
	"			$(\"#visible_${child_name}\" + data.value).click();     \n" + 
	"			$(\"#visible_${child_name}\" + data.value).prop('checked',true).trigger('refresh');     \n" + 
	"			$(\"#visible_${child_name}\").change();     \n" + 
	"			$(\"input#${child_name}\").trigger('setHiddenValue',[data.value]);   \n" + 
	"			$('#visible_${child_name}').bind('change', function(){   \n" + 
	"				$('#${system_prefix}_changed_${child_name}').val('1')   \n" + 
	"			});   \n" + 
	"			$(\"#visible_${child_name}\").unbind('setRadioValue');      \n" + 
	"		});                                                           \n" + 
	"	} else { \n" + 
	"		if ($(\"#visible_${child_name}\" + data.value).attr('disabled')) { \n" + 
	"			$(\"input#${child_name}\").trigger('setUnlocked')  \n" + 
	" 			$(\"#visible_${child_name}\" + data.value).click();           \n" + 
	"			$(\"input#${child_name}\").trigger('setLocked') \n" + 
	"		} else { \n" + 
	" 			$(\"#visible_${child_name}\" + data.value).click();           \n" + 
	"		} \n" + 
	" 		$(\"#visible_${child_name}\" + data.value).prop('checked',true).trigger('refresh');     \n" + 
	"		$(\"#visible_${child_name}\").change();     \n" + 
	"		$(\"input#${child_name}\").trigger('setHiddenValue',[data.value]);     \n" + 
	"	}                                              \n")
				.replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX);
	}

	@Override
	public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
		String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_value(this);")
				.replace("${parrent_name}",
						((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID))
								.concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
						.concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

		// Вызываем функцию связи в событиях родительского элемента
		for (Tag radioSwitch : parrentGenerator.getDom().getChildren()) {
			radioSwitch.setProperty(Tag.Property.CHANGE,
					radioSwitch.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
			radioSwitch.setProperty(Tag.Property.CLICK, radioSwitch.getProperty(Tag.Property.CLICK).concat(onChangeJS));
		}

		return super.getValueConnectJS(currentGenerator, parrentGenerator);
	}

	@Override
	public String getVisibleConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
		String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_visible(this);")
				.replace("${parrent_name}",
						((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID))
								.concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
						.concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

		// Вызываем функцию связи в событиях родительского элемента
		Tag parrentTag = parrentGenerator.getDom().locateDown(Tag.Type.INPUT);
		if (parrentTag != null) {
			parrentTag.setProperty(Tag.Property.CHANGE,
			parrentTag.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
			parrentTag.setProperty(Tag.Property.CLICK, parrentTag.getProperty(Tag.Property.CLICK).concat(onChangeJS));
		}

		return super.getVisibleConnectJS(currentGenerator, parrentGenerator);
	}

	@Override
	public String getActiveConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
		String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_active(this);")
				.replace("${parrent_name}",
						((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID))
								.concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
						.concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

		// Вызываем функцию связи в событиях родительского элемента
		Tag parrentTag = parrentGenerator.getDom().locateDown(Tag.Type.INPUT);
		if (parrentTag != null) {
			parrentTag.setProperty(Tag.Property.CHANGE,
			parrentTag.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
			parrentTag.setProperty(Tag.Property.CLICK, parrentTag.getProperty(Tag.Property.CLICK).concat(onChangeJS));
		}

		return super.getActiveConnectJS(currentGenerator, parrentGenerator);
	}

	@Override
	public String getListConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) throws SAXException {
		String prefix = (String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX);
		String handler = (String) currentGenerator.getAttribute(TagGenerator.Attribute.HANDLER);
		String valueJS = getValueJS(currentGenerator, prefix, TagGenerator.Attribute.AJAX_LIST_PARRENT);
		String name = ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(prefix);

		String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_list(this);")
				.replace("${parrent_name}",
						((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID))
								.concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
				.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
						.concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

		// Вызываем функцию связи в событиях родительского элемента
		Tag parrentTag = parrentGenerator.getDom().locateDown(Tag.Type.INPUT);
		if (parrentTag != null) {
			parrentTag.setProperty(Tag.Property.CHANGE,
			parrentTag.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
			parrentTag.setProperty(Tag.Property.CLICK, parrentTag.getProperty(Tag.Property.CLICK).concat(onChangeJS));
		}

		super.getListConnectJS(currentGenerator, parrentGenerator);

		String bodyJS = 	("					function onChange${parrent_name}_${child_name}_ct_ajax_list(${parrent_name}List){             \n" + 
	"				if (!ajax_is_parrent_blocked${prefix}[\"${child_name}\"]) { // прерывание циклических зависимостей  \n" + 
	"						$(\"#background_overlay_wait_${parrent_name}\").show();             \n" + 
	"	            		$(\"#message_box_wait_${parrent_name}\").show();             \n" + 
	"						var valueJS = ${value_js};   \n" + 
	"						$(\"input#${child_name}\").trigger('cleanValue');         \n" + 
	"						if (valueJS.match(/${force_ajax}${value_separator}(none|${fias_code_name_separator})?(${parameter_separator}|$)/)){ return };             \n" + 
	"						$(\"#visible_container_${child_name}\").empty();             \n" + 
	"						$(\"#visible_${child_name}\").trigger('refresh');             \n" + 
	"						$(\"input#${parrent_name}\").trigger('lock');         \n" + 
	"						if (!ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"]) {             \n" + 
	"							ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] = 0;             \n" + 
	"						}             \n" + 
	"						++ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];            \n" + 
	"						if (!ajax_is_child_blocked${prefix}[\"${child_name}\"]) {        \n" + 
	"							ajax_is_child_blocked${prefix}[\"${child_name}\"] = 0;        \n" + 
	"						}        \n" + 
	"						++ajax_is_child_blocked${prefix}[\"${child_name}\"];        \n" + 
	"						ajax({          \n" + 
	"					            	url: '${service}get_list_interactive',          \n" + 
	"							data: {          \n" + 
	"								parameter_name:'${child_name_api}',          \n" + 
	"								form_api: '${api}',          \n" + 
	"								parameters: valueJS,          \n" + 
	"								rnd: Math.floor(Math.random() * 10000),          \n" + 
	"							},         \n" + 
	"					            	type: 'post',          \n" + 
	"					           	dataType: 'json',          \n" + 
	"			            			contentType: 'application/x-www-form-urlencoded',          \n" + 
	"						}, function (data) {    \n" + 
	"								$(\"#visible_container_${child_name}\").empty();             \n" + 
	"								${list_item_js}   \n" + 
	"								var ${child_name} = $('#tr_${child_name}');             \n" + 
	"								if (data.data && data.data.length > 0){             \n" + 
	"									if (\"${hide_if_empty}\"){             \n" + 
	"										${child_name}.css(\"display\", 'none');             \n" + 
	"										$('#visible_${child_name}').val('');             \n" + 
	"										$('input#${child_name}').val('');             \n" + 
	"										$('#is_empty_${child_name}').val(1);             \n" + 
	"									}else{             \n" + 
	"										if ($('input#${child_name}').attr('invisible') == 'false') {             \n" + 
	"											if ((document.getElementById && !document.all) || window.opera)             \n" + 
	"												${child_name}.css(\"display\",'table-row');             \n" + 
	"											else             \n" + 
	"												${child_name}.css(\"display\",'inline');             \n" + 
	"											}             \n" + 
	"										}             \n" + 
	"								}else{             \n" + 
	"									if ($('input#${child_name}').attr('invisible') == 'false') {             \n" + 
	"										if ((document.getElementById && !document.all) || window.opera)             \n" + 
	"											${child_name}.css(\"display\",'table-row');             \n" + 
	"										else             \n" + 
	"											${child_name}.css(\"display\",'inline');             \n" + 
	"									}             \n" + 
	"								}             \n" + 
	"								--ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"];             \n" + 
	"								$(\"#visible_${child_name}\").trigger('after_load');             \n" + 
	"								if (ajax_is_parrent_blocked${prefix}[\"${parrent_name}\"] == 0) {             \n" + 
	"									$(\"#visible_${parrent_name}\").trigger('on_parrent_unblocked');             \n" + 
	"									$(\"#background_overlay_wait_${parrent_name}\").hide();             \n" + 
	"		      	      				$(\"#message_box_wait_${parrent_name}\").hide();             \n" + 
	"									$(\"input#${parrent_name}\").trigger('unlock');         \n" + 
	"								}             \n" + 
	"								--ajax_is_child_blocked${prefix}[\"${child_name}\"];      \n" + 
	"								if (ajax_is_child_blocked${prefix}[\"${child_name}\"] == 0) {      \n" + 
	"				            		$(\"#visible_${child_name}\").trigger( 'on_child_unblocked');      \n" + 
	"									$(\"#visible_${child_name}\").trigger('setRadioValue'); \n" +
	"								}      \n" + 
	"								$(\"#visible_${parrent_name}\").trigger('refresh');             \n" + 
	"								$(\"#visible_${child_name}\").trigger('refresh');             \n" + 
	"						});            \n" + 
	"					}            \n" + 
	"				}             \n")
						.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
						.replace("${parameter_separator}", PARAMETER_SEPARATOR)
						.replace("${fias_code_name_separator}", "\\" + FIAS_CODE_NAME_SEPARATOR)
						.replace("${force_ajax}",
								!"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.FORCE_AJAX))
										? ("(^|:p:)(?!${child_name_api}|" + ((String) currentGenerator
												.getAttribute(TagGenerator.Attribute.FORCE_AJAX)).replace(",", "|") + ")\\w*")
										: "(^|:p:)(?!${child_name_api})\\w*")
						.replace("${value_js}", valueJS)
						.replace("${list_item_js}", getListItemJS().replace("${name}", name))
						.replace("${handler}", handler)
						.replace("${parrent_name}",
								((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(
										((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
						.replace("${value_js}", valueJS).replace("${prefix}", prefix)
						.replace("${api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.API))
						.replace("${child_name}", name)
						.replace("${child_name_api}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.ID))
						.replace("${hide_if_empty}",
								(String) currentGenerator.getAttribute(TagGenerator.Attribute.HIDE_IF_EMPTY))
						.replace("${service}", (String) currentGenerator.getAttribute(TagGenerator.Attribute.SERVICE));

		return bodyJS;

	}

	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {
		element.add(Tag.Type.SCRIPT, 		("  \n" + 
		"		$(\"#visible_${child_name}\").bind('setValue', function(event, value){      \n" + 
		"			$(\"#visible_${child_name}\" + value).click();     \n" + 
		"			$(\"#visible_${child_name}\" + value).prop('checked',true).trigger('refresh');     \n" + 
		"			$(\"#visible_${child_name}\").change();     \n" + 
		"			$(\"input#${child_name}\").val(value);   \n" + 
		"		});     \n")
		.replace("${child_name}", name));

		return element;
	}
}
