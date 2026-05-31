package com.equipok.DAO;

import com.equipok.model.Empleado;
import com.equipok.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAOImpl implements IEmpleadoDAO {

    @Override
    public boolean registrarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, puesto, telefono, turno) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getPuesto());
            ps.setString(3, empleado.getTelefono());
            ps.setString(4, empleado.getTurno());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Empleado> listarEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getInt("id"));
                e.setNombre(rs.getString("nombre"));
                e.setPuesto(rs.getString("puesto"));
                e.setTelefono(rs.getString("telefono"));
                e.setTurno(rs.getString("turno"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        return lista;
    }

    @Override
    public boolean eliminarEmpleado(int id) {
        String sql = "DELETE FROM empleados WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    /* ñ empleados */
    public boolean registerWaiterOnly(Empleado employee) {
        String sql = "INSERT INTO waiters (name, phone, status) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, employee.getNombre());
            ps.setString(2, employee.getTelefono());
            ps.setString(3, "ACTIVE");
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error (registerWaiterOnly): " + e.getMessage());
            return false;
        }
    }

    public List<Empleado> listWaitersOnly() {
        List<Empleado> list = new ArrayList<>();
        String sql = "SELECT waiter_id, name, phone, status FROM waiters";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getInt("waiter_id"));
                e.setNombre(rs.getString("name"));
                e.setTelefono(rs.getString("phone"));
                e.setPuesto("Mesero"); 
                e.setTurno(rs.getString("status")); // Mapping 'status' to 'turno' properties safely
                list.add(e);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error (listWaitersOnly): " + e.getMessage());
        }
        return list;
    }

    public boolean updateWaiterData(Empleado employee) {
        String sql = "UPDATE waiters SET name = ?, phone = ?, status = ? WHERE waiter_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, employee.getNombre());
            ps.setString(2, employee.getTelefono());
            ps.setString(3, employee.getTurno()); 
            ps.setInt(4, employee.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error (updateWaiterData): " + e.getMessage());
            return false;
        }
    }

    public boolean disableWaiterStatus(int id) {
        String sql = "UPDATE waiters SET status = 'INACTIVE' WHERE waiter_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error (disableWaiterStatus): " + e.getMessage());
            return false;
        }
    }

    public boolean deleteWaiterPhysically(int id) {
    String sql = "DELETE FROM waiters WHERE waiter_id = ?";
    try (Connection con = ConexionDB.obtenerConexion(); 
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
        } catch (SQLException e) {
        System.err.println("SQL Error (deleteWaiterPhysically): " + e.getMessage());
        return false;
        }
    }

}