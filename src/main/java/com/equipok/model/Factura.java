package com.equipok.model;

import java.time.LocalDateTime;

public class Factura {
    private int id;
    private int saleId;
    private String rfc;
    private String razonSocial;
    private String regimenFiscal;
    private String codigoPostal;
    private String usoCfdi;
    private String uuid;
    private LocalDateTime fechaFacturacion;

    public Factura() {}

    public Factura(int saleId, String rfc, String razonSocial, String regimenFiscal, String codigoPostal, String usoCfdi, String uuid) {
        this.saleId = saleId;
        this.rfc = rfc;
        this.razonSocial = razonSocial;
        this.regimenFiscal = regimenFiscal;
        this.codigoPostal = codigoPostal;
        this.usoCfdi = usoCfdi;
        this.uuid = uuid;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getRegimenFiscal() { return regimenFiscal; }
    public void setRegimenFiscal(String regimenFiscal) { this.regimenFiscal = regimenFiscal; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public String getUsoCfdi() { return usoCfdi; }
    public void setUsoCfdi(String usoCfdi) { this.usoCfdi = usoCfdi; }
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public LocalDateTime getFechaFacturacion() { return fechaFacturacion; }
    public void setFechaFacturacion(LocalDateTime fechaFacturacion) { this.fechaFacturacion = fechaFacturacion; }
}