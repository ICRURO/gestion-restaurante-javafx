package com.equipok.model;

public class Table {
    private int tableId;
    private String status; 
    private int capacity;

    public void setAvailable() {
        this.status = "AVAILABLE";
    }

    public Table(int tableId, String status, int capacity) {
        this.tableId = tableId;
        this.status = status; 
        this.capacity = capacity;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCapacity() { 
        return capacity; 
    }

    public void setCapacity(int capacity) { 
        this.capacity = capacity; 
    }
}
