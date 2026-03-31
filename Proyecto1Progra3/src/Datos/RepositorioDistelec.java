/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;

import Entidad.Direccion;
import java.util.Optional;

/**
 *
 * @author sherr
 */
public interface RepositorioDistelec {
    Optional<Direccion> buscarPorCodigo(String codigoElectoral);
}