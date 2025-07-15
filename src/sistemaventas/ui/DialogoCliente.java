/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.ui; 

import sistemaventas.dto.ClienteDTO;
import sistemaventas.service.interfaces.IClienteService;

import javax.swing.*;
import java.awt.*;

public class DialogoCliente extends JDialog {

    private final IClienteService clienteService;
    private final ClienteDTO clienteExistente;
    private boolean guardadoExitoso = false;

    private JTextField txtNombre;
    private JTextField txtDocumento;
    private JTextField txtCorreo;

    /**
     * Constructor para agregar un nuevo cliente o editar uno existente.
     * @param parent El frame padre.
     * @param service El servicio de cliente para realizar operaciones.
     * @param clienteAEditar El cliente a editar. Si es null, el diálogo estará en modo "Agregar".
     */
    public DialogoCliente(Frame parent, IClienteService service, ClienteDTO clienteAEditar) {
        super(parent, true); // JDialog modal
        this.clienteService = service;
        this.clienteExistente = clienteAEditar;
        
        initUI();
        
        if (clienteExistente != null) {
            setTitle("Editar Cliente");
            cargarDatosExistentes();
        } else {
            setTitle("Registrar Nuevo Cliente");
        }
    }

    private void initUI() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Panel de formulario con GridBagLayout para un alineamiento perfecto
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelForm.add(txtNombre, gbc);

        // Fila 2: Documento
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelForm.add(new JLabel("N° Documento:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDocumento = new JTextField(20);
        panelForm.add(txtDocumento, gbc);

        // Fila 3: Correo
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelForm.add(new JLabel("Correo (opcional):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCorreo = new JTextField(20);
        panelForm.add(txtCorreo, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelForm, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // --- Lógica de botones ---
        btnGuardar.addActionListener(e -> guardarCliente());
        btnCancelar.addActionListener(e -> dispose());
    }
    
    private void cargarDatosExistentes() {
        txtNombre.setText(clienteExistente.getNombre());
        txtDocumento.setText(clienteExistente.getNroDocumento());
        txtCorreo.setText(clienteExistente.getCorreo() != null ? clienteExistente.getCorreo() : "");
    }

    private void guardarCliente() {
        String nombre = txtNombre.getText().trim();
        String documento = txtDocumento.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || documento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Documento son campos obligatorios.", "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClienteDTO clienteDTO = new ClienteDTO(nombre, documento, correo.isEmpty() ? null : correo);

        try {
            if (clienteExistente == null) { // Modo Agregar
                clienteService.crearCliente(clienteDTO);
                JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else { // Modo Editar
                clienteDTO.setIdCliente(clienteExistente.getIdCliente());
                clienteService.actualizarCliente(clienteDTO);
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            guardadoExitoso = true;
            dispose(); // Cierra el diálogo
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}