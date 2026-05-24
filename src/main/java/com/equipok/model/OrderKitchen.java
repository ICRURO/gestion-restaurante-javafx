package com.equipok.model;

import javafx.beans.property.*;

public class OrderKitchen {
    private final IntegerProperty idItem = new SimpleIntegerProperty();
    private final IntegerProperty idBill = new SimpleIntegerProperty();
    private final IntegerProperty idTable = new SimpleIntegerProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final StringProperty specialNote = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public OrderKitchen(int idItem, int idBill, int idTable, String productName, String specialNote, String status) {
        this.idItem.set(idItem);
        this.idBill.set(idBill);
        this.idTable.set(idTable);
        this.productName.set(productName);
        this.specialNote.set(specialNote);
        this.status.set(status);
    }

    public int getIdItem() { return idItem.get(); }
    public IntegerProperty idItemProperty() { return idItem; }

    public int getIdBill() { return idBill.get(); }
    public IntegerProperty idBillProperty() { return idBill; }

    public int getIdTable() { return idTable.get(); }
    public IntegerProperty idTableProperty() { return idTable; }

    public String getProductName() { return productName.get(); }
    public StringProperty productNameProperty() { return productName; }

    public String getSpecialNote() { return specialNote.get(); }
    public StringProperty specialNoteProperty() { return specialNote; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}
