package com.app.multithreading.port;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger logger = LogManager.getLogger();
    private static Port instance;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition berthAvailable = lock.newCondition();
    private final List<Thread> dockedShips = new ArrayList<>();
    private final int containersCapacity;

    private int availableBerths;
    private int currentContainers;

    private Port(int berths, int containersCapacity, int currentContainers) {
        this.availableBerths = berths;
        this.containersCapacity = containersCapacity;
        this.currentContainers = currentContainers;
        logger.info("Port initialized: {} berths, capacity: {}, current containers: {}",
                berths, containersCapacity, currentContainers);
    }

    public static Port getInstance(int berths, int containersCapacity, int currentContainers) {
        if (instance == null) {
            instance = new Port(berths, containersCapacity, currentContainers);
        }
        return instance;
    }

    public boolean addContainer() {
        lock.lock();
        try {
            if (currentContainers < containersCapacity) {
                currentContainers++;
                logger.debug("Container added. Current containers: {}/{}", currentContainers, containersCapacity);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean takeContainer() {
        lock.lock();
        try {
            if (currentContainers > 0) {
                currentContainers--;
                logger.debug("Container taken. Current containers: {}/{}", currentContainers, containersCapacity);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }


    public void requestBerth() {
        lock.lock();
        try {
            while (availableBerths == 0) {
                logger.debug("{} waiting for berth. Available berths: {}",
                        Thread.currentThread().getName(), availableBerths);
                berthAvailable.await();
            }
            availableBerths--;
            dockedShips.add(Thread.currentThread());
            logger.info("{} has received permission to dock. Available berths: {}",
                    Thread.currentThread().getName(), availableBerths);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("{} interrupted while waiting for berth", Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
    }


    public void releaseBerth() {
        lock.lock();
        try {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            logger.info("{} is leaving berth", Thread.currentThread().getName());
            logger.info("Current containers in Port: {}/{}", currentContainers, containersCapacity);

            if (dockedShips.contains(Thread.currentThread())) {
                availableBerths++;
                dockedShips.remove(Thread.currentThread());
            }

            berthAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }
}