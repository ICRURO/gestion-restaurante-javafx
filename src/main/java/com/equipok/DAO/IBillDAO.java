package com.equipok.DAO;

import com.equipok.model.Bill;
import java.util.List;

public interface IBillDAO {
    boolean saveBill(Bill bill); 
    List<Bill> getUnpaidBills(); 
    boolean processPayment(Bill bill, double tip, String method);
}