/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.impl;

import sistemaventas.dao.interfaces.IInventarioDAO;
import sistemaventas.dao.impl.InventarioDAOImpl;
import sistemaventas.dto.InventarioDTO;
import sistemaventas.entity.Inventario;
import sistemaventas.exception.BusinessException;
import sistemaventas.exception.ValidationException;
import sistemaventas.service.interfaces.IInventarioService;
import sistemaventas.util.ValidationUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de inventario Contiene la lógica de negocio para
 * la gestión de inventario
 */
public class InventarioServiceImpl implements IInventarioService {

    private final IInventarioDAO inventarioDAO;

    public InventarioServiceImpl() {
        this.inventarioDAO = new InventarioDAOImpl();
    }

    // Constructor para inyección de dependencias (testing)
    public InventarioServiceImpl(IInventarioDAO inventarioDAO) {
        this.inventarioDAO = inventarioDAO;
    }

    @Override
    public InventarioDTO crearInventario(InventarioDTO inventarioDTO) throws Exception {
        // Validaciones de negocio
        validarDatosInventario(inventarioDTO);

        // Verificar que no exista ya el producto en ese almacén
        try {
            InventarioDTO existente = obtenerInventarioPorProductoYAlmacen(
                    inventarioDTO.getIdProducto(), inventarioDTO.getIdAlmacen());
            if (existente != null) {
                throw new BusinessException("El producto ya existe en este almacén. Use actualizar stock.");
            }
        } catch (BusinessException e) {
            // Si no existe, continuamos con la creación
            if (!e.getMessage().contains("no encontrado")) {
                throw e;
            }
        }

        // Convertir DTO a Entity
        Inventario inventario = convertirDTOAEntity(inventarioDTO);

        // Crear inventario
        Inventario inventarioCreado = inventarioDAO.create(inventario);

        // Convertir Entity a DTO y retornar
        return convertirEntityADTO(inventarioCreado);
    }

    @Override
    public InventarioDTO obtenerInventarioPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de inventario inválido");
        }

        Optional<Inventario> inventario = inventarioDAO.findById(id);
        if (inventario.isEmpty()) {
            throw new BusinessException("Inventario no encontrado con ID: " + id);
        }

        return convertirEntityADTO(inventario.get());
    }

    @Override
    public List<InventarioDTO> listarInventarios() throws Exception {
        List<Inventario> inventarios = inventarioDAO.findAll();
        return convertirListaEntityADTO(inventarios);
    }

    @Override
    public List<InventarioDTO> obtenerInventarioPorAlmacen(Integer idAlmacen) throws Exception {
        if (idAlmacen == null || idAlmacen <= 0) {
            throw new ValidationException("ID de almacén inválido");
        }

        List<Inventario> inventarios = inventarioDAO.findByAlmacen(idAlmacen);
        return convertirListaEntityADTO(inventarios);
    }

    @Override
    public List<InventarioDTO> obtenerInventarioPorProducto(Integer idProducto) throws Exception {
        if (idProducto == null || idProducto <= 0) {
            throw new ValidationException("ID de producto inválido");
        }

        List<Inventario> inventarios = inventarioDAO.findByProducto(idProducto);
        return convertirListaEntityADTO(inventarios);
    }

    @Override
    public List<InventarioDTO> obtenerStockBajo() throws Exception {
        List<Inventario> inventarios = inventarioDAO.findStockBajo();
        return convertirListaEntityADTO(inventarios);
    }

    @Override
    public List<InventarioDTO> obtenerStockAlto() throws Exception {
        InventarioDAOImpl inventarioDAOImpl = (InventarioDAOImpl) inventarioDAO;
        List<Inventario> inventarios = inventarioDAOImpl.findStockAlto();
        return convertirListaEntityADTO(inventarios);
    }

    @Override
    public List<InventarioDTO> obtenerStockNormal() throws Exception {
        InventarioDAOImpl inventarioDAOImpl = (InventarioDAOImpl) inventarioDAO;
        List<Inventario> inventarios = inventarioDAOImpl.findStockNormal();
        return convertirListaEntityADTO(inventarios);
    }

    @Override
    public boolean verificarStockDisponible(Integer idInventario, Integer cantidad) throws Exception {
        if (idInventario == null || idInventario <= 0) {
            throw new ValidationException("ID de inventario inválido");
        }

        if (!ValidationUtil.isValidQuantity(cantidad)) {
            throw new ValidationException("Cantidad inválida");
        }

        Optional<Inventario> inventario = inventarioDAO.findById(idInventario);
        if (inventario.isEmpty()) {
            throw new BusinessException("Inventario no encontrado");
        }

        return inventario.get().puedeVender(cantidad);
    }

    @Override
    public InventarioDTO actualizarStock(Integer idInventario, Integer cantidad) throws Exception {
        if (idInventario == null || idInventario <= 0) {
            throw new ValidationException("ID de inventario inválido");
        }

        if (cantidad == null || cantidad < 0) {
            throw new ValidationException("Cantidad debe ser mayor o igual a cero");
        }

        // Verificar que el inventario existe
        InventarioDTO inventarioActual = obtenerInventarioPorId(idInventario);

        // Actualizar en base de datos
        boolean actualizado = inventarioDAO.actualizarStock(idInventario, cantidad);
        if (!actualizado) {
            throw new BusinessException("No se pudo actualizar el stock");
        }

        // Retornar inventario actualizado
        return obtenerInventarioPorId(idInventario);
    }

    @Override
    public InventarioDTO aumentarStock(Integer idInventario, Integer cantidad) throws Exception {
        if (!ValidationUtil.isValidQuantity(cantidad)) {
            throw new ValidationException("Cantidad a aumentar debe ser mayor a cero");
        }

        // Obtener inventario actual
        InventarioDTO inventarioActual = obtenerInventarioPorId(idInventario);

        // Calcular nueva cantidad
        int nuevaCantidad = inventarioActual.getCantidad() + cantidad;

        // Verificar que no exceda el stock máximo
        if (nuevaCantidad > inventarioActual.getStockMaximo()) {
            throw new BusinessException(
                    String.format("La cantidad resultante (%d) excede el stock máximo (%d)",
                            nuevaCantidad, inventarioActual.getStockMaximo()));
        }

        // Actualizar stock
        return actualizarStock(idInventario, nuevaCantidad);
    }

    @Override
    public InventarioDTO disminuirStock(Integer idInventario, Integer cantidad) throws Exception {
        if (!ValidationUtil.isValidQuantity(cantidad)) {
            throw new ValidationException("Cantidad a disminuir debe ser mayor a cero");
        }

        // Obtener inventario actual
        InventarioDTO inventarioActual = obtenerInventarioPorId(idInventario);

        // Verificar que haya suficiente stock
        if (inventarioActual.getCantidad() < cantidad) {
            throw new BusinessException(
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                            inventarioActual.getCantidad(), cantidad));
        }

        // Calcular nueva cantidad
        int nuevaCantidad = inventarioActual.getCantidad() - cantidad;

        // Actualizar stock
        return actualizarStock(idInventario, nuevaCantidad);
    }

    @Override
    public InventarioDTO obtenerInventarioPorProductoYAlmacen(Integer idProducto, Integer idAlmacen) throws Exception {
        if (idProducto == null || idProducto <= 0) {
            throw new ValidationException("ID de producto inválido");
        }

        if (idAlmacen == null || idAlmacen <= 0) {
            throw new ValidationException("ID de almacén inválido");
        }

        Optional<Inventario> inventario = inventarioDAO.findByProductoAndAlmacen(idProducto, idAlmacen);
        if (inventario.isEmpty()) {
            throw new BusinessException("Inventario no encontrado para el producto y almacén especificados");
        }

        return convertirEntityADTO(inventario.get());
    }

    @Override
    public InventarioDTO actualizarLimitesStock(Integer idInventario, Integer stockMinimo, Integer stockMaximo) throws Exception {
        if (idInventario == null || idInventario <= 0) {
            throw new ValidationException("ID de inventario inválido");
        }

        if (stockMinimo == null || stockMinimo < 0) {
            throw new ValidationException("Stock mínimo debe ser mayor o igual a cero");
        }

        if (stockMaximo == null || stockMaximo < 0) {
            throw new ValidationException("Stock máximo debe ser mayor o igual a cero");
        }

        if (stockMinimo > stockMaximo) {
            throw new ValidationException("Stock mínimo no puede ser mayor al stock máximo");
        }

        // Obtener inventario actual
        Optional<Inventario> inventarioOpt = inventarioDAO.findById(idInventario);
        if (inventarioOpt.isEmpty()) {
            throw new BusinessException("Inventario no encontrado");
        }

        Inventario inventario = inventarioOpt.get();
        inventario.setStockMinimo(stockMinimo);
        inventario.setStockMaximo(stockMaximo);

        // Actualizar en base de datos
        inventarioDAO.update(inventario);

        // Retornar inventario actualizado
        return obtenerInventarioPorId(idInventario);
    }

    @Override
    public Object obtenerEstadisticasInventario() throws Exception {
        InventarioDAOImpl inventarioDAOImpl = (InventarioDAOImpl) inventarioDAO;
        return inventarioDAOImpl.getInventarioStats();
    }

    @Override
    public boolean transferirStock(Integer idProducto, Integer idAlmacenOrigen, Integer idAlmacenDestino, Integer cantidad) throws Exception {
        if (idProducto == null || idProducto <= 0) {
            throw new ValidationException("ID de producto inválido");
        }

        if (idAlmacenOrigen == null || idAlmacenOrigen <= 0) {
            throw new ValidationException("ID de almacén origen inválido");
        }

        if (idAlmacenDestino == null || idAlmacenDestino <= 0) {
            throw new ValidationException("ID de almacén destino inválido");
        }

        if (idAlmacenOrigen.equals(idAlmacenDestino)) {
            throw new ValidationException("El almacén origen no puede ser el mismo que el destino");
        }

        if (!ValidationUtil.isValidQuantity(cantidad)) {
            throw new ValidationException("Cantidad inválida para transferir");
        }

        // Obtener inventarios origen y destino
        InventarioDTO inventarioOrigen = obtenerInventarioPorProductoYAlmacen(idProducto, idAlmacenOrigen);

        // Verificar stock suficiente en origen
        if (inventarioOrigen.getCantidad() < cantidad) {
            throw new BusinessException("Stock insuficiente en almacén origen");
        }

        // Verificar si existe inventario en destino
        InventarioDTO inventarioDestino = null;
        try {
            inventarioDestino = obtenerInventarioPorProductoYAlmacen(idProducto, idAlmacenDestino);
        } catch (BusinessException e) {
            // Si no existe, crear nuevo inventario en destino
            if (e.getMessage().contains("no encontrado")) {
                InventarioDTO nuevoInventario = new InventarioDTO();
                nuevoInventario.setIdProducto(idProducto);
                nuevoInventario.setIdAlmacen(idAlmacenDestino);
                nuevoInventario.setCantidad(0);
                nuevoInventario.setStockMinimo(inventarioOrigen.getStockMinimo());
                nuevoInventario.setStockMaximo(inventarioOrigen.getStockMaximo());

                inventarioDestino = crearInventario(nuevoInventario);
            } else {
                throw e;
            }
        }

        // Realizar la transferencia
        disminuirStock(inventarioOrigen.getIdInventario(), cantidad);
        aumentarStock(inventarioDestino.getIdInventario(), cantidad);

        return true;
    }

    // ============================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN Y CONVERSIÓN
    // ============================================
    private void validarDatosInventario(InventarioDTO inventarioDTO) throws ValidationException {
        if (inventarioDTO == null) {
            throw new ValidationException("Datos del inventario requeridos");
        }

        if (inventarioDTO.getIdProducto() == null || inventarioDTO.getIdProducto() <= 0) {
            throw new ValidationException("ID de producto inválido");
        }

        if (inventarioDTO.getIdAlmacen() == null || inventarioDTO.getIdAlmacen() <= 0) {
            throw new ValidationException("ID de almacén inválido");
        }

        if (inventarioDTO.getCantidad() == null || inventarioDTO.getCantidad() < 0) {
            throw new ValidationException("Cantidad debe ser mayor o igual a cero");
        }

        if (inventarioDTO.getStockMinimo() != null && inventarioDTO.getStockMinimo() < 0) {
            throw new ValidationException("Stock mínimo debe ser mayor o igual a cero");
        }

        if (inventarioDTO.getStockMaximo() != null && inventarioDTO.getStockMaximo() < 0) {
            throw new ValidationException("Stock máximo debe ser mayor o igual a cero");
        }

        if (inventarioDTO.getStockMinimo() != null && inventarioDTO.getStockMaximo() != null
                && inventarioDTO.getStockMinimo() > inventarioDTO.getStockMaximo()) {
            throw new ValidationException("Stock mínimo no puede ser mayor al stock máximo");
        }
    }

    private Inventario convertirDTOAEntity(InventarioDTO dto) {
        Inventario inventario = new Inventario();
        inventario.setIdInventario(dto.getIdInventario());
        inventario.setIdProducto(dto.getIdProducto());
        inventario.setIdAlmacen(dto.getIdAlmacen());
        inventario.setCantidad(dto.getCantidad());
        inventario.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 10);
        inventario.setStockMaximo(dto.getStockMaximo() != null ? dto.getStockMaximo() : 1000);
        inventario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return inventario;
    }

    private InventarioDTO convertirEntityADTO(Inventario entity) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdInventario(entity.getIdInventario());
        dto.setIdProducto(entity.getIdProducto());
        dto.setIdAlmacen(entity.getIdAlmacen());
        dto.setCantidad(entity.getCantidad());
        dto.setStockMinimo(entity.getStockMinimo());
        dto.setStockMaximo(entity.getStockMaximo());
        dto.setFechaActualizacion(entity.getFechaActualizacion());
        dto.setActivo(entity.getActivo());

        // Mapear información adicional si está disponible
        if (entity.getProducto() != null) {
            dto.setNombreProducto(entity.getProducto().getNombreproducto());
            dto.setPrecioProducto(entity.getProducto().getPrecio());
            dto.setDescripcionProducto(entity.getProducto().getDescripcion());

            // Calcular valor del inventario
            if (entity.getProducto().getPrecio() != null && entity.getCantidad() != null) {
                BigDecimal valorInventario = entity.getProducto().getPrecio()
                        .multiply(BigDecimal.valueOf(entity.getCantidad()));
                dto.setValorInventario(valorInventario);
            }
        }

        if (entity.getAlmacen() != null) {
            dto.setDescripcionAlmacen(entity.getAlmacen().getDescripcion());
            dto.setDireccionAlmacen(entity.getAlmacen().getDireccion());
            dto.setTelefonoAlmacen(entity.getAlmacen().getTelefono());
        }

        // Determinar estado del stock
        String estadoStock = "NORMAL";
        if (entity.isStockBajo()) {
            estadoStock = "BAJO";
        } else if (entity.isStockAlto()) {
            estadoStock = "ALTO";
        }
        dto.setEstadoStock(estadoStock);

        return dto;
    }

    private List<InventarioDTO> convertirListaEntityADTO(List<Inventario> entities) {
        List<InventarioDTO> dtos = new ArrayList<>();
        for (Inventario entity : entities) {
            dtos.add(convertirEntityADTO(entity));
        }
        return dtos;
    }
}
