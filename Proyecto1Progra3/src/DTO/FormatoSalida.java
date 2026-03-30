/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package DTO;

public enum FormatoSalida {
    JSON, XML;

    public static FormatoSalida desde(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Vacio");
        }
        if (texto.trim().equalsIgnoreCase("JSON")) {
            return JSON;
        }
        if (texto.trim().equalsIgnoreCase("XML")) {
            return XML;
        }
        throw new IllegalArgumentException("No valido: " + texto);
    }
}