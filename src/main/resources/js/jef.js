	window.ajaxRequestStack = [];                  
	var ajaxRequestId = 0;                  
        window.ajaxPool = [];        
        window.ajaxXNR = [];        
        window.ajaxPool.hasSame = function(parameters) {        
            	if (ajaxPool.indexOf(parameters) != -1) {       
 					return true;       
				}        
				return false;        
        };        
        window.ajaxPool.delete	= function(parameters) {        
            	var i = ajaxPool.indexOf(parameters);        
				if (i > -1) ajaxPool.splice(i, 1);       
        };       
	function showError (header, message){               
		$("#background_overlay_error").show();                      
   		$("#message_box_error").show();                      
		$("#message_header_error").html(header);               
		$("#message_body_error").html(message);               
	}                
	function operateResult (result, parameters, callback, current) {
		if (parameters.dataType == 'text') {
			if (callback) {             
				callback(result, current);             
			}       			
		} else {            
			if (result.status_code == 2) {             
				if (result.error.error_code == 2) {             
					window.logout(document, result.error.error_message);             
				} else if (result.error.error_button === 'close') { //Close             
					showError(result.error.error_description, result.error.error_message);
					$( "#message_buttons_error" ).show();
					$( "#message_error_button" ).unbind("click");
					$( "#message_error_button" ).on("click", function(){
						$("#background_overlay_error").hide();                      
				   		$("#message_box_error").hide();                      
					});
             
				} else if (result.error.error_button === 'back') { //Back             
					showError(result.error.error_description, result.error.error_message);
					$( "#message_buttons_error" ).show();
					$( "#message_error_button" ).unbind("click");
					$( "#message_error_button" ).on("click", function(){
						history.back();                      
					});
             
				} else if (result.error.error_button === 'reload') { //Refrech             
					showError(result.error.error_description, result.error.error_message);
					$( "#message_buttons_error" ).show();
					$( "#message_error_button" ).unbind("click");
					$( "#message_error_button" ).on("click", function(){
						window.location.reload();                      
					});
             
				} else { //Incorrect form error             
					showError(result.error.error_description, result.error.error_message);
					$( "#message_buttons_error" ).hide();
				}             
			} else {             
				if (callback) {             
					callback(result, current);             
				}             
			}
		}             
	}   
	function execOrBindAfterAll(handler){
		setTimeout(function() {         
			if (window.ajaxRequestStack.length > 0){         
				$( document ).bind('allRequestsReleased', handler);         
			} else {
				handler();
			}         
		}, 0);
	}        
	function releaseRequest(request){
		if (window.ajaxRequestStack.indexOf(request) > -1) {
			window.ajaxRequestStack.splice(window.ajaxRequestStack.indexOf(request), 1);         
		}
		if (window.ajaxRequestStack.length == 0){         
			$( document ).trigger('allRequestsReleased');
			$( document ).unbind('allRequestsReleased');         
			$('#is_loading').val('0');
		}         
	}  
	function bindIsFormLoading (){         
		if (window.ajaxRequestStack.length == 0){   
 			window.isFormLoading = false;       
			$("#message_overlay_wait_form").hide();                              
			$("#message_box_overlay_wait_form").hide();                              
   			$("#message_box_wait_form").hide();                              
		} else {   
			$( document ).on('allRequestsReleased', function(){     
 				window.isFormLoading = false;       
				$("#message_overlay_wait_form").hide();                              
				$("#message_box_overlay_wait_form").hide();                              
	    			$("#message_box_wait_form").hide();                              
			});    
		}  
	}   
 	function ajax(parameters, callback) { 
		pooledParameters = JSON.stringify(parameters).replace(/"(rnd|no_cache)":\d+/g, "");      
		if (!window.ajaxPool.hasSame(pooledParameters)) {  
			window.ajaxPool.push(pooledParameters);   
			$('#is_loading').val('1');
			$( document ).trigger('beforeLoading');        
			$( document ).bind('allRequestsReleased', function(){
				$( document ).trigger('afterLoading');        
			});
			var currentAjaxRequest = {requestId: ajaxRequestId++};         
			window.ajaxRequestStack.push(currentAjaxRequest);         
			window.ajaxXNR[pooledParameters] = $.ajax(            
				parameters            
			).done(function(data){            
				window.ajaxPool.delete(pooledParameters);
				operateResult(data, parameters, callback, this);         
				releaseRequest(currentAjaxRequest);         
			}).fail(function(jqXHR, textStatus, errorThrown){            
				window.ajaxPool.delete(pooledParameters);         
				showError("Error: " + errorThrown, jqXHR.responseText);            
				releaseRequest(currentAjaxRequest);         
			});            
		} else {   
			window.ajaxXNR[pooledParameters].done(function(data){            
				operateResult(data, parameters, callback, this);         
			}).fail(function(jqXHR, textStatus, errorThrown){            
				showError("Error: " + errorThrown, jqXHR.responseText);            
			});   
		}           
	}            
	function getJSON(url, parameters, callback){        
		var currentAjaxRequest = {requestId: ajaxRequestId++};         
		window.ajaxRequestStack.push(currentAjaxRequest);         
		$('#is_loading').val('1');
		$( document ).trigger('beforeLoading');        
		$( document ).bind('allRequestsReleased', function(){
			$( document ).trigger('afterLoading');        
		});
		$.getJSON(           
			url,           
			parameters           
		).done(function(data){            
			operateResult(data, parameters, callback, this);            
			releaseRequest(currentAjaxRequest);       
		}).fail(function(jqXHR, textStatus, errorThrown){            
			showError("Error: " + errorThrown, jqXHR.responseText);            
			releaseRequest(currentAjaxRequest);         
		});        
	}           
	function getWindowParams(place){
		place = place || $('.container').attr('id');
		if (place) {
			if (!window.params_places) {
				window.params_places = {};
			}
			return window.params_places[place];
		} else {
			return window.params;
		}

	}
	
	function setWindowParams(location, place){
		var paramsArray = location 
			.replace(/#\d*$/,'')       
			.replace('?','&')    
			.split('&');
		paramsArray.shift();
		var paramsHash = paramsArray.reduce(    
					function(p,e){    
						var a = e.split('=');    
						try{
							p[ decodeURIComponent(a[0])] = decodeURIComponent(a[1]);
	 					}catch(e){}
						return p;    
					},    
		    {}    
		);
		paramsHash.no_cache = Math.floor(Math.random() * 10000);
	
		if (place) {
			if (!window.params_places) {
				window.params_places = {};
			}
			window.params_places[place] = paramsHash;
		} else {
			window.params = paramsHash;
		}
	
	}
	
	function appendWindowParams(parameters, place) {
		if (place) {
			if (!window.params_places) {
				window.params_places = {};
			}
			window.params_places[place] = $.extend(getWindowParams(place), parameters);
		} else {
			window.params = $.extend(getWindowParams(place), parameters);
		}
		
	}

	function ajaxOverlay(parameters, callback) {
		$("#background_overlay_load").show();
		$("#message_box_load").show();
	
		$( document ).bind('allRequestsReleased', function(){
			$("#background_overlay_load").hide();
			$("#message_box_load").hide();
		});
		ajax(parameters, function (result, current) {
	    	callback(result, current);
	    });
	} 
	
	
	function getJSONOverlay(url, parameters, callback) {
		$("#background_overlay_load").show();
		$("#message_box_load").show();
	
		$( document ).bind('allRequestsReleased', function(){
			$("#background_overlay_load").hide();
			$("#message_box_load").hide();
		});
		ajax({
	           url: url,
	           data: parameters,
	           parameters: parameters
	    }, function (result, current) {
	    	callback(result, current);
	    });
	} 
	
	function getHTMLOverlay(url, parameters, method, callback) {
		$("#background_overlay_load").show();
		$("#message_box_load").show();
		$( document ).bind('allRequestsReleased', function(){
			$("#background_overlay_load").hide();
			$("#message_box_load").hide();
		})

		ajax({
	           url: url,
	           data: parameters,
	           dataType:'text',
	           method: method,
	           parameters: parameters
	    }, function (result) {
	        if (result.redirect) {
				$("#background_overlay_load").hide();
				$("#message_box_load").hide();
	            alert(data.redirect);
	        } else if (callback) {
				$("#background_overlay_load").hide();
				$("#message_box_load").hide();
				callback(result, this);
			}
		});
	} 

	function loadPage(uri, parameters, method, place, callback){
		window.resourcesStack = [];
		var loadedStack = [];
// 		var page = uri.substr(uri.lastIndexOf('/')+1, uri.indexOf('?') == -1 ? uri.length : uri.indexOf('?'))
		setWindowParams(uri, place);
		parameters.uri = uri + (parameters && Object.keys(parameters).length > 0 
			? ( uri.indexOf('?') == -1 
				? '?'
				: '&'
			) + Object.keys(parameters).map(function(key){return key + '=' + parameters[key]}).join('&')
			: '');
		appendWindowParams(parameters, place);
		

		parameters.no_cache = Math.floor(Math.random() * 10000);
		getHTMLOverlay( uri, parameters, method, function( result, ajax ) {
			
		
			var redirectRegExp = /<\s*meta\s+http-equiv\s*=\s*["']Refresh["']\s*CONTENT\s*=["'][^"']+?URL=([^;"']+)/i; 
			var redirectMatch = redirectRegExp.exec(result);
			if (redirectMatch && redirectMatch[1]) {
				loadPage(redirectMatch[1], {}, 'get', place, callback);

			} else {
				if ($(place + '_shade').length > 0) {
					var cleanHtml = $(place).html();
					var replace = '<\\s*script[\\s\\S]+?</script>';
					var re = new RegExp(replace,"g");   
					cleanHtml = cleanHtml.replace(re, '');    

					replace = 'id\\s*=\\s*[\'"].+?[\'"]';
					re = new RegExp(replace,"g");   
					cleanHtml = cleanHtml.replace(re, '');    

					$(place + '_shade_body').html(cleanHtml);
					$(place + '_shade').show();
				}
				
				$(place).empty();

				$('script').each( function() { 
					if($( this ).attr('src')) {
						window.resourcesStack.push($( this ).attr('src').split('?')[0]);
					}
				});

				var filtredData = result;     

				var replace = 'getWindowParams\\s*(\\s*\\(\\))?([^\\(])';
				var re = new RegExp(replace,"g");   
				filtredData = filtredData.replace(re, "getWindowParams('"+place+"')$2");    


				var resourcesRegExp = /<\s*script\s+src\s*=\s*['"](.+?\.js)/g;     
				var resourcesRegExpResult;     
				while((resourcesRegExpResult = resourcesRegExp.exec(result)) !== null) {    
					if ($.inArray(resourcesRegExpResult[1], window.resourcesStack) > -1) {    
						var replace = '<\\s*script\\s+src\\s*=\\s*[\'"]' + resourcesRegExpResult[1] + '.*?[\'"][\\s\\S]+?</script>';
						var re = new RegExp(replace,"g");   
						filtredData = filtredData.replace(re, '');    

						replace = 'src\\s*=\\s*[\'"]' + resourcesRegExpResult[1] + '.*?[\'"]';  
						re = new RegExp(replace,"g");   
						filtredData = filtredData.replace(re, '');    
					} else {  
						loadedStack.push(resourcesRegExpResult[1])    
					}     
				}     


				$.ajaxSetup({
				    dataFilter: function(response, type) {
				        if (type === 'script'){
				        	var filtredData = response;
							var replace = 'getWindowParams\\s*(\\s*\\(\\))?([^\\(\\\\])';
							var re = new RegExp(replace,"g");   
							filtredData = filtredData.replace(re, "getWindowParams('"+place+"')$2");    

				        	return filtredData;
				        }
				        return response;
				    }
				});
				$(place).append(filtredData);
				$.ajaxSetup({
				    dataFilter: function(response, type) {
				        return response;
				    }
				});

				
				window.scrollTo(0, 0);

				if ($(place + '_shade').length > 0) {
					execOrBindAfterAll(function(){
						$(place + '_shade').hide();
					});
				}
				if (callback){
					callback(place);
				}
			}
		});
	}

	
	if (!window.params) {          
		setWindowParams(window.location.href);
	}         

	$( document ).ready(function() {
		if (!$('#is_loading').length) {
			$('<input />', {
			    'type': 'hidden',
			    'id': 'is_loading',
			    'name': 'is_loading',
			}).appendTo('body');
		}

		if (!$('#background_overlay_load').length) {
			$('<div />', {
			    'class': 'background_overlay_load background_overlay_color',
			    'style': 'display:none;',
			    'id': 'background_overlay_load',
			    'name': 'background_overlay_load',
			}).appendTo('body');
		
			var $box = $('<div />', {
			    'class': 'message_box_load message_box_load_color',
			    'id': 'message_box_load',
			    'name': 'message_box_load',
			    'style': 'display:none;',
			}).appendTo('body');
		
		
			$('<div />', {
			    'class': 'background_overlay_error',
			    'id': 'background_overlay_error',
			    'name': 'background_overlay_error',
			    'style': 'display:none;',
			}).appendTo('body');
		
			var $error_box = $('<div />', {
			    'class': 'message_box_error',
			    'id': 'message_box_error',
			    'name': 'message_box_error',
			    'style': 'display:none;',
			}).appendTo('body');
		
			$('<div />', {
			    'class': 'message_header_error',
			    'id': 'message_header_error',
			    'name': 'message_header_error',
			}).appendTo($error_box);
			$('<div />', {
			    'class': 'message_body_error',
			    'id': 'message_body_error',
			    'name': 'message_body_error',
			}).appendTo($error_box);
			var $buttons_box = $('<div />', {
			    'class': 'message_buttons_error messages_color',
			    'id': 'message_buttons_error',
			    'name': 'message_buttons_error',
			}).appendTo($error_box);
			$('<input />', {
			    'class': 'interface_button first_color second_text_color',
			    'id': 'message_error_button',
			    'type': 'button',
			    'value': 'Назад',
			}).on('click', function(){
				window.location.reload();
			}).appendTo($buttons_box);
		}
	});

	$( document ).scroll(function(){
		if ($('#group_bar:visible').length) {
			if (isIE() && $('#group_bar').css('position') != 'fixed') {
				$('#group_bar').css('position', 'fixed');
			}
			$("[id^='fildset_']").each(function(){
				var fildsetId = $( this ).attr('name');
				var spanId = fildsetId.replace('fildset_', 'span_');
				var divId = fildsetId.replace('fildset_', 'div_');
				var divPreviewId = fildsetId.replace('fildset_', 'div_preview');
				var currentTop = $('#group_bar').offset().top + $('#group_bar').height();
	
				if (
					currentTop  >= $( this ).offset().top  
					&& $('#group_bar').find("[name^='"+fildsetId+"']").length == 0
				){
					var source = $('#' + fildsetId).clone(true).attr('id', '');
					source.css('border-bottom', 'none');
					source.css('padding-bottom', '0px');
					source.css('padding-top', '2px');
					source.css('margin-bottom', '0px');
					if (isIE()) {
						source.css('border', 'none');
					}
					source.find("#" + divId).attr('id', '').empty();
					source.find("#" + spanId).css('font-weight', '800').attr('id', '');
					source.find("#" + divPreviewId).remove();

					source.appendTo(downTree($('#group_bar')));
					source.attr('data-current-height', source.height());
				}
				
				currentTop = $('#group_bar').offset().top + $('#group_bar').height();
				
				if ($('#group_bar').find("[name^='"+fildsetId+"']").length > 0 && currentTop > $('#' + fildsetId).height() + $( this ).offset().top) {
					$('#group_bar').find("[name^='"+fildsetId+"']").remove();
				}
				
				if ($('#group_bar').find("[name^='"+fildsetId+"']").length > 0 && $( this ).offset().top > currentTop - $('#group_bar').find("[name^='"+fildsetId+"']").attr('data-current-height')){
					$('#group_bar').find("[name^='"+fildsetId+"']").remove();
					$('#group_bar').find("[name^='"+fildsetId+"']").attr('data-current-height');
				}
			});
		}
	});

	function downTree (node){
		if (node.children().length) {
			return downTree(node.children().last());
		} else {
			return node;
		}
	}
	function isIE() {
	    var ua = window.navigator.userAgent; //Check the userAgent property of the window.navigator object
	    var msie = ua.indexOf('MSIE '); // IE 10 or older
	    var trident = ua.indexOf('Trident/'); //IE 11
	    
	    return (msie > 0 || trident > 0);
		}