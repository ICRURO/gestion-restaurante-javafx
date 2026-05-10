package com.equipok.model;

public class Empleado {
    private int id;
    private String nombre;
    private String puesto;
    private String telefono;
    private String turno;

    public Empleado() {}

    public Empleado(String nombre, String puesto, String telefono, String turno) {
        this.nombre = nombre;
        this.puesto = puesto;
        this.telefono = telefono;
        this.turno = turno;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
}