package com.inspur.api.pub;

import java.io.IOException;
import java.util.Properties;

public class PropertiesLoaderUtils {
	
	public static Properties loadAllProperties(String file) {
		Properties prop = new Properties();
		try {
			prop.load(PropertiesLoaderUtils.class.getResourceAsStream("/"+file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
