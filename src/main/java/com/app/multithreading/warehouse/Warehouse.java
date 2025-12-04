package com.app.multithreading.warehouse;

public class Warehouse {
    private int capacity;
    private int current;


    public Warehouse(int capacity) {
        this.capacity = capacity;
        this.current = 0;
    }

    public boolean addContainers(int count) {
        if (current + count <= capacity) {
            current += count;
            return true;
        }
        return false;
    }

    public boolean removeContainers(int count) {
        if (current >= count) {
            current -= count;
            return true;
        }
        return false;
    }

    public int getCurrent() {
        return current;
    }
}
