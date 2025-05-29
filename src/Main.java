 
import sistemaventas.util.ConnectionUtil;
import sistemaventas.dto.ClienteDTO;
import sistemaventas.dto.InventarioDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import sistemaventas.service.interfaces.IClienteService;
import sistemaventas.service.interfaces.IInventarioService;
import sistemaventas.service.impl.ClienteServiceImpl;
import sistemaventas.service.impl.InventarioServiceImpl;

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
        System.out.println("\n===========================================");
        System.out.println("            GESTIÓN DE VENTAS");
        System.out.println("===========================================");
        System.out.println("⚠️  Módulo en desarrollo...");
        System.out.println("Próximamente: Registro y gestión de ventas");
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
 