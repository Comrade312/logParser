package dev.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Works with properties file
 */
public class PropertiesWorker {
    private final Properties properties;

    /**
     * Constructor that loads the properties file
     *
     * @param path - path to properties file
     * @throws IOException
     */
    public PropertiesWorker(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            this.properties = new Properties();
            this.properties.load(fis);
        }
    }

    /**
     * Getting parameter by name from properties file
     *
     * @param propertyName - parameter name
     * @return - parameter
     */
    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public String getLogin() {
        return properties.getProperty("db.login");
    }

    public String getPassword() {
        return properties.getProperty("db.password");
    }

    public String getUrl() {
        return properties.getProperty("db.url");
    }

    public String getTableJson() {
        return properties.getProperty("path.tableJson");
    }

    public String getLogFile() {
        return properties.getProperty("path.logFile");
    }

    public String getLogFolder() {
        return properties.getProperty("path.logFolder");
    }

    public Boolean isArchive() {
        return Boolean.valueOf(properties.getProperty("path.isArchive"));
    }
}
