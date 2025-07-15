package sistemaventas.ui;

import sistemaventas.dto.ClienteDTO;
import sistemaventas.dto.DetalleVentaDTO;
import sistemaventas.dto.InventarioDTO;
import sistemaventas.dto.VentaDTO;
import sistemaventas.service.impl.ClienteServiceImpl;
import sistemaventas.service.impl.InventarioServiceImpl;
import sistemaventas.service.impl.VentaServiceImpl;
import sistemaventas.service.interfaces.IClienteService;
import sistemaventas.service.interfaces.IInventarioService;
import sistemaventas.service.interfaces.IVentaService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DialogoNuevaVenta extends JDialog {

    private IClienteService clienteService;
    private IInventarioService inventarioService;
    private IVentaService ventaService;

    private ClienteDTO clienteSeleccionado;
    private boolean ventaExitosa = false;

    private JLabel lblClienteInfo;
    private JTable tablaProductos, tablaCarrito;
    private DefaultTableModel modelProductos, modelCarrito;
    private JLabel lblTotal;
    private JButton btnFinalizarVenta;

    public DialogoNuevaVenta(Frame parent) {
        super(parent, "Nueva Venta", true);

        this.clienteService = new ClienteServiceImpl();
        this.inventarioService = new InventarioServiceImpl();
        this.ventaService = new VentaServiceImpl();

        initUI();
    }

    private void initUI() {
        setSize(1100, 700);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // --- Panel Norte: Selección de Cliente ---
        JPanel panelCliente = new JPanel(new BorderLayout(10, 0));
        panelCliente.setBorder(new TitledBorder("Paso 1: Seleccionar Cliente"));
        lblClienteInfo = new JLabel("Cliente: (Ninguno seleccionado)");
        lblClienteInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton btnBuscarCliente = new JButton("Buscar Cliente");
        panelCliente.add(lblClienteInfo, BorderLayout.CENTER);
        panelCliente.add(btnBuscarCliente, BorderLayout.EAST);
        panelCliente.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Paso 1: Seleccionar Cliente"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5))
        );

        // <--- CAMBIO CLAVE: Panel central que contendrá todo el proceso de venta --->
        JPanel panelVenta = new JPanel(new BorderLayout(10, 10));

        // Productos disponibles (Izquierda)
        String[] colsProds = {"ID Inv.", "Producto", "Stock", "Precio"};
        modelProductos = new DefaultTableModel(colsProds, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        tablaProductos = new JTable(modelProductos);
        JScrollPane scrollProds = new JScrollPane(tablaProductos);
        scrollProds.setBorder(new TitledBorder("Paso 2: Productos Disponibles"));
        panelVenta.add(scrollProds, BorderLayout.WEST);

        // Carrito (Derecha)
        String[] colsCarrito = {"ID Inv.", "Producto", "Cant.", "P. Unit.", "Subtotal"};
        modelCarrito = new DefaultTableModel(colsCarrito, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        tablaCarrito = new JTable(modelCarrito);
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBorder(new TitledBorder("Paso 3: Carrito de Compras"));
        panelVenta.add(scrollCarrito, BorderLayout.EAST);

        // Panel para el botón de agregar (Centro)
        JPanel panelBotonAgregar = new JPanel();
        // Usamos un BoxLayout para centrar el botón verticalmente
        panelBotonAgregar.setLayout(new BoxLayout(panelBotonAgregar, BoxLayout.Y_AXIS));
        JButton btnAgregar = new JButton(">>");
        btnAgregar.setToolTipText("Agregar producto seleccionado al carrito");
        btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el botón
        
        panelBotonAgregar.add(Box.createVerticalGlue()); // Espacio flexible arriba
        panelBotonAgregar.add(btnAgregar);
        panelBotonAgregar.add(Box.createVerticalGlue()); // Espacio flexible abajo
        
        panelVenta.add(panelBotonAgregar, BorderLayout.CENTER);
        
        // Configurar tamaños preferidos para que el layout se vea bien
        scrollProds.setPreferredSize(new Dimension(500, 0));
        scrollCarrito.setPreferredSize(new Dimension(500, 0));


        // --- Panel Sur: Total y Acciones Finales ---
        JPanel panelSur = new JPanel(new BorderLayout());
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: S/ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panelTotal.add(lblTotal);

        JPanel panelAccionesFinales = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnFinalizarVenta = new JButton("Finalizar Venta");
        btnFinalizarVenta.setEnabled(false);
        JButton btnCancelar = new JButton("Cancelar");
        panelAccionesFinales.add(btnFinalizarVenta);
        panelAccionesFinales.add(btnCancelar);

        panelSur.add(panelTotal, BorderLayout.NORTH);
        panelSur.add(panelAccionesFinales, BorderLayout.CENTER);

        // --- Armar la ventana principal del diálogo ---
        add(panelCliente, BorderLayout.NORTH);
        add(panelVenta, BorderLayout.CENTER); // <-- Añadimos el panel reestructurado
        add(panelSur, BorderLayout.SOUTH);

        // --- Lógica de Botones ---
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnFinalizarVenta.addActionListener(e -> finalizarVenta());
        btnCancelar.addActionListener(e -> dispose());

        cargarProductosDisponibles();
    }

    // El resto de los métodos (cargarProductosDisponibles, buscarCliente, agregarAlCarrito, etc.)
    // permanecen exactamente iguales a como estaban antes.
    // ... (pega aquí el resto de los métodos de la versión anterior sin cambios) ...

    private void cargarProductosDisponibles() {
        try {
            List<InventarioDTO> inventarios = inventarioService.listarInventarios();
            modelProductos.setRowCount(0);
            for (InventarioDTO inv : inventarios) {
                if (inv.getCantidad() > 0) {
                    modelProductos.addRow(new Object[]{
                        inv.getIdInventario(),
                        inv.getNombreProducto(),
                        inv.getCantidad(),
                        inv.getPrecioProducto()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarCliente() {
        String idClienteStr = JOptionPane.showInputDialog(this, "Ingrese ID o Documento del Cliente:");
        if (idClienteStr != null && !idClienteStr.trim().isEmpty()) {
            try {
                try {
                    int id = Integer.parseInt(idClienteStr);
                    clienteSeleccionado = clienteService.obtenerClientePorId(id);
                } catch (NumberFormatException ex) {
                    clienteSeleccionado = clienteService.obtenerClientePorDocumento(idClienteStr);
                }

                if (clienteSeleccionado != null) {
                    lblClienteInfo.setText("Cliente: " + clienteSeleccionado.getNombre() + " (ID: " + clienteSeleccionado.getIdCliente() + ")");
                    checkEstadoFinalizar();
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Búsqueda", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void agregarAlCarrito() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idInventario = (int) modelProductos.getValueAt(filaSeleccionada, 0);
        String nombreProd = (String) modelProductos.getValueAt(filaSeleccionada, 1);
        int stockDisp = (int) modelProductos.getValueAt(filaSeleccionada, 2);
        BigDecimal precio = (BigDecimal) modelProductos.getValueAt(filaSeleccionada, 3);

        String cantStr = JOptionPane.showInputDialog(this, "Cantidad para '" + nombreProd + "':", "Agregar Producto", JOptionPane.QUESTION_MESSAGE);
        if (cantStr != null) {
            try {
                int cantidad = Integer.parseInt(cantStr);
                if (cantidad <= 0 || cantidad > stockDisp) {
                    JOptionPane.showMessageDialog(this, "Cantidad inválida o excede el stock.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));
                modelCarrito.addRow(new Object[]{idInventario, nombreProd, cantidad, precio, subtotal});
                actualizarTotal();
                checkEstadoFinalizar();

            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this, "Ingrese una cantidad numérica válida.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < modelCarrito.getRowCount(); i++) {
            total = total.add((BigDecimal) modelCarrito.getValueAt(i, 4));
        }
        lblTotal.setText(String.format("Total: S/ %.2f", total));
    }

    private void checkEstadoFinalizar() {
        boolean clienteOk = clienteSeleccionado != null;
        boolean carritoOk = modelCarrito.getRowCount() > 0;
        btnFinalizarVenta.setEnabled(clienteOk && carritoOk);
    }

    private void finalizarVenta() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Confirmar y registrar esta venta?", "Finalizar Venta", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                VentaDTO nuevaVenta = new VentaDTO(clienteSeleccionado.getIdCliente(), LocalDate.now());
                VentaDTO ventaCreada = ventaService.crearVenta(nuevaVenta);
                
                for (int i = 0; i < modelCarrito.getRowCount(); i++) {
                    int idInventario = (int) modelCarrito.getValueAt(i, 0);
                    int cantidad = (int) modelCarrito.getValueAt(i, 2);
                    BigDecimal precio = (BigDecimal) modelCarrito.getValueAt(i, 3);
                    
                    DetalleVentaDTO detalle = new DetalleVentaDTO(ventaCreada.getIdVentas(), idInventario, cantidad, precio);
                    ventaService.agregarDetalleVenta(ventaCreada.getIdVentas(), detalle);
                    inventarioService.disminuirStock(idInventario, cantidad);
                }
                
                JOptionPane.showMessageDialog(this, "Venta registrada exitosamente con ID: " + ventaCreada.getIdVentas(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.ventaExitosa = true;
                dispose();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al registrar la venta: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public boolean isVentaExitosa() {
        return ventaExitosa;
    }
}