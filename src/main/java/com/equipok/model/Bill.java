package com.equipok.model;

import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int id;
    private int tableId;
    private double total;
    private String status;
    private double discount;    
    private double paidAmount;
    private List<Product> itemList = new ArrayList<>(); 

    public Bill(int id, int tableId, double total) {
        this.id = id;
        this.tableId = tableId;
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

    public double getDiscount() {
        return discount;
    } 

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public List<Product> getItems() {
        return itemList;
    }

    public void setItemList(List<Product> itemList) {
        this.itemList = itemList;
    }


    public double getRemainingBalance() {
        return (total + discount) - paidAmount;
    }

    public List<Product> getItemList() {
        return itemList;
    }
}