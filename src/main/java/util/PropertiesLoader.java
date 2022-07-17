package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PropertiesLoader {
    private static final String REDIRECT_PROPERTIES = "redirect.properties";

    public static String getProperty (String propertyName) throws IOException {
        try(InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(REDIRECT_PROPERTIES)){
            if (input == null){
                throw new IOException("The file \"" + REDIRECT_PROPERTIES + "\" is empty or epson");
            }
            Properties propertiesFile = new Properties();
            propertiesFile.load(input);
            propertyName = propertyName.toLowerCase(Locale.ROOT).replace(" ", "_");
            return propertiesFile.getProperty(propertyName);
        }
    }

    public static Properties getAllPropertiesFromPropertyFile(String fileName) throws IOException {
        try(InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName)){
            if (input == null){
                throw new IOException("The file \"" + fileName + "\" is empty or epson");
            }
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        }
    }
}