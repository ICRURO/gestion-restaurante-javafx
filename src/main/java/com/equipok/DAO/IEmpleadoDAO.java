package com.equipok.DAO;

import com.equipok.model.Empleado;
import java.util.List;

public interface IEmpleadoDAO {
    boolean registrarEmpleado(Empleado empleado);
    List<Empleado> listarEmpleados();
    boolean eliminarEmpleado(int id);
}