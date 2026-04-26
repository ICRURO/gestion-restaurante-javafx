package com.equipok.DAO;

import com.equipok.model.Bill;
import com.equipok.model.Product;
import java.util.List;

public interface IBillDAO {
    boolean saveBill(Bill bill, List<Product> products);
    List<Bill> getUnpaidBills(); 
    List<Product> getPendingItems(int billId); 
    boolean processPayment(Bill bill, List<Product> itemsToPay, double tip, double discount, String method);
    void updateBillStatus(int billId);
    Bill getActiveBillByTable(int tableId);
    boolean addProductsToExistingBill(int billId, List<Product> products);
}