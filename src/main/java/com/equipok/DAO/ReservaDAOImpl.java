package com.equipok.DAO;

import com.equipok.model.Reserva;
import com.equipok.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements IReservaDAO {

    @Override
    public boolean guardarReserva(Reserva reserva) {
        String sql = "INSERT INTO reservas (fecha, hora, personas, nombre_cliente, telefono_cliente, table_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, reserva.getFecha());
            ps.setString(2, reserva.getHora());
            ps.setInt(3, reserva.getPersonas());
            ps.setString(4, reserva.getNombreCliente());
            ps.setString(5, reserva.getTelefonoCliente());
            ps.setInt(6, reserva.getTableId());
            
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                String updateMesa = "UPDATE tables SET status_table = 'RESERVED' WHERE table_id = ?";
                try (PreparedStatement psMesa = con.prepareStatement(updateMesa)) {
                    psMesa.setInt(1, reserva.getTableId());
                    psMesa.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Integer> obtenerMesasDisponibles() {
        List<Integer> mesas = new ArrayList<>();
        String sql = "SELECT table_id FROM tables WHERE status_table = 'AVAILABLE'";
        
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                mesas.add(rs.getInt("table_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesas;
    }

    @Override
    public List<Reserva> obtenerTodasLasReservas() {
        List<Reserva> listaReservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reserva r = new Reserva(
                    rs.getInt("id_reserva"),
                    rs.getString("fecha"),
                    rs.getString("hora"),
                    rs.getInt("personas"),
                    rs.getString("nombre_cliente"),
                    rs.getString("telefono_cliente"),
                    rs.getInt("table_id")
                );
                listaReservas.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaReservas;
    }

    @Override
    public boolean modificarReserva(Reserva reserva) {
        // Cambia la consulta SQL a:
String sql = "UPDATE reservas SET fecha = ?, hora = ?, personas = ?, nombre_cliente = ?, telefono_cliente = ? WHERE id_reserva = ?";
        
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, reserva.getFecha());
            ps.setString(2, reserva.getHora());
            ps.setInt(3, reserva.getPersonas());
            ps.setString(4, reserva.getNombreCliente());
            ps.setString(5, reserva.getTelefonoCliente());
            ps.setInt(6, reserva.getIdReserva());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean cancelarReserva(int idReserva, int tableId) {
        String sqlDelete = "DELETE FROM reservas WHERE id_reserva = ?";
        String sqlUpdateMesa = "UPDATE tables SET status_table = 'AVAILABLE' WHERE table_id = ?";
        
        try (Connection con = ConexionDB.obtenerConexion()) {
            con.setAutoCommit(false); 
            
            try (PreparedStatement psDelete = con.prepareStatement(sqlDelete);
                 PreparedStatement psMesa = con.prepareStatement(sqlUpdateMesa)) {
                 
                psDelete.setInt(1, idReserva);
                int filasEliminadas = psDelete.executeUpdate();
                
                if (filasEliminadas > 0) {
                    psMesa.setInt(1, tableId);
                    psMesa.executeUpdate();
                    con.commit();
                    return true;
                }
            } catch (SQLException ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}