package com.app.multithreading;

import com.app.multithreading.config.ConfigManager;
import com.app.multithreading.port.Port;
import com.app.multithreading.ship.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    
    public static void main(String[] args) {
        logger.info("Starting port simulation");

        ConfigManager configManager = ConfigManager.getInstance();

        String configFile = "src/main/resources/config.txt";
        try {
            configManager.loadConfig(configFile);
        } catch (IOException e) {
            logger.error("Failed to load config file: {}", e.getMessage());
            logger.info("Using default configuration");
            configManager = ConfigManager.getInstance();
            createDefaultConfig(configFile);
            try {
                configManager.loadConfig(configFile);
            } catch (IOException ex) {
                logger.error("Failed to load default config: {}", ex.getMessage());
                return;
            }
        }

        Port port = Port.getInstance(
            configManager.getPortBerths(),
            configManager.getPortCapacity(),
            configManager.getPortInitialContainers()
        );

        List<Ship> ships = new ArrayList<>();
        for (ConfigManager.ShipConfig shipConfig : configManager.getShipConfigs()) {
            Ship ship = new Ship(
                shipConfig.name(),
                shipConfig.containersToTake(),
                shipConfig.containersToLeave(),
                port
            );
            ships.add(ship);
        }
        
        logger.info("Created {} ships", ships.size());

        for (Ship ship : ships) {
            try {
                ship.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for ships", e);
            }
        }
        
        logger.info("All ships have finished their tasks");
    }
    
    private static void createDefaultConfig(String filename) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(filename);
            writer.write("4 1000 100\n"); // причалы
            writer.write("20 0\n"); // корабли
            writer.write("20 0\n");
            writer.write("20 0\n");
            writer.write("20 0\n");
            writer.write("0 20\n");
            writer.write("0 20\n");
            writer.write("0 20\n");
            writer.write("0 20\n");
            writer.close();
            logger.info("Created default config file: {}", filename);
        } catch (IOException e) {
            logger.error("Failed to create default config file", e);
        }
    }
}