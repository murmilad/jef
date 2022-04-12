package com.technology.jef.generators;

import static com.technology.jef.server.serialize.SerializeConstant.GROUP_SEPARATOR;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator.Name;

/**
* Класс сборщика DOM модели уровня предпросмотра группы
*/
public class PreviewGenerator extends TagGenerator {

	/**
	   * Метод формирования DOM модели на текущем уровне
	   * 
	   * @param qName имя тега в XML представлении интерфейса
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
	@Override
	public Tag generate(String qName) throws SAXException {

		String groupName = "true".equals(getAttribute(TagGenerator.Attribute.IS_MULTIPLIE))
				? "<NUMBER>"
				: (Name.GROUP.equals(getParrent().getName()) && "true".equals(getParrent().getAttribute(TagGenerator.Attribute.IS_MULTIPLIE)))
					 ? "<NUMBER>" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.ID)
					 : (String) getAttribute(TagGenerator.Attribute.ID);

		Tag previewTag = dom.getParrent().add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "div_preview" + groupName);
		     put(Tag.Property.CLASS, "interface_expand");
		     put(Tag.Property.STYLE, "display: none;");
		}});
		
		return previewTag;
	}

	@Override
	public void onEndElement() throws SAXException  {
		String groupName = "true".equals(getAttribute(TagGenerator.Attribute.IS_MULTIPLIE))
				? "<NUMBER>"
				: (Name.GROUP.equals(getParrent().getName()) && "true".equals(getParrent().getAttribute(TagGenerator.Attribute.IS_MULTIPLIE)))
					 ? "<NUMBER>" + GROUP_SEPARATOR + getAttribute(TagGenerator.Attribute.ID)
					 : (String) getAttribute(TagGenerator.Attribute.ID);

		dom.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
		     put(Tag.Property.ID, "div_preview_groups" + groupName);
		     put(Tag.Property.CLASS, "div_interface_element_horizontal interface_element_horizontal"); 
		     put(Tag.Property.STYLE, "position:relative;width:100%");
		     
		}});

		dom.add(Tag.Type.SCRIPT, 
				("	$( document ).ready(function() {  \n" + 
				"		if (window.isFormLoading) {$('#span_${name}').click();}   \n" + 
				"		$('#span_${name}').on('refreshGroupPreview', function(){   \n" + 
				"			$('#div_preview_groups${name}').empty();\n" + 
				"			$( '[id^=\"button_add_${name}${group_separator}\"]').each(function(index) { \n" + 
				"		   		var groupName = $( this ).attr('id').split('${group_separator}').pop();\n" + 
				"				$('<div />', {    \n" + 
				"				    'class': 'widgets_header interface_element_body',    \n" + 
				"				    'style': 'display: inline-block; width: 100%;',    \n" + 
				"				    'id': 'preview_group${name}${group_separator}' + groupName,  \n" + 
				"				}).appendTo('#div_preview_groups${name}');  \n" + 

				"				$('#place_${name}${group_separator}' + groupName ).find('[id^=\"span_${name}${group_separator}'+groupName+'\"]').each(function(index) { \n" + 
				"					var item = $( this ); \n " +
				"					$('<div />', {    \n" + 
				"				    	'class': 'third_text_color interface_expand',    \n" + 
				"				    	'style': 'float:left; margin: 2px 5px 2px 5px;',    \n" + 
				"				    	'id': 'preview_group_item${name}${group_separator}' + groupName,  \n" + 
				"				    	'html': item.html(),  \n" + 
				"					}).on('click', function(){  \n" + 
				"						setTimeout(function( x ) { \n" + 
				"							item.click(); \n" + 
				"						}, 300); \n" + 
				"					}).appendTo('#preview_group${name}${group_separator}' + groupName);  \n" + 
				"					\n" + 
				"				});   \n" + 
				"			});   \n" + 
				"		});   \n" + 
				"	}); \n")
				.replace("${name}", groupName)
				.replace("${group_separator}", GROUP_SEPARATOR)
		);

	}
	
	@Override
	public Object getAttribute(TagGenerator.Attribute attributeName) throws SAXException {
		switch (attributeName) {
		case PREFIX:
			return (String) getParrent().getAttribute(TagGenerator.Attribute.PREFIX);
		case IS_MULTIPLIE:
			return (String) getParrent().getAttribute(TagGenerator.Attribute.IS_MULTIPLIE);
		case ID:
			return (String) getParrent().getAttribute(TagGenerator.Attribute.ID);
		default:
			return super.getAttribute(attributeName);
		}
		
	}

	@Override
	public Name getName() {
		return Name.PREVIEW;
	}

}
