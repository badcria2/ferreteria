 
import sistemaventas.util.ConnectionUtil;
import sistemaventas.dto.ClienteDTO;
import sistemaventas.dto.InventarioDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import sistemaventas.dto.DetalleVentaDTO;
import sistemaventas.dto.VentaDTO;
import sistemaventas.service.interfaces.IClienteService;
import sistemaventas.service.interfaces.IInventarioService;
import sistemaventas.service.impl.ClienteServiceImpl;
import sistemaventas.service.impl.InventarioServiceImpl;
import sistemaventas.service.impl.VentaServiceImpl;
import sistemaventas.service.interfaces.IVentaService;

/**
 * Clase principal del Sistema de Gestión de Ventas
 * Compatible con Java 1.8
 * 
 * @author Sistema de Ventas
 * @version 1.0
 */
public class Main {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final IClienteService clienteService = new ClienteServiceImpl();
    private static final IInventarioService inventarioService = new InventarioServiceImpl();
    private static final IVentaService ventaService = new VentaServiceImpl();

    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("    SISTEMA DE GESTIÓN DE VENTAS");
        System.out.println("===========================================");
        
        // Verificar conexión a base de datos
        try {
            ConnectionUtil.getConnection();
            System.out.println("✓ Conexión a base de datos establecida");
        } catch (Exception e) {
            System.err.println("✗ Error de conexión a base de datos: " + e.getMessage());
            System.err.println("Verifique la configuración en DatabaseConfig.java");
            return;
        }
        
        mostrarMenuPrincipal();
    }
    
    private static void mostrarMenuPrincipal() {
        int opcion;
        
        do {
            System.out.println("\n===========================================");
            System.out.println("                MENÚ PRINCIPAL");
            System.out.println("===========================================");
            System.out.println("1. Gestión de Clientes");
            System.out.println("2. Gestión de Inventario");
            System.out.println("3. Gestión de Ventas");
            System.out.println("4. Reportes");
            System.out.println("0. Salir");
            System.out.println("===========================================");
            System.out.print("Seleccione una opción: ");
            
            opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    menuClientes();
                    break;
                case 2:
                    menuInventario();
                    break;
                case 3:
                    menuVentas();
                    break;
                case 4:
                    menuReportes();
                    break;
                case 0:
                    System.out.println("\n¡Gracias por usar el Sistema de Gestión de Ventas!");
                    cerrarRecursos();
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }
        } while (opcion != 0);
    }
    
    private static void menuClientes() {
        int opcion;
        
        do {
            System.out.println("\n===========================================");
            System.out.println("            GESTIÓN DE CLIENTES");
            System.out.println("===========================================");
            System.out.println("1. Registrar Cliente");
            System.out.println("2. Buscar Cliente por ID");
            System.out.println("3. Buscar Cliente por Documento");
            System.out.println("4. Listar Todos los Clientes");
            System.out.println("5. Buscar Clientes por Nombre");
            System.out.println("6. Actualizar Cliente");
            System.out.println("7. Eliminar Cliente");
            System.out.println("0. Volver al Menú Principal");
            System.out.println("===========================================");
            System.out.print("Seleccione una opción: ");
            
            opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    registrarCliente();
                    break;
                case 2:
                    buscarClientePorId();
                    break;
                case 3:
                    buscarClientePorDocumento();
                    break;
                case 4:
                    listarClientes();
                    break;
                case 5:
                    buscarClientesPorNombre();
                    break;
                case 6:
                    actualizarCliente();
                    break;
                case 7:
                    eliminarCliente();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }
        } while (opcion != 0);
    }
    
    private static void registrarCliente() {
        try {
            System.out.println("\n--- REGISTRAR NUEVO CLIENTE ---");
            
            System.out.print("Nombre completo: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Número de documento: ");
            String documento = scanner.nextLine();
            
            System.out.print("Correo electrónico (opcional): ");
            String correo = scanner.nextLine();
            if (correo.trim().isEmpty()) correo = null;
            
            ClienteDTO clienteDTO = new ClienteDTO(nombre, documento, correo);
            ClienteDTO clienteCreado = clienteService.crearCliente(clienteDTO);
            
            System.out.println("\n✓ Cliente registrado exitosamente:");
            mostrarCliente(clienteCreado);
            
        } catch (Exception e) {
            System.err.println("✗ Error al registrar cliente: " + e.getMessage());
        }
    }
    
    private static void buscarClientePorId() {
        try {
            System.out.println("\n--- BUSCAR CLIENTE POR ID ---");
            System.out.print("Ingrese el ID del cliente: ");
            int id = leerEntero();
            
            ClienteDTO cliente = clienteService.obtenerClientePorId(id);
            mostrarCliente(cliente);
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void buscarClientePorDocumento() {
        try {
            System.out.println("\n--- BUSCAR CLIENTE POR DOCUMENTO ---");
            System.out.print("Ingrese el número de documento: ");
            String documento = scanner.nextLine();
            
            ClienteDTO cliente = clienteService.obtenerClientePorDocumento(documento);
            mostrarCliente(cliente);
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void listarClientes() {
        try {
            System.out.println("\n--- LISTA DE CLIENTES ---");
            List<ClienteDTO> clientes = clienteService.listarClientes();
            
            if (clientes.isEmpty()) {
                System.out.println("No hay clientes registrados.");
                return;
            }
            
            System.out.printf("%-5s %-25s %-15s %-30s%n", "ID", "NOMBRE", "DOCUMENTO", "CORREO");
            System.out.println(repetirCaracter("=", 80));
            
            for (ClienteDTO cliente : clientes) {
                System.out.printf("%-5d %-25s %-15s %-30s%n",
                    cliente.getIdCliente(),
                    cliente.getNombre(),
                    cliente.getNroDocumento(),
                    cliente.getCorreo() != null ? cliente.getCorreo() : "N/A"
                );
            }
            
            System.out.println("\nTotal de clientes: " + clientes.size());
            
        } catch (Exception e) {
            System.err.println("✗ Error al listar clientes: " + e.getMessage());
        }
    }
    
    private static void buscarClientesPorNombre() {
        try {
            System.out.println("\n--- BUSCAR CLIENTES POR NOMBRE ---");
            System.out.print("Ingrese el nombre a buscar: ");
            String nombre = scanner.nextLine();
            
            List<ClienteDTO> clientes = clienteService.buscarClientesPorNombre(nombre);
            
            if (clientes.isEmpty()) {
                System.out.println("No se encontraron clientes con ese nombre.");
                return;
            }
            
            System.out.printf("%-5s %-25s %-15s %-30s%n", "ID", "NOMBRE", "DOCUMENTO", "CORREO");
            System.out.println(repetirCaracter("=", 80));
            
            for (ClienteDTO cliente : clientes) {
                System.out.printf("%-5d %-25s %-15s %-30s%n",
                    cliente.getIdCliente(),
                    cliente.getNombre(),
                    cliente.getNroDocumento(),
                    cliente.getCorreo() != null ? cliente.getCorreo() : "N/A"
                );
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error en la búsqueda: " + e.getMessage());
        }
    }
    
    private static void actualizarCliente() {
        try {
            System.out.println("\n--- ACTUALIZAR CLIENTE ---");
            System.out.print("Ingrese el ID del cliente a actualizar: ");
            int id = leerEntero();
            
            // Buscar cliente existente
            ClienteDTO clienteExistente = clienteService.obtenerClientePorId(id);
            System.out.println("\nCliente actual:");
            mostrarCliente(clienteExistente);
            
            // Solicitar nuevos datos
            System.out.println("\nIngrese los nuevos datos (Enter para mantener el valor actual):");
            
            System.out.print("Nombre [" + clienteExistente.getNombre() + "]: ");
            String nombre = scanner.nextLine();
            if (nombre.trim().isEmpty()) nombre = clienteExistente.getNombre();
            
            System.out.print("Documento [" + clienteExistente.getNroDocumento() + "]: ");
            String documento = scanner.nextLine();
            if (documento.trim().isEmpty()) documento = clienteExistente.getNroDocumento();
            
            System.out.print("Correo [" + (clienteExistente.getCorreo() != null ? clienteExistente.getCorreo() : "N/A") + "]: ");
            String correo = scanner.nextLine();
            if (correo.trim().isEmpty()) correo = clienteExistente.getCorreo();
            
            // Actualizar cliente
            ClienteDTO clienteActualizado = new ClienteDTO(nombre, documento, correo);
            clienteActualizado.setIdCliente(id);
            
            clienteService.actualizarCliente(clienteActualizado);
            System.out.println("\n✓ Cliente actualizado exitosamente");
            
        } catch (Exception e) {
            System.err.println("✗ Error al actualizar cliente: " + e.getMessage());
        }
    }
    
    private static void eliminarCliente() {
        try {
            System.out.println("\n--- ELIMINAR CLIENTE ---");
            System.out.print("Ingrese el ID del cliente a eliminar: ");
            int id = leerEntero();
            
            // Mostrar cliente a eliminar
            ClienteDTO cliente = clienteService.obtenerClientePorId(id);
            System.out.println("\nCliente a eliminar:");
            mostrarCliente(cliente);
            
            System.out.print("\n¿Está seguro de eliminar este cliente? (S/N): ");
            String confirmacion = scanner.nextLine();
            
            if (confirmacion.equalsIgnoreCase("S") || confirmacion.equalsIgnoreCase("SI")) {
                boolean eliminado = clienteService.eliminarCliente(id);
                if (eliminado) {
                    System.out.println("✓ Cliente eliminado exitosamente");
                } else {
                    System.out.println("✗ No se pudo eliminar el cliente");
                }
            } else {
                System.out.println("Operación cancelada");
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error al eliminar cliente: " + e.getMessage());
        }
    }
    
    private static void menuInventario() {
        int opcion;
        
        do {
            System.out.println("\n===========================================");
            System.out.println("            GESTIÓN DE INVENTARIO");
            System.out.println("===========================================");
            System.out.println("1. Ver Todo el Inventario");
            System.out.println("2. Buscar Inventario por ID");
            System.out.println("3. Ver Inventario por Almacén");
            System.out.println("4. Ver Inventario por Producto");
            System.out.println("5. Ver Stock Bajo");
            System.out.println("6. Actualizar Stock");
            System.out.println("0. Volver al Menú Principal");
            System.out.println("===========================================");
            System.out.print("Seleccione una opción: ");
            
            opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    listarInventario();
                    break;
                case 2:
                    buscarInventarioPorId();
                    break;
                case 3:
                    verInventarioPorAlmacen();
                    break;
                case 4:
                    verInventarioPorProducto();
                    break;
                case 5:
                    verStockBajo();
                    break;
                case 6:
                    actualizarStock();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }
        } while (opcion != 0);
    }
    
    private static void listarInventario() {
        try {
            System.out.println("\n--- INVENTARIO COMPLETO ---");
            List<InventarioDTO> inventarios = inventarioService.listarInventarios();
            
            if (inventarios.isEmpty()) {
                System.out.println("No hay registros en el inventario.");
                return;
            }
            
            mostrarTablaInventario(inventarios);
            
        } catch (Exception e) {
            System.err.println("✗ Error al listar inventario: " + e.getMessage());
        }
    }
    
    private static void buscarInventarioPorId() {
        try {
            System.out.println("\n--- BUSCAR INVENTARIO POR ID ---");
            System.out.print("Ingrese el ID del inventario: ");
            int id = leerEntero();
            
            InventarioDTO inventario = inventarioService.obtenerInventarioPorId(id);
            mostrarDetalleInventario(inventario);
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void verInventarioPorAlmacen() {
        try {
            System.out.println("\n--- INVENTARIO POR ALMACÉN ---");
            System.out.print("Ingrese el ID del almacén: ");
            int idAlmacen = leerEntero();
            
            List<InventarioDTO> inventarios = inventarioService.obtenerInventarioPorAlmacen(idAlmacen);
            
            if (inventarios.isEmpty()) {
                System.out.println("No hay productos en este almacén.");
                return;
            }
            
            mostrarTablaInventario(inventarios);
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void verInventarioPorProducto() {
        try {
            System.out.println("\n--- INVENTARIO POR PRODUCTO ---");
            System.out.print("Ingrese el ID del producto: ");
            int idProducto = leerEntero();
            
            List<InventarioDTO> inventarios = inventarioService.obtenerInventarioPorProducto(idProducto);
            
            if (inventarios.isEmpty()) {
                System.out.println("Este producto no está en ningún almacén.");
                return;
            }
            
            mostrarTablaInventario(inventarios);
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void verStockBajo() {
        try {
            System.out.println("\n--- PRODUCTOS CON STOCK BAJO ---");
            List<InventarioDTO> inventarios = inventarioService.obtenerStockBajo();
            
            if (inventarios.isEmpty()) {
                System.out.println("✓ No hay productos con stock bajo.");
                return;
            }
            
            System.out.println("⚠️  ALERTA: Los siguientes productos tienen stock bajo:");
            mostrarTablaInventario(inventarios);
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void actualizarStock() {
        try {
            System.out.println("\n--- ACTUALIZAR STOCK ---");
            System.out.print("Ingrese el ID del inventario: ");
            int idInventario = leerEntero();
            
            // Mostrar inventario actual
            InventarioDTO inventario = inventarioService.obtenerInventarioPorId(idInventario);
            mostrarDetalleInventario(inventario);
            
            System.out.print("\nIngrese la nueva cantidad: ");
            int nuevaCantidad = leerEntero();
            
            if (nuevaCantidad < 0) {
                System.out.println("✗ La cantidad no puede ser negativa");
                return;
            }
            
            // Actualizar usando el método aumentar o disminuir según corresponda
            if (nuevaCantidad > inventario.getCantidad()) {
                int incremento = nuevaCantidad - inventario.getCantidad();
                inventarioService.aumentarStock(idInventario, incremento);
            } else if (nuevaCantidad < inventario.getCantidad()) {
                int decremento = inventario.getCantidad() - nuevaCantidad;
                inventarioService.disminuirStock(idInventario, decremento);
            }
            
            System.out.println("✓ Stock actualizado exitosamente");
            
            // Mostrar inventario actualizado
            InventarioDTO inventarioActualizado = inventarioService.obtenerInventarioPorId(idInventario);
            mostrarDetalleInventario(inventarioActualizado);
            
        } catch (Exception e) {
            System.err.println("✗ Error al actualizar stock: " + e.getMessage());
        }
    }
    
    private static void menuVentas() {
    int opcion;
    
    do {
        System.out.println("\n===========================================");
        System.out.println("            GESTIÓN DE VENTAS");
        System.out.println("===========================================");
        System.out.println("1. Nueva Venta");
        System.out.println("2. Ver Venta por ID");
        System.out.println("3. Listar Ventas del Día");
        System.out.println("4. Listar Todas las Ventas");
        System.out.println("5. Buscar Ventas por Cliente");
        System.out.println("6. Completar Venta");
        System.out.println("7. Cancelar Venta");
        System.out.println("8. Ver Detalle de Venta");
        System.out.println("0. Volver al Menú Principal");
        System.out.println("===========================================");
        System.out.print("Seleccione una opción: ");
        
        opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                nuevaVenta();
                break;
            case 2:
                buscarVentaPorId();
                break;
            case 3:
                listarVentasDelDia();
                break;
            case 4:
                listarTodasLasVentas();
                break;
            case 5:
                buscarVentasPorCliente();
                break;
            case 6:
                completarVenta();
                break;
            case 7:
                cancelarVenta();
                break;
            case 8:
                verDetalleVenta();
                break;
            case 0:
                System.out.println("Volviendo al menú principal...");
                break;
            default:
                System.out.println("Opción inválida. Intente nuevamente.");
                break;
        }
    } while (opcion != 0);
}

private static void nuevaVenta() {
    try {
        System.out.println("\n--- NUEVA VENTA ---");
        
        // Buscar cliente
        System.out.print("Ingrese el ID del cliente: ");
        int idCliente = leerEntero();
        
        ClienteDTO cliente = clienteService.obtenerClientePorId(idCliente);
        System.out.println("Cliente: " + cliente.getNombre());
        
        // Crear nueva venta
        VentaDTO ventaDTO = new VentaDTO(idCliente, LocalDate.now());
        VentaDTO ventaCreada = ventaService.crearVenta(ventaDTO);
        
        System.out.println("\n✓ Venta creada con ID: " + ventaCreada.getIdVentas());
        System.out.println("Estado: " + ventaCreada.getEstadoDescripcion());
        
        // Agregar productos
        agregarProductosAVenta(ventaCreada.getIdVentas());
        
    } catch (Exception e) {
        System.err.println("✗ Error al crear venta: " + e.getMessage());
    }
}

private static void agregarProductosAVenta(int idVenta) {
    String continuar="N";
    BigDecimal totalAcumulado = BigDecimal.ZERO;
    
    do {
        try {
            System.out.println("\n--- AGREGAR PRODUCTO A LA VENTA ---");
            
            // Mostrar inventario disponible
            System.out.println("\nProductos disponibles:");
            List<InventarioDTO> inventarios = inventarioService.listarInventarios();
            
            System.out.printf("%-5s %-25s %-20s %-10s %-10s%n", 
                "ID", "PRODUCTO", "ALMACÉN", "STOCK", "PRECIO");
            System.out.println(repetirCaracter("=", 75));
            
            for (InventarioDTO inv : inventarios) {
                if (inv.getCantidad() > 0) {
                    System.out.printf("%-5d %-25s %-20s %-10d S/ %-8.2f%n",
                        inv.getIdInventario(),
                        inv.getNombreProducto().length() > 24 ? 
                            inv.getNombreProducto().substring(0, 21) + "..." : 
                            inv.getNombreProducto(),
                        inv.getDescripcionAlmacen().length() > 19 ? 
                            inv.getDescripcionAlmacen().substring(0, 16) + "..." : 
                            inv.getDescripcionAlmacen(),
                        inv.getCantidad(),
                        inv.getPrecioProducto()
                    );
                }
            }
            
            System.out.print("\nIngrese el ID del inventario: ");
            int idInventario = leerEntero();
            
            InventarioDTO inventario = inventarioService.obtenerInventarioPorId(idInventario);
            System.out.println("Producto seleccionado: " + inventario.getNombreProducto());
            System.out.println("Stock disponible: " + inventario.getCantidad());
            System.out.println("Precio unitario: S/ " + inventario.getPrecioProducto());
            
            System.out.print("Cantidad a vender: ");
            int cantidad = leerEntero();
            
            if (cantidad > inventario.getCantidad()) {
                System.out.println("✗ No hay suficiente stock disponible");
                continue;
            }
            
            // Crear detalle de venta
            DetalleVentaDTO detalleDTO = new DetalleVentaDTO(
                idVenta, 
                idInventario, 
                cantidad, 
                inventario.getPrecioProducto()
            );
            
            DetalleVentaDTO detalleCreado = ventaService.agregarDetalleVenta(idVenta,detalleDTO);
            
            // Actualizar stock
            inventarioService.disminuirStock(idInventario, cantidad);
            
            totalAcumulado = totalAcumulado.add(detalleCreado.getPreciototal());
            
            System.out.println("\n✓ Producto agregado exitosamente");
            System.out.println("Subtotal del producto: S/ " + detalleCreado.getPreciototal());
            System.out.println("Total acumulado: S/ " + totalAcumulado);
            
            System.out.print("\n¿Agregar otro producto? (S/N): ");
            continuar = scanner.nextLine();
            
        } catch (Exception e) {
            System.err.println("✗ Error al agregar producto: " + e.getMessage());
            continuar = "N";
        }
    } while (continuar.equalsIgnoreCase("S") || continuar.equalsIgnoreCase("SI"));
    
    System.out.println("\n===========================================");
    System.out.println("TOTAL DE LA VENTA: S/ " + totalAcumulado);
    System.out.println("===========================================");
}

private static void buscarVentaPorId() {
    try {
        System.out.println("\n--- BUSCAR VENTA POR ID ---");
        System.out.print("Ingrese el ID de la venta: ");
        int id = leerEntero();
        
        VentaDTO venta = ventaService.obtenerVentaPorId(id);
        mostrarVenta(venta);
        
    } catch (Exception e) {
        System.err.println("✗ Error: " + e.getMessage());
    }
}

private static void listarVentasDelDia() {
    try {
        System.out.println("\n--- VENTAS DEL DÍA ---");
        LocalDate hoy = LocalDate.now();
        List<VentaDTO> ventas = ventaService.obtenerVentasHoy();
        
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas el día de hoy.");
            return;
        }
        
        mostrarTablaVentas(ventas);
        mostrarResumenVentas(ventas);
        
    } catch (Exception e) {
        System.err.println("✗ Error al listar ventas: " + e.getMessage());
    }
}

private static void listarTodasLasVentas() {
    try {
        System.out.println("\n--- TODAS LAS VENTAS ---");
        List<VentaDTO> ventas = ventaService.listarVentas();
        
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }
        
        mostrarTablaVentas(ventas);
        mostrarResumenVentas(ventas);
        
    } catch (Exception e) {
        System.err.println("✗ Error al listar ventas: " + e.getMessage());
    }
}

private static void buscarVentasPorCliente() {
    try {
        System.out.println("\n--- BUSCAR VENTAS POR CLIENTE ---");
        System.out.print("Ingrese el ID del cliente: ");
        int idCliente = leerEntero();
        
        ClienteDTO cliente = clienteService.obtenerClientePorId(idCliente);
        System.out.println("\nCliente: " + cliente.getNombre());
        
        List<VentaDTO> ventas = ventaService.buscarVentasPorCliente(idCliente);
        
        if (ventas.isEmpty()) {
            System.out.println("Este cliente no tiene ventas registradas.");
            return;
        }
        
        mostrarTablaVentas(ventas);
        mostrarResumenVentas(ventas);
        
    } catch (Exception e) {
        System.err.println("✗ Error: " + e.getMessage());
    }
}

private static void completarVenta() {
    try {
        System.out.println("\n--- COMPLETAR VENTA ---");
        System.out.print("Ingrese el ID de la venta: ");
        int id = leerEntero();
        
        VentaDTO venta = ventaService.obtenerVentaPorId(id);
        
        if (!venta.isPendiente()) {
            System.out.println("✗ Esta venta no puede ser completada. Estado actual: " + venta.getEstadoDescripcion());
            return;
        }
        
        mostrarVenta(venta);
        
        System.out.print("\n¿Confirmar que la venta ha sido completada? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S") || confirmacion.equalsIgnoreCase("SI")) {
            ventaService.completarVenta(id);
            System.out.println("✓ Venta completada exitosamente");
        } else {
            System.out.println("Operación cancelada");
        }
        
    } catch (Exception e) {
        System.err.println("✗ Error al completar venta: " + e.getMessage());
    }
}

private static void cancelarVenta() {
    try {
        System.out.println("\n--- CANCELAR VENTA ---");
        System.out.print("Ingrese el ID de la venta: ");
        int id = leerEntero();
        
        VentaDTO venta = ventaService.obtenerVentaPorId(id);
        
        if (!venta.isPendiente()) {
            System.out.println("✗ Esta venta no puede ser cancelada. Estado actual: " + venta.getEstadoDescripcion());
            return;
        }
        
        mostrarVenta(venta);
        
        System.out.print("\n¿Está seguro de cancelar esta venta? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S") || confirmacion.equalsIgnoreCase("SI")) {
            // Restaurar stock
            List<DetalleVentaDTO> detalles = venta.getDetalles();
            if (detalles != null) {
                for (DetalleVentaDTO detalle : detalles) {
                    inventarioService.aumentarStock(detalle.getIdInventario(), detalle.getCantidad());
                }
            }
            
            ventaService.cancelarVenta(id);
            System.out.println("✓ Venta cancelada exitosamente");
            System.out.println("✓ Stock restaurado");
        } else {
            System.out.println("Operación cancelada");
        }
        
    } catch (Exception e) {
        System.err.println("✗ Error al cancelar venta: " + e.getMessage());
    }
}

private static void verDetalleVenta() {
    try {
        System.out.println("\n--- VER DETALLE DE VENTA ---");
        System.out.print("Ingrese el ID de la venta: ");
        int id = leerEntero();
        
        VentaDTO venta = ventaService.obtenerVentaPorId(id);
        mostrarVentaCompleta(venta);
        
    } catch (Exception e) {
        System.err.println("✗ Error: " + e.getMessage());
    }
}

// Métodos auxiliares para mostrar información de ventas

private static void mostrarVenta(VentaDTO venta) {
    System.out.println("\n--- INFORMACIÓN DE LA VENTA ---");
    System.out.println("ID Venta: " + venta.getIdVentas());
    System.out.println("Fecha: " + venta.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    System.out.println("Cliente: " + venta.getNombreCliente());
    System.out.println("Documento: " + venta.getDocumentoCliente());
    System.out.println("Total: " + venta.getTotalFormateado());
    System.out.println("Estado: " + venta.getEstadoDescripcion());
    if (venta.getTotalItems() != null) {
        System.out.println("Total Items: " + venta.getTotalItems());
        System.out.println("Total Cantidad: " + venta.getTotalCantidad());
    }
}

private static void mostrarVentaCompleta(VentaDTO venta) {
    mostrarVenta(venta);
    
    if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
        System.out.println("\n--- DETALLE DE PRODUCTOS ---");
        System.out.printf("%-5s %-25s %-15s %-10s %-12s %-12s%n", 
            "ITEM", "PRODUCTO", "ALMACÉN", "CANTIDAD", "P.UNIT", "SUBTOTAL");
        System.out.println(repetirCaracter("=", 85));
        
        int item = 1;
        for (DetalleVentaDTO detalle : venta.getDetalles()) {
            System.out.printf("%-5d %-25s %-15s %-10d %-12s %-12s%n",
                item++,
                detalle.getNombreProducto() != null ?
                    (detalle.getNombreProducto().length() > 24 ? 
                        detalle.getNombreProducto().substring(0, 21) + "..." : 
                        detalle.getNombreProducto()) : "N/A",
                detalle.getDescripcionAlmacen() != null ?
                    (detalle.getDescripcionAlmacen().length() > 14 ? 
                        detalle.getDescripcionAlmacen().substring(0, 11) + "..." : 
                        detalle.getDescripcionAlmacen()) : "N/A",
                detalle.getCantidad(),
                detalle.getPrecioUnitarioFormateado(),
                detalle.getPreciototalFormateado()
            );
        }
        
        System.out.println(repetirCaracter("-", 85));
        System.out.printf("%64s %-12s%n", "TOTAL:", venta.getTotalFormateado());
    }
}

private static void mostrarTablaVentas(List<VentaDTO> ventas) {
    System.out.printf("%-8s %-12s %-25s %-15s %-12s %-12s%n", 
        "ID", "FECHA", "CLIENTE", "DOCUMENTO", "TOTAL", "ESTADO");
    System.out.println(repetirCaracter("=", 90));
    
    for (VentaDTO venta : ventas) {
        System.out.printf("%-8d %-12s %-25s %-15s %-12s %-12s%n",
            venta.getIdVentas(),
            venta.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            venta.getNombreCliente() != null ?
                (venta.getNombreCliente().length() > 24 ? 
                    venta.getNombreCliente().substring(0, 21) + "..." : 
                    venta.getNombreCliente()) : "N/A",
            venta.getDocumentoCliente() != null ? venta.getDocumentoCliente() : "N/A",
            venta.getTotalFormateado(),
            venta.getEstadoDescripcion()
        );
    }
}

private static void mostrarResumenVentas(List<VentaDTO> ventas) {
    // Calcular resumen
    long ventasPendientes = 0;
    long ventasCompletadas = 0;
    long ventasCanceladas = 0;
    BigDecimal totalCompletadas = BigDecimal.ZERO;
    
    for (VentaDTO venta : ventas) {
        if (venta.isPendiente()) ventasPendientes++;
        else if (venta.isCompletada()) {
            ventasCompletadas++;
            totalCompletadas = totalCompletadas.add(venta.getTotal());
        }
        else if (venta.isCancelada()) ventasCanceladas++;
    }
    
    System.out.println("\n--- RESUMEN ---");
    System.out.println("Total de ventas: " + ventas.size());
    System.out.println("• Pendientes: " + ventasPendientes);
    System.out.println("• Completadas: " + ventasCompletadas);
    System.out.println("• Canceladas: " + ventasCanceladas);
    System.out.println("Total en ventas completadas: S/ " + totalCompletadas);
}
    
    private static void menuReportes() {
        System.out.println("\n===========================================");
        System.out.println("                REPORTES");
        System.out.println("===========================================");
        System.out.println("⚠️  Módulo en desarrollo...");
        System.out.println("Próximamente: Reportes de ventas, inventario y clientes");
    }
    
    // ============================================
    // MÉTODOS AUXILIARES PARA LA INTERFAZ
    // ============================================
    
    private static void mostrarCliente(ClienteDTO cliente) {
        System.out.println("\n--- INFORMACIÓN DEL CLIENTE ---");
        System.out.println("ID: " + cliente.getIdCliente());
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("Documento: " + cliente.getNroDocumento());
        System.out.println("Correo: " + (cliente.getCorreo() != null ? cliente.getCorreo() : "N/A"));
        System.out.println("Fecha Registro: " + (cliente.getFechaRegistro() != null ? 
            cliente.getFechaRegistro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"));
        System.out.println("Estado: " + (cliente.getActivo() ? "Activo" : "Inactivo"));
    }
    
    private static void mostrarTablaInventario(List<InventarioDTO> inventarios) {
        System.out.printf("%-5s %-25s %-20s %-10s %-8s %-8s %-12s%n", 
            "ID", "PRODUCTO", "ALMACÉN", "CANTIDAD", "MIN", "MAX", "ESTADO");
        System.out.println(repetirCaracter("=", 95));
        
        for (InventarioDTO inv : inventarios) {
            String estado = "NORMAL";
            if (inv.isStockBajo()) estado = "⚠️ BAJO";
            else if (inv.isStockAlto()) estado = "⬆️ ALTO";
            
            System.out.printf("%-5d %-25s %-20s %-10d %-8d %-8d %-12s%n",
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
            );
        }
        
        System.out.println("\nTotal de registros: " + inventarios.size());
        
        // Mostrar resumen de estados (compatible con Java 8)
        long stockBajo = 0;
        long stockAlto = 0;
        for (InventarioDTO inv : inventarios) {
            if (inv.isStockBajo()) stockBajo++;
            if (inv.isStockAlto()) stockAlto++;
        }
        long stockNormal = inventarios.size() - stockBajo - stockAlto;
        
        System.out.println("\nResumen:");
        System.out.println("• Stock Normal: " + stockNormal);
        System.out.println("• Stock Bajo: " + stockBajo + (stockBajo > 0 ? " ⚠️" : ""));
        System.out.println("• Stock Alto: " + stockAlto);
    }
    
    private static void mostrarDetalleInventario(InventarioDTO inventario) {
        System.out.println("\n--- DETALLE DE INVENTARIO ---");
        System.out.println("ID Inventario: " + inventario.getIdInventario());
        System.out.println("Producto: " + inventario.getNombreProducto());
        System.out.println("Precio Unitario: S/ " + inventario.getPrecioProducto());
        System.out.println("Almacén: " + inventario.getDescripcionAlmacen());
        System.out.println("Cantidad Actual: " + inventario.getCantidad());
        System.out.println("Stock Mínimo: " + inventario.getStockMinimo());
        System.out.println("Stock Máximo: " + inventario.getStockMaximo());
        
        // Calcular valor total del inventario
        if (inventario.getPrecioProducto() != null && inventario.getCantidad() != null) {
            BigDecimal valorTotal = inventario.getPrecioProducto().multiply(BigDecimal.valueOf(inventario.getCantidad()));
            System.out.println("Valor Total: S/ " + valorTotal);
        }
        
        // Estado del stock
        String estado = "NORMAL";
        if (inventario.isStockBajo()) {
            estado = "⚠️ BAJO - Requiere reposición";
        } else if (inventario.isStockAlto()) {
            estado = "⬆️ ALTO - Considerar reducir pedidos";
        }
        System.out.println("Estado: " + estado);
        
        if (inventario.getFechaActualizacion() != null) {
            System.out.println("Última Actualización: " + 
                inventario.getFechaActualizacion().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
    }
    
    private static int leerEntero() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Por favor, ingrese un número válido: ");
            }
        }
    }
    
    /**
     * Método auxiliar para repetir un carácter (reemplazo de String.repeat de Java 11)
     * @param character Carácter a repetir
     * @param times Número de veces a repetir
     * @return String con el carácter repetido
     */
    private static String repetirCaracter(String character, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(character);
        }
        return sb.toString();
    }
    
    private static void cerrarRecursos() {
        try {
            ConnectionUtil.getInstance().closeAllConnections();
            scanner.close();
            System.out.println("✓ Recursos liberados correctamente");
        } catch (Exception e) {
            System.err.println("⚠️ Error al cerrar recursos: " + e.getMessage());
        }
    }
}
 