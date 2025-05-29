/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.impl;

import sistemaventas.dao.interfaces.IVentaDAO;
import sistemaventas.dao.interfaces.IDetalleVentaDAO;
import sistemaventas.dao.impl.VentaDAOImpl;
import sistemaventas.dao.impl.DetalleVentaDAOImpl;
import sistemaventas.dto.VentaDTO;
import sistemaventas.dto.DetalleVentaDTO;
import sistemaventas.entity.Venta;
import sistemaventas.entity.DetalleVenta;
import sistemaventas.exception.BusinessException;
import sistemaventas.exception.ValidationException;
import sistemaventas.service.interfaces.IVentaService;
import sistemaventas.service.interfaces.IInventarioService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de ventas
 * Contiene la lógica de negocio para la gestión de ventas
 */
public class VentaServiceImpl implements IVentaService {
    
    private final IVentaDAO ventaDAO;
    private final IDetalleVentaDAO detalleVentaDAO;
    private final IInventarioService inventarioService;
    
    public VentaServiceImpl() {
        this.ventaDAO = new VentaDAOImpl();
        this.detalleVentaDAO = new DetalleVentaDAOImpl();
        this.inventarioService = new InventarioServiceImpl();
    }
    
    // Constructor para inyección de dependencias (testing)
    public VentaServiceImpl(IVentaDAO ventaDAO, IDetalleVentaDAO detalleVentaDAO, IInventarioService inventarioService) {
        this.ventaDAO = ventaDAO;
        this.detalleVentaDAO = detalleVentaDAO;
        this.inventarioService = inventarioService;
    }
    
    @Override
    public VentaDTO crearVenta(VentaDTO ventaDTO) throws Exception {
        // Validaciones de negocio
        validarDatosVenta(ventaDTO);
        
        // Convertir DTO a Entity
        Venta venta = convertirDTOAEntity(ventaDTO);
        venta.setEstado(Venta.EstadoVenta.PENDIENTE);
        venta.setTotal(BigDecimal.ZERO);
        
        // Crear venta
        Venta ventaCreada = ventaDAO.create(venta);
        
        // Convertir Entity a DTO y retornar
        return convertirEntityADTO(ventaCreada);
    }
    
    @Override
    public VentaDTO obtenerVentaPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de venta inválido");
        }
        
        Optional<Venta> venta = ventaDAO.findById(id);
        if (venta.isEmpty()) {
            throw new BusinessException("Venta no encontrada con ID: " + id);
        }
        
        VentaDTO ventaDTO = convertirEntityADTO(venta.get());
        
        // Cargar detalles de la venta
        List<DetalleVentaDTO> detalles = obtenerDetallesVenta(id);
        ventaDTO.setDetalles(detalles);
        
        return ventaDTO;
    }
    
    @Override
    public List<VentaDTO> listarVentas() throws Exception {
        List<Venta> ventas = ventaDAO.findAll();
        return convertirListaEntityADTO(ventas);
    }
    
    @Override
    public List<VentaDTO> buscarVentasPorCliente(Integer idCliente) throws Exception {
        if (idCliente == null || idCliente <= 0) {
            throw new ValidationException("ID de cliente inválido");
        }
        
        List<Venta> ventas = ventaDAO.findByCliente(idCliente);
        return convertirListaEntityADTO(ventas);
    }
    
    @Override
    public List<VentaDTO> buscarVentasPorFecha(LocalDate fecha) throws Exception {
        if (fecha == null) {
            throw new ValidationException("Fecha es requerida");
        }
        
        List<Venta> ventas = ventaDAO.findByFecha(fecha);
        return convertirListaEntityADTO(ventas);
    }
    
    @Override
    public List<VentaDTO> buscarVentasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        if (fechaInicio == null || fechaFin == null) {
            throw new ValidationException("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidationException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        List<Venta> ventas = ventaDAO.findByFechaRange(fechaInicio, fechaFin);
        return convertirListaEntityADTO(ventas);
    }
    
    @Override
    public List<VentaDTO> buscarVentasPorEstado(String estado) throws Exception {
        if (estado == null || estado.trim().isEmpty()) {
            throw new ValidationException("Estado es requerido");
        }
        
        try {
            Venta.EstadoVenta estadoVenta = Venta.EstadoVenta.valueOf(estado.toUpperCase());
            List<Venta> ventas = ventaDAO.findByEstado(estadoVenta);
            return convertirListaEntityADTO(ventas);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Estado inválido. Debe ser: PENDIENTE, COMPLETADA o CANCELADA");
        }
    }
    
    @Override
    public VentaDTO actualizarVenta(VentaDTO ventaDTO) throws Exception {
        if (ventaDTO.getIdVentas() == null) {
            throw new ValidationException("ID de venta requerido para actualización");
        }
        
        // Validar que la venta existe y puede ser modificada
        VentaDTO ventaExistente = obtenerVentaPorId(ventaDTO.getIdVentas());
        if (!puedeModificarVenta(ventaDTO.getIdVentas())) {
            throw new BusinessException("La venta no puede ser modificada en su estado actual");
        }
        
        // Validar datos
        validarDatosVenta(ventaDTO);
        
        // Convertir y actualizar
        Venta venta = convertirDTOAEntity(ventaDTO);
        venta.setEstado(Venta.EstadoVenta.valueOf(ventaExistente.getEstado()));
        
        Venta ventaActualizada = ventaDAO.update(venta);
        return convertirEntityADTO(ventaActualizada);
    }
    
    @Override
    public boolean cancelarVenta(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de venta inválido");
        }
        
        // Verificar que la venta existe y puede ser cancelada
        VentaDTO venta = obtenerVentaPorId(id);
        if (!"PENDIENTE".equals(venta.getEstado())) {
            throw new BusinessException("Solo se pueden cancelar ventas en estado PENDIENTE");
        }
        
        // Restaurar stock de los productos vendidos
        List<DetalleVentaDTO> detalles = obtenerDetallesVenta(id);
        for (DetalleVentaDTO detalle : detalles) {
            inventarioService.aumentarStock(detalle.getIdInventario(), detalle.getCantidad());
        }
        
        // Marcar venta como cancelada
        return ventaDAO.delete(id);
    }
    
    @Override
    public VentaDTO completarVenta(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de venta inválido");
        }
        
        // Verificar que la venta existe y puede ser completada
        VentaDTO ventaDTO = obtenerVentaPorId(id);
        if (!"PENDIENTE".equals(ventaDTO.getEstado())) {
            throw new BusinessException("Solo se pueden completar ventas en estado PENDIENTE");
        }
        
        // Verificar que tiene detalles
        if (ventaDTO.getDetalles() == null || ventaDTO.getDetalles().isEmpty()) {
            throw new BusinessException("La venta debe tener al menos un producto para ser completada");
        }
        
        // Convertir y actualizar estado
        Venta venta = convertirDTOAEntity(ventaDTO);
        venta.setEstado(Venta.EstadoVenta.COMPLETADA);
        
        Venta ventaActualizada = ventaDAO.update(venta);
        return convertirEntityADTO(ventaActualizada);
    }
    
    @Override
    public DetalleVentaDTO agregarDetalleVenta(Integer idVenta, DetalleVentaDTO detalleVentaDTO) throws Exception {
        if (idVenta == null || idVenta <= 0) {
            throw new ValidationException("ID de venta inválido");
        }
        
        // Validar que la venta existe y puede ser modificada
        if (!puedeModificarVenta(idVenta)) {
            throw new BusinessException("No se pueden agregar productos a esta venta");
        }
        
        // Validar datos del detalle
        validarDatosDetalleVenta(detalleVentaDTO);
        
        // Verificar stock disponible
        if (!inventarioService.verificarStockDisponible(detalleVentaDTO.getIdInventario(), detalleVentaDTO.getCantidad())) {
            throw new BusinessException("Stock insuficiente para la cantidad solicitada");
        }
        
        // Reducir stock del inventario
        inventarioService.disminuirStock(detalleVentaDTO.getIdInventario(), detalleVentaDTO.getCantidad());
        
        // Crear detalle de venta
        DetalleVenta detalleVenta = convertirDetalleVentaDTOAEntity(detalleVentaDTO);
        detalleVenta.setIdVentas(idVenta);
        detalleVenta.calcularTotal();
        
        DetalleVenta detalleCreado = detalleVentaDAO.create(detalleVenta);
        
        // Actualizar total de la venta
        recalcularTotalVenta(idVenta);
        
        return convertirDetalleVentaEntityADTO(detalleCreado);
    }
    
    @Override
    public boolean eliminarDetalleVenta(Integer idVenta, Integer idDetalleVenta) throws Exception {
        if (idVenta == null || idVenta <= 0) {
            throw new ValidationException("ID de venta inválido");
        }
        
        if (idDetalleVenta == null || idDetalleVenta <= 0) {
            throw new ValidationException("ID de detalle venta inválido");
        }
        
        // Validar que la venta puede ser modificada
        if (!puedeModificarVenta(idVenta)) {
            throw new BusinessException("No se pueden eliminar productos de esta venta");
        }
        
        // Obtener detalle para restaurar stock
        Optional<DetalleVenta> detalleOpt = detalleVentaDAO.findById(idDetalleVenta);
        if (detalleOpt.isEmpty()) {
            throw new BusinessException("Detalle de venta no encontrado");
        }
        
        DetalleVenta detalle = detalleOpt.get();
        
        // Verificar que el detalle pertenece a la venta
        if (!detalle.getIdVentas().equals(idVenta)) {
            throw new BusinessException("El detalle no pertenece a la venta especificada");
        }
        
        // Restaurar stock al inventario
        inventarioService.aumentarStock(detalle.getIdInventario(), detalle.getCantidad());
        
        // Eliminar detalle
        boolean eliminado = detalleVentaDAO.delete(idDetalleVenta);
        
        if (eliminado) {
            // Recalcular total de la venta
            recalcularTotalVenta(idVenta);
        }
        
        return eliminado;
    }
    
    @Override
    public List<DetalleVentaDTO> obtenerDetallesVenta(Integer idVenta) throws Exception {
        if (idVenta == null || idVenta <= 0) {
            throw new ValidationException("ID de venta inválido");
        }
        
        List<DetalleVenta> detalles = detalleVentaDAO.findByVenta(idVenta);
        return convertirListaDetalleVentaEntityADTO(detalles);
    }
    
    @Override
    public BigDecimal calcularTotalVentas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        if (fechaInicio == null || fechaFin == null) {
            throw new ValidationException("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidationException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        return ventaDAO.calcularTotalVentas(fechaInicio, fechaFin);
    }
    
    @Override
    public List<VentaDTO> obtenerVentasHoy() throws Exception {
        List<Venta> ventas = ventaDAO.findVentasHoy();
        return convertirListaEntityADTO(ventas);
    }
    
    @Override
    public List<VentaDTO> obtenerVentasMesActual() throws Exception {
        List<Venta> ventas = ventaDAO.findVentasMesActual();
        return convertirListaEntityADTO(ventas);
    }
    
    @Override
    public List<VentaDTO> obtenerVentasRecientes(int limite) throws Exception {
        if (limite <= 0) {
            throw new ValidationException("El límite debe ser mayor a cero");
        }
        
        List<Venta> ventas = ventaDAO.findVentasRecientes(limite);
        return convertirListaEntityADTO(ventas);
    }
    
    @Override
    public boolean puedeModificarVenta(Integer id) throws Exception {
        VentaDTO venta = obtenerVentaPorId(id);
        return "PENDIENTE".equals(venta.getEstado());
    }
    
    @Override
    public Object obtenerEstadisticasVentas() throws Exception {
        // Implementar estadísticas básicas
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        
        BigDecimal ventasHoy = calcularTotalVentas(hoy, hoy);
        BigDecimal ventasMes = calcularTotalVentas(inicioMes, hoy);
        
        long ventasPendientes = ventaDAO.countByEstado(Venta.EstadoVenta.PENDIENTE);
        long ventasCompletadas = ventaDAO.countByEstado(Venta.EstadoVenta.COMPLETADA);
        long ventasCanceladas = ventaDAO.countByEstado(Venta.EstadoVenta.CANCELADA);
        
        return new VentaStats(ventasHoy, ventasMes, ventasPendientes, ventasCompletadas, ventasCanceladas);
    }
    
    // ============================================
    // MÉTODOS PRIVADOS DE UTILIDAD
    // ============================================
    
    private void recalcularTotalVenta(Integer idVenta) throws Exception {
        List<DetalleVentaDTO> detalles = obtenerDetallesVenta(idVenta);
        
        BigDecimal total = detalles.stream()
                .map(DetalleVentaDTO::getPreciototal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Actualizar total en la venta
        Optional<Venta> ventaOpt = ventaDAO.findById(idVenta);
        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();
            venta.setTotal(total);
            ventaDAO.update(venta);
        }
    }
    
    // ============================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN Y CONVERSIÓN
    // ============================================
    
    private void validarDatosVenta(VentaDTO ventaDTO) throws ValidationException {
        if (ventaDTO == null) {
            throw new ValidationException("Datos de la venta requeridos");
        }
        
        if (ventaDTO.getIdCliente() == null || ventaDTO.getIdCliente() <= 0) {
            throw new ValidationException("ID de cliente inválido");
        }
        
        if (ventaDTO.getFecha() == null) {
            throw new ValidationException("Fecha de venta es requerida");
        }
        
        if (ventaDTO.getFecha().isAfter(LocalDate.now())) {
            throw new ValidationException("La fecha de venta no puede ser futura");
        }
    }
    
    private void validarDatosDetalleVenta(DetalleVentaDTO detalleDTO) throws ValidationException {
        if (detalleDTO == null) {
            throw new ValidationException("Datos del detalle de venta requeridos");
        }
        
        if (detalleDTO.getIdInventario() == null || detalleDTO.getIdInventario() <= 0) {
            throw new ValidationException("ID de inventario inválido");
        }
        
        if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
            throw new ValidationException("Cantidad debe ser mayor a cero");
        }
        
        if (detalleDTO.getPrecioUnitario() == null || detalleDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Precio unitario debe ser mayor a cero");
        }
    }
    
    private Venta convertirDTOAEntity(VentaDTO dto) {
        Venta venta = new Venta();
        venta.setIdVentas(dto.getIdVentas());
        venta.setFecha(dto.getFecha());
        venta.setIdCliente(dto.getIdCliente());
        venta.setTotal(dto.getTotal() != null ? dto.getTotal() : BigDecimal.ZERO);
        
        if (dto.getEstado() != null) {
            venta.setEstado(Venta.EstadoVenta.valueOf(dto.getEstado()));
        }
        
        return venta;
    }
    
    private VentaDTO convertirEntityADTO(Venta entity) {
        VentaDTO dto = new VentaDTO();
        dto.setIdVentas(entity.getIdVentas());
        dto.setFecha(entity.getFecha());
        dto.setIdCliente(entity.getIdCliente());
        dto.setTotal(entity.getTotal());
        dto.setEstado(entity.getEstado().name());
        dto.setFechaRegistro(entity.getFechaRegistro());
        return dto;
    }
    
    private List<VentaDTO> convertirListaEntityADTO(List<Venta> entities) {
        List<VentaDTO> dtos = new ArrayList<>();
        for (Venta entity : entities) {
            dtos.add(convertirEntityADTO(entity));
        }
        return dtos;
    }
    
    private DetalleVenta convertirDetalleVentaDTOAEntity(DetalleVentaDTO dto) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdDetalleVenta(dto.getIdDetalleVenta());
        detalle.setIdVentas(dto.getIdVentas());
        detalle.setIdInventario(dto.getIdInventario());
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        detalle.setPreciototal(dto.getPreciototal());
        return detalle;
    }
    
    private DetalleVentaDTO convertirDetalleVentaEntityADTO(DetalleVenta entity) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setIdDetalleVenta(entity.getIdDetalleVenta());
        dto.setIdVentas(entity.getIdVentas());
        dto.setIdInventario(entity.getIdInventario());
        dto.setCantidad(entity.getCantidad());
        dto.setPrecioUnitario(entity.getPrecioUnitario());
        dto.setPreciototal(entity.getPreciototal());
        dto.setFechaRegistro(entity.getFechaRegistro());
        return dto;
    }
    
    private List<DetalleVentaDTO> convertirListaDetalleVentaEntityADTO(List<DetalleVenta> entities) {
        List<DetalleVentaDTO> dtos = new ArrayList<>();
        for (DetalleVenta entity : entities) {
            dtos.add(convertirDetalleVentaEntityADTO(entity));
        }
        return dtos;
    }
// ============================================
    // CLASE INTERNA PARA ESTADÍSTICAS
    // ============================================
    
    public static class VentaStats {
        private final BigDecimal ventasHoy;
        private final BigDecimal ventasMes;
        private final long ventasPendientes;
        private final long ventasCompletadas;
        private final long ventasCanceladas;
        
        public VentaStats(BigDecimal ventasHoy, BigDecimal ventasMes, long ventasPendientes, 
                         long ventasCompletadas, long ventasCanceladas) {
            this.ventasHoy = ventasHoy;
            this.ventasMes = ventasMes;
            this.ventasPendientes = ventasPendientes;
            this.ventasCompletadas = ventasCompletadas;
            this.ventasCanceladas = ventasCanceladas;
        }
        
        // Getters
        public BigDecimal getVentasHoy() { return ventasHoy; }
        public BigDecimal getVentasMes() { return ventasMes; }
        public long getVentasPendientes() { return ventasPendientes; }
        public long getVentasCompletadas() { return ventasCompletadas; }
        public long getVentasCanceladas() { return ventasCanceladas; }
        public long getTotalVentas() { return ventasPendientes + ventasCompletadas + ventasCanceladas; }
        
        @Override
        public String toString() {
            return String.format(
                "VentaStats{ventasHoy=%s, ventasMes=%s, pendientes=%d, completadas=%d, canceladas=%d}",
                ventasHoy, ventasMes, ventasPendientes, ventasCompletadas, ventasCanceladas
            );
        }
    }
}