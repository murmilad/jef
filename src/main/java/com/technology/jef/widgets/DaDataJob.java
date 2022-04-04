package com.technology.jef.widgets;

import com.technology.jef.CurrentLocale;
import com.technology.jef.Tag;
import com.technology.jef.generators.TagGenerator;

import java.util.HashMap;

import org.xml.sax.SAXException;

/**
* Виджет поиск при помощи DaData
*/
public class DaDataJob extends com.technology.jef.widgets.Widget {

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
			//language=JavaScript
			 parrent.add(Tag.Type.SCRIPT,	("  				   \n" +
	"				var dadataJob_url;   \n" +
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

	"									dadataJob_url = responce.dadataJob_url;   \n" +
	"									if ( !dadataJob_url){    \n" +
	"			  							$('#visible_${name}${group_prefix}').attr(\"placeholder\", \"${dadata_is_not_configured}\");    \n" + 
	"										$('#visible_${name}${group_prefix}').attr(\"disabled\", \"disabled\");    \n" + 
	"  							          	} else {    \n" + 

	"								         $.ajax({    \n" + 
	"										method : 'POST', \n" + 
	"						                            url: dadataJob_url, \n" +
	"						                            data: JSON.stringify({'query': 'test-string'}), \n" +
	"						                            headers:{ \n" + 

	"						                                    'Content-Type' :'application/json', \n" + 
	"       						                             'Accept': 'application/json' \n" + 
	"										}, \n" + 
	"		            				                success: function() {    \n" + 
	"														$('#visible_${name}${group_prefix}').autocomplete({    \n" + 
	"															serviceUrl: dadataJob_url,    \n" +
	"															deferRequestBy: 1000,    \n" + 
	"															type:'POST',    \n" + 
	"															dataType:'json',    \n" + 
	"															ajaxSettings:{    \n" + 
	"																headers:{    \n" + 
	"																	'Content-Type' :'application/json',    \n" +
	"																	'Accept': 'application/json'    \n" + 
	"																	},    \n" + 
	"															},    \n" + 
	"															forceFixPosition: true,    \n" + 
	"															onSelect: function (suggestion) {    \n" +
	"																	// в hidden пишем тип организации\n" +
	"																	$('#${name}').val(suggestion.data.type);\n" +
	"																	if(suggestion.data.type ==='LEGAL'){\n"+
	"																			$(\"#job_legal_name\").val((suggestion.data.name.short_with_opf||suggestion.data.name.full_with_opf).replace(/\"/g,''));\n" +
	"																			$(\"#visible_job_legal_name\").val((suggestion.data.name.short_with_opf||suggestion.data.name.full_with_opf).replace(/\"/g,''));\n"+
	"																	}else if(suggestion.data.type ==='INDIVIDUAL'){\n"+
	"																			$(\"#job_legal_name\").val(suggestion.data.name.full.replace(/\"/g,''));\n" +
	"																			$(\"#visible_job_legal_name\").val(suggestion.data.name.full.replace(/\"/g,''));\n"+
	"																	}\n"+
	"																	$(\"#job_employer_inn\").val(suggestion.data.inn);\n" +
	"																	$(\"#visible_job_employer_inn\").val(suggestion.data.inn);\n" +
	"																	if ($(\"#visible_job\")){\n" +
	"																	$(\"#visible_job\").val(suggestion.data.address.value);\n" +
	"																	$(\"#visible_job_region_code\").val(suggestion.data.address.data.region);\n" +
	"																	if (suggestion.data.address.data.region_fias_id>'') $(\"#job_region_code\").val(suggestion.data.address.data.region_fias_id+'|'+suggestion.data.address.data.region);\n" +
	"																	else $(\"#job_region_code\").val('');\n" +
	"																	$(\"#visible_job_district_code\").val(suggestion.data.address.data.city_district_with_type);\n" +
	"																	if (suggestion.data.address.data.city_district_fias_id>'') $(\"#job_district_code\").val(suggestion.data.address.data.city_district_fias_id+'|'+suggestion.data.address.data.city_district_with_type);\n" +
	"																	else $(\"#job_district_code\").val('');\n" +
	"																	$(\"#visible_job_city_code\").val(suggestion.data.address.data.city);\n" +
	"																	if (suggestion.data.address.data.city_fias_id>'') $(\"#job_city_code\").val(suggestion.data.address.data.city_fias_id+'|'+suggestion.data.address.data.city);\n" +
	"																	else $(\"#job_city_code\").val('');\n" +
	"																	$(\"#visible_job_settlement_code\").val(suggestion.data.address.data.settlement_with_type);\n" +
	"																	if (suggestion.data.address.data.settlement_fias_id>'') $(\"#job_settlement_code\").val(suggestion.data.address.data.settlement_fias_id+'|'+suggestion.data.address.data.settlement_with_type);\n" +
	"																	else $(\"#job_settlement_code\").val('');" +
	"																	$(\"#visible_job_street_code\").val(suggestion.data.address.data.street_with_type);\n" +
	"																	if (suggestion.data.address.data.street_fias_id>'') $(\"#job_street_code\").val(suggestion.data.address.data.street_fias_id+'|'+suggestion.data.address.data.street_with_type);\n" +
	"																	else $(\"#job_street_code\").val('');\n" +
	"																	$(\"#visible_job_house\").val(suggestion.data.address.data.house);\n" +
	"																	if (suggestion.data.address.data.house_fias_id>'') $(\"#job_house\").val(suggestion.data.address.data.house_fias_id+'|'+suggestion.data.address.data.house);\n" +
	"																	else $(\"#job_house\").val('');\n" +
	"																	$(\"#visible_job_section\").val(suggestion.data.address.data.block);\n" +
	"																	$(\"#job_section\").val(suggestion.data.address.data.block);\n" +
	"																	$(\"#visible_job_building\").val('');\n" +
	"																	$(\"#job_building\").val('');\n" +
	"																	$(\"#visible_job_apartment\").val(suggestion.data.address.data.flat);\n" +
	"																	$(\"#job_apartment\").val(suggestion.data.address.data.flat);\n" +

	"																	} \n" +
	"															},    \n" +
	"															formatResult: function(suggestion, currentValue) {\n" +
	"															if (!currentValue) {\n" +
	"																return suggestion.value;\n" +
	"															}\n" +
	"															var data = suggestion.data;\n" +
	"															var pattern = '(' + currentValue + ')';\n" +
	"															var status;var style;\n" +
	"															switch (data.state.status) {\n" +
	"															  case \"ACTIVE\":\n" +
	"															      status='';\n" +
	"															      style='';\n" +
	"															  break;\n" +
	"															  case \"LIQUIDATING\":\n" +
	"															      status='ЛИКВИДАЦИЯ';\n" +
	"															      style='background-color:#FFB6C1';\n" +
	"															  break;\n" +
	"															  case \"LIQUIDATED\":\n" +
	"															      status='!ЛИКВИДИРОВАНО!';\n" +
	"															      style='background-color:#FF6347';\n" +
	"															  break;\n" +
	"															  case \"REORGANIZING\":\n" +
	"															      status='';\n" +
	"															      style='';\n" +
	"															  break;\n" +
	"															}\n" +
	"															var address;\n" +
	"															if (data.address) {\n" +
	"															if (data.address.data.qc == \"0\") {\n" +
	"															address = data.address.data.postal_code+', '+data.address.value;\n" +
	"															} else {\n" +
	"															address = data.address.data.source;\n" +
	"															}\n" +
	"															}\n" +
	"															address = '<span style=\"'+style+'\"><strong>'+status+'<\\/strong><\\/span>' + address;\n" +
	"															return suggestion.value.replace(new RegExp(pattern, 'gi'), '<strong>\\$1<\\/strong>')+'<br><small>'+address+'<\\/small>'\n" +
	"															}"+
	"													});    \n" +
	"                        						    },    \n" + 
	"				            		                error: function(jqxhr, status, exception) {    \n" + 
	"                        							$('#visible_${name}').attr(\"placeholder\", \"${dadata_is_not_respond}\");    \n" +
	"													$('#visible_${name}').attr(\"disabled\", \"disabled\");   \n" +
	"										    }   \n" + 
	"						                        });   \n" + 
	"									    }   \n" + 
	"									} else {   \n" + 
	"										$('#visible_${name}').attr(\"placeholder\", \"${dadata_is_not_respond}\");    \n" +
	"										$('#visible_${name}').attr(\"disabled\", \"disabled\");    \n" +
	"									}   \n" + 
	"		            		     },    \n" + 
	"		            		     error: function(jqxhr, status, exception) {    \n" + 
	"										$('#visible_${name}').attr(\"placeholder\", \"${dadata_is_not_respond}\");    \n" +
	"										$('#visible_${name}').attr(\"disabled\", \"disabled\");    \n" +
	"							}   \n" + 
	"		                    });    \n" + 
	"				});")
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
						 put(Tag.Property.STYLE, "padding-right:0px;width:100%;font-size:large;height:28px;-webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;");
			}});
			
			return elementInput;
		}

}
