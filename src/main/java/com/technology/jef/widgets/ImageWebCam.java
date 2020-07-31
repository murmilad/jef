package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.helpers.AttributesImpl;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;
import com.technology.jef.widgets.Widget.ViewType;

/**
* Виджет картинка
*/
public class ImageWebCam extends Image {

	@Override
	public ViewType getType () {
		return ViewType.DOUBLE;
	}
	@Override
	public String getSetActiveJS() {
		//language=JavaScript
		return 
		"		if (data.value) { \n " + 
		"			$(\"#tr_${child_name}\" ).css('color', 'black'); \n "+
		"			$('#div_header_visible_${child_name}').show(); \n " +
		"			$('#div_submit_data_${child_name}').show(); \n " +
		"		} else { \n " +
		"			$('#div_header_visible_${child_name}').hide(); \n " +
		"			$('#div_submit_data_${child_name}').hide(); \n " +
		"			$(\"#tr_${child_name}\" ).css('color', 'lightgray'); \n " +
		"		} \n ";
	}
	@Override
		public Tag assembleTag(String name, TagGenerator generator) {
			
			
			parrent.add(Tag.Type.IMG, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "img_visible_" + name);
				 if (!"".equals(generator.getAttribute(TagGenerator.Attribute.DEFAULT_IMAGE))) {
					 put(Tag.Property.SRC, (String) generator.getAttribute(TagGenerator.Attribute.DEFAULT_IMAGE));
				 }
				 put(Tag.Property.NAME, "img_visible_" + name);
				 put(Tag.Property.ONERROR, "this.onerror=null; this.src='" + generator.getAttribute(TagGenerator.Attribute.DEFAULT_IMAGE) + "'");
				 put(Tag.Property.STYLE, "max-width: " + generator.getAttribute(TagGenerator.Attribute.WIDTH) +";");
			}});
			// слой для ввывода информации об ошибках в фото
			parrent.add(Tag.Type.SPAN, new HashMap<Tag.Property, String>(){{
				put(Tag.Property.ID, "recognize_message_" + name);
			}});
			// функция вызывается при изменении картики, для вывода результат проверки фото, вызывается колбэком после завершения проверок
			//language=JavaScript
			parrent.add(Tag.Type.SCRIPT,("function checkImg(result){\n" +
					"                var message='';\n" +
					"                if (result.indexOf(-1) != -1){\n" +
					"                    message = \"<span style='background-color:yellow'>Предупреждение: изображение слишком темное</span>\"\n" +
					"                }\n" +
					"                if (result.indexOf(-2)!= -1){\n" +
					"                    message = \"<span style='background-color:yellow'>Предупреждение: изображение слишком светлое</span>\"\n" +
					"                }\n" +
					"                if (result.indexOf(-3)!= -1){\n" +
					"                    message += \"<br><span style='background-color:red;color:white;'>Ошибка: На изображении не обнаружено лицо человека, необходимо сделать новое фото</span>\";\n" +
					"                    $('#visible_${name}').val('');\n" +
					"                     $('#${name}').val('');\n" +
					"                }\n" +
					"                if (result.indexOf(-4)!= -1){\n" +
					"                    message += \"<br><span style='background-color:red;color:white;'>Ошибка: На изображении обнаружено несколько лиц, необходимо сделать новое фото</span>\";\n" +
					"                    $('#visible_${name}').val('');\n" +
					"                    $('#${name}').val('');\n" +
					"                }\n" +
					"                document.getElementById('recognize_message_${name}').innerHTML = message;\n" +
					"            }\n"
			).replace("${name}", name));
			Tag fieldset = parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "div_header_visible_" + name);
				 put(Tag.Property.NAME, "div_header_visible_" + name);
				 put(Tag.Property.STYLE, "display:none; position: relative;");
			}});

			Tag input = fieldset.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "visible_" + name);
				 put(Tag.Property.NAME, "visible_" + name);
				 put(Tag.Property.TYPE, "file");
				 put(Tag.Property.STYLE, "width:100%;");
				 put(Tag.Property.ACCEPT, "image/jpg,image/jpeg,image/gif,image/bmp");
				 put(Tag.Property.DATA_URL, (String) generator.getAttribute(TagGenerator.Attribute.SERVICE) + "image_to_base64");
				 put(Tag.Property.DATA_FILEBROWSETEXT, CurrentLocale.getInstance().getTextSource().getString("file_browse_text"));
				 put(Tag.Property.DATA_FILEPLACEHOLDER, CurrentLocale.getInstance().getTextSource().getString("file_placeholder"));
				 // сделано через эвент выше
				// put(Tag.Property.CHANGE, CurrentLocale.getInstance().getTextSource().getString("file_placeholder"));
			}});

			
			input.add(Tag.Type.FONT, "<br>" + (String) generator.getAttribute(TagGenerator.Attribute.HINT), new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.TYPE, "text");
				 put(Tag.Property.CLASS, "interface_hint");
			}});

			parrent.add(Tag.Type.DIV, new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "div_camera_" + name);
				 put(Tag.Property.NAME, "div_camera_" + name);
			}});

			parrent.add(Tag.Type.DIV, 	(
					"	<script src=\"js/webcam.min.js\"></script> \n" +
					"	<script src=\"js/main.bundle.js\"></script>"+
					"	<img src=\"data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=\" id=\"camera_snapshot_${name}\"><br> \n" + 
					"	<input style=\"width:200px;\"               id=\"init_camera_button_${name}\"   type=\"button\" value=\"Клиент готов. Включить камеру\" onclick=\"init_camera_${name}()\"> \n" + 
					"	<input style=\"width:80px;\"                id=\"not_present_camera_button_${name}\"   type=\"button\" value=\"Нет камеры\" onclick=\"not_present_camera_${name}()\"> \n" + 
					"	<input style=\"display:none; width:300px;\" id=\"take_snapshot_button_${name}\" type=\"button\" value=\"Сделать фото клиента\" onclick=\"take_snapshot_${name}()\"> \n")
					.replace("${name}", name),
				new HashMap<Tag.Property, String>(){{
				 put(Tag.Property.ID, "div_submit_data_" + name);
				 put(Tag.Property.NAME, "div_submit_data_" + name);
			}});
			//language=JavaScript
			parrent.add(Tag.Type.SCRIPT, 	(" \n" + 
	"  var field_name_${name} = '#img_visible_${name}'; \n" + 
	"                        var name_${name} = '#${name}' \n" + 
	"                        var header_name_${name} = '#div_header_visible_${name}'; \n" + 
	"            if (!document.addEventListener) { \n" + 
	"                // IE8 или ниже \n" + 
	"                $(field_name_${name}).show(); // возвращаем превью для старого механизма \n" + 
	"                $(header_name_${name}).show(); // возвращаем старую панель загрузки файла \n" + 
	"                $('#div_camera_${name}').hide(); \n" + 
	"                $('#div_submit_data_${name}').hide(); // скрываем панель запуска камеры \n" + 
	"            }else{ \n" + 
	"                // задание параметров камеры \n" + 
	"                Webcam.set({ \n" + 
	"                    width: 720, \n" + 
	"                    height: 540, \n" + 
	"                    image_format: 'jpeg', \n" + 
	"                    jpeg_quality: 90, \n" + 
	"                    force_flash: false, \n" + 
	"                    flip_horiz: false, \n" + 
	"                    fps: 16, \n" + 
	"                    constraints:{ \n" + 
	"                        width: { ideal: 1280 }, \n" + 
	"                        height: { ideal: 1024 } \n" + 
	"                    } \n" + 
	"                }); \n" + 
	"                Webcam.on( 'error', function(err) { \n" + 
	"                        Webcam.reset(); \n" + 
	"                        $('#div_camera_${name}').hide(); \n" + 
	"                        $('#take_snapshot_button_${name}').hide(); \n" + 
	"                        $('#not_present_camera_button_${name}').hide(); \n" + 
	"                        $('#init_camera_button_${name}').val('Ошибка при работе с камерой'); \n" + 
	"                        $('#init_camera_button_${name}').prop('disabled', true); \n" + 
	"                        $('#init_camera_button_${name}').show(); \n" + 

	"                        $(header_name_${name}).show(); // возвращаем старую панель загрузки файла \n" + 
	"                        $(field_name_${name}).show(); // возвращаем превью для старого механизма \n" + 
	"                        console.log(err); \n" + 

	"                } ); \n" + 
	"                function not_present_camera_${name}() { \n" + 
	"                        $('#not_present_camera_button_${name}').hide(); \n" + 
	"                        $('#div_camera_${name}').hide(); \n" + 
	"                        $('#take_snapshot_button_${name}').hide(); \n" + 
	"                        $('#init_camera_button_${name}').hide(); \n" + 

	"                        $(header_name_${name}).show(); // возвращаем старую панель загрузки файла \n" + 
	"                        $(field_name_${name}).show(); // возвращаем превью для старого механизма \n" + 
	"                } \n" + 
	"                function init_camera_${name}(){ \n" + 
	"                    Webcam.attach( '#div_camera_${name}' ); \n" + 
	"                    $(field_name_${name}).hide(); \n" + 
	"                    $('#camera_snapshot_${name}').attr(\"src\", 'data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs='); // очищаем превью \n" + 
	"                    $('#init_camera_button_${name}').hide(); \n" + 
	"                    $('#take_snapshot_button_${name}').show(); \n" + 
	"                    $(header_name_${name}).hide(); \n" + 
	"                    $('#not_present_camera_button_${name}').hide(); \n" + 
	"                } \n" + 
	"                function take_snapshot_${name}() { \n" + 
	"                    Webcam.snap( function(data_uri) { \n" + 
	"                       $('#camera_snapshot_${name}').attr(\"src\", data_uri); // подменяем картику на фото с камеры \n" + 
	"                       $(name_${name}).val(data_uri);                        // записываем в поле для передачи серверу \n" +
	"                       checkPhoto('img_visible_${name}',checkImg);"+
	"                    } ); \n" + 
	"                    Webcam.reset(); \n" + 

	"                    $('#take_snapshot_button_${name}').hide(); \n" + 
	"                    $('#init_camera_button_${name}').val('Повторить съемку'); \n" + 
	"                    $('#init_camera_button_${name}').show(); \n" + 
	"                    $('#div_camera_${name}').attr('style',''); \n" + 
	"                } \n" + 
	"	} \n")
	.replace("${name}", name));

			return input;
		}
	@Override
	protected Tag postAssembleTag(String name, TagGenerator generator, Tag element) {

		parrent.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
			put(Tag.Property.TYPE, "text/javascript");
			put(Tag.Property.SRC, "js/jquery.ui.widget.js");
		}});

		parrent.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
			put(Tag.Property.TYPE, "text/javascript");
			put(Tag.Property.SRC, "js/jquery.iframe-transport.js");
		}});

		parrent.add(Tag.Type.SCRIPT, new HashMap<Tag.Property, String>(){{
			put(Tag.Property.TYPE, "text/javascript");
			put(Tag.Property.SRC, "js/jquery.fileupload.js");
		}});

		parrent.add(Tag.Type.SCRIPT, 																("         \n" +
				"	$(function () {       \n" +
				"	    $('#visible_${name}').fileupload({    \n" +
				"		type: 'POST',   \n" +
				"		dataType: 'text',   \n" +
				"		paramName: 'file',    \n" +
				"		cache : false,   \n" +
				"		contentType : false,   \n" +
				"		processData : false,  \n" +
				"	    submit: function (e, data) {       \n" +
				"			if ( window.FileReader) {     \n" +
				"				if (data.files && data.files[0]) {     \n" +
				"					var reader = new FileReader();     \n" +
				"					reader.onload = function(e) {     \n" +
				"						$('#img_visible_${name}').attr('src', e.target.result);     \n" +
				"						$('input#${name}').val(e.target.result);     \n" +
				"						checkPhoto('img_visible_${name}',checkImg);  \n"+
				"					}     \n" +
				"					reader.readAsDataURL(data.files[0]);     \n" +
				"					return false;    \n" +
				"				}     \n" +
				"			}     \n" +
				"	        },       \n" +
				"		success : function(data, textStatus, jqXHR) {   \n" +
				"			$('#visible_${name}').change(); \n" +
				"			$('#img_visible_${name}').attr('src', data); \n" +
				"			$('input#${name}').attr('value', data);     \n" +
				"		},   \n" +
				"		error : function(jqXHR, textStatus, errorThrown) {   \n" +
				"			showError(\"Error: \" +  textStatus + \" \"+ errorThrown, jqXHR.responseText);    \n" +
				"		}   \n" +
				"	    });       \n" +
				"	}); \n")
				.replace("${name}", name));

		return element;
	}
}
