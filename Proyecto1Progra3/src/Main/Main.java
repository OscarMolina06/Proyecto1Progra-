/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;
import Datos.*;
import Logica_Negocio.PadronService;
import Presentacion.*;
import javax.swing.SwingUtilities;
/**
 *
 * @author oscar
 */
public class Main {

    public static void main(String[] args) {
        // Rutas de archivos
        String archivoPadron = "PADRON_COMPLETO.txt"; 
        String archivoDistritos = "distelec.txt";

        try {
            // Inicializar Lógica
            RepositorioPadron repoP = new RepositorioPadronImpl(archivoPadron);
            RepositorioDistelec repoD = new RepositorioDistelecImpl(archivoDistritos);
            PadronService service = new PadronService(repoP, repoD);

            // 1. Iniciar Servidor TCP (Puerto 5555)
            new Thread(() -> new ServidorTCP(5555, service).iniciar()).start();
            
            // 2. Iniciar Servidor HTTP (Puerto 9090)
            new Thread(() -> {
                try {
                    new ServidorHTTP(9090, service).iniciar();
                } catch (Exception e) { e.printStackTrace(); }
            }).start();

            // 3. Iniciar Interfaz Gráfica (GUI)
            SwingUtilities.invokeLater(() -> {
                new InterfazGrafica(service).setVisible(true);
            });

            System.out.println(">>> Sistema de Padrón en línea (TCP, HTTP y GUI)");

        } catch (Exception e) {
            System.err.println("Error al iniciar: " + e.getMessage());
        }
    }
}