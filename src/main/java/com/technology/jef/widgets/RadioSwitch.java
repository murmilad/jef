package com.technology.jef.widgets;

import java.util.HashMap;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет переключатель
*/
public class RadioSwitch extends List {

		@Override
		public ViewType getType () {
			return ViewType.SINGLE;
		}
		
		@Override
		public String getCleanValueJS() {
			
			return 			("							   \n" + 
							"							$(\"[name='visible_${child_name}']\").each(function(index, item){ \n" + 
							"								$(this).prop(\"checked\", false).trigger('refresh'); \n" + 
							"							}); \n" + 
							"							$('#${child_name}').val(''); \n");
		}
		
		@Override
		public Tag assembleTag(String name, TagGenerator generator) {

			Tag mainInput = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "fieldset_" + name);
			}});

			Tag elementInput = mainInput
					.add(Tag.Type.LEGEND)
					.add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.FOR, "visible_" + name);
					}})
					.add(Tag.Type.SPAN, (String) generator.getAttribute(TagGenerator.Attribute.NAME), new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.NAME, "span_" + name);
						 put(Tag.Property.STYLE, !"".equals(generator.getAttribute(TagGenerator.Attribute.REQUIRED)) ? "color: rgb(170, 0, 0);" : "color: rgb(0, 0, 0);");
					}})
					.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
						 	put(Tag.Property.ID, "visible_" + name);
							 put(Tag.Property.NAME, "visible_" + name);
							 put(Tag.Property.STYLE, "display: table-row;");
					}});			

			mainInput.add(Tag.Type.LINK, "очистить", new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "link_" + name);
				 put(Tag.Property.STYLE, "border-bottom: 1px dashed; cursor: pointer; margin: 0pt 2px 2px; text-align: left;");
				 put(Tag.Property.CLICK, "$(\"[name='visible_" + name + "']\").each(function(index, item){ $(this).prop(\"checked\", false).trigger('refresh');});$('#" + name + "').val('');return false;");
			}});

			return elementInput;
		}


		@Override
		String getListItemJS() {
			return
						("		$.each(data.data, function(key, val) {  \n" + 
						"			var visible_name = 'visible_${name}' + val.id;  \n" + 
						"			$(\"<input/>\", {  \n" + 
						"				'value': val.id,   \n" + 
						"				'checked': val.value,   \n" + 
						"				'type': 'radio',   \n" + 
						"				'id': visible_name,   \n" + 
						"				'name': 'visible_${name}',   \n" + 
						"				'onchange': 'onChangeReadOnly${name}(this);',   \n" + 
						"				'onclick': 'onChangeReadOnly${name}(this);',   \n" + 
						"			}).appendTo(\"#visible_${name}\");  \n" + 
						"			$(\"<label/>\", {   \n" + 
						"				style: 'margin:3px;',   \n" + 
						"				html: val.name,   \n" + 
						"				'for': visible_name,   \n" + 
						"			}).appendTo(\"#visible_${name}\");  \n" + 
						"		});  \n" + 
						"		 \n"); 
//TODO Добавить возможность вызова привязанных событий при выборе каждого элемента 
		}

		@Override
			public String getSetItemsJS() {
				
				return 				("			$(\"#background_overlay_wait_${name}\").show();         \n" + 
									"			$(\"#message_box_wait_${name}\").show();         \n" + 
									"			$(\"#visible_${name}\").attr(\"disabled\",\"disabled\");        \n" + 
									"			params['form_api'] = \"${api}\";     \n" + 
									"			params['parameter_name'] = \"${name}\";     \n" + 
									"			ajax({     \n" + 
									"					url: \"${service}\" + \"get_list\",    \n" + 
									"					data:  params, \n" + 
									"					type: \"post\",    \n" + 
									"					dataType: \"json\",   \n" + 
									"					contentType: 'application/x-www-form-urlencoded'   \n" + 
									"				}, function( data ) {     \n" + 
									"					$(\"#visible_${name}\").empty();         \n" + 
									"					${list_item_js}         \n" + 
									"					$(\"#visible_${name}\").removeAttr('disabled');        \n" + 
									"					$(\"#background_overlay_wait_${name}\").hide();         \n" + 
									"					$(\"#message_box_wait_${name}\").hide();         \n" + 
									"					$(\"#visible_${name}\").trigger('refresh');         \n" + 
									"					$(\"#visible_${name}\").unbind(\"focusin\");        \n" + 
									"					$(\"#visible_${name}\").find('input').styler({});  \n" + 
									"					$(\"#visible_${name}\").trigger('setValue');   \n" + 
									"			});    \n");
			}
		
			@Override
			public String getSetActiveJS() {
				
				return 
				"		if (val.value) { \n " + 
				"			$( \"[name='visible_${child_name}']\" ).each(function( index, element) { \n " + 
				"				$( element ).prop( \"disabled\", false); \n	" +
				"			}); \n " +
				"			$(\"#tr_${child_name}\" ).css('color', 'black'); \n "+
				"		} else { \n " +
				"			$( \"[name='visible_${child_name}']\" ).each(function( index, element) { \n " + 
				"				$( element ).prop( \"disabled\", true); \n	" +
				"			}); \n " +
				"			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n " +
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
				return			("	if (isLoading) {  \n" + 
								"		$(\"#visible_${child_name}\").bind('setValue', function(){    \n" + 
								"			$(\"#visible_${child_name}\" + data.value).click();   \n" + 
								"			$(\"#visible_${child_name}\").change();   \n" + 
								"			$(\"#${child_name}\").val(data.value); \n" + 
								"			 \n" +
								"		});    \n" + 
								"	} else { \n" + 
								"		$(\"#visible_${child_name}\" + data.value).click();   \n" + 
								"		$(\"#visible_${child_name}\").change();   \n" + 
								"		$(\"#${child_name}\").val(data.value);   \n" + 
								"	} \n");
				}

			@Override
			public String getValueConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
				String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_value(this);")
						.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

				// Вызываем функцию связи в событиях родительского элемента
				for (Tag radioSwitch : parrentGenerator.getDom().getChildren()) {
					radioSwitch.setProperty(Tag.Property.CHANGE, radioSwitch.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
					radioSwitch.setProperty(Tag.Property.CLICK, radioSwitch.getProperty(Tag.Property.CLICK).concat(onChangeJS));
				}

				return super.getValueConnectJS(currentGenerator, parrentGenerator);
			}

			@Override
			public String getVisibleConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
				String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_visible(this);")
						.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

				// Вызываем функцию связи в событиях родительского элемента
				for (Tag radioSwitch : parrentGenerator.getDom().getChildren()) {
					radioSwitch.setProperty(Tag.Property.CHANGE, radioSwitch.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
					radioSwitch.setProperty(Tag.Property.CLICK, radioSwitch.getProperty(Tag.Property.CLICK).concat(onChangeJS));
				}

				return super.getVisibleConnectJS(currentGenerator, parrentGenerator);
			}
			
			@Override
			public String getActiveConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
				String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_active(this);")
						.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

				// Вызываем функцию связи в событиях родительского элемента
				for (Tag radioSwitch : parrentGenerator.getDom().getChildren()) {
					radioSwitch.setProperty(Tag.Property.CHANGE, radioSwitch.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
					radioSwitch.setProperty(Tag.Property.CLICK, radioSwitch.getProperty(Tag.Property.CLICK).concat(onChangeJS));
				}

				return super.getActiveConnectJS(currentGenerator, parrentGenerator);
			}
			
			@Override
			public String getListConnectJS(TagGenerator currentGenerator, TagGenerator parrentGenerator) {
				String onChangeJS = ("onChange${parrent_name}_${child_name}_ct_ajax_list(this);")
						.replace("${parrent_name}", ((String) parrentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))))
						.replace("${child_name}", ((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID)).concat(((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))));

				// Вызываем функцию связи в событиях родительского элемента
				for (Tag radioSwitch : parrentGenerator.getDom().getChildren()) {
					radioSwitch.setProperty(Tag.Property.CHANGE, radioSwitch.getProperty(Tag.Property.CHANGE).concat(onChangeJS));
					radioSwitch.setProperty(Tag.Property.CLICK, radioSwitch.getProperty(Tag.Property.CLICK).concat(onChangeJS));
				}

				return super.getListConnectJS(currentGenerator, parrentGenerator);
			}

}
