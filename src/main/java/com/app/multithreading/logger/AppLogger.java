package com.app.multithreading.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;

public class AppLogger {
    private static AppLogger instance;
    private Logger logger;

    private AppLogger() {
        initializeLogger();
    }

    public static AppLogger getInstance() {
        if (instance == null) {
            synchronized (AppLogger.class) {
                if (instance == null) {
                    instance = new AppLogger();
                }
            }
        }
        return instance;
    }

    private void initializeLogger() {
        String projectPath = System.getProperty("user.dir");
        File configFile = new File(projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "log4j2.xml");
        
        if (configFile.exists()) {
            String configPath = configFile.getAbsolutePath();
            try {
                System.setProperty("log4j.configurationFile", configPath);
                Configurator.initialize(null, configPath);
                logger = LogManager.getLogger(AppLogger.class);
            } catch (Exception e) {
                System.err.println("Error loading configuration: " + e.getMessage());
                e.printStackTrace();
                logger = LogManager.getLogger(AppLogger.class);
            }
        } else {
            System.err.println("Could not find file log4j2.xml in path: " + configFile.getAbsolutePath());
            logger = LogManager.getLogger(AppLogger.class);
        }
    }
    
    public void info(String message) {
        logger.info(message);
    }
    
    public void warn(String message) {
        logger.warn(message);
    }
    
    public void error(String message) {
        logger.error(message);
    }
    
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}

