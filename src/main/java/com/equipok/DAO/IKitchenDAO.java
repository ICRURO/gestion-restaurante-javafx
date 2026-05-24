package com.equipok.DAO;

import com.equipok.model.OrderKitchen;
import java.util.List;

public interface IKitchenDAO {
    List<OrderKitchen> obtenerPedidosPendientes();
    boolean actualizarEstadoPlatillo(int idDetalle, String nuevoEstado);
    boolean marcarProductoAgotadoPorNombre(String nombrePlatillo);
}
