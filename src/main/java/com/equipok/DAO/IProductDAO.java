package com.equipok.DAO;

import com.equipok.model.Product;
import java.util.List;

public interface IProductDAO {
    boolean agregarProducto(Product producto);
    List<Product> obtenerProductos();
    boolean eliminarProducto(int id);
}