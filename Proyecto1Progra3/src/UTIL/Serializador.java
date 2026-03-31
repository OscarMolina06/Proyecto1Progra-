/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UTIL;

import DTO.RespuestaPadron;

public class Serializador {

    public static String aJSON(RespuestaPadron res) {
        if (!res.isExito()) {
            return "{\"exito\": false, \"mensaje\": \"Cédula no encontrada\"}";
        }
        
        // Obtenemos la dirección para no repetir el código
        var dir = res.getDireccion();
        String provincia = (dir != null) ? dir.getProvincia().trim() : "N/A";
        String canton = (dir != null) ? dir.getCanton().trim() : "N/A";
        String distrito = (dir != null) ? dir.getDistrito().trim() : "N/A";

        // Construimos el JSON con todos los niveles geográficos
        return String.format(
            "{\n" +
            "  \"exito\": true,\n" +
            "  \"persona\": {\n" +
            "    \"cedula\": \"%s\",\n" +
            "    \"nombre\": \"%s\",\n" +
            "    \"apellido1\": \"%s\",\n" +
            "    \"apellido2\": \"%s\"\n" +
            "  },\n" +
            "  \"ubicacion\": {\n" +
            "    \"provincia\": \"%s\",\n" +
            "    \"canton\": \"%s\",\n" +
            "    \"distrito\": \"%s\"\n" +
            "  }\n" +
            "}",
            res.getPersona().getCedula().trim(),
            res.getPersona().getNombre().trim(),
            res.getPersona().getPrimerApellido().trim(),
            res.getPersona().getSegundoApellido().trim(),
            provincia,
            canton,
            distrito
        );
    }

    public static String aXML(RespuestaPadron res) {
        if (!res.isExito()) {
            return "<respuesta><exito>false</exito><mensaje>No encontrada</mensaje></respuesta>";
        }
        
        var dir = res.getDireccion();
        
        return "<respuesta>\n" +
               "  <exito>true</exito>\n" +
               "  <persona>\n" +
               "    <cedula>" + res.getPersona().getCedula().trim() + "</cedula>\n" +
               "    <nombre>" + res.getPersona().getNombre().trim() + "</nombre>\n" +
               "    <apellido1>" + res.getPersona().getPrimerApellido().trim() + "</apellido1>\n" +
               "    <apellido2>" + res.getPersona().getSegundoApellido().trim() + "</apellido2>\n" +
               "  </persona>\n" +
               "  <ubicacion>\n" +
               "    <provincia>" + (dir != null ? dir.getProvincia().trim() : "N/A") + "</provincia>\n" +
               "    <canton>" + (dir != null ? dir.getCanton().trim() : "N/A") + "</canton>\n" +
               "    <distrito>" + (dir != null ? dir.getDistrito().trim() : "N/A") + "</distrito>\n" +
               "  </ubicacion>\n" +
               "</respuesta>";
    }
}