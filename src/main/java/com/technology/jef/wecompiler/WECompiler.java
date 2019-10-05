package com.technology.jef.wecompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.technology.jef.DOMGenerator;


/**
 * Компилятор HTML файлов .
 * <p>
 * Предназначен для преобразования исходного файла интерфейса в формате XML
 * в результирующий файл, содержащий HTML и JavaScript,
 * размещается в той же директории, что исходный файл.
 * <p>
 * <p>
 * <u>Правила вызова:</u><br>
 * <p>
 * <I><code>java -cp &lt;ScriptletAndDataSourceClasses&gt;;jef.jar com.technology.jef.wecompiler.WECompiler Interface.xml</code></I>
 * <I><code>java -cp &lt;ScriptletAndDataSourceClasses&gt;;jef.jar com.technology.jef.wecompiler.WECompiler interface_directory</code></I>
 * <p>
 * <p>
 * <u>Примеры использования:</u>
 * <p>
 * Компиляция интерфейса модуля <I>&lt;ModuleName&gt;</I> из <I>make.bat</I><br>
 * <I><code>
 * ...<br>
 * %JAVA_HOME%\bin\java -cp %CLASSES%;%WEB-ENGINE_LIB%\lib\jef.jar com.technology.jef.wecompiler.WECompiler %RESOURCE_HOME%\%MODULE_PACKAGE%\jef\%MODULE_NAME%Interface.xml<br>
 * ...<br>
 * </code></I>
 * <I><code>
 * ...<br>
 * %JAVA_HOME%\bin\java -cp %CLASSES%;%WEB-ENGINE_LIB%\lib\jef.jar com.technology.jef.wecompiler.WECompiler %RESOURCE_HOME%\%MODULE_PACKAGE%\jef<br>
 * ...<br>
 * </code></I>
 */
public class WECompiler {

	/**
	 * @param args параметры запуска
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			showUsageError("Wrong parameter number.");
		}

		try {
			if (args.length == 3) {
				compile(args[0], args[1], args[2]);
				
			} else if (args.length < 3) {
				compile(args[0], "src/html/", "src/html/");
			}
		} catch (Exception e) {
			showError(e.getMessage());
		}
	}

	private static void compile(String srcFilePath, String destHtmlPath, String destJsPath) {
		File srcFile = new File(srcFilePath);

		if ( srcFile.isFile()) {
			// Передали 1 файл
			String srcFileName = srcFile.getAbsolutePath();

			
			try {
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            SAXParser saxParser = factory.newSAXParser();
	 
	            // Здесь мы определили анонимный класс, расширяющий класс DefaultHandler до нужной нам функциональности
	            DefaultHandler handler = new DefaultHandler() {
	                
	 
	                // Метод вызывается когда SAXParser "натыкается" на интерфейс
	                @Override
	                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        				// Нашли тег интерфейса
	                	if (qName.equals("interface")) {
	        				DOMGenerator generator = new DOMGenerator(attributes.getValue("id"), srcFileName);

	        				generator.createDOM();

							try {
		        				PrintWriter outHTML;
								outHTML = new PrintWriter(destHtmlPath + "/" +  attributes.getValue("id") + ".html");
		        				outHTML.println(generator.getHtml());
		        				outHTML.close();
							} catch (FileNotFoundException e) {
								showError(e.getMessage());
							}

							try {
		        				PrintWriter outJS = new PrintWriter(destJsPath + "/" +  attributes.getValue("id") + ".js");
		        				outJS.println(generator.getJs());
		        				outJS.close();
							} catch (FileNotFoundException e) {
								showError(e.getMessage());
							}

	                	}
	                }
	            };
	 
	            // Стартуем разбор методом parse, которому передаем наследника от DefaultHandler, который будет вызываться в нужные моменты
	            saxParser.parse(srcFileName, handler);
		 
			} catch (Exception e) {
				showError(e.getMessage());
			}
		} else { // передали директорию
			// Создание списка файлов, состоящих из исходников текущей директории и вложенных директорий
			File[] files = srcFile.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String path = dir + "/" + name;
					return new File(path).isDirectory() || name.endsWith(".xml");
				}
			});

			if (files != null) {
				for (File file : files) {
					compile(file.getPath(), destHtmlPath, destJsPath);
				}
			}
		}
	}

	private static void showError(String message) {
		System.out.println("ERROR: " + message);
	}

	private static void showUsageError(String message) {
		System.out.println(message);
		System.out.println("Usage:");
		System.out.println("java -jar wecompiler <sourceFilePath>");
		System.out.println("where:");
		System.out.println("sourceFilePath:\t\tFull path of the file containing a interface definition");

		System.exit(-1);
	}

}