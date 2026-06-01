package com.equipok.model;

import java.time.LocalDateTime;

public class CorteCaja {
    private int id;
    private LocalDateTime fechaCorte;
    private double fondoApertura;
    private double ventasEfectivoEsperado;
    private double ventasTarjetaEsperado;
    private double efectivoRealContado;
    private double diferencia;
    private String usuarioCajero;
    private String observaciones;

    public CorteCaja() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFechaCorte() { return fechaCorte; }
    public void setFechaCorte(LocalDateTime fechaCorte) { this.fechaCorte = fechaCorte; }
    public double getFondoApertura() { return fondoApertura; }
    public void setFondoApertura(double fondoApertura) { this.fondoApertura = fondoApertura; }
    public double getVentasEfectivoEsperado() { return ventasEfectivoEsperado; }
    public void setVentasEfectivoEsperado(double vEfectivo) { this.ventasEfectivoEsperado = vEfectivo; }
    public double getVentasTarjetaEsperado() { return ventasTarjetaEsperado; }
    public void setVentasTarjetaEsperado(double vTarjeta) { this.ventasTarjetaEsperado = vTarjeta; }
    public double getEfectivoRealContado() { return efectivoRealContado; }
    public void setEfectivoRealContado(double efectivoReal) { this.efectivoRealContado = efectivoReal; }
    public double getDiferencia() { return diferencia; }
    public void setDiferencia(double diferencia) { this.diferencia = diferencia; }
    public String getUsuarioCajero() { return usuarioCajero; }
    public void setUsuarioCajero(String usuario) { this.usuarioCajero = usuario; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String obs) { this.observaciones = obs; }
}