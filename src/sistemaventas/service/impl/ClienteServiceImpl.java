/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.impl; 

import sistemaventas.dao.interfaces.IClienteDAO;
import sistemaventas.dao.impl.ClienteDAOImpl;
import sistemaventas.dto.ClienteDTO;
import sistemaventas.entity.Cliente;
import sistemaventas.exception.BusinessException;
import sistemaventas.exception.ValidationException;
import sistemaventas.service.interfaces.IClienteService;
import sistemaventas.util.ValidationUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sistemaventas.dto.ClienteDTO;

/**
 * Implementación del servicio de clientes
 * Contiene la lógica de negocio para la gestión de clientes
 */
public class ClienteServiceImpl implements IClienteService {
    
    private final IClienteDAO clienteDAO;
    
    public ClienteServiceImpl() {
        this.clienteDAO = new ClienteDAOImpl();
    }
    
    // Constructor para inyección de dependencias (testing)
    public ClienteServiceImpl(IClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }
    
    @Override
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) throws Exception {
        // Validaciones de negocio
        validarDatosCliente(clienteDTO);
        
        // Verificar documento único
        if (!validarDocumentoUnico(clienteDTO.getNroDocumento())) {
            throw new BusinessException("El documento ya está registrado en el sistema");
        }
        
        // Convertir DTO a Entity
        Cliente cliente = convertirDTOAEntity(clienteDTO);
        
        // Crear cliente
        Cliente clienteCreado = clienteDAO.create(cliente);
        
        // Convertir Entity a DTO y retornar
        return convertirEntityADTO(clienteCreado);
    }
    
    @Override
    public ClienteDTO obtenerClientePorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de cliente inválido");
        }
        
        Optional<Cliente> cliente = clienteDAO.findById(id);
        if (cliente.isEmpty()) {
            throw new BusinessException("Cliente no encontrado con ID: " + id);
        }
        
        return convertirEntityADTO(cliente.get());
    }
    
    @Override
    public ClienteDTO obtenerClientePorDocumento(String documento) throws Exception {
        if (!ValidationUtil.isValidDocument(documento)) {
            throw new ValidationException("Documento inválido");
        }
        
        Optional<Cliente> cliente = clienteDAO.findByDocumento(documento);
        if (cliente.isEmpty()) {
            throw new BusinessException("Cliente no encontrado con documento: " + documento);
        }
        
        return convertirEntityADTO(cliente.get());
    }
    
    @Override
    public List<ClienteDTO> listarClientes() throws Exception {
        List<Cliente> clientes = clienteDAO.findAll();
        return convertirListaEntityADTO(clientes);
    }
    
    @Override
    public List<ClienteDTO> buscarClientesPorNombre(String nombre) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("Nombre no puede estar vacío");
        }
        
        List<Cliente> clientes = clienteDAO.findByNombre(nombre.trim());
        return convertirListaEntityADTO(clientes);
    }
    
    @Override
    public ClienteDTO actualizarCliente(ClienteDTO clienteDTO) throws Exception {
        if (clienteDTO.getIdCliente() == null) {
            throw new ValidationException("ID de cliente requerido para actualización");
        }
        
        // Validar que el cliente existe
        obtenerClientePorId(clienteDTO.getIdCliente());
        
        // Validar datos
        validarDatosCliente(clienteDTO);
        
        // Verificar documento único (excluyendo el cliente actual)
        if (!validarDocumentoUnico(clienteDTO.getNroDocumento(), clienteDTO.getIdCliente())) {
            throw new BusinessException("El documento ya está registrado por otro cliente");
        }
        
        // Convertir y actualizar
        Cliente cliente = convertirDTOAEntity(clienteDTO);
        Cliente clienteActualizado = clienteDAO.update(cliente);
        
        return convertirEntityADTO(clienteActualizado);
    }
    
    @Override
    public boolean eliminarCliente(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de cliente inválido");
        }
        
        // Verificar que el cliente existe
        obtenerClientePorId(id);
        
        return clienteDAO.delete(id);
    }
    
    @Override
    public boolean validarDocumentoUnico(String documento) throws Exception {
        Optional<Cliente> cliente = clienteDAO.findByDocumento(documento);
        return cliente.isEmpty();
    }
    
    @Override
    public boolean validarDocumentoUnico(String documento, Integer excludeId) throws Exception {
        Optional<Cliente> clienteExistente = clienteDAO.findByDocumento(documento);
        if (clienteExistente.isEmpty()) {
            return true;
        }
        
        // Si existe, verificar que no sea el mismo cliente que se está actualizando
        return clienteExistente.get().getIdCliente().equals(excludeId);
    }
    
    @Override
    public ClienteDTO obtenerClientePorCorreo(String correo) throws Exception {
        if (!ValidationUtil.isValidEmail(correo)) {
            throw new ValidationException("Correo electrónico inválido");
        }
        
        Optional<Cliente> cliente = clienteDAO.findByCorreo(correo);
        if (cliente.isEmpty()) {
            throw new BusinessException("Cliente no encontrado con correo: " + correo);
        }
        
        return convertirEntityADTO(cliente.get());
    }
    
    @Override
    public List<ClienteDTO> obtenerClientesPorFechaRegistro(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        if (fechaInicio == null || fechaFin == null) {
            throw new ValidationException("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidationException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        List<Cliente> clientes = clienteDAO.findByFechaRegistro(fechaInicio, fechaFin);
        return convertirListaEntityADTO(clientes);
    }
    
    @Override
    public long contarClientesActivos() throws Exception {
        return clienteDAO.countActivos();
    }
    
    @Override
    public List<ClienteDTO> obtenerClientesRecientes(int limite) throws Exception {
        if (limite <= 0) {
            throw new ValidationException("El límite debe ser mayor a cero");
        }
        
        LocalDate fechaLimite = LocalDate.now().minusDays(30); // Últimos 30 días
        List<Cliente> clientes = clienteDAO.findByFechaRegistro(fechaLimite, LocalDate.now());
        
        // Limitar la cantidad de resultados
        List<Cliente> clientesLimitados = clientes.size() > limite ? 
            clientes.subList(0, limite) : clientes;
        
        return convertirListaEntityADTO(clientesLimitados);
    }
    
    // ============================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN Y CONVERSIÓN
    // ============================================
    
    private void validarDatosCliente(ClienteDTO clienteDTO) throws ValidationException {
        if (clienteDTO == null) {
            throw new ValidationException("Datos del cliente requeridos");
        }
        
        if (!ValidationUtil.isValidName(clienteDTO.getNombre())) {
            throw new ValidationException("Nombre inválido. Debe tener entre 2 y 50 caracteres");
        }
        
        if (!ValidationUtil.isValidDocument(clienteDTO.getNroDocumento())) {
            throw new ValidationException("Documento inválido. Debe tener entre 8 y 12 dígitos");
        }
        
        if (clienteDTO.getCorreo() != null && !clienteDTO.getCorreo().trim().isEmpty() 
            && !ValidationUtil.isValidEmail(clienteDTO.getCorreo())) {
            throw new ValidationException("Email inválido");
        }
    }
    
    private Cliente convertirDTOAEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(dto.getIdCliente());
        cliente.setNombre(ValidationUtil.sanitizeString(dto.getNombre()));
        cliente.setNroDocumento(dto.getNroDocumento().trim());
        cliente.setCorreo(dto.getCorreo() != null ? dto.getCorreo().trim() : null);
        cliente.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return cliente;
    }
    
    private ClienteDTO convertirEntityADTO(Cliente entity) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(entity.getIdCliente());
        dto.setNombre(entity.getNombre());
        dto.setNroDocumento(entity.getNroDocumento());
        dto.setCorreo(entity.getCorreo());
        dto.setFechaRegistro(entity.getFechaRegistro());
        dto.setActivo(entity.getActivo());
        return dto;
    }
    
    private List<ClienteDTO> convertirListaEntityADTO(List<Cliente> entities) {
        List<ClienteDTO> dtos = new ArrayList<>();
        for (Cliente entity : entities) {
            dtos.add(convertirEntityADTO(entity));
        }
        return dtos;
    }
}