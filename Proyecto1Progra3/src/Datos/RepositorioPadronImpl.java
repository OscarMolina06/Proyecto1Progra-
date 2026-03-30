/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;

import Entidad.Persona;
import java.io.*;
import java.util.Optional;

public class RepositorioPadronImpl implements RepositorioPadron {
    private String ruta;
    public RepositorioPadronImpl(String ruta) { this.ruta = ruta; }

    @Override
    public Optional<Persona> buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) return Optional.empty();
        while (cedula.length() < 9) cedula = "0" + cedula; // Normaliza a 9 dígitos

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] col = linea.split(",");
                if (col.length >= 8 && col[0].trim().equals(cedula)) {
                    Persona p = new Persona();
                    p.setCedula(col[0].trim());
                    p.setCodigoElectoral(col[1].trim());
                    p.setNombre(col[5].trim());
                    p.setPrimerApellido(col[6].trim());
                    p.setSegundoApellido(col[7].trim());
                    return Optional.of(p);
                }
            }
        } catch (IOException e) { System.err.println("Error: " + e.getMessage()); }
        return Optional.empty();
    }
}
