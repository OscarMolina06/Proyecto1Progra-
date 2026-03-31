/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica_Negocio;

import DTO.RespuestaPadron;
import Datos.*;
import Entidad.*;
import java.util.Optional;

public class PadronService {
    private RepositorioPadron repoP;
    private RepositorioDistelec repoD;

    public PadronService(RepositorioPadron p, RepositorioDistelec d) {
        this.repoP = p; this.repoD = d;
    }

    public RespuestaPadron consultar(String cedula) {
        if (cedula == null || cedula.isBlank()) return RespuestaPadron.error("Cédula vacía");
        Optional<Persona> pOpt = repoP.buscarPorCedula(cedula);
        if (pOpt.isPresent()) {
            Direccion d = repoD.buscarPorCodigo(pOpt.get().getCodigoElectoral()).orElse(null);
            return RespuestaPadron.exitosa(pOpt.get(), d);
        }
        return RespuestaPadron.error("No encontrado");
    }
}

