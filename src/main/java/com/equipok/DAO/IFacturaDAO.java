package com.equipok.DAO;

import com.equipok.model.Sales;
import com.equipok.model.Factura;

public interface IFacturaDAO {
    Sales buscarVentaPorId(int saleId);
    boolean registrarFactura(Factura factura);
    boolean existeFactura(int saleId);
}