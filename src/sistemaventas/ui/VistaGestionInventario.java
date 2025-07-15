package sistemaventas.ui;

import sistemaventas.dto.InventarioDTO;
import sistemaventas.service.impl.InventarioServiceImpl;
import sistemaventas.service.interfaces.IInventarioService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class VistaGestionInventario extends JFrame {

    private final IInventarioService inventarioService;
    private DefaultTableModel tableModel;
    private JTable tablaInventario;
    private JTextField txtBusqueda;
    private JComboBox<String> comboFiltro;
    private JLabel lblEstado;

    public VistaGestionInventario() {
        this.inventarioService = new InventarioServiceImpl();
        initUI();
    }

    private void initUI() {
        setTitle("Gestión de Inventario");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel de Controles (Norte) ---
        panelPrincipal.add(crearPanelControles(), BorderLayout.NORTH);

        // --- Panel de la Tabla (Centro) ---
        String[] columnas = {"ID Inventario","PRODUCTO", "ALMACÉN", "CANTIDAD", "MIN", "MAX", "ESTADO"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaInventario = new JTable(tableModel);
        panelPrincipal.add(new JScrollPane(tablaInventario), BorderLayout.CENTER);

        // --- Panel de Estado (Sur) ---
        lblEstado = new JLabel("Cargando datos...");
        panelPrincipal.add(lblEstado, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Carga inicial de todos los registros
        cargarTodoElInventario();
    }

    private JPanel crearPanelControles() {
        JPanel panelControles = new JPanel(new BorderLayout(20, 10));
        
        // Panel de Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(new TitledBorder("Filtros de Búsqueda"));
        
        comboFiltro = new JComboBox<>(new String[]{"ID Inventario", "ID Almacén", "ID Producto"});
        txtBusqueda = new JTextField(15);
        JButton btnBuscar = new JButton("Buscar");
        
        panelFiltros.add(new JLabel("Filtrar por:"));
        panelFiltros.add(comboFiltro);
        panelFiltros.add(txtBusqueda);
        panelFiltros.add(btnBuscar);
        
        // Panel de Acciones y Vistas Rápidas
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVerTodo = new JButton("Ver Todo el Inventario");
        JButton btnVerStockBajo = new JButton("Ver Stock Bajo");
        JButton btnActualizarStock = new JButton("Actualizar Stock");
        
        // Iconos para mejorar la apariencia
        btnVerStockBajo.setIcon(new ImageIcon(getClass().getResource("/resources/icons/warning.png")));
        btnActualizarStock.setIcon(new ImageIcon(getClass().getResource("/resources/icons/edit.png")));

        panelAcciones.add(btnVerTodo);
        panelAcciones.add(btnVerStockBajo);
        panelAcciones.add(btnActualizarStock);

        panelControles.add(panelFiltros, BorderLayout.CENTER);
        panelControles.add(panelAcciones, BorderLayout.EAST);
        
        // --- Lógica de los botones ---
        btnBuscar.addActionListener(e -> accionBuscar());
        btnVerTodo.addActionListener(e -> cargarTodoElInventario());
        btnVerStockBajo.addActionListener(e -> accionVerStockBajo());
        btnActualizarStock.addActionListener(e -> accionActualizarStock());
        
        return panelControles;
    }

    // --- MÉTODOS DE LÓGICA Y ACCIONES ---

    private void cargarTodoElInventario() {
        try {
            List<InventarioDTO> inventarios = inventarioService.listarInventarios();
            actualizarTabla(inventarios);
            lblEstado.setText("Mostrando " + inventarios.size() + " registros del inventario.");
        } catch (Exception e) {
            handleError("Error al cargar el inventario completo.", e);
        }
    }
    
    private void accionBuscar() {
        String criterio = (String) comboFiltro.getSelectedItem();
        String valorStr = txtBusqueda.getText().trim();
        
        if (valorStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un valor para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(valorStr);
            List<InventarioDTO> resultados = Collections.emptyList();
            
            switch (criterio) {
                case "ID Inventario":
                    InventarioDTO inv = inventarioService.obtenerInventarioPorId(id);
                    if (inv != null) resultados = Collections.singletonList(inv);
                    break;
                case "ID Almacén":
                    resultados = inventarioService.obtenerInventarioPorAlmacen(id);
                    break;
                case "ID Producto":
                    resultados = inventarioService.obtenerInventarioPorProducto(id);
                    break;
            }
            
            actualizarTabla(resultados);
            lblEstado.setText(resultados.size() + " resultado(s) para '" + criterio + " = " + id + "'.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID numérico válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            handleError("Error durante la búsqueda.", e);
        }
    }
    
    private void accionVerStockBajo() {
        try {
            List<InventarioDTO> inventarios = inventarioService.obtenerStockBajo();
            actualizarTabla(inventarios);
            if (inventarios.isEmpty()) {
                lblEstado.setText("No hay productos con stock bajo.");
            } else {
                lblEstado.setText("Mostrando " + inventarios.size() + " productos con stock bajo.");
            }
        } catch (Exception e) {
            handleError("Error al obtener productos con stock bajo.", e);
        }
    }
    
    private void accionActualizarStock() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro del inventario para actualizar.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idInventario = (int) tableModel.getValueAt(filaSeleccionada, 0);
            String productoNombre = (String) tableModel.getValueAt(filaSeleccionada, 1);
            int cantidadActual = (int) tableModel.getValueAt(filaSeleccionada, 3);

            String nuevaCantidadStr = JOptionPane.showInputDialog(
                this,
                "Ingrese la nueva cantidad para el producto:\n'" + productoNombre + "'",
                "Actualizar Stock (ID: " + idInventario + ")",
                JOptionPane.QUESTION_MESSAGE
            );

            if (nuevaCantidadStr == null || nuevaCantidadStr.trim().isEmpty()) {
                return; // El usuario canceló o no ingresó nada
            }

            int nuevaCantidad = Integer.parseInt(nuevaCantidadStr);
            if (nuevaCantidad < 0) {
                JOptionPane.showMessageDialog(this, "La cantidad no puede ser negativa.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int cambio = nuevaCantidad - cantidadActual;

            if (cambio > 0) {
                inventarioService.aumentarStock(idInventario, cambio);
            } else if (cambio < 0) {
                inventarioService.disminuirStock(idInventario, -cambio); // El servicio espera un valor positivo
            } else {
                // No hay cambios
                return;
            }

            JOptionPane.showMessageDialog(this, "Stock actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Recargar la vista actual para reflejar el cambio
            int indiceFila = tablaInventario.getSelectedRow();
            cargarTodoElInventario(); // O una recarga más inteligente si se quiere mantener el filtro
            if (indiceFila != -1) {
                tablaInventario.setRowSelectionInterval(indiceFila, indiceFila);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número entero válido para la cantidad.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            handleError("No se pudo actualizar el stock.", e);
        }
    }

    private void actualizarTabla(List<InventarioDTO> inventarios) {
        tableModel.setRowCount(0); // Limpiar la tabla
        if (inventarios != null) {
            for (InventarioDTO inv : inventarios) {
                String estado = "NORMAL";
                tableModel.addRow(new Object[]{
                    inv.getIdInventario(),
                    inv.getNombreProducto() != null ? 
                    (inv.getNombreProducto().length() > 24 ? 
                        inv.getNombreProducto().substring(0, 21) + "..." : 
                        inv.getNombreProducto()) : "N/A",
                     inv.getDescripcionAlmacen() != null ? 
                    (inv.getDescripcionAlmacen().length() > 19 ? 
                        inv.getDescripcionAlmacen().substring(0, 16) + "..." : 
                        inv.getDescripcionAlmacen()) : "N/A",
                     inv.getCantidad(),
                inv.getStockMinimo(),
                inv.getStockMaximo(),
                estado
                });
            }
        }
    }
    
    private void handleError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(this, mensaje + "\nDetalle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace(); // Para depuración en consola
        lblEstado.setText("Error: " + mensaje);
    }
}