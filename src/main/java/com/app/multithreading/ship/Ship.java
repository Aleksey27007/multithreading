package com.app.multithreading.ship;

import com.app.multithreading.logger.AppLogger;
import com.app.multithreading.port.Berth;
import com.app.multithreading.port.Port;

public class Ship implements Runnable {
    private static final AppLogger logger = AppLogger.getInstance();
    private final Port port;
    private String name;
    private int containerCount;


    public Ship(String name, int containerCount, Port port) {
        this.name = name;
        this.containerCount = containerCount;
        this.port = port;
    }

    @Override
    public void run() {
        boolean serviced = false;
        while (!serviced) {
            Berth berth = port.getBerth();
            if (berth.tryOccupy()) {
                try {
                    logger.info("Ship " + name + " occupies a berth.");
                    if (containerCount > 0) {
                        if (port.getWarehouse().addContainers(containerCount)) {
                            logger.info("Ship " + name + " unloaded " + containerCount + " containers.");
                        } else {
                            logger.info("There are not enough containers in the warehouse to load the ship " + name + ".");
                        }
                    } else {
                        if (port.getWarehouse().removeContainers(-containerCount)) {
                            logger.info("Ship " + name + " unloaded " + -containerCount + " containers.");
                        } else {
                            logger.info("There are not enough containers in the warehouse to load the ship. " + name + ".");
                        }
                    }
                    TimeUnit.SECONDS.sleep(1);
//                    Thread.sleep(1000);
                    serviced = true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    berth.free();
                    logger.info("Ship " + name + " clears the berth.");
                    logger.info("-----------------------------------");
                }
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
//                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}