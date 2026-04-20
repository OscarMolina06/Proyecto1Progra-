package Datos;

import Entidad.Persona;
import java.io.IOException;
import java.util.Optional;

public interface RepositorioPadron {
    Optional<Persona> buscarPorCedula(String cedulaNormalizada) throws IOException;
}
