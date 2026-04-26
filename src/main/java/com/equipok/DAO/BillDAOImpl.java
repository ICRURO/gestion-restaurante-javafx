package com.equipok.DAO;//Ya todo se pasó al DAO :))))))))))))))))))))))))))))))))))))))

import com.equipok.model.Bill;
import com.equipok.model.Product;
import com.equipok.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAOImpl implements IBillDAO {

    @Override
public boolean saveBill(Bill bill, List<Product> products) {
    String sqlBill = "INSERT INTO bills (table_id, items, total, status_bill) VALUES (?, ?, ?, 'PENDING')";
    String sqlItem = "INSERT INTO bill_items (bill_id, product_name, price, status) VALUES (?, ?, ?, 'PENDING')";

    try (Connection con = ConexionDB.obtenerConexion()) {
        con.setAutoCommit(false); 
        try (PreparedStatement psB = con.prepareStatement(sqlBill, Statement.RETURN_GENERATED_KEYS)) {
            psB.setInt(1, bill.getTableId());
            psB.setString(2, "Pedido en mesa " + bill.getTableId());
            psB.setDouble(3, bill.getTotal());
            psB.executeUpdate();
            try (ResultSet rs = psB.getGeneratedKeys()) {
                if (rs.next()) {
                    int billId = rs.getInt(1);
                    bill.setId(billId);
                    try (PreparedStatement psI = con.prepareStatement(sqlItem)) {
                        for (Product p : products) {
                            psI.setInt(1, billId);
                            psI.setString(2, p.getName());
                            psI.setDouble(3, p.getPrice());
                            psI.addBatch(); 
                        }
                        psI.executeBatch();
                    }
                    try (PreparedStatement psT = con.prepareStatement("UPDATE tables SET status_table = 'OCCUPIED' WHERE table_id = ?")) {
                        psT.setInt(1, bill.getTableId());
                        psT.executeUpdate();
                    }
                }
            }
            con.commit(); 
            return true;
        } catch (SQLException e) {
            con.rollback(); 
            e.printStackTrace();
            return false;
        }
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
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Bill b = new Bill(rs.getInt("id"), rs.getInt("table_id"), rs.getDouble("total"));
            b.setDiscount(rs.getDouble("discount"));
            b.setPaidAmount(rs.getDouble("paid_amount"));
            bills.add(b);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return bills;
}

    @Override
    public List<Product> getPendingItems(int billId) {
        List<Product> pending = new ArrayList<>();
        String sql = "SELECT product_name, price FROM bill_items WHERE bill_id = ? AND status = 'PENDING'";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pending.add(new Product(rs.getString("product_name"), rs.getDouble("price")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return pending;
    }

    @Override
    public boolean processPayment(Bill bill, List<Product> itemsToPay, double tip, double discount, String method) {
        double subtotal = itemsToPay.stream().mapToDouble(Product::getPrice).sum();
        String sqlSale = "INSERT INTO sales (bill_id, table_id, subtotal, tip, total, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlMarkItem = "UPDATE bill_items SET status = 'PAID' WHERE bill_id = ? AND product_name = ? AND status = 'PENDING' LIMIT 1";
        String sqlUpdateBill = "UPDATE bills SET paid_amount = paid_amount + ?, discount = ? WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psS = conn.prepareStatement(sqlSale);
                 PreparedStatement psI = conn.prepareStatement(sqlMarkItem);
                 PreparedStatement psB = conn.prepareStatement(sqlUpdateBill)) {
                psS.setInt(1, bill.getId());
                psS.setInt(2, bill.getTableId());
                psS.setDouble(3, subtotal);
                psS.setDouble(4, tip);
                psS.setDouble(5, subtotal + tip);
                psS.setString(6, method);
                psS.executeUpdate();
                for (Product p : itemsToPay) {
                    psI.setInt(1, bill.getId());
                    psI.setString(2, p.getName());
                    psI.executeUpdate();
                }
                psB.setDouble(1, subtotal);
                psB.setDouble(2, discount);
                psB.setInt(3, bill.getId());
                psB.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    @Override
    public void updateBillStatus(int billId) {
        String sql = "UPDATE bills SET status_bill = 'PAID' WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Bill getActiveBillByTable(int tableId) {
        String sql = "SELECT * FROM bills WHERE table_id = ? AND status_bill = 'PENDING' LIMIT 1";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill b = new Bill(rs.getInt("id"), rs.getInt("table_id"), rs.getDouble("total"));
                b.setDiscount(rs.getDouble("discount"));
                b.setPaidAmount(rs.getDouble("paid_amount"));
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addProductsToExistingBill(int billId, List<Product> products) {
        String sqlItem = "INSERT INTO bill_items (bill_id, product_name, price, status) VALUES (?, ?, ?, 'PENDING')";
        String sqlUpdateTotal = "UPDATE bills SET total = total + ? WHERE id = ?";
        double addedTotal = 0;
        try (Connection con = ConexionDB.obtenerConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement psI = con.prepareStatement(sqlItem);
                 PreparedStatement psU = con.prepareStatement(sqlUpdateTotal)) {
                for (Product p : products) {
                    psI.setInt(1, billId);
                    psI.setString(2, p.getName());
                    psI.setDouble(3, p.getPrice());
                    psI.addBatch();
                    addedTotal += p.getPrice();
                }
                psI.executeBatch();
                psU.setDouble(1, addedTotal);
                psU.setInt(2, billId);
                psU.executeUpdate();
                con.commit();
                return true;
            } catch (SQLException e) { con.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }
}