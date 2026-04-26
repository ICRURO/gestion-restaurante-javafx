package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements IProductDAO {

    @Override
    public boolean agregarProducto(Product producto) {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getName());
            pstmt.setDouble(2, producto.getPrice());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Product> obtenerProductos() {
        List<Product> listaProductos = new ArrayList<>();
        String sql = "SELECT product_id, name, price FROM products";
        
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product producto = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                );
                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaProductos;
    }

    @Override
    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}