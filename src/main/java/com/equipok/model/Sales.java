package com.equipok.model;

import java.time.LocalDateTime;

public class Sales {
    private int id;
    private int billId;
    private int tableId;
    private double subtotal;
    private double tip; 
    private double totalFinal; 
    private String paymentMethod; 
    private LocalDateTime saleDate;

    public Sales(int billId, int tableId, double subtotal, double tip, String method) {
        this.billId = billId;
        this.tableId = tableId;
        this.subtotal = subtotal;
        this.tip = tip;
        this.totalFinal = subtotal + tip;
        this.paymentMethod = method;
        this.saleDate = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } 

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public double getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(double totalFinal) {
        this.totalFinal = totalFinal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }
}
