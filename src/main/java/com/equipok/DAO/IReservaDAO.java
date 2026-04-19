package com.equipok.DAO;
import com.equipok.model.Reserva;
import java.util.List;

public interface IReservaDAO {
    boolean guardarReserva(Reserva reserva);
    List<Integer> obtenerMesasDisponibles(); 
}