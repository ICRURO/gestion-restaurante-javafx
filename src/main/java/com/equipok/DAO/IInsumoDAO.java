package com.equipok.DAO;

import com.equipok.model.Insumo;
import java.util.List;

public interface IInsumoDAO {
    boolean agregarInsumo(Insumo insumo);
    boolean actualizarInsumo(Insumo insumo);
    List<Insumo> obtenerInsumos();
    boolean eliminarInsumo(int id);
    List<Insumo> obtenerInsumosBajoStock(); 
}