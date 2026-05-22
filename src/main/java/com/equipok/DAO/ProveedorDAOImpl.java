package com.equipok.DAO;

import com.equipok.ConexionDB;
import com.equipok.model.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImpl implements IProveedorDAO {

    @Override
    public void addProveedor(Proveedor proveedor) throws Exception {
        String sql = "INSERT INTO proveedores (nombre, empresa, telefono, correo, categoria) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getEmpresa());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getCorreo());
            pstmt.setString(5, proveedor.getCategoria());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Proveedor> getAllProveedores() throws Exception {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores";
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Proveedor prov = new Proveedor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("empresa"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("categoria")
                );
                proveedores.add(prov);
            }
        }
        return proveedores;
    }

    @Override
    public void updateProveedor(Proveedor proveedor) throws Exception {
        String sql = "UPDATE proveedores SET nombre=?, empresa=?, telefono=?, correo=?, categoria=? WHERE id=?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getEmpresa());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getCorreo());
            pstmt.setString(5, proveedor.getCategoria());
            pstmt.setInt(6, proveedor.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteProveedor(int id) throws Exception {
        String sql = "DELETE FROM proveedores WHERE id=?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}