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
					window.logout(document);             
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
			} else if (result.status_code == 1) {             
				if (callback) {             
					callback(result, current);             
				}             
			}
		}             
	}         
	function releaseRequest(request){         
		window.ajaxRequestStack.splice((window.ajaxRequestStack.indexOf(request)-1), 1);         
		if (window.ajaxRequestStack.length == 0){         
			$( document ).trigger('allRequestsReleased');         
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
				$( window ).unbind('allRequestsReleased');     
			});    
		}  
	}   
 	function ajax(parameters, callback) {         
		pooledParameters = JSON.stringify(parameters).replace(/"(rnd|no_cache)":\d+/g, "");      
		if (!window.ajaxPool.hasSame(pooledParameters)) {  
			window.ajaxPool.push(pooledParameters);   
			$('#is_loading').val('1');
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
	if (!uri_params) {          
		var uri_params = window               
		    	.location               
		    	.search               
		    	.replace('?','')               
		    	.split('&')               
		    	.reduce(               
		    			function(p,e){               
		    				var a = e.split('=');               
		    				p[ decodeURIComponent(a[0])] = decodeURIComponent(a[1]);               
		    				return p;               
		    			},               
		        {}               
		    );          
	}         

	$( document ).ready(function() {
		if (!$('#is_loading').length) {
			$('<input />', {
			    'type': 'hidden',
			    'id': 'is_loading',
			    'name': 'is_loading'
			}).appendTo('body');
		}
	});
