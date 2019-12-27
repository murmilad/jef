package com.technology.jef.wecompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.technology.jef.CurrentLocale;
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
			if (args.length == 4) {
				compile(args[0], args[1], args[2], args[3]);
				
				
				FileUtils.copyResourcesRecursively(WECompiler.class.getClassLoader().getResource("js"), new File(args[2]));

				File newDestDir = new File(args[2].replaceAll("js[\\/]?$", "css"));
				if (!newDestDir.exists() && newDestDir.mkdir()) {
					FileUtils.copyResourcesRecursively(WECompiler.class.getClassLoader().getResource("css"), newDestDir);
				}
			} else if (args.length < 4) {
				compile(args[0], "src/html/", "src/html/", "ru_RU");

				FileUtils.copyResourcesRecursively(WECompiler.class.getClassLoader().getResource("js"), new File("src/html/"));
				FileUtils.copyResourcesRecursively(WECompiler.class.getClassLoader().getResource("css"), new File("src/html/"));
			}
		} catch (Exception e) {
			showError(e.getMessage());
		}
	}


	private static void compile(String srcFilePath, String destHtmlPath, String destJsPath, String locale) {
		
		File srcFile = new File(srcFilePath);


		Locale currentLocale;
	    ResourceBundle textSource;

	    CurrentLocale.getInstance().setLocale(new Locale(locale));

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
								File fileHTML = new File(destHtmlPath + "/" +  attributes.getValue("id") + ".html");
								fileHTML.getParentFile().mkdirs();

								PrintWriter outHTML = new PrintWriter(new OutputStreamWriter(
										new FileOutputStream(fileHTML), StandardCharsets.UTF_8), true);
		        				outHTML.println(generator.getHtml());
		        				outHTML.close();
							} catch (FileNotFoundException e) {
								showError(e.getMessage());
							}

							try {
								File fileJS = new File(destJsPath + "/" +  attributes.getValue("id") + ".js");
								fileJS.getParentFile().mkdirs();

		        				PrintWriter outJS = new PrintWriter(new OutputStreamWriter(
										new FileOutputStream(fileJS), StandardCharsets.UTF_8), true);
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
					compile(file.getPath(), destHtmlPath, destJsPath, locale);
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