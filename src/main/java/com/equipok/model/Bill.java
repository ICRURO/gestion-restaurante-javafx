package com.equipok.model;

public class Bill {
    private int id;
    private int tableId;
    private String items;
    private double total;
    private String status;
    private double tip;

    public Bill(int id, int tableId, String items, double total) {
        this.id = id;
        this.tableId = tableId;
        this.items = items;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTip() {
        return tip;
    } 
    
    public void setTip(double tip) {
        this.tip = tip;
    }
}
