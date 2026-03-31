/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentacion;

import Logica_Negocio.PadronService;
import UTIL.Serializador;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorTCP {
    private int puerto;
    private PadronService service;
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    public ServidorTCP(int puerto, PadronService service) { this.puerto = puerto; this.service = service; }

    public void iniciar() {
        try (ServerSocket server = new ServerSocket(puerto)) {
            System.out.println("TCP en puerto " + puerto);
            while (true) {
                Socket cliente = server.accept();
                pool.execute(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                         PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {
                        String linea = in.readLine();
                        if (linea != null && linea.startsWith("GET|")) {
                            String[] p = linea.split("\\|");
                            out.println(p[2].equalsIgnoreCase("XML") ? Serializador.aXML(service.consultar(p[1])) : Serializador.aJSON(service.consultar(p[1])));
                        }
                    } catch (Exception e) {}
                });
            }
        } catch (IOException e) {}
    }
}