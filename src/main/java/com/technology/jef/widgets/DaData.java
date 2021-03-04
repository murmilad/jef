package com.technology.jef.widgets;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

/**
* Виджет поиск при помощи DaData
*/
public class DaData extends Widget {

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
		
	  /**
	   * Метод формирования DOM модели для виджета
	   * 
	   * @param name имя элемента в DOM модели
	   * @param generator генератор тегов уровня текущего элеметна
	   * @param parrent родительский тег в DOM модели
	   * @return DOM модель на текущем уровне
	 * @throws SAXException 
	   */
		@Override
		public Tag assembleTag(String name, TagGenerator generator) throws SAXException {

			String prefix = (String) generator.getAttribute(TagGenerator.Attribute.PREFIX);
			String nameAPI = name.replace(prefix, "");
			String hint = (String) generator.getAttribute(TagGenerator.Attribute.HINT);

			 parrent.add(Tag.Type.SCRIPT,	("  				var dadata_key;   \n" + 
	"				var dadata_url;   \n" + 
	"				$( document ).ready(function() {    \n" + 
	"							$.ajax({    \n" + 
	"		                            method : 'GET',    \n" + 
	"            		                url: 'rest/settings/get',    \n" + 
	"                        		    headers:{    \n" + 
	"                                    		'Content-Type' :'application/json',    \n" + 
	"		                                    'Accept': 'application/json'    \n" + 
	"							},    \n" + 
	"                        		    success: function(responce) {    \n" + 
	"								if (responce.status_code == 1) {   \n" + 
	"									dadata_key = responce.dadata_key;   \n" + 
	"									dadata_url = responce.dadata_url;   \n" + 
	"									if (!dadata_key || !dadata_url){    \n" + 
	"			  							$('#visible_${name}${group_prefix}').attr(\"placeholder\", \"${dadata_is_not_configured}\");    \n" + 
	"										$('#visible_${name}${group_prefix}').attr(\"disabled\", \"disabled\");    \n" + 
	"  							          	} else {    \n" + 
	"  							          	 var token = 'Token ' + dadata_key;   \n" + 
	"								         $.ajax({    \n" + 
	"										method : 'POST', \n" + 
	"						                            url: dadata_url, \n" +
	"						                            headers:{ \n" + 
	"						                                    'Authorization': token, \n" +
	"						                                    'Content-Type' :'application/json', \n" + 
	"       						                             'Accept': 'application/json' \n" + 
	"										}, \n" + 
	"		            				                success: function() {    \n" + 
	"														$('#visible_${name}${group_prefix}').autocomplete({    \n" + 
	"															serviceUrl: dadata_url,    \n" + 
	"															deferRequestBy: 1000,    \n" + 
	"															type:'POST',    \n" + 
	"															dataType:'json',    \n" + 
	"															ajaxSettings:{    \n" + 
	"																headers:{    \n" + 
	"																	'Authorization': token,    \n" + 
	"																	'Content-Type' :'application/json',    \n" + 
	"																	'Accept': 'application/json'    \n" + 
	"																	},    \n" + 
	"															},    \n" + 
	"															forceFixPosition: true,    \n" + 
	"															onSelect: function (suggestion) {    \n" + 
	"							                                    var region_composite_name = suggestion.data.region?suggestion.data.region+' '+suggestion.data.region_type:'';    \n" + 
	"																$(\"#visible_${name}_region_code${group_prefix}\").val(region_composite_name);    \n" + 
	"																$(\"#visible_${name}_region_code${group_prefix}\").removeClass('warning_color');    \n" + 
	"																$(\"input#${name}_region_code${group_prefix}\").val((suggestion.data.region_fias_id||'')+'|'+(region_composite_name||''));    \n" + 
	"																$(\"#visible_${name}_district_code${group_prefix}\").val(suggestion.data.area_with_type);    \n" + 
	"																$(\"#visible_${name}_district_code${group_prefix}\").removeClass('warning_color');    \n" + 
	"																$(\"input#${name}_district_code${group_prefix}\").val((suggestion.data.area_fias_id||'')+'|'+(suggestion.data.area_with_type||''));    \n" + 
	"							                                    var city_composite_name = suggestion.data.city?suggestion.data.city+' '+suggestion.data.city_type:'';    \n" + 
	"																$(\"#visible_${name}_city_code${group_prefix}\").val(city_composite_name);    \n" + 
	"																$(\"#visible_${name}_city_code${group_prefix}\").removeClass('warning_color');    \n" + 
	"																$(\"input#${name}_city_code${group_prefix}\").val((suggestion.data.city_fias_id||'')+'|'+(city_composite_name||''));    \n" + 
	"							                                    var settlement_composite_name = suggestion.data.settlement?suggestion.data.settlement+' '+suggestion.data.settlement_type:'';    \n" + 
	"																$(\"#visible_${name}_settlement_code${group_prefix}\").val(settlement_composite_name);    \n" + 
	"																$(\"#visible_${name}_settlement_code${group_prefix}\").removeClass('warning_color');    \n" + 
	"																$(\"input#${name}_settlement_code${group_prefix}\").val((suggestion.data.settlement_fias_id||'')+'|'+(settlement_composite_name||''));    \n" + 
	"																$(\"#visible_${name}_post_index${group_prefix}\").val(suggestion.data.postal_code);    \n" + 
	"																$(\"input#${name}_post_index${group_prefix}\").val(suggestion.data.postal_code);    \n" + 
	"							                                    var street_composite_name = suggestion.data.street?suggestion.data.street+' '+suggestion.data.street_type:'';    \n" + 
	"																$(\"#visible_${name}_street_code${group_prefix}\").val(street_composite_name);    \n" + 
	"																$(\"#visible_${name}_street_code${group_prefix}\").removeClass('warning_color');    \n" + 
	"																$(\"input#${name}_street_code${group_prefix}\").val((suggestion.data.street_fias_id||'')+'|'+(street_composite_name||''));    \n" + 
	"																$(\"#visible_${name}_apartment${group_prefix}\").val(suggestion.data.flat);    \n" + 
	"																$(\"input#${name}_apartment${group_prefix}\").val(suggestion.data.flat);    \n" + 
	"																// dadata не передает строение и корпус в отдельных полях, разносим вручную    \n" + 
	"																if (suggestion.data.block_type && suggestion.data.block_type==='${building}'){ // передано как строение    \n" + 
	"										                                        $(\"#visible_${name}_section${group_prefix}\").val('');    \n" + 
	"																    $(\"input#${name}_section${group_prefix}\").val('');    \n" + 
	"							    										$(\"#visible_${name}_building${group_prefix}\").val(suggestion.data.block);    \n" + 
	"								    									$(\"input#${name}_building${group_prefix}\").val(suggestion.data.block);    \n" + 
	"																}else{ // передано как корпус    \n" + 
	"																	if (suggestion.data.block_type){    \n" + 
	"																		var pos = suggestion.data.block.indexOf('${building}');    \n" + 
	"																		if (pos>-1){ // НО в корпусе может содержаться строение (!)    \n" + 
	"																			var building=''; var section='';    \n" + 
	"																			section =  suggestion.data.block.substr(0,pos-1);    \n" + 
	"																			building = suggestion.data.block.substr(pos+4);    \n" + 
	"																			$(\"#visible_${name}_section${group_prefix}\").val(section);    \n" + 
	"																			$(\"input#${name}_section${group_prefix}\").val(section);    \n" + 
	"																			$(\"#visible_${name}_building${group_prefix}\").val(building);    \n" + 
	"																			$(\"input#${name}_building${group_prefix}\").val(building);    \n" + 
	"											    							}else{    \n" + 
	"												                                        $(\"#visible_${name}_section${group_prefix}\").val(suggestion.data.block);    \n" + 
	"																		    $(\"input#${name}_section${group_prefix}\").val(suggestion.data.block);    \n" + 
	"										    									$(\"#visible_${name}_building${group_prefix}\").val('');    \n" + 
	"											    								$(\"input#${name}_building${group_prefix}\").val('');    \n" + 
	"												                                    }    \n" + 
	"																	}else{    \n" + 
	"																		$(\"#visible_${name}_section${group_prefix}\").val('');    \n" + 
	"																		$(\"input#${name}_section${group_prefix}\").val('');    \n" + 
	"																		$(\"#visible_${name}_building${group_prefix}\").val('');    \n" + 
	"																		$(\"input#${name}_building${group_prefix}\").val('');    \n" + 
	"																	}    \n" + 
	"																}    \n" + 
	"																// dadata вносит строение корпус в номер дома если номер дома отсутствует, поэтому разносим сами    \n" + 
	"																switch(suggestion.data.house_type) {    \n" + 
	"																	case '${house}':    \n" + 
	"																	case '${property}':    \n" + 
	"																	default:    \n" + 
	"																		$(\"#visible_${name}_house${group_prefix}\").val(suggestion.data.house);    \n" + 
	"																		$(\"#visible_${name}_house${group_prefix}\").removeClass('warning_color');    \n" + 
	"																		$(\"input#${name}_house${group_prefix}\").val((suggestion.data.house_fias_id||'')+'|'+(suggestion.data.house||''));    \n" + 
	"																		break;    \n" + 
	"																	case '${corpus}':    \n" + 
	"																		$(\"#visible_${name}_house${group_prefix}\").val('');    \n" + 
	"																		$(\"#visible_${name}_house${group_prefix}\").removeClass('warning_color');    \n" + 
	"																		$(\"input#${name}_house${group_prefix}\").val((suggestion.data.house_fias_id||'')+'|');    \n" + 
	"																		$(\"#visible_${name}_section${group_prefix}\").val(suggestion.data.house);    \n" + 
	"																		$(\"input#${name}_section${group_prefix}\").val(suggestion.data.house);    \n" + 
	"																		break;    \n" + 
	"																	case '${building}':    \n" + 
	"																		$(\"#visible_${name}_house${group_prefix}\").val('');    \n" + 
	"																		$(\"#visible_${name}_house${group_prefix}\").removeClass('warning_color');    \n" + 
	"																		$(\"input#${name}_house${group_prefix}\").val((suggestion.data.house_fias_id||'')+'|');    \n" + 
	"																		$(\"#visible_${name}_building${group_prefix}\").val(suggestion.data.house);    \n" + 
	"																		$(\"input#${name}_building${group_prefix}\").val(suggestion.data.house);    \n" + 
	"																		break;    \n" + 
	"																}    \n" + 
	"							                                    // проверка что поля адреса не из справочника (нет code)    \n" + 
	"																if (suggestion.data.city_with_type && !suggestion.data.city_fias_id){    \n" + 
	"																	$(\"#visible_${name}_city_code${group_prefix}\").addClass('warning');    \n" + 
	"																	$(\"#visible_${name}_city_code${group_prefix}\").attr('title', '${cant_find_city}');    \n" + 
	"																}else{    \n" + 
	"																   $(\"#visible_${name}_city_code${group_prefix}\").removeClass('warning');    \n" + 
	"																   $(\"#visible_${name}_city_code${group_prefix}\").attr('title', '');    \n" + 
	"																}    \n" + 
	"																if (suggestion.data.settlement_with_type && !suggestion.data.settlement_fias_id){    \n" + 
	"																	$(\"#visible_${name}_settlement_code${group_prefix}\").addClass('warning');    \n" + 
	"																	$(\"#visible_${name}_settlement_code${group_prefix}\").attr('title', '${cant_find_village}');    \n" + 
	"																}else{    \n" + 
	"																   $(\"#visible_${name}_settlement_code${group_prefix}\").removeClass('warning');    \n" + 
	"																   $(\"#visible_${name}_settlement_code${group_prefix}\").attr('title', '');    \n" + 
	"																}    \n" + 
	"																if (suggestion.data.street_with_type && !suggestion.data.street_fias_id){    \n" + 
	"																	$(\"#visible_${name}_street_code${group_prefix}\").addClass('warning');    \n" + 
	"																	$(\"#visible_${name}_street_code${group_prefix}\").attr('title', '${cant_find_village}');    \n" + 
	"																}else{    \n" + 
	"																   $(\"#visible_${name}_street_code${group_prefix}\").removeClass('warning');    \n" + 
	"																   $(\"#visible_${name}_street_code${group_prefix}\").attr('title', '');    \n" + 
	"																}    \n" + 
	"																if (suggestion.data.house && !suggestion.data.house_fias_id){    \n" + 
	"																	$(\"#visible_${name}_house${group_prefix}\").addClass('warning');    \n" + 
	"																	$(\"#visible_${name}_house${group_prefix}\").attr('title', '${cant_find_village}');    \n" + 
	"																}else{    \n" + 
	"																   $(\"#visible_${name}_house${group_prefix}\").removeClass('warning');    \n" + 
	"																   $(\"#visible_${name}_house${group_prefix}\").attr('title', '');    \n" + 
	"																}    \n" + 
	"																// инвалидируем кэш у полей с адресами    \n" + 
	"																$(\"#visible_${name}_region_code${group_prefix}\").autocomplete('clear');    \n" + 
	"																$(\"#visible_${name}_region_code${group_prefix}\").autocomplete('clearCache');    \n" + 
	"																$(\"#visible_${name}_district_code${group_prefix}\").autocomplete('clear');    \n" + 
	"																$(\"#visible_${name}_district_code${group_prefix}\").autocomplete('clearCache');    \n" + 
	"																$(\"#visible_${name}_city_code${group_prefix}\").autocomplete('clear');    \n" + 
	"																$(\"#visible_${name}_city_code${group_prefix}\").autocomplete('clearCache');    \n" + 
	"																$(\"#visible_${name}_settlement_code${group_prefix}\").autocomplete('clear');    \n" + 
	"																$(\"#visible_${name}_settlement_code${group_prefix}\").autocomplete('clearCache');    \n" + 
	"																$(\"#visible_${name}_street_code${group_prefix}\").autocomplete('clear');    \n" + 
	"																$(\"#visible_${name}_street_code${group_prefix}\").autocomplete('clearCache');    \n" + 
	"																$(\"#visible_${name}_house${group_prefix}\").autocomplete('clear');    \n" + 
	"																$(\"#visible_${name}_house${group_prefix}\").autocomplete('clearCache');    \n" + 
	"																check_addr_field('${name}', 'visible_real_and_reg_equals');    \n" + 
	"															},    \n" + 
	"															transformResult: function(response) {    \n" + 
	"																return {    \n" + 
	"																	suggestions: $.map(response.suggestions, function(dataItem) {    \n" + 
	"																		return { value: dataItem.value , data: dataItem.data};    \n" + 
	"																	})    \n" + 
	"																};    \n" + 
	"														}    \n" + 
	"													});    \n" + 
	"                        						    },    \n" + 
	"				            		                error: function(jqxhr, status, exception) {    \n" + 
	"                        						        $('#visible_${name}').attr(\"placeholder\", \"${dadata_is_not_respond}\");    \n" + 
	"									              $('#visible_${name}').attr(\"disabled\", \"disabled\");   \n" + 
	"										    }   \n" + 
	"						                        });   \n" + 
	"									    }   \n" + 
	"									} else {   \n" + 
	"		               		                     $('#visible_${name}').attr(\"placeholder\", \"${dadata_is_not_respond}\");    \n" + 
	"								         $('#visible_${name}').attr(\"disabled\", \"disabled\");    \n" + 
	"									}   \n" + 
	"		            		     },    \n" + 
	"		            		     error: function(jqxhr, status, exception) {    \n" + 
	"            		                    $('#visible_${name}').attr(\"placeholder\", \"${dadata_is_not_respond}\");    \n" + 
	"						         $('#visible_${name}').attr(\"disabled\", \"disabled\");    \n" + 
	"							}   \n" + 
	"		                    });    \n" + 
	"				});")
					 
				.replace("${cant_find_street}", CurrentLocale.getInstance().getTextSource().getString("cant_find_street"))
				.replace("${cant_find_house}", CurrentLocale.getInstance().getTextSource().getString("cant_find_house"))
				.replace("${cant_find_village}", CurrentLocale.getInstance().getTextSource().getString("cant_find_village"))
				.replace("${cant_find_city}", CurrentLocale.getInstance().getTextSource().getString("cant_find_city"))
				.replace("${house}", CurrentLocale.getInstance().getTextSource().getString("house"))
				.replace("${property}", CurrentLocale.getInstance().getTextSource().getString("property"))
				.replace("${corpus}", CurrentLocale.getInstance().getTextSource().getString("corpus"))
				.replace("${building}", CurrentLocale.getInstance().getTextSource().getString("building"))
				.replace("${dadata_is_not_configured}", CurrentLocale.getInstance().getTextSource().getString("dadata_is_not_configured"))
				.replace("${dadata_is_not_respond}", CurrentLocale.getInstance().getTextSource().getString("dadata_is_not_respond"))
				.replace("${name}", nameAPI)
				.replace("${group_prefix}", prefix)
			);
			
			
			Tag elementInput = parrent.add(Tag.Type.INPUT, new HashMap<Tag.Property, String>(){{
						 put(Tag.Property.ID, "visible_" + name);
						 put(Tag.Property.NAME, "visible_" + name);
						 put(Tag.Property.TYPE, "search");
						 put(Tag.Property.PLACEHOLDER, hint);
						 put(Tag.Property.STYLE, "padding-right:0px;width:100%;font-size:large;height:28px;");
			}});
			
			return elementInput;
		}

}
