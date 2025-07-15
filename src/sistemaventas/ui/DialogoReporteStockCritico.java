package sistemaventas.ui;

import sistemaventas.dto.InventarioDTO;
import sistemaventas.service.impl.InventarioServiceImpl;
import sistemaventas.service.interfaces.IInventarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogoReporteStockCritico extends JDialog {

    private IInventarioService inventarioService;

    public DialogoReporteStockCritico(Frame parent) {
        super(parent, "Reporte de Niveles de Stock", true);
        this.inventarioService = new InventarioServiceImpl();
        initUI();
        generarReporte();
    }

    private void initUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Crear una tabla para cada categoría de stock
        tabbedPane.addTab("Stock Crítico (≤ 5)", crearPanelTabla());
        tabbedPane.addTab("Stock Bajo (< Mínimo)", crearPanelTabla());
        tabbedPane.addTab("Stock Alto (> Máximo)", crearPanelTabla());
        tabbedPane.addTab("Stock Normal", crearPanelTabla());

        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JScrollPane crearPanelTabla() {
        String[] columnas = {"Producto", "Almacén", "Stock Actual", "Mínimo", "Máximo", "Reposición Sugerida"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        return new JScrollPane(table);
    }

    private void generarReporte() {
        try {
            List<InventarioDTO> inventarios = inventarioService.listarInventarios();
            
            List<InventarioDTO> stockCritico = new ArrayList<>();
            List<InventarioDTO> stockBajo = new ArrayList<>();
            List<InventarioDTO> stockAlto = new ArrayList<>();
            List<InventarioDTO> stockNormal = new ArrayList<>();
            
            for (InventarioDTO inv : inventarios) {
                if (inv.getCantidad() <= 5) {
                    stockCritico.add(inv);
                } else if (inv.isStockBajo()) {
                    stockBajo.add(inv);
                } else if (inv.isStockAlto()) {
                    stockAlto.add(inv);
                } else {
                    stockNormal.add(inv);
                }
            }
            
            JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
            
            // Llenar cada tabla en su respectiva pestaña
            llenarTabla((JScrollPane) tabbedPane.getComponentAt(0), stockCritico);
            llenarTabla((JScrollPane) tabbedPane.getComponentAt(1), stockBajo);
            llenarTabla((JScrollPane) tabbedPane.getComponentAt(2), stockAlto);
            llenarTabla((JScrollPane) tabbedPane.getComponentAt(3), stockNormal);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte de stock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void llenarTabla(JScrollPane scrollPane, List<InventarioDTO> datos) {
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        for (InventarioDTO inv : datos) {
            int reposicion = Math.max(0, inv.getStockMaximo() - inv.getCantidad());
            model.addRow(new Object[]{
                inv.getNombreProducto(),
                inv.getDescripcionAlmacen(),
                inv.getCantidad(),
                inv.getStockMinimo(),
                inv.getStockMaximo(),
                reposicion
            });
        }
    }
}