package ru.clevertec.product.util;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YAMLParser {
	private final Map<String, Object> yamlProperties;
	
	public YAMLParser(String propertyFile) {
		Yaml yaml = new Yaml();
		InputStream fileSettingsStream = this.getClass().getClassLoader().getResourceAsStream(propertyFile);
		yamlProperties =  yaml.load(fileSettingsStream);
	}
	
	@SuppressWarnings("unchecked")
	public String getProperty(String propertyObject, String propertyName) {
		Map<String, Object> objectProperties = (Map<String, Object>) yamlProperties.get(propertyObject);
		String propertyValue = String.valueOf((Object) objectProperties.get(propertyName));
		return propertyValue;
	}
	
	@SuppressWarnings("unchecked")
	public String getProperty(String propertyObject, String propertySubObject, String propertyName) {
		Map<Object, Object> objectProperties = (Map<Object, Object>) yamlProperties.get(propertyObject);
		Map<Object, Object> subObjectProperties = (Map<Object, Object>) objectProperties.get(propertySubObject);
		String propertyValue = String.valueOf((Object) subObjectProperties.get(propertyName));
		return propertyValue;
	}
}
