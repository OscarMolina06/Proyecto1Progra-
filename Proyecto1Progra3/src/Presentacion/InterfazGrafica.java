/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentacion;

import Logica_Negocio.PadronService;
import DTO.RespuestaPadron;
import UTIL.Serializador;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InterfazGrafica extends JFrame {
    private JTextField txtCedula;
    private JComboBox<String> comboFormato;
    private JTextArea txtResultado;
    private JTextField urlHttp;
    private JTextField urlTcp;
    private PadronService service;

    public InterfazGrafica(PadronService service) {
        this.service = service;
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
    }

    private void configurarVentana() {
        setTitle("Sistema de Padrón Electoral - Validaciones Activas");
        setSize(650, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- PANEL DE INFO (DIRECCIONES DINÁMICAS) ---
        JPanel panelInfo = new JPanel(new GridLayout(3, 1, 2, 2));
        panelInfo.setBackground(new Color(245, 245, 245));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Enlaces de Consulta (Copiables)"));
        
        urlHttp = crearCampoCopiable("🌐 HTTP: http://localhost:9090/padron?cedula=&format=json");
        urlTcp = crearCampoCopiable("🔌 TCP: localhost:5555 (Comando: GET||JSON)");
        
        panelInfo.add(new JLabel(" Copie la URL para probar en el navegador:"));
        panelInfo.add(urlHttp);
        panelInfo.add(urlTcp);

        // --- PANEL DE ENTRADA ---
        JPanel panelEntrada = new JPanel(new GridLayout(2, 2, 10, 10));
        panelEntrada.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelEntrada.add(new JLabel("Número de Cédula (Solo números):"));
        txtCedula = new JTextField();
        txtCedula.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        // --- MEJORA 1: SOLO NÚMEROS ---
        txtCedula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Si no es un número y no es la tecla de borrar (backspace), se ignora
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // Bloquea la tecla
                    Toolkit.getDefaultToolkit().beep(); // Sonido de alerta opcional
                }
            }
        });

        panelEntrada.add(txtCedula);
        panelEntrada.add(new JLabel("Formato de salida:"));
        comboFormato = new JComboBox<>(new String[]{"JSON", "XML"});
        panelEntrada.add(comboFormato);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelInfo, BorderLayout.NORTH);
        panelNorte.add(panelEntrada, BorderLayout.CENTER);
        add(panelNorte, BorderLayout.NORTH);

        // --- PANEL CENTRAL: RESULTADOS ---
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setBackground(new Color(30, 30, 30));
        txtResultado.setForeground(new Color(0, 255, 65));
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);

        // --- PANEL INFERIOR: BOTÓN ---
        JButton btnConsultar = new JButton("Consultar Localmente");
        btnConsultar.setPreferredSize(new Dimension(200, 40));
        btnConsultar.setBackground(new Color(0, 123, 255));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.addActionListener(e -> realizarConsulta());
        
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.add(btnConsultar);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        txtCedula.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarDirecciones(); }
            public void removeUpdate(DocumentEvent e) { actualizarDirecciones(); }
            public void changedUpdate(DocumentEvent e) { actualizarDirecciones(); }
        });
        comboFormato.addActionListener(e -> actualizarDirecciones());
    }

    private void actualizarDirecciones() {
        String ced = txtCedula.getText().trim();
        String form = ((String) comboFormato.getSelectedItem()).toLowerCase();
        urlHttp.setText("🌐 HTTP: http://localhost:9090/padron?cedula=" + ced + "&format=" + form);
        urlTcp.setText("🔌 TCP: localhost:5555 (Comando: GET|" + ced + "|" + form.toUpperCase() + ")");
    }

    private JTextField crearCampoCopiable(String texto) {
        JTextField campo = new JTextField(texto);
        campo.setEditable(false);
        campo.setBorder(null);
        campo.setOpaque(false);
        campo.setFont(new Font("Monospaced", Font.BOLD, 12));
        campo.setForeground(new Color(0, 102, 51));
        return campo;
    }

    private void realizarConsulta() {
        String cedula = txtCedula.getText().trim();
        
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número de cédula.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            RespuestaPadron res = service.consultar(cedula);
            
            // --- MEJORA 2: VALIDACIÓN POR POP-UP (FRAME) ---
            if (!res.isExito()) {
                txtResultado.setText(""); // Limpiamos el área de texto
                JOptionPane.showMessageDialog(this, 
                    "La cédula '" + cedula + "' no existe en nuestra base de datos.", 
                    "Resultado No Encontrado", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                String formato = (String) comboFormato.getSelectedItem();
                String salida = formato.equals("XML") ? Serializador.aXML(res) : Serializador.aJSON(res);
                txtResultado.setText(salida);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error técnico: " + e.getMessage(), "Error de Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}