package com.equipok.DAO;

import com.equipok.model.Bill;
import com.equipok.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAOImpl implements IBillDAO {

    @Override
    public boolean saveBill(Bill bill) {
        String sql = "INSERT INTO bills (table_id, items, total, status_bill) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bill.getTableId());
            ps.setString(2, bill.getItems());
            ps.setDouble(3, bill.getTotal());
            ps.setString(4, "PENDING");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Bill> getUnpaidBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE status_bill = 'PENDING'";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { // Usamos executeQuery para SELECT
            
            while (rs.next()) {
                bills.add(new Bill(
                    rs.getInt("id"),
                    rs.getInt("table_id"),
                    rs.getString("items"),
                    rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    @Override
    public boolean processPayment(Bill bill, double tip, String method) {
        String sqlBill = "UPDATE bills SET status_bill = 'PAID' WHERE id = ?";
        String sqlTable = "UPDATE tables SET status_table = 'AVAILABLE' WHERE table_id = ?";
        String sqlSale = "INSERT INTO sales (bill_id, table_id, subtotal, tip, total, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false); 
            try (PreparedStatement psB = conn.prepareStatement(sqlBill);
                 PreparedStatement psT = conn.prepareStatement(sqlTable);
                 PreparedStatement psS = conn.prepareStatement(sqlSale)) {
                psB.setInt(1, bill.getId());
                psB.executeUpdate();
                psT.setInt(1, bill.getTableId());
                psT.executeUpdate();
                psS.setInt(1, bill.getId());
                psS.setInt(2, bill.getTableId());
                psS.setDouble(3, bill.getTotal());
                psS.setDouble(4, tip);
                psS.setDouble(5, bill.getTotal() + tip);
                psS.setString(6, method);
                psS.executeUpdate();
                conn.commit(); 
                return true;
            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}