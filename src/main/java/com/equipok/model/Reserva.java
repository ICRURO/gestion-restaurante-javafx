package com.equipok.model;

public class Reserva {
    private int idReserva;
    private String fecha;
    private String hora;
    private int personas;
    private String nombreCliente;
    private String telefonoCliente;
    private int tableId;

    public Reserva(String fecha, String hora, int personas, String nombreCliente, String telefonoCliente, int tableId) {
        this.fecha = fecha;
        this.hora = hora;
        this.personas = personas;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.tableId = tableId;
    }

    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public int getPersonas() { return personas; }
    public String getNombreCliente() { return nombreCliente; }
    public String getTelefonoCliente() { return telefonoCliente; }
    public int getTableId() { return tableId; }
}