package com.app.multithreading.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger();
    private static final ReentrantLock instanceLock = new ReentrantLock();
    private static ConfigManager instance;
    private final List<ShipConfig> shipConfigs = new ArrayList<>();
    private int portBerths;
    private int portCapacity;
    private int portInitialContainers;


    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instanceLock.lock();
            try {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            } finally {
                instanceLock.unlock();
            }
        }
        return instance;
    }

    public void loadConfig(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String firstLine = reader.readLine();
            if (firstLine != null) {
                String[] parts = firstLine.trim().split("\\s+");
                if (parts.length >= 3) {
                    portBerths = Integer.parseInt(parts[0]);
                    portCapacity = Integer.parseInt(parts[1]);
                    portInitialContainers = Integer.parseInt(parts[2]);
                    logger.info("Port config loaded: berths={}, capacity={}, initial={}",
                            portBerths, portCapacity, portInitialContainers);
                }
            }

            String line;
            int shipNumber = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        String name = parts.length >= 3 ? parts[0] : "Ship " + shipNumber;
                        int containersToTake = parts.length >= 3 ? Integer.parseInt(parts[1]) : Integer.parseInt(parts[0]);
                        int containersToLeave = parts.length >= 3 ? Integer.parseInt(parts[2]) : Integer.parseInt(parts[1]);

                        shipConfigs.add(new ShipConfig(name, containersToTake, containersToLeave));
                        shipNumber++;
                    }
                }
            }

            logger.info("Loaded {} ship configurations", shipConfigs.size());
        }
    }

    public int getPortBerths() {
        return portBerths;
    }

    public int getPortCapacity() {
        return portCapacity;
    }

    public int getPortInitialContainers() {
        return portInitialContainers;
    }

    public List<ShipConfig> getShipConfigs() {
        return new ArrayList<>(shipConfigs);
    }

    public record ShipConfig(String name, int containersToTake, int containersToLeave) {
    }
}
