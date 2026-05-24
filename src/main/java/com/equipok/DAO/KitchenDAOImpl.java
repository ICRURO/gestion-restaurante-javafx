package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.OrderKitchen;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KitchenDAOImpl implements IKitchenDAO {

    @Override
    public List<OrderKitchen> obtenerPedidosPendientes() {
        List<OrderKitchen> lista = new ArrayList<>();
        String sql = "SELECT bi.id, bi.bill_id, b.table_id, bi.product_name, bi.special_note, bi.status " +
                     "FROM bill_items bi " +
                     "JOIN bills b ON bi.bill_id = b.id " +
                     "WHERE bi.status IN ('PENDING', 'IN_PREPARATION') " +
                     "ORDER BY bi.id ASC";
        
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new OrderKitchen(
                    rs.getInt("id"),
                    rs.getInt("bill_id"),
                    rs.getInt("table_id"),
                    rs.getString("product_name"),
                    rs.getString("special_note"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizarEstadoPlatillo(int idItem, String nuevoEstado) {
        String sql = "UPDATE bill_items SET status = ? WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idItem);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean marcarProductoAgotadoPorNombre(String productName) {
        // Apunta directamente a tu tabla 'products' y columna 'name'
        String sql = "UPDATE products SET stock = 0 WHERE name = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, productName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
