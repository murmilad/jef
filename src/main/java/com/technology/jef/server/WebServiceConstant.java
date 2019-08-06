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
  
	public static final String APPLICATION_ID_PARAMETER_NAME = "applicationId";

	public static final Integer GUEST_OPERATOR_ID = 1076297;
	public static final Integer GUEST_CITY_ID = 1;
  
	public static final Integer SERVICE_STATUS_OK = 1;
	public static final Integer SERVICE_STATUS_ERROR = 2;
	
	public static final int INCORRECT_PARAMETER_ERROR_CODE = 1;
	public static final int AUTHORIZATION_REQUIRED = 2;
	public static final int SYSTEM_ERROR = 3;
	public static final int REDIRECT_REQUIRED = 4;
}
