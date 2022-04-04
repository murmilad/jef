package com.technology.jef.server.serialize;


/**
 * Константы, которые используются для работы с сериализацией.<br/>
 * Многие константы используются как разделители в строке параметра. Поэтому очень важно (для корректного разбора параметра
 * и правильного восстановления объектов на его основе), чтобы значения констант-разделителей были уникальными.<br/>
 * <br/>
 * 
 */
public class SerializeConstant {

  /**
   * Разделитель между параметрами 
   * 
   */
  public static final String PARAMETER_SEPARATOR = ":p:";

  /**
   * Разделитель между именем и значением параметра
   *
   */
  public static final String PARAMETER_NAME_VALUE_SEPARATOR = ":i:";

  
  /**
   * Разделитель между параметром и группой
   * 
   */
  public static final String GROUP_SEPARATOR = "_g_";

  /**
   * Разделитель между вложенными мультигруппами
   */
  public static final String JOINED_GROUP_SEPARATOR = ":g:";

  /**
   * Разделитель между параметрами внутри вложенной мультигруппы
   * 
   */
  public static final String JOINED_GROUP_PARAMETER_SEPARATOR = ":gp:";

  /**
   * Разделитель между элементами списка, содержащегося в параметре
   * 
   */
  public static final String LIST_SEPARATOR = ":l:";

  /**
   * Разделитель между кодом ошибки и текстом ошибки
   * 
   */
  public static final String ERROR_TEXT_FIELD_CODE_SEPARATOR = ":e:";

  /**
   * Разделитель между кодом и именем в адресе ФИАС
   * 
   */
  public static final String FIAS_CODE_NAME_SEPARATOR = "|";
  
  /**
   * Префикс для системных параметров
   * 
   */
  public static final String SYSTEM_PARAMETER_PREFIX = "t_sys";

  
}
