package com.equipok.DAO;

import com.equipok.model.Proveedor;
import java.util.List;

public interface IProveedorDAO {
    void addProveedor(Proveedor proveedor) throws Exception;
    List<Proveedor> getAllProveedores() throws Exception;
    void updateProveedor(Proveedor proveedor) throws Exception;
    void deleteProveedor(int id) throws Exception;
}