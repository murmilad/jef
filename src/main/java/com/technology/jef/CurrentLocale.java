package com.technology.jef;

import java.util.Locale;
import java.util.ResourceBundle;

public class CurrentLocale {
    private static volatile CurrentLocale instance;
	
    private ResourceBundle textSource;
    private Locale locale;
    
    public static CurrentLocale getInstance() {
	    CurrentLocale localInstance = instance;
		if (localInstance == null) {
			synchronized (CurrentLocale.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new CurrentLocale();
				}
			}
		}
		return localInstance;
	}

	public ResourceBundle getTextSource() {
		return textSource;
	}

	public void setTextSource(ResourceBundle textSource) {
		this.textSource = textSource;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		setTextSource(ResourceBundle.getBundle("CoreText", locale));
	}

    

}
