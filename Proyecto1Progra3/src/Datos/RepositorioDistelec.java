package Datos;

import Entidad.Direccion;
import java.util.Optional;

public interface RepositorioDistelec {
    Optional<Direccion> buscarPorCodigo(String codigoElectoral);
}
