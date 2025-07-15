package sistemaventas.ui;

import sistemaventas.dto.DetalleVentaDTO;
import sistemaventas.dto.VentaDTO;
import sistemaventas.service.impl.VentaServiceImpl;
import sistemaventas.service.interfaces.IVentaService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class VistaGestionVentas extends JFrame {

    private final IVentaService ventaService;
    private JTable tablaVentas, tablaDetalles;
    private DefaultTableModel modelVentas, modelDetalles;
    private JButton btnCompletar, btnCancelar;
    private JLabel lblEstado;

    // Iconos para la tabla de estados. Colócalos en src/main/resources/icons/
    private final ImageIcon iconPendiente = new ImageIcon(getClass().getResource("/resources/icons/pending.png"));
    private final ImageIcon iconCompletada = new ImageIcon(getClass().getResource("/resources/icons/ok.png"));
    private final ImageIcon iconCancelada = new ImageIcon(getClass().getResource("/resources/icons/cancel.png")); 

    public VistaGestionVentas() {
        this.ventaService = new VentaServiceImpl();
        initUI();
    }

    private void initUI() {
        setTitle("Gestión de Ventas y Transacciones");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.setBackground(new Color(230, 235, 240));

        panelPrincipal.add(crearPanelControles(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, crearPanelTablaVentas(), crearPanelTablaDetalles());
        splitPane.setResizeWeight(0.6);
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        
        lblEstado = new JLabel("Listo.");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        panelPrincipal.add(lblEstado, BorderLayout.SOUTH);

        add(panelPrincipal);
        
        tablaVentas.getSelectionModel().addListSelectionListener(this::onVentaSeleccionada);
        cargarTodasLasVentas();
    }
    
    private JPanel crearPanelControles() {
        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.setOpaque(false);
        
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelAcciones.setOpaque(false);
        
        JButton btnNuevaVenta = new JButton("Nueva Venta");
        btnCompletar = new JButton("Completar Venta");
        btnCancelar = new JButton("Cancelar Venta");
        
        estilizarBoton(btnNuevaVenta, new ImageIcon(getClass().getResource("/resources/icons/box_add.png")));
        estilizarBoton(btnCompletar, new ImageIcon(getClass().getResource("/resources/icons/save.png")));
        estilizarBoton(btnCancelar, new ImageIcon(getClass().getResource("/resources/icons/delete.png")));

        btnCompletar.setEnabled(false);
        btnCancelar.setEnabled(false);
        
        panelAcciones.add(btnNuevaVenta);
        panelAcciones.add(btnCompletar);
        panelAcciones.add(btnCancelar);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBusqueda.setOpaque(false);
        JButton btnVerTodo = new JButton("Ver Todas");
        JButton btnVerHoy = new JButton("Ver Hoy");
        JButton btnBuscarPorCliente = new JButton("Buscar por Cliente");

        panelBusqueda.add(btnVerTodo);
        panelBusqueda.add(btnVerHoy);
        panelBusqueda.add(btnBuscarPorCliente);

        panelControles.add(panelAcciones, BorderLayout.WEST);
        panelControles.add(panelBusqueda, BorderLayout.EAST);
        
        btnNuevaVenta.addActionListener(e -> accionNuevaVenta());
        btnVerTodo.addActionListener(e -> cargarTodasLasVentas());
        btnVerHoy.addActionListener(e -> cargarVentasHoy());
        btnBuscarPorCliente.addActionListener(e -> accionBuscarPorCliente());
        btnCompletar.addActionListener(e -> accionCompletarVenta());
        btnCancelar.addActionListener(e -> accionCancelarVenta());

        return panelControles;
    }

    private JScrollPane crearPanelTablaVentas() {
        String[] colsVentas = {"ID", "Fecha", "Cliente", "Total", "Estado"};
        modelVentas = new DefaultTableModel(colsVentas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return VentaDTO.class;
                return super.getColumnClass(columnIndex);
            }
        };
        tablaVentas = new JTable(modelVentas);
        tablaVentas.setRowHeight(25);
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaVentas.getColumn("Estado").setCellRenderer(new EstadoVentaRenderer());
        return new JScrollPane(tablaVentas);
    }
    
    private JScrollPane crearPanelTablaDetalles() {
        String[] colsDetalles = {"Producto", "Cantidad", "P. Unitario", "Subtotal"};
        modelDetalles = new DefaultTableModel(colsDetalles, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalles = new JTable(modelDetalles);
        tablaDetalles.setRowHeight(25);
        tablaDetalles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollDetalles = new JScrollPane(tablaDetalles);
        scrollDetalles.setBorder(new TitledBorder("Detalle de la Venta Seleccionada"));
        return scrollDetalles;
    }

    private void actualizarTablaVentas(List<VentaDTO> ventas) {
        modelVentas.setRowCount(0);
        modelDetalles.setRowCount(0);
        if (ventas != null) {
            for (VentaDTO venta : ventas) {
                modelVentas.addRow(new Object[]{
                    venta.getIdVentas(),
                    venta.getFecha(),
                    venta.getNombreCliente(), // Asegúrate que tu servicio llene este campo
                    venta.getTotalFormateado(),
                    venta // Pasamos el objeto completo para que el renderer lo use
                });
            }
        }
        lblEstado.setText("Mostrando " + modelVentas.getRowCount() + " ventas.");
    }
    
    private void onVentaSeleccionada(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting() && tablaVentas.getSelectedRow() != -1) {
            int filaView = tablaVentas.getSelectedRow();
            int idVenta = (int) modelVentas.getValueAt(filaView, 0);
            VentaDTO ventaSeleccionada = (VentaDTO) modelVentas.getValueAt(filaView, 4);
            
            try {
                VentaDTO ventaCompleta = ventaService.obtenerVentaPorId(idVenta);
                modelDetalles.setRowCount(0);
                if (ventaCompleta != null && ventaCompleta.getDetalles() != null) {
                    for (DetalleVentaDTO detalle : ventaCompleta.getDetalles()) {
                        modelDetalles.addRow(new Object[]{
                            detalle.getNombreProducto(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitarioFormateado(),
                            detalle.getPreciototalFormateado()
                        });
                    }
                }
                boolean esPendiente = ventaSeleccionada.isPendiente();
                btnCompletar.setEnabled(esPendiente);
                btnCancelar.setEnabled(esPendiente);
            } catch (Exception e) {
                handleError("Error al cargar el detalle de la venta", e);
            }
        }
    }
    
    private void cargarTodasLasVentas() {
        try {
            List<VentaDTO> ventas = ventaService.listarVentas();
            actualizarTablaVentas(ventas);
        } catch (Exception e) {
            handleError("Error al cargar todas las ventas", e);
        }
    }
    
    private void cargarVentasHoy() {
        try {
            List<VentaDTO> ventas = ventaService.obtenerVentasHoy();
            actualizarTablaVentas(ventas);
        } catch (Exception e) {
            handleError("Error al cargar las ventas del día", e);
        }
    }

    private void accionNuevaVenta() {
        DialogoNuevaVenta dialogo = new DialogoNuevaVenta(this);
        dialogo.setVisible(true);
        if (dialogo.isVentaExitosa()) {
            cargarTodasLasVentas();
        }
    }
    
    private void accionBuscarPorCliente() {
        String idClienteStr = JOptionPane.showInputDialog(this, "Ingrese el ID del Cliente:");
        if (idClienteStr != null && !idClienteStr.trim().isEmpty()) {
            try {
                int idCliente = Integer.parseInt(idClienteStr);
                List<VentaDTO> ventas = ventaService.buscarVentasPorCliente(idCliente);
                actualizarTablaVentas(ventas);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID de cliente inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                handleError("Error buscando ventas por cliente", e);
            }
        }
    }
    
    private void accionCompletarVenta() {
        int idVenta = getSelectedVentaId();
        if (idVenta == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Confirmar que la venta ID " + idVenta + " ha sido completada?", "Completar Venta", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ventaService.completarVenta(idVenta);
                JOptionPane.showMessageDialog(this, "Venta completada exitosamente.");
                refrescarFilaSeleccionada();
            } catch (Exception e) {
                handleError("No se pudo completar la venta", e);
            }
        }
    }

    private void accionCancelarVenta() {
        int idVenta = getSelectedVentaId();
        if (idVenta == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Esto cancelará la venta ID " + idVenta + " y restaurará el stock.\n¿Está seguro?", "Cancelar Venta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ventaService.cancelarVenta(idVenta);
                JOptionPane.showMessageDialog(this, "Venta cancelada y stock restaurado.");
                refrescarFilaSeleccionada();
            } catch (Exception e) {
                handleError("No se pudo cancelar la venta", e);
            }
        }
    }
    
    private int getSelectedVentaId() {
        int filaSeleccionada = tablaVentas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una venta de la tabla.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) modelVentas.getValueAt(filaSeleccionada, 0);
    }
    
    private void refrescarFilaSeleccionada() {
        int fila = tablaVentas.getSelectedRow();
        cargarTodasLasVentas();
        if(fila != -1 && fila < tablaVentas.getRowCount()) {
             tablaVentas.setRowSelectionInterval(fila, fila);
        }
    }

    private void estilizarBoton(JButton button, ImageIcon icon) {
        button.setIcon(icon);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(245, 245, 245));
        button.setFocusPainted(false);
    }
    
    private void handleError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(this, mensaje + ":\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    class EstadoVentaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof VentaDTO) {
                VentaDTO venta = (VentaDTO) value;
                setText(venta.getEstadoDescripcion());
                if (venta.isPendiente()) setIcon(iconPendiente);
                else if (venta.isCompletada()) setIcon(iconCompletada);
                else if (venta.isCancelada()) setIcon(iconCancelada);
                else setIcon(null);
            }
            return this;
        }
    }
}