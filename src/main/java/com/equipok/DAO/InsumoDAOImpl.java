package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.Insumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAOImpl implements IInsumoDAO {

    @Override
    public boolean agregarInsumo(Insumo insumo) {
        String sql = "INSERT INTO insumos (name, quantity, unit, min_stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, insumo.getName());
            pstmt.setDouble(2, insumo.getQuantity());
            pstmt.setString(3, insumo.getUnit());
            pstmt.setDouble(4, insumo.getMinStock());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizarInsumo(Insumo insumo) {
        String sql = "UPDATE insumos SET name = ?, quantity = ?, unit = ?, min_stock = ? WHERE insumo_id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, insumo.getName());
            pstmt.setDouble(2, insumo.getQuantity());
            pstmt.setString(3, insumo.getUnit());
            pstmt.setDouble(4, insumo.getMinStock());
            pstmt.setInt(5, insumo.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Insumo> obtenerInsumos() {
        List<Insumo> listaInsumos = new ArrayList<>();
        String sql = "SELECT insumo_id, name, quantity, unit, min_stock FROM insumos";
        
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Insumo insumo = new Insumo(
                    rs.getInt("insumo_id"),
                    rs.getString("name"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getDouble("min_stock")
                );
                listaInsumos.add(insumo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaInsumos;
    }

    @Override
    public boolean eliminarInsumo(int id) {
        String sql = "DELETE FROM insumos WHERE insumo_id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Insumo> obtenerInsumosBajoStock() {
        List<Insumo> listaInsumos = new ArrayList<>();
        String sql = "SELECT insumo_id, name, quantity, unit, min_stock FROM insumos WHERE quantity <= min_stock";
        
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Insumo insumo = new Insumo(
                    rs.getInt("insumo_id"),
                    rs.getString("name"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getDouble("min_stock")
                );
                listaInsumos.add(insumo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaInsumos;
    }
}