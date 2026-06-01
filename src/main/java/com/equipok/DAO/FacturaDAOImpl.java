package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.Sales;
import com.equipok.model.Factura;
import java.sql.*;

public class FacturaDAOImpl implements IFacturaDAO {

    @Override
    public Sales buscarVentaPorId(int saleId) {
        String sql = "SELECT * FROM sales WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sales sale = new Sales();
                    sale.setId(rs.getInt("id"));
                    sale.setSubtotal(rs.getDouble("subtotal"));
                    sale.setTip(rs.getDouble("tip"));
                    sale.setTotalFinal(rs.getDouble("total"));
                    sale.setPaymentMethod(rs.getString("payment_method"));
                    sale.setItems(rs.getString("items"));
                    return sale;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean registrarFactura(Factura f) {
        String sql = "INSERT INTO facturas (sale_id, rfc, razon_social, regimen_fiscal, codigo_postal, uso_cfdi, uuid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getSaleId());
            ps.setString(2, f.getRfc());
            ps.setString(3, f.getRazonSocial());
            ps.setString(4, f.getRegimenFiscal());
            ps.setString(5, f.getCodigoPostal());
            ps.setString(6, f.getUsoCfdi());
            ps.setString(7, f.getUuid());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean existeFactura(int saleId) {
        String sql = "SELECT id FROM facturas WHERE sale_id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}