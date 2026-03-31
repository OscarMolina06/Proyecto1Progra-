/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import Entidad.*;

public class RespuestaPadron {

    private boolean exito;
    private String mensaje;
    private Persona persona;
    private Direccion direccion;

    public static RespuestaPadron exitosa(Persona p, Direccion d) {
        RespuestaPadron r = new RespuestaPadron();
        r.exito = true;
        r.persona = p;
        r.direccion = d;
        return r;
    }

    public static RespuestaPadron error(String m) {
        RespuestaPadron r = new RespuestaPadron();
        r.exito = false;
        r.mensaje = m;
        return r;
    }

    // Getters y Setters
    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Persona getPersona() {
        return persona;
    }

    public Direccion getDireccion() {
        return direccion;
    }
}