package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.CorteCaja;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CorteCajaDAOImpl implements ICorteCajaDAO {

    @Override
    public Map<String, Double> obtenerVentasDelDia() {
        Map<String, Double> totales = new HashMap<>();
        totales.put("Efectivo", 0.0);
        totales.put("Tarjeta de crédito", 0.0);

        String sql = "SELECT payment_method, SUM(total) AS total_metodo FROM sales " +
                     "WHERE DATE(sale_date) = CURDATE() GROUP BY payment_method";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String metodo = rs.getString("payment_method");
                double suma = rs.getDouble("total_metodo");
                if (totales.containsKey(metodo)) {
                    totales.put(metodo, suma);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return totales;
    }

    @Override
    public boolean registrarCorteCaja(CorteCaja c) {
        String sql = "INSERT INTO cortes_caja (fondo_apertura, ventas_efectivo_esperado, " +
                     "ventas_tarjeta_esperado, efectivo_real_contado, diferencia, usuario_cajero, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
                     
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, c.getFondoApertura());
            ps.setDouble(2, c.getVentasEfectivoEsperado());
            ps.setDouble(3, c.getVentasTarjetaEsperado());
            ps.setDouble(4, c.getEfectivoRealContado());
            ps.setDouble(5, c.getDiferencia());
            ps.setString(6, c.getUsuarioCajero());
            ps.setString(7, c.getObservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}