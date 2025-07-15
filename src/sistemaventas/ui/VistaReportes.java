/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class VistaReportes extends JFrame {

    public VistaReportes() {
        initUI();
    }

    private void initUI() {
        setTitle("Central de Reportes");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitulo = new JLabel("Seleccione el Reporte a Generar", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 10, 10)); // Layout vertical
        panelBotones.setBorder(new TitledBorder("Reportes Disponibles"));
        
        // Crear un botón por cada reporte
        JButton btnVentasPeriodo = new JButton("1. Reporte de Ventas por Período");
        /*JButton btnProductosMasVendidos = new JButton("2. Productos Más Vendidos");
        JButton btnClientesFrecuentes = new JButton("3. Clientes Frecuentes");
        JButton btnInventarioValorizado = new JButton("4. Inventario Valorizado");*/
        JButton btnStockCritico = new JButton("2. Productos con Stock Crítico");
        
        // Añadir botones al panel
        panelBotones.add(btnVentasPeriodo);
       /* panelBotones.add(btnProductosMasVendidos);
        panelBotones.add(btnClientesFrecuentes);
        panelBotones.add(btnInventarioValorizado);*/
        panelBotones.add(btnStockCritico);
        
        // ... (puedes añadir los otros 5 botones de la misma forma) ...
        
        // --- Lógica de los botones ---
        btnVentasPeriodo.addActionListener(e -> {
            DialogoReporteVentasPeriodo dialogo = new DialogoReporteVentasPeriodo(this);
            dialogo.setVisible(true);
        });

        btnStockCritico.addActionListener(e -> {
            DialogoReporteStockCritico dialogo = new DialogoReporteStockCritico(this);
            dialogo.setVisible(true);
        });
        
        // Placeholder para los otros reportes
       /* btnProductosMasVendidos.addActionListener(e -> showPlaceholder());
        btnClientesFrecuentes.addActionListener(e -> showPlaceholder());
        btnInventarioValorizado.addActionListener(e -> showPlaceholder());*/
        
        panelPrincipal.add(new JScrollPane(panelBotones), BorderLayout.CENTER);
        add(panelPrincipal);
    }
    
    private void showPlaceholder() {
        JOptionPane.showMessageDialog(this, "Esta funcionalidad de reporte aún no ha sido implementada.", "En Construcción", JOptionPane.INFORMATION_MESSAGE);
    }
}