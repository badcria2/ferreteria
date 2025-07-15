/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.ui;

import javax.swing.*;
import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDashboard extends JFrame {

    // Definimos una paleta de colores para un look consistente
    private static final Color COLOR_FONDO = new Color(240, 245, 250); // Un gris muy claro
    private static final Color COLOR_HEADER = new Color(30, 50, 100);  // Azul oscuro
    private static final Color COLOR_BOTON = Color.WHITE;
    private static final Color COLOR_BOTON_HOVER = new Color(220, 230, 245); // Azul claro al pasar el ratón
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 16);

    public MainDashboard() {
        initUI();
    }

    private void initUI() {
        // --- Configuración General de la Ventana ---
        setTitle("Ferretería 'El Martillo de Oro' - Panel Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrar en pantalla
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(10, 10)); // Layout principal con espaciado

        // --- 1. Panel del Encabezado (Header) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_HEADER);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel lblTitulo = new JLabel("Sistema de Gestión");
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Panel Central con los Botones del Menú ---
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 25, 25)); // Rejilla 2x2 con espaciado
        menuPanel.setBackground(COLOR_FONDO);
        menuPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); // Más padding para centrar

        // Crear y añadir los botones de opción
        JButton btnClientes = createMenuButton("Gestión de Clientes", "resources/icons/clientes.png");
        JButton btnInventario = createMenuButton("Gestión de Inventario", "resources/icons/inventario.png");
        JButton btnVentas = createMenuButton("Gestión de Ventas", "resources/icons/cart.png");
        JButton btnReportes = createMenuButton("Reportes", "resources/icons/list.png");

        menuPanel.add(btnClientes);
        menuPanel.add(btnInventario);
        menuPanel.add(btnVentas);
        menuPanel.add(btnReportes);

        add(menuPanel, BorderLayout.CENTER);

        // --- 3. Panel del Pie de Página (Footer) con botón de Salir ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(COLOR_FONDO);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnSalir = new JButton("Salir");
        // Estilo simple para el botón de salir
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSalir.setIcon(new ImageIcon(getClass().getResource("/resources/icons/exit.png"))); // Cargar icono

        footerPanel.add(btnSalir);

        add(footerPanel, BorderLayout.SOUTH);

        // --- LÓGICA DE LOS BOTONES (Action Listeners) ---
        btnClientes.addActionListener(e -> {
            new VistaGestionClientes().setVisible(true);
            JOptionPane.showMessageDialog(this, "Abriendo gestión de clientes...", "Información", JOptionPane.INFORMATION_MESSAGE);
        });

        btnInventario.addActionListener(e -> {
            new VistaGestionInventario().setVisible(true);

        });

        btnVentas.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abriendo gestión de ventas...", "Información", JOptionPane.INFORMATION_MESSAGE);
            new VistaGestionVentas().setVisible(true);

        });

        btnReportes.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abriendo módulo de reportes...", "Información", JOptionPane.INFORMATION_MESSAGE);
            new VistaReportes().setVisible(true);

        });

        btnSalir.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea salir de la aplicación?",
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    /**
     * Helper para crear botones de menú estilizados y con efecto hover.
     *
     * @param text El texto del botón.
     * @param iconPath La ruta al icono dentro de la carpeta 'resources'.
     * @return un JButton estilizado.
     */
    private JButton createMenuButton(String text, String iconPath) {
        // Usamos getClass().getResource() para que encuentre los iconos
        // tanto en el entorno de desarrollo como en el archivo JAR final.
        ImageIcon icon = new ImageIcon(getClass().getResource("/" + iconPath));

        JButton button = new JButton(text);
        button.setIcon(icon);

        // Posicionar texto debajo del icono
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        // Estilos visuales
        button.setFont(FUENTE_BOTON);
        button.setBackground(COLOR_BOTON);
        button.setForeground(COLOR_HEADER);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cambia el cursor a una mano

        // Efecto "Hover" (cuando el ratón pasa por encima)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BOTON_HOVER);
                button.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, COLOR_HEADER));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_BOTON);
                button.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)));
            }
        });

        return button;
    }

    /**
     * El punto de entrada para la aplicación gráfica.
     */
    public static void main(String[] args) {
        // Es una buena práctica establecer el Look and Feel para un mejor aspecto.
        // Nimbus es una opción moderna y disponible en Java.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible, no pasa nada, usará el por defecto.
        }

        // Iniciar la UI en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard();
            dashboard.setVisible(true);
        });
    }
}
