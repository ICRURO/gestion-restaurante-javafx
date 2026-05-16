package com.equipok.model;

import java.sql.Timestamp;

public class Waste {
    private int wasteId;
    private int productId;
    private int quantity;
    private String reason;
    private Timestamp wasteDate;

    public Waste(int productId, int quantity, String reason) {
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
    }

    public Waste(int wasteId, int productId, int quantity, String reason, Timestamp wasteDate) {
        this.wasteId = wasteId;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
        this.wasteDate = wasteDate;
    }

    public int getWasteId() { return wasteId; }
    public void setWasteId(int wasteId) { this.wasteId = wasteId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Timestamp getWasteDate() { return wasteDate; }
    public void setWasteDate(Timestamp wasteDate) { this.wasteDate = wasteDate; }
}
