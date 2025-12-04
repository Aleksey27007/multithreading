package com.app.multithreading.port;

import java.util.concurrent.locks.ReentrantLock;

public class Berth {
    private final ReentrantLock lock = new ReentrantLock();

    public boolean tryOccupy() {
        return lock.tryLock();
    }

    public void free() {
        lock.unlock();
    }
}
