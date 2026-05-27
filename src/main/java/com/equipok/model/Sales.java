package com.equipok.model;

import java.time.LocalDateTime;

/**
 * <p>
 * Clase Sales - Modelo de Datos para Ventas Finalizadas
 * </p>
 * @author Ian R. - Equipo K
 * @version 2.0
 * @since 2026-04-18
 */

public class Sales {
    /**
     * Atributos de la clase Sales
     */
    private int id;
    private int billId;
    private int tableId;
    private double subtotal;
    private double tip; 
    private double totalFinal; 
    private String paymentMethod; 
    private LocalDateTime saleDate;
    private String items;
    public Sales() {}

 /**
  * Constructor para crear una nueva venta finalizada con detalles de los items vendidos
  * @param billId
  * @param tableId
  * @param subtotal
  * @param tip
  * @param method
  * @param items
  */

    public Sales(int billId, int tableId, double subtotal, double tip, String method, String items) {
        this.billId = billId;
        this.tableId = tableId;
        this.subtotal = subtotal;
        this.tip = tip;
        this.totalFinal = subtotal + tip;
        this.paymentMethod = method;
        this.saleDate = LocalDateTime.now();
        this.items = items;
    }

       /**
     * Constructor para crear una nueva venta finalizada
     * @param billId
     * @param tableId
     * @param subtotal
     * @param tip
     * @param method
     */

    public Sales(int billId, int tableId, double subtotal, double tip, String method) {
        this.billId = billId;
        this.tableId = tableId;
        this.subtotal = subtotal;
        this.tip = tip;
        this.totalFinal = subtotal + tip;
        this.paymentMethod = method;
        this.saleDate = LocalDateTime.now();
    }

/**
 * Getters y Setters
 * @return
 */
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

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    /**
     * Métodos para obtener el día, mes, año y hora de la venta
     * @return
     */
 
    public int getSaleHour() {
        return this.saleDate != null ? this.saleDate.getHour() : 0;
    }

    public int getSaleMonthValue() {
        return this.saleDate != null ? this.saleDate.getMonthValue() : 0;
    }

    public int getSaleYear() {
        return this.saleDate != null ? this.saleDate.getYear() : 0;
    }
}
