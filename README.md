# Java Enterprise Form framework

It's simple RESTy framework for fast developing forms with dependent on each other fields.
You can simply create Forms also with a dynamic dependant fields as below:
![](connected-elements.gif)

*Showcase project https://github.com/murmilad/jefshowcase*

## Howo to develop
Simple bunch of steps:

### Form design with XML 
Declare interface with fields and theirs connections 
![](xml-form-creation.gif)

### Create your Form and implement fields
Just cteate your Form inherited Class and implement Listeners for your fields
![](rest-form-parameters.gif)

###  Implement Load/Save methods
Create load and save methods for your Form
![](rest-form-loadsave.gif)

### Register your new form
![](rest-form-register.gif)

## Technical details
### REST Entry point
Implement Java services *aka Jersey, Django,.. etc.* using this Framework classess. 
This link for Jersey [realisation](https://github.com/murmilad/jefshowcase/blob/master/src/main/java/com/technology/showcase/jersey/FormWebService.java).

If you use  non-Jersey realisation you can create your own FormWebService but using same functional as the link above .
## Sources
### Maven Repository to Github howto
Russian [instruction](https://devcolibri.com/%D0%BA%D0%B0%D0%BA-%D1%81%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C-%D1%81%D0%B2%D0%BE%D0%B9-maven-repository-%D0%BD%D0%B0-github/)
### Handhsake error
Russian [issue](https://overcoder.net/q/310603/%D0%BF%D1%80%D0%BE%D0%B1%D0%BB%D0%B5%D0%BC%D1%8B-%D1%81-%D0%BF%D0%BE%D0%B4%D0%BA%D0%BB%D1%8E%D1%87%D0%B5%D0%BD%D0%B8%D0%B5%D0%BC-%D1%87%D0%B5%D1%80%D0%B5%D0%B7-https-ssl-%D1%87%D0%B5%D1%80%D0%B5%D0%B7-%D1%81%D0%BE%D0%B1%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D1%8B%D0%B9-%D0%BA%D0%BB%D0%B8%D0%B5%D0%BD%D1%82-java)
 
### Incorrect encoding workaround
			byte[] encodedHTML = null;
			byte[] encodedJS = null;
			try {
				encodedHTML = Base64.encodeBase64(templateParrentGroup.getHTML().getBytes("Cp1251"));
				encodedJS = Base64.encodeBase64(templateParrentGroup.getJS().getBytes("Cp1251"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
