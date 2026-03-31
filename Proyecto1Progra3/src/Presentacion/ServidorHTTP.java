/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentacion;

import Logica_Negocio.PadronService;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ServidorHTTP {
    private int puerto;
    private PadronService service;

    public ServidorHTTP(int puerto, PadronService service) { this.puerto = puerto; this.service = service; }

    public void iniciar() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        server.createContext("/padron", (ex) -> {
            try {
                String q = ex.getRequestURI().getQuery();
                String cedula = ""; String formato = "json";
                if (q != null) {
                    for (String s : q.split("&")) {
                        if (s.startsWith("cedula=")) cedula = s.split("=")[1];
                        if (s.startsWith("format=")) formato = s.split("=")[1];
                    }
                }
                String resp = formato.equalsIgnoreCase("xml") ? UTIL.Serializador.aXML(service.consultar(cedula)) : UTIL.Serializador.aJSON(service.consultar(cedula));
                ex.sendResponseHeaders(200, resp.getBytes().length);
                try (OutputStream os = ex.getResponseBody()) { os.write(resp.getBytes()); }
            } catch (Exception e) { ex.sendResponseHeaders(500, 0); ex.close(); }
        });
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("HTTP en puerto " + puerto);
    }
}