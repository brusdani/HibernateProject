package com.example.databaseapplication.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    private Properties properties;

    public Configuration(){
        properties = loadProperties();
    }
    private Properties loadProperties() {
        Properties p = new Properties();

        try (FileReader fr = new FileReader("configuration.properties", StandardCharsets.UTF_8)) {
            p.load(fr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }

    public String getValue(String key){
        return properties.getProperty(key);
    }

    public Map<String, String> getDbCredentials(){
        return Map.of(
                "javax.persistence.jdbc.user", properties.getProperty("db.user"),
                "javax.persistence.jdbc.password", properties.getProperty("db.password")
        );
    }


}
