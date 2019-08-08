package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.technology.jef.Tag;
import com.technology.jef.widgets.Widget;

/**
* Класс сборщика DOM модели уровня формы (страницы)
*/
public class FormGenerator extends TagGenerator {

	HashMap<String, TagGenerator> connectedElements = new HashMap<String, TagGenerator>();
	List<TagGenerator> multiplieGroups = new LinkedList<TagGenerator>();
	HashMap<String, String> formInterfaceApiMap = new HashMap<String, String>();


	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	   */
	@Override
	public Tag generate(String qName) {

		// Добавляем сообщение "Подождите...", которое будет показываться при загрузке формы
		dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "background_overlay_wait_form");
			 put(Tag.Property.NAME, "background_overlay_wait");
			 put(Tag.Property.CLASS, "background_overlay_wait_form");
			 put(Tag.Property.STYLE, "display: block;");
		}});

		dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "message_box_wait_form");
			 put(Tag.Property.NAME, "message_box_wait");
			 put(Tag.Property.CLASS, "message_box_wait");
			 put(Tag.Property.STYLE, "display: block;");
		}}).add(Tag.Type.DIV, "Подождите...", new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "message_overlay_wait");
			 put(Tag.Property.CLASS, "message_overlay_wait");
		}});

		
		Tag form = dom.add(Tag.Type.FORM, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "form");
		}});

		form.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "form_id");
		     put(Tag.Property.NAME, "form_id");
		     put(Tag.Property.TYPE, "hidden");
		}});

		form.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "next_form");
		     put(Tag.Property.NAME, "next_form");
		     put(Tag.Property.TYPE, "hidden");
		}});

		form.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "next_form_id");
		     put(Tag.Property.NAME, "next_form_id");
		     put(Tag.Property.TYPE, "hidden");
		}});
		form.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.STYLE, "color:red;");
		}}).add(Tag.Type.UL, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "error_list");
		     put(Tag.Property.NAME, "error_list");
		}});
		
		
		//Добавляем флаг JS свидетельствующий о том, что происходит загрузка связных списков
		form.add(Tag.Type.SCRIPT, 	"	var ajax_is_parrent_blocked = {}; \n" + 
									"	var ajax_is_child_blocked = {}; \n"
		);

		addHandler(TagGenerator.Name.GROUP, new Handler() {
			@Override
			public void handle(TagGenerator currentGenerator) {
				if (!"".equals(currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX))) { // Если группа множественная
					//Добавляем флаг JS свидетельствующий о том, что происходит загрузка связных списков для множественных групп, если таковые имеются
					currentGenerator.getDom().add(Tag.Type.SCRIPT, 	"var ajax_is_parrent_blocked" + currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX) + " = {};\n"+
																	"var ajax_is_child_blocked" + currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX) + " = {};\n"
					);
					
					//Добавляем мультигруппу всписок для последующей обработки (генерации JS и HTML)
					multiplieGroups.add(currentGenerator);
				}
			}
		});

		// Реализуем накопление связанных элементов формы
		// TODO нужно задуматься о групповых формах, как там генерить связные скприпты?
		// Реализуем накопление API для заполнения параметров на уровне элементов
		addHandler(TagGenerator.Name.FORM_ITEM, new Handler() {
			@Override
			public void handle(TagGenerator currentGenerator) {
				connectedElements.put((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID), currentGenerator);
				
				String formInterfaceApi = (String) currentGenerator.getAttribute(TagGenerator.Attribute.API);
				if  (!"".equals(formInterfaceApi)) {
					// Добавляем в карту API встретившийся у парамера API и записываем в текущую API родительские API (те что должны быть вызваны первыми)
					formInterfaceApiMap.put(formInterfaceApi, (String) currentGenerator.getParrent().getAttribute(TagGenerator.Attribute.PARRENT_API));
				}
			}
		});

		// Реализуем накопление API для заполнения параметров на уровне групп
		addHandler(TagGenerator.Name.GROUP, new Handler() {
			@Override
			public void handle(TagGenerator currentGenerator) {
				String formInterfaceApi = (String) currentGenerator.getAttribute(TagGenerator.Attribute.API);
				if  (!"".equals(formInterfaceApi)) {
					// Добавляем в карту API встретившийся у парамера API и записываем в текущую API родительские API (те что должны быть вызваны первыми)
					formInterfaceApiMap.put(formInterfaceApi, (String) currentGenerator.getAttribute(TagGenerator.Attribute.PARRENT_API));
				}
			}
		});

		return form;
	}

	private void addConectionJS (String conectionName, TagGenerator currentGenerator, String currentName, TagGenerator.Attribute attribute, Handler widgetJSHandler){
		// Идем по связям элемента, влияющих на видимость
		String[] ajax_visible_list = (String[])currentGenerator.getAttribute(attribute);
		for (String parrentName: ajax_visible_list) {
			TagGenerator parrentGenerator = connectedElements.get(parrentName);
			if (parrentGenerator == null) {
				System.out.println("ERROR: Can't find parrent element '" + parrentName + "' for '" + currentName  + "'");
			} else {
				String onChangeJS = ("onChange${parrent_name}_${child_name}_" + conectionName +"(this);")
						// Указываем имя родителя с префиксом в зависимости от того, в добавляемой ли он группе или нет
						.replace("${parrent_name}", parrentName.concat((String) parrentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)))
						// Указываем имя подчиненного элемента с префиксом в зависимости от того, в добавляемой ли он группе или нет
						.replace("${child_name}", currentName.concat((String) currentGenerator.getAttribute(TagGenerator.Attribute.PREFIX)));

				
				Widget.Type type = Widget.Type.valueOf(((String)parrentGenerator.getAttribute(TagGenerator.Attribute.TYPE)).toUpperCase());

				// Взываем разные процедуры в зависимости от типа элемента
				switch (type) {
				case SHORT_RADIO_SWITCH:
					// Вызываем функцию связи в событиях родительского элемента
				break;
				default:
				break;
				}

				widgetJSHandler.handle(parrentGenerator);
			}
		}
		
	} 
	
	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	   * 
	   */
	@Override
	public void onEndElement() {
		//Добавляем кнопки сохранения формы и перехода
		
		Tag buttons = dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS, "interface_buttons");
		}});

		Tag buttonsRow = buttons.add(Tag.Type.TABLE, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.CLASS, "interface_buttons");
		}}).add(Tag.Type.TR, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "tr_submit_button");
		     put(Tag.Property.CLASS, "buttons");
		}});
		
		buttonsRow.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ALIGN, "left");
		}});
		buttonsRow.add(Tag.Type.TD, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ALIGN, "right");
		}});
		//TODO продумать динамическое указание readonly для кнопок перехода

		buttonsRow.getChildren().get(1).add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "submit_button");
		     put(Tag.Property.NAME, "submit_button");
		     put(Tag.Property.TYPE, "button");
		     put(Tag.Property.VALUE, "Далее »");
		     put(Tag.Property.CLASS, "submit_button");
		     put(Tag.Property.CLICK, "$(\"#api_action\").val(\"save\");");
		}});

		//TODO предусматреть внедрение JS из XML при нажатии кнопки "Далее" 
		
		// При нажатии клавиши Enter вызываем нажатие кноеки "Далее"
		buttonsRow.add(Tag.Type.SCRIPT, 
					("				$( document ).ready(function() {          \n" + 
							"					$(\":input\").keypress(function (e) {          \n" + 
							"					      if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {          \n" + 
							"					          $(\"#submit_button:visible\").click();          \n" + 
							"					          return false;          \n" + 
							"					      } else {          \n" + 
							"					          return true;          \n" + 
							"					      }          \n" + 
							"					});          \n" + 
							"					$(\"#submit_button\").on(\"click\", function(event){         \n" + 
							"						var field_values = [];         \n" + 
							"						$(':input[type=\"hidden\"]').each( function(index, element){         \n" + 
							"							field_values[index] = $( this ).attr('id') +':i:'+ $( this ).val();         \n" + 
							"						});         \n" + 
							"						$(\"#background_overlay_wait_form\").show();                   \n" + 
							"			            		$(\"#message_box_wait_form\").show();                   \n" + 
							"						$(\"[id^='visible_']\").each(function(index, item){   \n" + 
							"							$(this).removeClass(\"error\");   \n" + 
							"						});   \n" + 
							" 						ajax({                \n" + 
							"					       	url: '${service}set',                \n" + 
							"							data: {                \n" + 
							"								city_id: params.city_id,                        \n" + 
							"								application_id: params.application_id,                        \n" + 
							"								parameters: field_values.join(':p:'),                \n" + 
							"							},               \n" + 
							"						       type: 'post',                \n" + 
							"						       dataType: 'json',                \n" + 
							"				            		contentType: 'application/x-www-form-urlencoded',                \n" + 
							"						}, function (data) {   \n" + 
							"								var hasErrors = false;       \n" + 
							"								$(\"#error_list\").empty();     \n" + 
							"								if (data.errors.parametersErrors != null) {    \n" + 
							"									$.each(data.errors.parametersErrors, function(name, errors) {            \n" + 
							"										$.each( errors, function(index, error) {       \n" + 
							"											$(\"#visible_\" + name).addClass(\"error\");       \n" + 
							"											$(\"<li/>\", {html: $.trim($(\"[for='visible_\" + name + \"']\").html()) + \" \" + error}).appendTo(\"#error_list\");        \n" + 
							"											hasErrors = true;  \n" + 
							"										});    \n" + 
							"									});    \n" + 
							"								}    \n" + 
							"								if (data.errors.formErrors != null) {    \n" + 
							"									$.each( data.errors.formErrors, function(index, error) {       \n" + 
							"										$(\"<li/>\", {html: error}).appendTo(\"#error_list\");        \n" + 
							"										hasErrors = true;  \n" + 
							"									});    \n" + 
							"								}    \n" + 
							"								if (hasErrors) {  \n" + 
							"									$(\"#background_overlay_wait_form\").hide();        \n" + 
							"	    							$(\"#message_box_wait_form\").hide();        \n" + 
							"									window.scrollTo(0, 0);     \n" + 
							"								} else { \n" + 
							"									${redirect_js} \n" + 
							"								}  \n" + 
							"						});                  \n" + 
							"					});                  \n" + 
							"				});        \n")
							.replace("${redirect_js}", !"".equals((String) getAttribute(TagGenerator.Attribute.REDIRECT))? "window.location.replace('" + (String) getAttribute(TagGenerator.Attribute.REDIRECT) + "');" : "location.reload();")
							.replace("${service}", (String) getAttribute(TagGenerator.Attribute.SERVICE))
		);
		
		// при наличии множества форм (страниц) на интерфейсе добавляем кнопку "Назад"
		if ((Integer) getParrent().getAttribute(TagGenerator.Attribute.FORM_COUNT) > 1) {
			buttonsRow.getChildren().get(0).add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.ID, "api_back_button");
			     put(Tag.Property.NAME, "api_back_button");
			     put(Tag.Property.TYPE, "button");
			     put(Tag.Property.VALUE, "« Назад");
			     put(Tag.Property.CLASS, "submit_button");
			     put(Tag.Property.CLICK, "$(\"#api_action\").val(\"back\");");
			}});
		}
		
		// Обработка связных списков формы (страницы)
		for(String currentName : connectedElements.keySet()) {
			TagGenerator currentGenerator = connectedElements.get(currentName);
			
			Widget currentWidget = (Widget) currentGenerator.getAttribute(TagGenerator.Attribute.WIDGET);
			currentGenerator.getDom().add(Tag.Type.SCRIPT, currentWidget.getInitJS());			
			
			// Пишем функционал связи влияющий на видимость элемента 
			addConectionJS("ct_ajax_visible", currentGenerator, currentName, TagGenerator.Attribute.AJAX_VISIBLE_PARRENT, new Handler(){
				@Override
				public void handle(TagGenerator parrentGenerator) {
					// Пишем функционал связи влияющий на видимость элемента 
					currentGenerator.getDom().add(Tag.Type.SCRIPT, currentWidget.getVisibleConnectJS(currentGenerator, parrentGenerator));
				}
			});

			// Пишем функционал связи влияющий на доступность редактирования элемента 
			addConectionJS("ct_ajax_active", currentGenerator, currentName, TagGenerator.Attribute.AJAX_ACTIVE_PARRENT, new Handler(){
				@Override
				public void handle(TagGenerator parrentGenerator) {
					// Пишем функционал связи влияющий на видимость элемента 
					currentGenerator.getDom().add(Tag.Type.SCRIPT, currentWidget.getActiveConnectJS(currentGenerator, parrentGenerator));
				}
			});

			// Пишем функционал связи влияющий на содержание элемента 
			addConectionJS("ct_ajax_value", currentGenerator, currentName, TagGenerator.Attribute.AJAX_VALUE_PARRENT, new Handler(){
				@Override
				public void handle(TagGenerator parrentGenerator) {
					// Пишем функционал связи влияющий на содержание элемента 
					currentGenerator.getDom().add(Tag.Type.SCRIPT, currentWidget.getValueConnectJS(currentGenerator, parrentGenerator));
				}
			});

			// Пишем функционал связи влияющий на состав списочного элемента 
			addConectionJS("ct_ajax_list", currentGenerator, currentName, TagGenerator.Attribute.AJAX_LIST_PARRENT, new Handler(){
				@Override
				public void handle(TagGenerator parrentGenerator) {
					// Пишем функционал связи влияющий на содержание элемента 
					currentGenerator.getDom().add(Tag.Type.SCRIPT, currentWidget.getListConnectJS(currentGenerator, parrentGenerator));
				}
			});

		}
		
		// Собираем мультигруппы после обработки связных параметров
		for (TagGenerator multiplieGroupGenerator: multiplieGroups) {
			Tag templateParrentGroup = (Tag) multiplieGroupGenerator.getAttribute(TagGenerator.Attribute.TEMPLATE);

			byte[] encodedHTML = Base64.encodeBase64(templateParrentGroup.getHTML().getBytes());
			byte[] encodedJS = Base64.encodeBase64(templateParrentGroup.getJS().getBytes());

			multiplieGroupGenerator.getDom(Name.SCRIPT, multiplieGroupGenerator.attributes).add(Tag.Type.SCRIPT,( 
					"					var tag_${multiplie_group_name} = '${encoded_tag}';  \n" + 
					"					var script_${multiplie_group_name} = '${encoded_script}';  \n" + 
					"					var count_${multiplie_group_name} = 0;  \n" + 
					"					var number_${multiplie_group_name} = 0;  \n" + 
					"					var DMap = {0: 0, 1: 1, 2: 2, 3: 3, 4: 4, 5: 5, 6: 6, 7: 7, 8: 8, 9: 9, 10: 10, 11: 11, 12: 12, 13: 13, 14: 14, 15: 15, 16: 16, 17: 17, 18: 18, 19: 19, 20: 20, 21: 21, 22: 22, 23: 23, 24: 24, 25: 25, 26: 26, 27: 27, 28: 28, 29: 29, 30: 30, 31: 31, 32: 32, 33: 33, 34: 34, 35: 35, 36: 36, 37: 37, 38: 38, 39: 39, 40: 40, 41: 41, 42: 42, 43: 43, 44: 44, 45: 45, 46: 46, 47: 47, 48: 48, 49: 49, 50: 50, 51: 51, 52: 52, 53: 53, 54: 54, 55: 55, 56: 56, 57: 57, 58: 58, 59: 59, 60: 60, 61: 61, 62: 62, 63: 63, 64: 64, 65: 65, 66: 66, 67: 67, 68: 68, 69: 69, 70: 70, 71: 71, 72: 72, 73: 73, 74: 74, 75: 75, 76: 76, 77: 77, 78: 78, 79: 79, 80: 80, 81: 81, 82: 82, 83: 83, 84: 84, 85: 85, 86: 86, 87: 87, 88: 88, 89: 89, 90: 90, 91: 91, 92: 92, 93: 93, 94: 94, 95: 95, 96: 96, 97: 97, 98: 98, 99: 99, 100: 100, 101: 101, 102: 102, 103: 103, 104: 104, 105: 105, 106: 106, 107: 107, 108: 108, 109: 109, 110: 110, 111: 111, 112: 112, 113: 113, 114: 114, 115: 115, 116: 116, 117: 117, 118: 118, 119: 119, 120: 120, 121: 121, 122: 122, 123: 123, 124: 124, 125: 125, 126: 126, 127: 127, 1027: 129, 8225: 135, 1046: 198, 8222: 132, 1047: 199, 1168: 165, 1048: 200, 1113: 154, 1049: 201, 1045: 197, 1050: 202, 1028: 170, 160: 160, 1040: 192, 1051: 203, 164: 164, 166: 166, 167: 167, 169: 169, 171: 171, 172: 172, 173: 173, 174: 174, 1053: 205, 176: 176, 177: 177, 1114: 156, 181: 181, 182: 182, 183: 183, 8221: 148, 187: 187, 1029: 189, 1056: 208, 1057: 209, 1058: 210, 8364: 136, 1112: 188, 1115: 158, 1059: 211, 1060: 212, 1030: 178, 1061: 213, 1062: 214, 1063: 215, 1116: 157, 1064: 216, 1065: 217, 1031: 175, 1066: 218, 1067: 219, 1068: 220, 1069: 221, 1070: 222, 1032: 163, 8226: 149, 1071: 223, 1072: 224, 8482: 153, 1073: 225, 8240: 137, 1118: 162, 1074: 226, 1110: 179, 8230: 133, 1075: 227, 1033: 138, 1076: 228, 1077: 229, 8211: 150, 1078: 230, 1119: 159, 1079: 231, 1042: 194, 1080: 232, 1034: 140, 1025: 168, 1081: 233, 1082: 234, 8212: 151, 1083: 235, 1169: 180, 1084: 236, 1052: 204, 1085: 237, 1035: 142, 1086: 238, 1087: 239, 1088: 240, 1089: 241, 1090: 242, 1036: 141, 1041: 193, 1091: 243, 1092: 244, 8224: 134, 1093: 245, 8470: 185, 1094: 246, 1054: 206, 1095: 247, 1096: 248, 8249: 139, 1097: 249, 1098: 250, 1044: 196, 1099: 251, 1111: 191, 1055: 207, 1100: 252, 1038: 161, 8220: 147, 1101: 253, 8250: 155, 1102: 254, 8216: 145, 1103: 255, 1043: 195, 1105: 184, 1039: 143, 1026: 128, 1106: 144, 8218: 130, 1107: 131, 8217: 146, 1108: 186, 1109: 190}  \n" + 
					"					var Base64={_keyStr:\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=\",decode:function(e){  \n" + 
					"						var t=\"\";  \n" + 
					"						var n,r,i;  \n" + 
					"						var s,o,u,a;  \n" + 
					"						var f=0;  \n" + 
					"						e=e.replace(/[^A-Za-z0-9+/=]/g,\"\");  \n" + 
					"						while(f<e.length){  \n" + 
					"							s=this._keyStr.indexOf(e.charAt(f++));  \n" + 
					"							o=this._keyStr.indexOf(e.charAt(f++));  \n" + 
					"							u=this._keyStr.indexOf(e.charAt(f++));  \n" + 
					"							a=this._keyStr.indexOf(e.charAt(f++));  \n" + 
					"							n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);  \n" + 
					"							if(u!=64){  \n" + 
					"								t=t+String.fromCharCode(r)  \n" + 
					"							}  \n" + 
					"							if(a!=64){  \n" + 
					"								t=t+String.fromCharCode(i)  \n" + 
					"							}  \n" + 
					"						}  \n" + 
					"						t=Base64._utf8_decode(t);  \n" + 
					"						return t  \n" + 
					"					},_utf8_decode:function(e){  \n" + 
					"						var t=\"\";  \n" + 
					"						var n=0;  \n" + 
					"						var r=c1=c2=0;  \n" + 
					"						while(n<e.length){  \n" + 
					"							r=e.charCodeAt(n);  \n" + 
					"							if (r == 184){  \n" + 
					"								t+=String.fromCharCode(1105);  \n" + 
					"							} else if (r == 168){  \n" + 
					"								t+=String.fromCharCode(1025);  \n" + 
					"							} else if (r > 191 && r < 256){  \n" + 
					"								t+=String.fromCharCode(r + 848);  \n" + 
					"							} else {  \n" + 
					"								t+=String.fromCharCode(r);  \n" + 
					"							}  \n" + 
					"							n++  \n" + 
					"						}  \n" + 
					"						return t  \n" + 
					"					}}  \n" + 
			
					"					  \n" + 
					"					$(\"#button_add_${multiplie_group_name}\").click(function(){  \n" + 
					"						$(\"#background_overlay_wait_${multiplie_group_name}\").show();  \n" + 
					"						$(\"#message_box_wait_${multiplie_group_name}\").show();  \n" + 
					"						  \n" + 
					"						setTimeout(function( x ) {  \n" + 
					"							var template_tag = Base64.decode(tag_${multiplie_group_name}).replace(/<NUMBER>/g, \":g:${multiplie_group_name}_\" + number_${multiplie_group_name});  \n" + 
					"							var template_script = Base64.decode(script_${multiplie_group_name}).replace(/<NUMBER>/g, \":g:${multiplie_group_name}_\" + number_${multiplie_group_name});  \n" + 
			
					"							$(\"#place_${multiplie_group_name}\").append(template_tag);  \n" + 
					"						  \n" + 
					"							number_${multiplie_group_name}++;  \n" + 
					"							count_${multiplie_group_name}++;  \n" + 
					"							if (jQuery.isFunction($(\"#place_${multiplie_group_name}\").find('input').styler)) {  \n" + 
					"								$(\"#place_${multiplie_group_name}\").find('input').styler({});  \n" + 
					"							}  \n" + 
					"							eval(template_script);" +
	//TODO Добавить ограничение количества добавляемых групп
					"							$( \"#place_${multiplie_group_name}\" ).trigger( \"add\" );" + 
	//TODO Во всех XML следует переписать события при добавлении группы на тригеры
					"							$(\"#background_overlay_wait_${multiplie_group_name}\").hide(); \n" + 
					"    	       				$(\"#message_box_wait_${multiplie_group_name}\").hide(); \n" + 
					"						},100);\r\n" + 
					"					});"
					).replace("${multiplie_group_name}", (String) multiplieGroupGenerator.getAttribute(TagGenerator.Attribute.ID))
					.replace("${encoded_tag}", new String(encodedHTML))
					.replace("${encoded_script}", new String(encodedJS))
				);
		}

		// Добавляем скрипт загрузки данных формы (страницы)

		// Получаем строку адреса и разбиваем ее на параметры, далее отправляем rest запрос 
		// с указанными параметрами по указанному в XML параметре service
		// после, получаем данные и заполняем ими форму (страницу)
		String serviceCallJS = "";
		for (String formApi: formInterfaceApiMap.keySet()) {
			
			// Если у группы есть родительские (которые должны загрузиться первыми) то вызываем их по событияа окончания загрузки родительских
			if ( !"".equals(formInterfaceApiMap.get(formApi))) {
				serviceCallJS += "	$('#form_id').bind('${parrent_api}_group_loaded', function(){ \n"
						.replace("${parrent_api}", formInterfaceApiMap.get(formApi));
			}
				
			serviceCallJS += 
																				("				formsWaitedToLoad++;                 \n" + 
				"				params['form_api'] = \"${api}\";                 \n" + 
				"				getJSON( \"${service}\" + \"get\", params, function( data ) {         \n" + 
				"					if (data.status_code === 1) {                 \n" + 
				"						$.each(data.groups, function(key, value) {                 \n" + 
				"							$(\"#button_add_\" + key).click();                 \n" + 
				"						});                \n" + 
				"						$.each(data.parameters, function(key, parameter) {        \n" + 
				"							if (parameter.attributes.READONLY) {                 \n" + 
				"				 				$('#visible_' + key).prop( \"disabled\", true);            \n" + 
				"					 			$(\"#tr_\" + key).css('color', 'lightgray');            \n" + 
				"							};           \n" + 
				"							if (parameter.attributes.INVISIBLE) {                 \n" + 
				"					 			$(\"#tr_\" + key).css('display', 'none');            \n" + 
				"							};           \n" + 
				"						});                \n" + 
				"						$.each(data.parameters, function(key, parameter) {                 \n" + 
				"							var valueArray = parameter.value.split(':i:');                \n" + 
				"							$(\"#\" + key).val(valueArray[0]);                 \n" + 
				"						});                \n" + 
				"						$.each(data.parameters, function(key, parameter) {                 \n" + 
				"							var valueArray = parameter.value.split(':i:');                \n" + 
				"							$(\"#\" + key).trigger('setValueOnLoad',[{value:valueArray[0], name:(valueArray.length > 1 ? valueArray[1] : '')}]);        \n" + 
				"						});         \n" + 
				"									if (data.errors.parametersErrors != null) {        \n" + 
				"									$.each(data.errors.parametersErrors, function(name, errors) {                \n" + 
				"										$.each( errors, function(index, error) {           \n" + 
				"											$(\"#visible_\" + name).addClass(\"error\");           \n" + 
				"											$(\"<li/>\", {html: $.trim($(\"[for='visible_\" + name + \"']\").html()) + \" \" + error}).appendTo(\"#error_list\");            \n" + 
				"											hasErrors = true;      \n" + 
				"										});        \n" + 
				"									});        \n" + 
				"								}        \n" + 
				"								if (data.errors.formErrors != null) {        \n" + 
				"									$.each( data.errors.formErrors, function(index, error) {           \n" + 
				"										$(\"<li/>\", {html: error}).appendTo(\"#error_list\");            \n" + 
				"										hasErrors = true;      \n" + 
				"									});        \n" + 
				"								}        \n" + 
				"					}         \n" + 
				"					formsWaitedToLoad--;                 \n" + 
				"					if (formsWaitedToLoad === 0) {                 \n" + 
				"						$(\"#background_overlay_wait_form\").hide();                 \n" + 
				"    						$(\"#message_box_wait_form\").hide();                 \n" + 
				"						$( document ).trigger('setListOnLoad');               \n" + 
				"					}					                 \n"+
				"					$('#form_id').trigger('${api}_group_loaded');  \n"+
				"				});               \n")
				.replace("${service}", (String) getParrent().getAttribute(TagGenerator.Attribute.SERVICE))				
				.replace("${api}",  formApi);

			

			if (!"".equals(formInterfaceApiMap.get(formApi))) {
				serviceCallJS += "	}); \n";
			}
		}
		dom.add(Tag.Type.SCRIPT,		("		var params = window    \n" + 
			"		    	.location    \n" + 
			"		    	.search    \n" + 
			"		    	.replace('?','')    \n" + 
			"		    	.split('&')    \n" + 
			"		    	.reduce(    \n" + 
			"		    			function(p,e){    \n" + 
			"		    				var a = e.split('=');    \n" + 
			"		    				p[ decodeURIComponent(a[0])] = decodeURIComponent(a[1]);    \n" + 
			"		    				return p;    \n" + 
			"		    			},    \n" + 
			"		        {}    \n" + 
			"		    );    \n" + 
			" 		var formsWaitedToLoad = 0;    \n" + 
			"		$( document ).ready(function() {    \n" + 
			"				$(\"#background_overlay_wait_form\").show();    \n" + 
			"				$(\"#message_box_wait_form\").show();    \n" + 
			"				params['no_cache'] = Math.floor(Math.random() * 10000);                 \n" + 
			"				getJSON( \"${service}\" + \"check\", params, function( data ) {});  \n" + 
			"				${service_call_js}		});    \n")
			.replace("${service}",  ((String) getParrent().getAttribute(TagGenerator.Attribute.SERVICE)).replaceAll("/\\w+/$", "/seseion/"))
			.replace("${service_call_js}", serviceCallJS)				
		);


	}

	  /**
	   * Метод получения атрибута текущего тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Содержимое атрибута
	   */
	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) {
		switch (attributeName) {
		case API:
			return !"".equals(super.getAttribute(TagGenerator.Attribute.API)) 
					? super.getAttribute(TagGenerator.Attribute.API)
					: this.getParrent().getAttribute(TagGenerator.Attribute.API);
		case SERVICE:
			return this.getParrent().getAttribute(TagGenerator.Attribute.SERVICE);
		default:
			return super.getAttribute(attributeName);
		}
	}
}
