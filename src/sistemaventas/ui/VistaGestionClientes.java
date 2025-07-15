package sistemaventas.ui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 

import sistemaventas.dto.ClienteDTO;
import sistemaventas.service.impl.ClienteServiceImpl;
import sistemaventas.service.interfaces.IClienteService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class VistaGestionClientes extends JFrame {

    private final IClienteService clienteService;
    private DefaultTableModel tableModel;
    private JTable tablaClientes;
    private JTextField txtBusqueda;
    private JComboBox<String> comboBusqueda;
    private JLabel lblEstado;

    public VistaGestionClientes() {
        this.clienteService = new ClienteServiceImpl(); // Instancia del servicio
        initUI();
    }

    private void initUI() {
        setTitle("Gestión de Clientes");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cierra solo esta ventana, no toda la app

        // --- Panel Principal ---
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. Panel Norte (Búsqueda y Acciones) ---
        panelPrincipal.add(crearPanelNorte(), BorderLayout.NORTH);

        // --- 2. Panel Central (Tabla de Clientes) ---
        panelPrincipal.add(crearPanelCentro(), BorderLayout.CENTER);

        // --- 3. Panel Sur (Barra de Estado) ---
        lblEstado = new JLabel("Listo.");
        panelPrincipal.add(lblEstado, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Cargar datos iniciales
        cargarTodosLosClientes();
    }

    private JPanel crearPanelNorte() {
        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));

        // Panel de Búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBorder(new TitledBorder("Búsqueda de Clientes"));

        comboBusqueda = new JComboBox<>(new String[]{"Nombre", "ID", "Documento"});
        txtBusqueda = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar / Recargar");

        panelBusqueda.add(new JLabel("Buscar por:"));
        panelBusqueda.add(comboBusqueda);
        panelBusqueda.add(txtBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        // Panel de Acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Registrar Nuevo Cliente");
        JButton btnEditar = new JButton("Actualizar Cliente");
        JButton btnEliminar = new JButton("Eliminar Cliente");
        
        // Añadir iconos a los botones 
        btnAgregar.setIcon(new ImageIcon(getClass().getResource("/resources/icons/user_add.png")));
        btnEditar.setIcon(new ImageIcon(getClass().getResource("/resources/icons/edit.png"))); 
        btnEliminar.setIcon(new ImageIcon(getClass().getResource("/resources/icons/delete.png")));

        panelAcciones.add(btnAgregar);
        panelAcciones.add(btnEditar);
        panelAcciones.add(btnEliminar);

        panelNorte.add(panelBusqueda, BorderLayout.CENTER);
        panelNorte.add(panelAcciones, BorderLayout.SOUTH);

        // --- Lógica de los botones ---
        btnBuscar.addActionListener(e -> accionBuscar());
        btnLimpiar.addActionListener(e -> cargarTodosLosClientes());
        btnAgregar.addActionListener(e -> accionAgregar());
        btnEditar.addActionListener(e -> accionEditar());
        btnEliminar.addActionListener(e -> accionEliminar());

        return panelNorte;
    }

    private JScrollPane crearPanelCentro() {
        String[] columnas = {"ID", "Nombre Completo", "N° Documento", "Correo Electrónico"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientes = new JTable(tableModel);
        return new JScrollPane(tablaClientes);
    }

    // --- MÉTODOS DE LÓGICA Y ACCIONES ---

    private void cargarTodosLosClientes() {
        try {
            List<ClienteDTO> clientes = clienteService.listarClientes();
            actualizarTabla(clientes);
            lblEstado.setText(clientes.size() + " clientes encontrados.");
            txtBusqueda.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionBuscar() {
        String criterio = (String) comboBusqueda.getSelectedItem();
        String valor = txtBusqueda.getText().trim();

        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo de búsqueda no puede estar vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<ClienteDTO> resultados = Collections.emptyList();
            switch (criterio) {
                case "ID":
                    try {
                        int id = Integer.parseInt(valor);
                        ClienteDTO cliente = clienteService.obtenerClientePorId(id);
                        if (cliente != null) {
                            resultados = Collections.singletonList(cliente);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Para buscar por ID, ingrese un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
                case "Documento":
                    ClienteDTO clienteDoc = clienteService.obtenerClientePorDocumento(valor);
                     if (clienteDoc != null) {
                        resultados = Collections.singletonList(clienteDoc);
                    }
                    break;
                case "Nombre":
                    resultados = clienteService.buscarClientesPorNombre(valor);
                    break;
            }
            actualizarTabla(resultados);
            lblEstado.setText(resultados.size() + " resultado(s) para '" + valor + "'.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la búsqueda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            actualizarTabla(Collections.emptyList());
        }
    }
    
    private void accionAgregar() {
        // Usamos el diálogo para crear un nuevo cliente
        DialogoCliente dialogo = new DialogoCliente(this, clienteService, null);
        dialogo.setVisible(true);
        
        // Si el diálogo fue exitoso, recargamos la tabla
        if (dialogo.isGuardadoExitoso()) {
            cargarTodosLosClientes();
        }
    }

    private void accionEditar() {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente de la tabla para editar.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idCliente = (int) tableModel.getValueAt(filaSeleccionada, 0);
            ClienteDTO clienteAEditar = clienteService.obtenerClientePorId(idCliente);
            
            // Usamos el mismo diálogo, pero ahora para editar
            DialogoCliente dialogo = new DialogoCliente(this, clienteService, clienteAEditar);
            dialogo.setVisible(true);

            if (dialogo.isGuardadoExitoso()) {
                cargarTodosLosClientes();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener datos para editar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar() {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para eliminar.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = (int) tableModel.getValueAt(filaSeleccionada, 0);
        String nombreCliente = (String) tableModel.getValueAt(filaSeleccionada, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar al cliente '" + nombreCliente + "' (ID: " + idCliente + ")?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean eliminado = clienteService.eliminarCliente(idCliente);
                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTodosLosClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el cliente.", "Fallo", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarTabla(List<ClienteDTO> clientes) {
        tableModel.setRowCount(0); // Limpiar tabla
        if (clientes != null) {
            for (ClienteDTO cliente : clientes) {
                tableModel.addRow(new Object[]{
                        cliente.getIdCliente(),
                        cliente.getNombre(),
                        cliente.getNroDocumento(),
                        cliente.getCorreo() != null ? cliente.getCorreo() : "N/A"
                });
            }
        }
    }
}