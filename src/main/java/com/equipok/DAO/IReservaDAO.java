package com.equipok.DAO;

import com.equipok.model.Reserva;
import java.util.List;

public interface IReservaDAO {
    boolean guardarReserva(Reserva reserva);
    List<Integer> obtenerMesasDisponibles();
    
    List<Reserva> obtenerTodasLasReservas();
    boolean modificarReserva(Reserva reserva);
    boolean cancelarReserva(int idReserva, int tableId);
}