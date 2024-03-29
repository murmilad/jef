package com.technology.jef.server;


public interface WebServiceConstant {

	public static final String ACTUAL_API_VERSION_NUMBER = "1.0.0";

	public static final String API_VERSION_HEADER_NAME = "X-API-Version";

	public static final String METHOD_GET = "GET";
  
	public static final String METHOD_POST = "POST";
  
	public static final String METHOD_PUT = "PUT";
  
	public static final String JSON_FORMAT = "json";
  
	public static final String JSONP_FORMAT = "jsonp";
  
	public static final String PREFIX_WEB_SERVICE_TYPE = "get";
  
	public static final Integer GUEST_OPERATOR_ID = 1076297;
	public static final Integer GUEST_CITY_ID = 1;
  
	public static final Integer SERVICE_STATUS_OK = 1;
	public static final Integer SERVICE_STATUS_ERROR = 2;
	

	//Form constants
	
	public static final String FORM_API  = "form_api";
	public static final String ID = "id";
	public static final String CITY_ID = "city_id";
	public static final String PARAMETER_NAME = "parameter_name";
	public static final String PARAMETERS = "parameters";

	//Group actions
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_UPDATE = "update";
	
}
