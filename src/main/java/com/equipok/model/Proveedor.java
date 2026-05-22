package com.equipok.model;

/**
 * Modelo que representa a un Proveedor de alimentos o bebidas.
 */
public class Proveedor {
    private int id;
    private String nombre;
    private String empresa;
    private String telefono;
    private String correo;
    private String categoria;

    public Proveedor() {
    }

    public Proveedor(int id, String nombre, String empresa, String telefono, String correo, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.empresa = empresa;
        this.telefono = telefono;
        this.correo = correo;
        this.categoria = categoria;
    }

    public Proveedor(String nombre, String empresa, String telefono, String correo, String categoria) {
        this.nombre = nombre;
        this.empresa = empresa;
        this.telefono = telefono;
        this.correo = correo;
        this.categoria = categoria;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}