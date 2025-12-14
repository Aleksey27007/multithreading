package com.app.multithreading.ship;

import com.app.multithreading.port.Port;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Ship extends Thread {
    private static final Logger logger = LogManager.getLogger();
    private final Port port;
    private int containersToTake;
    private int containersToLeave;
    private boolean served;

    public Ship(String name, int containersToTake, int containersToLeave, Port port) {
        super(name);
        this.containersToTake = containersToTake;
        this.containersToLeave = containersToLeave;
        this.port = port;
        served = false;
        logger.info("Ship {} created: take={}, leave={}", name, containersToTake, containersToLeave);
        start();
    }

    @Override
    public void run() {
        logger.info("{} started", getName());
        // Запрашиваем причал
        port.requestBerth();

        while (!served) {
            boolean operationPerformed = false;
            // Тут разгрузить
            if (containersToLeave > 0) {
                if (port.addContainer()) {
                    containersToLeave--;
                    operationPerformed = true;
                    logger.info("{} unloaded 1 container. Remaining to unload: {}", getName(), containersToLeave);
                }
            }
            // Тут загрузить
            else if (containersToTake > 0) {
                if (port.takeContainer()) {
                    containersToTake--;
                    operationPerformed = true;
                    logger.info("{} loaded 1 container. Remaining to load: {}", getName(), containersToTake);
                }
            } else {
                served = true;
            }

            if (operationPerformed) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("{} interrupted during operation", getName());
                    break;
                }
            } else if (!served) {
                // Если операция не выполнена, освобождаем причал и ждем
                port.releaseBerth();

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                // Снова запрашиваем причал
                port.requestBerth();
            }
        }

        // Все операции выполнены
        logger.info("{} has finished all tasks", getName());
        port.releaseBerth();
    }
}