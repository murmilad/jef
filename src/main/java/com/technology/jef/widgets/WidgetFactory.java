package com.technology.jef.widgets;

import com.technology.jef.widgets.text.AnyText;
import com.technology.jef.widgets.text.Date;
import com.technology.jef.widgets.text.Email;
import com.technology.jef.widgets.text.Inn;
import com.technology.jef.widgets.text.Money;
import com.technology.jef.widgets.text.Number;
import com.technology.jef.widgets.text.Phone;
import com.technology.jef.widgets.text.ReadOnly;

/**
 * Класс - фабрика виджетов
 */
public class WidgetFactory {
	
	
  /**
   * Метод инициализации виджетов
   * 
   * @param type тип виджета
   * @return экземпляр класса виджета
   */

	public static Widget getWidget(String type) {
		Widget widget = null;
		Widget.Type name = Widget.Type.valueOf(type.toUpperCase());

		// Выбираем виджет в зависимости от типа
		switch (name) {
		case TEXT:
			widget = new Text();
			break;
		case TEXTAREA:
			widget = new TextArea();
			break;
		case NUMBER:
			widget = new Number();
			break;
		case DATE:
			widget = new Date();
			break;
		case INN:
			widget = new Inn();
			break;
		case EMAIL:
			widget = new Email();
			break;
		case MONEY:
			widget = new Money();
			break;
		case PHONE:
			widget = new Phone();
			break;
		case INFO:
			widget = new Info();
			break;
		case HIDDEN:
			widget = new Hidden();
			break;
		case LABEL:
			widget = new Label();
			break;
		case POPUP_LIST:
			widget = new PopUpList();
			break;
		case LIST:
			widget = new List();
			break;
		case SHORT_LIST:
			widget = new ListShort();
			break;
		case EDITABLE_LIST:
			widget = new ListEditable();
			break;
		case SHORT_EDITABLE_LIST:
			widget = new ListEditableShort();
			break;
		case FIND:
			widget = new Find();
			break;
		case SHORT_FIND:
			widget = new FindShort();
			break;
		case CHECKBOX:
			widget = new CheckBox();
			break;
		case CHECKBOX_LIST:
			widget = new CheckBoxList();
			break;
		case SHORT_CHECKBOX_LIST:
			widget = new CheckBoxListShort();
			break;
		case RADIO_SWITCH:
			widget = new RadioSwitch();
			break;
		case SHORT_RADIO_SWITCH:
			widget = new RadioSwitchShort();
			break;
		case SHORT_RADIO_SWITCH_CLEANUP:
			widget = new RadioSwitchShortCleanup();
			break;
		case DADATA:
			widget = new DaData();
			break;
		case DADATAJOB:
			widget = new DaDataJob();
			break;
		case AUTO_COMPLETE:
			widget = new AutoComplete();
			break;
		case AUTO_COMPLETE_EDITABLE:
			widget = new AutoCompleteEditable();
			break;
		case AUTO_COMPLETE_EDITABLE_COMPACT:
			widget = new AutoCompleteEditableCompact();
			break;
		case AUTO_COMPLETE_ADDRESS:
			widget = new AutoCompleteAddress();
			break;
		case AUTO_COMPLETE_FILTER:
			widget = new AutoCompleteFilter();
			break;
		case ANYTEXT:
			widget = new AnyText();
			break;
		case IMAGE:
			widget = new Image();
			break;
		case FILE:
			widget = new File();
			break;
		case FILE_SHORT:
			widget = new FileShort();
			break;			
		case IMAGE_SHORT:
			widget = new ImageShort();
			break;
		case IMAGE_WEBCAM:
			widget = new ImageWebCam();
			break;
		case TEXT_READ_ONLY:
			widget = new ReadOnly();
			break;
		case FILL_BUTTON:
			widget = new FillButton();
			break;
		case BUTTON:
			widget = new Button();
			break;
		case HTML:
			widget = new HTML();
			break;
		case PRINT:
			widget = new Print();
			break;
		}
		
		
		
		return widget;
	}

}
