/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;

import Entidad.Persona;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class RepositorioPadronImpl implements RepositorioPadron {

    private static final Logger LOG = Logger.getLogger(RepositorioPadronImpl.class.getName());

    private final Map<String, Persona> cache;

    public RepositorioPadronImpl(String ruta) {
        this.cache = new HashMap<>();
        cargarEnMemoria(ruta);
    }

    @Override
    public Optional<Persona> buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) return Optional.empty();
        String cedulaNorm = normalizarCedula(cedula);
        return Optional.ofNullable(cache.get(cedulaNorm));
    }

    private void cargarEnMemoria(String ruta) {
        long inicio = System.currentTimeMillis();
        int cargados = 0, errores = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ruta), "UTF-8"), 65536)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                try {
                    Persona p = parsearLinea(linea);
                    if (p != null) {
                        cache.put(p.getCedula(), p);
                        cargados++;
                    }
                } catch (Exception e) {
                    errores++;
                }
            }
        } catch (IOException e) {
            LOG.severe("Error cargando padron: " + e.getMessage());
        }

        long ms = System.currentTimeMillis() - inicio;
        LOG.info(String.format(
            "Padron cargado: %,d registros en %d ms (%d errores)", cargados, ms, errores
        ));
    }

    private Persona parsearLinea(String linea) {
        if (linea == null || linea.isBlank()) return null;
        String[] col = linea.split(",");
        if (col.length < 8) return null;

        Persona p = new Persona();
        p.setCedula(normalizarCedula(col[0].trim()));
        p.setCodigoElectoral(col[1].trim());
        if (col.length > 4) p.setSexo(col[4].trim());
        if (col.length > 5) p.setNombre(col[5].trim());
        if (col.length > 6) p.setPrimerApellido(col[6].trim());
        if (col.length > 7) p.setSegundoApellido(col[7].trim());

        return p;
    }

    private String normalizarCedula(String cedula) {
        String num = cedula.replaceAll("[^0-9]", "");
        while (num.length() < 9) num = "0" + num;
        return num;
    }

    public int totalRegistros() {
        return cache.size();
    }
}
