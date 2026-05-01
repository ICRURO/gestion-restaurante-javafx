package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.Sales;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesDAOImpl implements ISalesDAO {

    @Override
    public List<Sales> getAllSales() {
        List<Sales> salesList = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sale_date DESC";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Sales sale = new Sales(); 
                sale.setId(rs.getInt("id"));
                sale.setBillId(rs.getInt("bill_id"));
                sale.setTableId(rs.getInt("table_id"));
                sale.setSubtotal(rs.getDouble("subtotal"));
                sale.setTip(rs.getDouble("tip"));
                sale.setTotalFinal(rs.getDouble("total")); 
                sale.setPaymentMethod(rs.getString("payment_method"));
                sale.setItems(rs.getString("items"));
                if (rs.getTimestamp("sale_date") != null) {
                    sale.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
                }  
                salesList.add(sale);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
        }
        return salesList;
    }
}