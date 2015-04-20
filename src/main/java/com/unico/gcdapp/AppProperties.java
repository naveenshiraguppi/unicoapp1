package com.unico.gcdapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton to access properties in the order.
 * 	- System properties
 *  - InitializationServlet init params - take precedence over the rest.
 * @author NAVEEN
 */
public class AppProperties {
	private Properties props;
	private static AppProperties appProp;
	private AppProperties() throws IOException{
		props = System.getProperties();
		File file = new File("props.properties");
		System.out.println("SystemProperties : " + file.getAbsolutePath());
		if(file.isFile()){
			FileReader reader = new FileReader(file);
			props.load(reader);
		}
	}
	public static String getApplicationProperty(String key) throws IOException{
		if(appProp == null){
			appProp = getAppPropertiies();
		}
		if(appProp != null){
			return appProp.getProperty(key);
		}
		else
			return null;
	}
	public static AppProperties getAppPropertiies() throws IOException{
		if(appProp == null){
			synchronized(AppProperties.class){
				if(appProp == null)
					appProp = new AppProperties();
			}
		}
		return appProp;
	}
	public String getProperty(String key) {
		return props.getProperty(key);
	}
	public void addProperty(String key, String value){
		props.put(key, value);
	}
}
