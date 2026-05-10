package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.Table;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    public List<Table> findAll() {
        List<Table> list = new ArrayList<>();
        String sql = "SELECT * FROM tables";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Table(
                    rs.getInt("table_id"), 
                    rs.getString("status_table"),
                    rs.getInt("capacity") 
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean save(Table t) {
        String sql = "INSERT INTO tables (table_id, status_table, capacity) VALUES (?, 'AVAILABLE', ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getTableId());
            ps.setInt(2, t.getCapacity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean update(Table t) {
        String sql = "UPDATE tables SET capacity = ? WHERE table_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getCapacity());
            ps.setInt(2, t.getTableId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM tables WHERE table_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}
