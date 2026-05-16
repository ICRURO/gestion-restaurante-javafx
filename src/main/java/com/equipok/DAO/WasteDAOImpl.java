package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.Waste;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WasteDAOImpl implements IWasteDAO {

    @Override
    public boolean addWaste(Waste waste) {
        String sql = "INSERT INTO wastes (product_id, quantity, reason) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, waste.getProductId());
            pstmt.setInt(2, waste.getQuantity());
            pstmt.setString(3, waste.getReason());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Waste> getWastes() {
        List<Waste> wasteList = new ArrayList<>();
        String sql = "SELECT waste_id, product_id, quantity, reason, waste_date FROM wastes ORDER BY waste_date DESC";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Waste waste = new Waste(
                    rs.getInt("waste_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getString("reason"),
                    rs.getTimestamp("waste_date")
                );
                wasteList.add(waste);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wasteList;
    }
}
