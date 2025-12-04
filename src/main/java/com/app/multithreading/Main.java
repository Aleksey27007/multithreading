package com.app.multithreading;

import com.app.multithreading.logger.AppLogger;
import com.app.multithreading.port.Port;
import com.app.multithreading.ship.Ship;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final AppLogger logger = AppLogger.getInstance();
    public static void main(String[] args) throws InterruptedException {
        Port port = new Port(100);
        int threadCount = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int containerLoad = (i % 2 == 0) ? 10 : -10;
            String shipName = "Ship_" + i;
            Ship ship = new Ship(shipName, containerLoad, port);
            executor.submit(ship);
        }


        executor.shutdown();

        Thread.sleep(threadCount * 1000);
        logger.info("Total remaining containers in the warehouse: " + port.getWarehouse().getCurrent());

    }
}
