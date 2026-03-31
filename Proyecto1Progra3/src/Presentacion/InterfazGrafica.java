/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentacion;

import Logica_Negocio.PadronService;
import DTO.RespuestaPadron;
import UTIL.Serializador;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class InterfazGrafica extends JFrame {

    private static final Color BG_MAIN = new Color(13, 17, 23);
    private static final Color BG_CARD = new Color(22, 27, 34);
    private static final Color BG_INPUT = new Color(33, 38, 45);
    private static final Color ACCENT_BLUE = new Color(31, 111, 235);
    private static final Color ACCENT_GREEN = new Color(35, 197, 94);
    private static final Color ACCENT_RED = new Color(218, 54, 51);
    private static final Color TEXT_PRIMARY = new Color(230, 237, 243);
    private static final Color TEXT_MUTED = new Color(125, 133, 144);
    private static final Color BORDER_COLOR = new Color(48, 54, 61);

    private JTextField txtCedula;
    private JComboBox<String> comboFormato;
    private JTextArea txtResultado;
    private JTextField urlHttp;
    private JTextField urlTcp;
    private JLabel lblCharCount;
    private JLabel lblStatus;
    private JButton btnConsultar;
    private JTextArea chatDisplay;
    private JTextField chatInput;
    private JButton btnSendChat;
    private ClaudeAssistant claudeAssistant;
    private DefaultListModel<String> historialModel;
    private JList<String> listaHistorial;

    private final PadronService service;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public InterfazGrafica(PadronService service) {
        this.service = service;
        this.claudeAssistant = new ClaudeAssistant();
        configurarVentana();
        inicializarUI();
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Sistema de Padron Electoral  Costa Rica");
        setSize(800, 680);
        setMinimumSize(new Dimension(720, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
    }

    private void inicializarUI() {
        setLayout(new BorderLayout());
        add(crearHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(BG_MAIN);
        tabs.setForeground(TEXT_PRIMARY);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        tabs.addTab("Consulta",crearPanelConsulta());
        tabs.addTab("Asistente IA",crearPanelChat());
        tabs.addTab("Historial",crearPanelHistorial());

        add(tabs, BorderLayout.CENTER);
        add(crearStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblTitulo = new JLabel("Padron Electoral");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel("Sistema de Consulta Electoral");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubtitulo.setForeground(TEXT_MUTED);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitulo);
        titlePanel.add(lblSubtitulo);
        header.add(titlePanel, BorderLayout.WEST);

        JPanel servers = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        servers.setOpaque(false);
        servers.add(crearBadge("HTTP :9090", ACCENT_GREEN));
        servers.add(crearBadge("TCP :5555",  ACCENT_BLUE));
        header.add(servers, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelConsulta() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        JPanel cardUrls = crearCard("Endpoints del Sistema");
        cardUrls.setLayout(new GridLayout(2, 1, 0, 6));
        urlHttp = crearCampoCopiable("http://localhost:9090/padron?cedula=&format=json");
        urlTcp  = crearCampoCopiable("TCP localhost:5555  =  GET||JSON");
        cardUrls.add(crearFilaUrl("HTTP", urlHttp, ACCENT_GREEN));
        cardUrls.add(crearFilaUrl("TCP",  urlTcp,  ACCENT_BLUE));

        JPanel cardBusqueda = crearCard("Consultar por Cedula");
        cardBusqueda.setLayout(new BorderLayout(0, 12));

        JPanel inputs = new JPanel(new GridBagLayout());
        inputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel lblCed = new JLabel("Numero de Cedula");
        lblCed.setForeground(TEXT_MUTED);
        lblCed.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        inputs.add(lblCed, gbc);

        txtCedula = new JTextField(16);
        estilizarInput(txtCedula);
        txtCedula.setFont(new Font("Monospaced", Font.BOLD, 16));
        txtCedula.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        txtCedula.addActionListener(e -> realizarConsulta());
        gbc.gridx = 1; gbc.weightx = 1;
        inputs.add(txtCedula, gbc);

        lblCharCount = new JLabel("0/9");
        lblCharCount.setForeground(TEXT_MUTED);
        lblCharCount.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 2; gbc.weightx = 0; gbc.insets = new Insets(0, 4, 0, 10);
        inputs.add(lblCharCount, gbc);

        JLabel lblFmt = new JLabel("Formato");
        lblFmt.setForeground(TEXT_MUTED);
        lblFmt.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridx = 3; gbc.insets = new Insets(0, 0, 0, 10);
        inputs.add(lblFmt, gbc);

        comboFormato = new JComboBox<>(new String[]{"JSON", "XML"});
        estilizarCombo(comboFormato);
        gbc.gridx = 4; gbc.weightx = 0;
        inputs.add(comboFormato, gbc);

        btnConsultar = crearBoton("Consultar", ACCENT_BLUE);
        btnConsultar.addActionListener(e -> realizarConsulta());
        gbc.gridx = 5; gbc.insets = new Insets(0, 0, 0, 0);
        inputs.add(btnConsultar, gbc);

        cardBusqueda.add(inputs, BorderLayout.NORTH);

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setBackground(new Color(20, 24, 30));
        txtResultado.setForeground(new Color(80, 220, 120));
        txtResultado.setFont(new Font("JetBrains Mono,Consolas,Monospaced", Font.PLAIN, 13));
        txtResultado.setMargin(new Insets(12, 14, 12, 14));
        txtResultado.setText("Aqui estan los resultados de la consulta realizada");

        JScrollPane scrollResult = new JScrollPane(txtResultado);
        scrollResult.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        cardBusqueda.add(scrollResult, BorderLayout.CENTER);

        JButton btnCopiar = crearBoton("Copiar", new Color(50, 55, 62));
        btnCopiar.addActionListener(e -> {
            txtResultado.selectAll();
            txtResultado.copy();
            btnCopiar.setText("Copiado");
            Timer t = new Timer(1500, ev -> btnCopiar.setText("Copiar"));
            t.setRepeats(false);
            t.start();
        });
        JPanel footerCard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        footerCard.setOpaque(false);
        footerCard.add(btnCopiar);
        cardBusqueda.add(footerCard, BorderLayout.SOUTH);

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setOpaque(false);
        norte.add(cardUrls,BorderLayout.NORTH);
        norte.add(cardBusqueda,BorderLayout.CENTER);
        panel.add(norte,BorderLayout.CENTER);

        txtCedula.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { actualizarDirecciones(); }
            @Override
            public void removeUpdate(DocumentEvent e) { actualizarDirecciones(); }
            @Override
            public void changedUpdate(DocumentEvent e) { actualizarDirecciones(); }
        });
        comboFormato.addActionListener(e -> actualizarDirecciones());

        return panel;
    }

    private JPanel crearPanelChat() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        chatDisplay = new JTextArea();
        chatDisplay.setEditable(false);
        chatDisplay.setBackground(BG_CARD);
        chatDisplay.setForeground(TEXT_PRIMARY);
        chatDisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatDisplay.setMargin(new Insets(14, 16, 14, 16));
        chatDisplay.setLineWrap(true);
        chatDisplay.setWrapStyleWord(true);
        chatDisplay.setText(
            "Asistente IA\n" +
            "Hola ,Soy tu asistente virtual del Sistema de Padrón Electoral.\n\n" +
            "Puedo ayudarte con:\n" +
            "  • Cómo usar la aplicación\n" +
            "  • Interpretar resultados de consultas\n" +
            "  • Preguntas sobre el sistema electoral costarricense\n" +
            "  • Dudas sobre los endpoints HTTP y TCP\n\n" 
        );

        JScrollPane chatScroll = new JScrollPane(chatDisplay);
        chatScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(chatScroll, BorderLayout.CENTER);

        JPanel inputArea = new JPanel(new BorderLayout(8, 0));
        inputArea.setOpaque(false);
        inputArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        chatInput = new JTextField();
        estilizarInput(chatInput);
        chatInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatInput.addActionListener(e -> enviarMensajeChat());

        btnSendChat = crearBoton("Enviar", ACCENT_BLUE);
        btnSendChat.addActionListener(e -> enviarMensajeChat());

        JButton btnReset = crearBoton("Nueva conversacion", new Color(50, 55, 62));
        btnReset.addActionListener(e -> {
            claudeAssistant.resetConversation();
            chatDisplay.append("\n CONVERSACION NUEVA \n\n");
        });

        inputArea.add(chatInput,   BorderLayout.CENTER);
        inputArea.add(btnSendChat, BorderLayout.EAST);

        JPanel botones = new JPanel(new BorderLayout(8, 0));
        botones.setOpaque(false);
        botones.add(inputArea, BorderLayout.CENTER);
        botones.add(btnReset,  BorderLayout.EAST);

        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        historialModel = new DefaultListModel<>();
        listaHistorial = new JList<>(historialModel);
        listaHistorial.setBackground(BG_CARD);
        listaHistorial.setForeground(TEXT_PRIMARY);
        listaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 13));
        listaHistorial.setSelectionBackground(new Color(31, 111, 235, 80));
        listaHistorial.setSelectionForeground(TEXT_PRIMARY);
        listaHistorial.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        listaHistorial.setFixedCellHeight(30);

        JScrollPane scroll = new JScrollPane(listaHistorial);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JLabel lblInfo = new JLabel("Registro de todas las consultas realizadas en esta sesion");
        lblInfo.setForeground(TEXT_MUTED);
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 12));

        JButton btnLimpiar = crearBoton("Limpiar historial", new Color(50, 55, 62));
        btnLimpiar.addActionListener(e -> historialModel.clear());

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(lblInfo,BorderLayout.WEST);
        footer.add(btnLimpiar,BorderLayout.EAST);

        panel.add(scroll,BorderLayout.CENTER);
        panel.add(footer,BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(18, 22, 28));
        bar.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),BorderFactory.createEmptyBorder(5, 16, 5, 16)
        ));
        lblStatus = new JLabel("Sistema listo  =  TCP :5555  =  HTTP :9090");
        lblStatus.setForeground(TEXT_MUTED);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        bar.add(lblStatus, BorderLayout.WEST);

        JLabel version = new JLabel("Padron Electoral v2.0  =  IA by Claude");
        version.setForeground(new Color(80, 88, 100));
        version.setFont(new Font("SansSerif", Font.PLAIN, 11));
        bar.add(version, BorderLayout.EAST);
        return bar;
    }

    private void realizarConsulta() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) { mostrarError("Ingrese un numero de cedula."); return; }
        if (cedula.length() > 9) { mostrarError("La cedula no puede tener mas de 9 digitos."); return; }

        btnConsultar.setEnabled(false);
        btnConsultar.setText("Buscando");
        lblStatus.setText("Buscando cedula " + cedula);
        txtResultado.setText("");

        SwingWorker<RespuestaPadron, Void> worker = new SwingWorker<>() {
            @Override protected RespuestaPadron doInBackground() {
                return service.consultar(cedula);
            }
            @Override protected void done() {
                try {
                    RespuestaPadron res = get();
                    String hora = LocalDateTime.now().format(TIME_FMT);
                    if (!res.isExito()) {
                        txtResultado.setForeground(ACCENT_RED);
                        txtResultado.setText("// Cedula '" + cedula + "' no encontrada en la base de datos.");
                        historialModel.add(0, "[" + hora + "]   " + cedula + "   No encontrado");
                        lblStatus.setText("No encontrado · " + hora);
                    } else {
                        String formato = (String) comboFormato.getSelectedItem();
                        String salida  = "XML".equals(formato) ? Serializador.aXML(res) : Serializador.aJSON(res);
                        txtResultado.setForeground(ACCENT_GREEN);
                        txtResultado.setText(salida);
                        String nombre = res.getPersona().getNombre() + " " + res.getPersona().getPrimerApellido();
                        historialModel.add(0, "[" + hora + "] " + cedula + "  =  " + nombre);
                        lblStatus.setText("Encontrado: " + nombre.trim() + " = " + hora);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    mostrarError("Error: " + ex.getMessage());
                } finally {
                    btnConsultar.setEnabled(true);
                    btnConsultar.setText("Consultar");
                }
            }
        };
        worker.execute();
    }

    private void enviarMensajeChat() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) return;
        chatInput.setText("");
        chatDisplay.append("Tu: " + msg + "\n\n");
        btnSendChat.setEnabled(false);
        chatInput.setEnabled(false);
        lblStatus.setText("Consultando al asistente IA");

        claudeAssistant.chat(msg).thenAccept(response ->
            SwingUtilities.invokeLater(() -> {
                chatDisplay.append("Asistente: " + response + "\n\n");
                chatDisplay.append("---------------------------------\n\n");
                chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
                btnSendChat.setEnabled(true);
                chatInput.setEnabled(true);
                chatInput.requestFocus();
                lblStatus.setText("Sistema listo  =  TCP :5555  =  HTTP :9090");
            })
        );
    }

    private void actualizarDirecciones() {
        String ced  = txtCedula.getText().trim();
        String form = ((String) comboFormato.getSelectedItem()).toLowerCase();
        urlHttp.setText("http://localhost:9090/padron?cedula=" + ced + "&format=" + form);
        urlTcp.setText("TCP localhost:5555  =  GET|" + ced + "|" + form.toUpperCase());
        int len = ced.length();
        lblCharCount.setText(len + "/9");
        lblCharCount.setForeground(len == 9 ? ACCENT_GREEN : (len > 9 ? ACCENT_RED : TEXT_MUTED));
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atencion", JOptionPane.WARNING_MESSAGE);
    }

    private JPanel crearCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR),BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12),titulo, TitledBorder.LEFT, TitledBorder.TOP,new Font("SansSerif", Font.BOLD, 12), TEXT_MUTED
                )
        ));
        return card;
    }

    private JPanel crearFilaUrl(String protocolo, JTextField campo, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(protocolo);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setForeground(color);
        lbl.setPreferredSize(new Dimension(36, 24));
        row.add(lbl,BorderLayout.WEST);
        row.add(campo,BorderLayout.CENTER);
        return row;
    }

    private JTextField crearCampoCopiable(String texto) {
        JTextField campo = new JTextField(texto);
        campo.setEditable(false);
        campo.setBackground(BG_INPUT);
        campo.setForeground(TEXT_PRIMARY);
        campo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR),BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        campo.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) { campo.selectAll(); }
        });
        return campo;
    }

    private void estilizarInput(JTextField field) {
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR),BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            @Override 
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    private JLabel crearBadge(String texto, Color color) {
        JLabel badge = new JLabel(texto);
        badge.setFont(new Font("Monospaced", Font.BOLD, 11));
        badge.setForeground(color);
        badge.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color, 1),BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        return badge;
    }
}