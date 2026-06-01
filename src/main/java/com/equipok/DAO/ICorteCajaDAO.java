package com.equipok.DAO;

import com.equipok.model.CorteCaja;
import java.util.Map;

public interface ICorteCajaDAO {
    Map<String, Double> obtenerVentasDelDia();
    boolean registrarCorteCaja(CorteCaja corte);
}