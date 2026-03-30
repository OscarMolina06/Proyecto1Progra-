/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;

import Entidad.Direccion;
import java.io.*;
import java.util.*;

public class RepositorioDistelecImpl implements RepositorioDistelec {

    private Map<String, Direccion> mapa = new HashMap<>();

    public RepositorioDistelecImpl(String ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String l;
            while ((l = br.readLine()) != null) {
                String[] p = l.split(",");
                if (p.length >= 4) {
                    mapa.put(p[0].trim(), new Direccion(p[0], p[1], p[2], p[3]));
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public Optional<Direccion> buscarPorCodigo(String cod) {
        return Optional.ofNullable(mapa.get(cod));
    }
}
