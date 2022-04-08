package com.technology.jef.generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator.Name;
import com.technology.jef.widgets.Text;
import com.technology.jef.widgets.Widget;

import static com.technology.jef.server.serialize.SerializeConstant.*;
import static com.technology.jef.server.WebServiceConstant.*;
/**
* Класс сборщика DOM модели уровня группы
*/
public class GroupGenerator extends TagGenerator {

	private Boolean isMultiplie = false;
	private Tag multilineParrentDOM;
	private Tag templateParrentGroup;
	private List<String> formItems = new LinkedList<String>();
	
	  /**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {
		Tag group = null;
		String groupName = "true".equals(getAttribute(TagGenerator.Attribute.IS_MULTIPLIE))
				? "<NUMBER>"
				: (Name.GROUP.equals(getParrent().getName()) && "true".equals(getParrent().getAttribute(TagGenerator.Attribute.IS_MULTIPLIE)))
					 ? "<NUMBER>" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.ID)
					 : (String) getAttribute(TagGenerator.Attribute.ID);

		isMultiplie = "true".equals(getAttribute(TagGenerator.Attribute.IS_MULTIPLIE));
		if (isMultiplie) {
			isMultiplie = true;

			// То что не должно пойти в шаблон
			multilineParrentDOM = dom.add(Tag.Type.DIV,new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.STYLE, "display: block;");
			}}).add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			     put(Tag.Property.ID, "place_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "")+ GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
			     put(Tag.Property.NAME, "place_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "")+ GROUP_SEPARATOR +getAttribute(TagGenerator.Attribute.API));
			}});
			
			// Формируем шаблон для мультигрупп и возвращаем его для дочерних генераторов
			// Что бы дочерние элементы группы добавлялись имменно в шаблон
			templateParrentGroup = new Tag(Tag.Type.DIV);
			group = addFormGroup(templateParrentGroup, groupName, (String) getAttribute(TagGenerator.Attribute.NAME));

			addHandler(TagGenerator.Name.FORM_ITEM, new Handler() {

				@Override
				public void handle(TagGenerator currentGenerator) throws SAXException {
					formItems.add((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				}
				
			});
			
			addHandler(TagGenerator.Name.SCRIPT, new Handler() {

				@Override
				public void handle(TagGenerator currentGenerator) throws SAXException {
				//  Убираем Propagation для событий добавления и удаления из группы поскольку 
				// если группы вложенны друг в друга то вызываются события 
				// добавления/удаления родительской группы
					if ("add".equals(currentGenerator.getAttribute(TagGenerator.Attribute.TYPE)) 
							|| "delete".equals(currentGenerator.getAttribute(TagGenerator.Attribute.TYPE))){
						currentGenerator.getDom().setBody(
								(
								"${body} \n" +
								"	if (event.target.getAttribute('id') !== 'div_<NUMBER>') { return; }\n "+
								"	event.stopPropagation(); \n"
								)
								.replace("${body}", currentGenerator.getDom().getBody())
								.replace("${current_item_id}", (String)currentGenerator.getParrent().getAttribute(TagGenerator.Attribute.ID))
								);
					}
					formItems.add((String) currentGenerator.getAttribute(TagGenerator.Attribute.ID));
				}
				
			});
//TODO			load params with prefix
		} else {
			group = addFormGroup(dom, groupName, (String) getAttribute(TagGenerator.Attribute.NAME));
		}
		
		return group;
	}
	
	  /**
	   * Метод добавляет тело формы  в DOM модель.
	   * 
	   * @param parrent текущий адрес в DOM модели
	   * @param name уникальное имя группы
	   * @param caption заголовок группы
	   * @return тег группы
	   */
	private Tag addFormGroup(Tag parrent, String name, String caption) {

		Tag tagFieldset = parrent.add(Tag.Type.FIELDSET, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.ID, "fildset_" + name);
			 put(Tag.Property.NAME, "fildset_" + name);
			 put(Tag.Property.CLASS, "fieldset second_frames_border");
			 put(Tag.Property.STYLE, "display: block; margin: 5px;");
		}});
		
		Tag tagCaption = tagFieldset.add(Tag.Type.LEGEND).add(Tag.Type.LABEL, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.FOR, "fildset_" + name);
			 put(Tag.Property.CLASS, "widgets_label_color");
		}});
		
		tagCaption.add(Tag.Type.SPAN, "-", new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "span_control_" + name);
			 put(Tag.Property.ID, "span_control_" + name);
			 put(Tag.Property.CLASS, "interface_expand");
		}});

		tagCaption.add(Tag.Type.SCRIPT,(" \n" + 
		"	var control_${name}_visible = true; \n" +
		"	$(\"#div_preview${name}, #span_${name}, #span_control_${name}\").click(function(){ \n" + 
		"		if (control_${name}_visible) { \n" + 
		"			$(\"#span_control_${name}\").html(\"+\"); \n" + 
		"			$(\"#div_${name}\").hide();\n" +
		"			$(\"#div_preview${name}\").show();\n" +
		"		} else { \n" + 
		"			$(\"#span_control_${name}\").html(\"-\"); \n" + 
		"			$(\"#div_preview${name}\").hide();\n" +
		"			$(\"#div_${name}\").show();\n" +
		"		} \n" + 
//		"		setTimeout(function( x ) {            \n" + 
		"			$(document.body).animate({ \n" + 
		"				'scrollTop':   ($('#span_${name}').offset().top - 100) \n" + 
		"			}, 200); \n" + 
//		"		}, 100); \n" + 
		"		control_${name}_visible = !control_${name}_visible; \n" + 
		"	}); \n"+
//		"	if ($(\"#div_${name}\").find(\"[id^='div_preview_'][id$='${name}']\") ) {\n" +
//		"		$('#span_${name}').click(); \n " +
//		"	} \n" +
		"").replace("${name}", name)
		);
		

		tagCaption.add(Tag.Type.SPAN, caption, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "span_" + name);
			 put(Tag.Property.ID, "span_" + name);
			 put(Tag.Property.CLASS, "third_text_color interface_expand");
			 
		}});


		tagFieldset = tagFieldset.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
			 put(Tag.Property.NAME, "div_" + name);
			 put(Tag.Property.ID, "div_" + name);
		}});
		

		return tagFieldset;
	}

	  /**
	   * Метод завершающей обработки DOM модели после прохода текущего тега в XML представлении интерфейса
	 * @throws SAXException 
	   * 
	   */
	@Override
	public void onEndElement() throws SAXException {
		// если группа у нас расширяемая, то добавляем соответсвующие элементы правления
		if (isMultiplie) {
			Tag buttonDel = dom.add(Tag.Type.DIV,
				new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.STYLE, "width: 100%; display: inline-block;");
					 put(Tag.Property.CLASS, "div_interface_element_horizontal interface_element_horizontal");
				}}).add(Tag.Type.DIV,
						new HashMap<Tag.Property, String>(){{
						put(Tag.Property.STYLE, "position:relative; width: 100%;text-align: left; display: block;");
				}});
			buttonDel.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_button_del_<NUMBER>");
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_loading background_color");
				}});
			buttonDel.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					put(Tag.Property.NAME, "button_del_<NUMBER>");
					put(Tag.Property.ID, "button_del_<NUMBER>");
					put(Tag.Property.CLASS, "interface_del_button buttons_color buttons_height");
					put(Tag.Property.TYPE, "button");
					put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("delete") + " " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll(CurrentLocale.getInstance().getTextSource().getString("multi_prefix") + "$", "").toLowerCase());
				}});
			
			dom.getParrent().add(Tag.Type.SCRIPT, 	(" \n" + 
	"	$(\"#button_del_<NUMBER>\").click(function(){  \n" + 
	"		$( '#div_<NUMBER>').trigger( 'delete' );           \n" +
	"		$('#action<NUMBER>').val('${action}'); \n" + 
	"		$('#group_bar').find(\"[name^='fildset_<NUMBER>']\").remove(); \n" +
	"		$(\"#fildset_<NUMBER>\").remove();  \n" + 
	"			$(\"<input/>\", {    \n" + 
	"				'value': '${api}',     \n" + 
	"				'type': 'hidden',     \n" + 
	"				'id': '${system_prefix}_api_group_id<NUMBER>',     \n" + 
	"				'name': '${system_prefix}_api_group_id<NUMBER>',     \n" + 
	"			}).appendTo( \"#place_${parrent_group_name}\" ); \n" + 
	"			$(\"<input/>\", {    \n" + 
	"				'value': '${parrent_api}',     \n" + 
	"				'type': 'hidden',     \n" + 
	"				'id': '${system_prefix}_parrent_api_group_id<NUMBER>',     \n" + 
	"				'name': '${system_prefix}_parrent_api_group_id<NUMBER>',     \n" + 
	"			}).appendTo( \"#place_${parrent_group_name}\" ); \n" + 
	"		count_multiplie_group['${parrent_group_name}${multiplie_group_name}']--; \n" +
	"						var parameters = ${value_js};  \n" + 
	"						if (!count_multiplie_group[parrentGroupPrefix + '${multiplie_group_name}']) {count_multiplie_group[parrentGroupPrefix + '${multiplie_group_name}'] = 0;}    \n" + 
	"						parameters += (parameters ? '${parameter_separator}' : '') + 'group_count${value_separator}' + count_multiplie_group['${parrent_group_name}'];  \n" + 
	"						parameters += parrentGroupPrefix ? '${parameter_separator}super_group_id${value_separator}' + $('#group_id' + parrentGroupPrefix).val() : '';     \n" + 
	"						$('#id_add_joined_group_${joined_by}').val(''); \n " + 
	"						$('#name_add_joined_group_${joined_by}').val(''); \n " + 
	"						setButtonVisiblity${multiplie_group_name}('button_add', '${parrent_group_name}', parameters);   \n" + 
	"						setButtonVisiblity${multiplie_group_name}('button_del', '${parrent_group_name}', parameters);   \n" + 
	"}); \n")
	.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
	.replace("${parameter_separator}", PARAMETER_SEPARATOR)
	.replace("${joined_by}", (String) getAttribute(TagGenerator.Attribute.JOINED_BY))
	.replace("${value_js}", new Text().getValueJS(null, "", null))
	.replace("${multiplie_group_name}", GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API))
	.replace("${parrent_group_name}", (Name.GROUP.equals(getParrent().getName()) ? "<GROUP_NUMBER>" : "") + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API))
	.replace("${api}", (String) getAttribute(TagGenerator.Attribute.API))
	.replace("${parrent_api}", (String) getAttribute(TagGenerator.Attribute.PARRENT_API))
	.replace("${action}", ACTION_DELETE)
	.replace("${system_prefix}", SYSTEM_PARAMETER_PREFIX)

	
			) ;
			dom.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "group_action_<NUMBER>");
				 put(Tag.Property.ID, "group_action_<NUMBER>");
				 put(Tag.Property.CLASS, "interface_del_button buttons_color buttons_height");
				 put(Tag.Property.TYPE, "hidden");
				 put(Tag.Property.VALUE, "create");
			}});

			multilineParrentDOM.getParrent().add(Tag.Type.SCRIPT, 		("  \n" + 
					"					number_multiplie_group['${parrent_group_name}${multiplie_group_name}'] = 0;            \n" + 
					"					count_multiplie_group['${parrent_group_name}${multiplie_group_name}'] = 0;            \n" + 
					"						$('#form_id').bind('setListOnLoad_${parrent_group_api}${parrent_group_name}', function() {        \n" + 

					"							var parameters = ${value_js};     \n" + 
					"							parameters += (parameters ? '${parameter_separator}' : '') + 'group_count${value_separator}' + count_multiplie_group['${parrent_group_name}${multiplie_group_name}'];     \n" + 
					"							parameters += '${parrent_group_name}' ? '${parameter_separator}super_group_id${value_separator}' + $('#group_id${parrent_group_name}').val() : '';     \n" + 
					"							setButtonVisiblity${multiplie_group_name}('button_add', '${parrent_group_name}${multiplie_group_name}', parameters);      \n" + 
					"						});        \n" + 

					"					$(\"#button_add_${parrent_group_name}${multiplie_group_name}\").click(function(){           \n" + 
					"						setTimeout(function( x ) {           \n" + 
					"							var prefix = add_${multiplie_group_name}(\"#${group_plase}\", '${parrent_group_name}');           \n" + 
					"							window.isFormLoading = true;             \n" + 
					"							load_form_data_${group_api}(groupInitialParams${group_api}, prefix, '${parrent_group_name}');             \n" + 
					"							window.isFormLoading = false;             \n" + 
					"							$( '#div_' + prefix ).trigger( 'add' );           \n" + 
					"							$( '#form_id' ).trigger('setListOnLoad_${group_api}'+prefix);                               \n" + 
					"							$( '#form_id' ).trigger('setListOnLoad_${group_api}', [prefix]); \n " + 
					"							$(document.body).animate({ \n" + 
					"						    	'scrollTop':   ($('#span_' + prefix).offset().top - 100) \n" + 
					"							}, 200); \n" + 
					"						}, 100);          \n" + 
					"					});           \n")
					.replace("${multiplie_group_name}", GROUP_SEPARATOR + (String) getAttribute(TagGenerator.Attribute.API))
					.replace("${parrent_group_name}", (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : ""))
					.replace("${group_plase}", !"".equals(getAttribute(TagGenerator.Attribute.JOINED_BY))
						? "place_joined_group_" + getAttribute(TagGenerator.Attribute.JOINED_BY)
						: "place_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "") + GROUP_SEPARATOR + (String) getAttribute(TagGenerator.Attribute.API))
					.replace("${group_api}", (String) getAttribute(TagGenerator.Attribute.API))
					.replace("${parrent_group_api}", Name.GROUP.equals(getParrent().getName()) ? (String) getParrent().getAttribute(TagGenerator.Attribute.API) : "")
					.replace("${value_separator}", PARAMETER_NAME_VALUE_SEPARATOR)
					.replace("${parameter_separator}", PARAMETER_SEPARATOR)
					.replace("${group_separator}", GROUP_SEPARATOR)
					.replace("${value_js}", new Text().getValueJS(null, "", null))
			);
			Tag buttonAdd = multilineParrentDOM.getParrent().add(Tag.Type.DIV,
					new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.STYLE, "width: 100%;");
					}}).add(Tag.Type.DIV,
							new HashMap<Tag.Property, String>(){{
							put(Tag.Property.STYLE, "position:relative; width: 100%;text-align: left; display: block;");
					}});
			buttonAdd.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_button_add_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "")+ GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_loading background_color");
				}}).add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "message_overlay_wait");
				 put(Tag.Property.CLASS, "message_overlay_loading");
				}});

			buttonAdd.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
					 put(Tag.Property.ID, "button_add_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "")+ GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
					 put(Tag.Property.NAME, "button_add_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "")+ GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
					 put(Tag.Property.TYPE, "button");
					 put(Tag.Property.CLASS, "interface_add_button buttons_color buttons_height");
					 put(Tag.Property.STYLE, !"".equals(getAttribute(TagGenerator.Attribute.JOINED_BY)) ? "display: none" : "display: inline-block");
					 put(Tag.Property.VALUE, CurrentLocale.getInstance().getTextSource().getString("add") + " " + ((String) getAttribute(TagGenerator.Attribute.NAME)).replaceAll(CurrentLocale.getInstance().getTextSource().getString("multi_prefix") + "$", "").toLowerCase());
				}});


			
			

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "background_overlay_wait_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "")+ GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "background_overlay_wait");
				 put(Tag.Property.CLASS, "background_overlay_wait background_color");
			}});

			multilineParrentDOM.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "message_box_wait_" + (Name.GROUP.equals(getParrent().getName()) ? "<NUMBER>" : "")+ GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.API));
				 put(Tag.Property.NAME, "message_box_wait");
				 put(Tag.Property.CLASS, "message_box_wait messages_border messages_color");
			}});

		} else {

			dom.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.NAME, "group_action_<NUMBER>");
				 put(Tag.Property.ID, "group_action_<NUMBER>");
				 put(Tag.Property.CLASS, "interface_del_button buttons_color buttons_height");
				 put(Tag.Property.TYPE, "hidden");
			}});
			
		}
	}


	  /**
	   * Метод получения атрибута текущего тега в XML представлении интерфейса
	   * 
	   * @param attributeName имя атрибута тега в XML представлении интерфейса
	   * @return Содержимое атрибута
	 * @throws SAXException 
	   */
	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) throws SAXException {
		switch (attributeName) {
		case API:
			return !"".equals(super.getAttribute(TagGenerator.Attribute.API)) 
					? super.getAttribute(TagGenerator.Attribute.API)
					: this.getParrent().getAttribute(TagGenerator.Attribute.API);
		case PREFIX: // Хотим получить префикс для параметра групповых форм
			return 
					(isMultiplie 
							? "<NUMBER>" 
							: (
								Name.GROUP.equals(this.getParrent().getName()) 
								&& "true".equals(this.getParrent().getAttribute(TagGenerator.Attribute.IS_MULTIPLIE))
							) 
								? "<NUMBER>"
								:""
					);
		case TEMPLATE: // Хотим получить шаблон для мультигруппы
			return templateParrentGroup;
		case SERVICE:
			return this.getParrent().getAttribute(TagGenerator.Attribute.SERVICE);
		case ITEMS:
			return formItems;
		default:
			return super.getAttribute(attributeName);
		}
	}

	@Override
	public Name getName() {
		return Name.GROUP;
	}

}
