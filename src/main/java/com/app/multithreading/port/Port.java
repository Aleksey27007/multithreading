package com.app.multithreading.port;

import com.app.multithreading.warehouse.Warehouse;

public class Port {
    private final Berth berth;
    private final Warehouse warehouse;

    public Port(int warehouseCapacity) {
        this.berth = new Berth();
        this.warehouse = new Warehouse(warehouseCapacity);
    }

    public Berth getBerth() {
        return berth;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
}
