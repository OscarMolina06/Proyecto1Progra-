package Datos;

import Entidades.Direccion;
import java.util.Optional;

public interface RepositorioDistelec {
    Optional<Direccion> buscarPorCodigo(String codigoElectoral);
}
