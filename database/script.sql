-- ============================================
-- SCRIPTS DE CREACIÓN DE BASE DE DATOS MYSQL
-- ============================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS sistema_ventas;
USE sistema_ventas;

-- Tabla Clientes
CREATE TABLE Clientes (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(45) NOT NULL,
    NroDocumento VARCHAR(45) UNIQUE NOT NULL,
    correo VARCHAR(45),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla Productos
CREATE TABLE Productos (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombreproducto VARCHAR(45) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    descripcion TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla Almacen
CREATE TABLE Almacen (
    idAlmacen INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(45) NOT NULL,
    direccion VARCHAR(100),
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla Inventario (relación N:N entre Productos y Almacen)
CREATE TABLE Inventario (
    idInventario INT AUTO_INCREMENT PRIMARY KEY,
    idProducto INT NOT NULL,
    idAlmacen INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    stock_minimo INT DEFAULT 10,
    stock_maximo INT DEFAULT 1000,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (idProducto) REFERENCES Productos(idProducto) ON DELETE CASCADE,
    FOREIGN KEY (idAlmacen) REFERENCES Almacen(idAlmacen) ON DELETE CASCADE,
    UNIQUE KEY unique_producto_almacen (idProducto, idAlmacen)
);

-- Tabla Ventas
CREATE TABLE Ventas (
    idVentas INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    idCliente INT NOT NULL,
    total DECIMAL(10,2) DEFAULT 0.00,
    estado ENUM('PENDIENTE', 'COMPLETADA', 'CANCELADA') DEFAULT 'PENDIENTE',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idCliente) REFERENCES Clientes(idCliente) ON DELETE RESTRICT
);

-- Tabla DetalleVenta
CREATE TABLE DetalleVenta (
    idDetalleVenta INT AUTO_INCREMENT PRIMARY KEY,
    idVentas INT NOT NULL,
    idInventario INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    preciototal DECIMAL(10,2) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idVentas) REFERENCES Ventas(idVentas) ON DELETE CASCADE,
    FOREIGN KEY (idInventario) REFERENCES Inventario(idInventario) ON DELETE RESTRICT
);

-- ============================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- ============================================

-- Índices en Clientes
CREATE INDEX idx_clientes_documento ON Clientes(NroDocumento);
CREATE INDEX idx_clientes_nombre ON Clientes(nombre);

-- Índices en Productos
CREATE INDEX idx_productos_nombre ON Productos(nombreproducto);

-- Índices en Inventario
CREATE INDEX idx_inventario_producto ON Inventario(idProducto);
CREATE INDEX idx_inventario_almacen ON Inventario(idAlmacen);
CREATE INDEX idx_inventario_cantidad ON Inventario(cantidad);

-- Índices en Ventas
CREATE INDEX idx_ventas_fecha ON Ventas(fecha);
CREATE INDEX idx_ventas_cliente ON Ventas(idCliente);
CREATE INDEX idx_ventas_estado ON Ventas(estado);

-- Índices en DetalleVenta
CREATE INDEX idx_detalle_venta ON DetalleVenta(idVentas);
CREATE INDEX idx_detalle_inventario ON DetalleVenta(idInventario);

-- ============================================
-- TRIGGERS PARA AUTOMATIZACIÓN
-- ============================================

-- Trigger para actualizar total de venta
DELIMITER //
CREATE TRIGGER tr_actualizar_total_venta
AFTER INSERT ON DetalleVenta
FOR EACH ROW
BEGIN
    UPDATE Ventas 
    SET total = (
        SELECT SUM(preciototal) 
        FROM DetalleVenta 
        WHERE idVentas = NEW.idVentas
    )
    WHERE idVentas = NEW.idVentas;
END//

-- Trigger para actualizar inventario después de venta
CREATE TRIGGER tr_actualizar_inventario_venta
AFTER INSERT ON DetalleVenta
FOR EACH ROW
BEGIN
    UPDATE Inventario 
    SET cantidad = cantidad - NEW.cantidad,
        fecha_actualizacion = CURRENT_TIMESTAMP 
    WHERE idInventario = NEW.idInventario;
END//

DELIMITER ;

-- ============================================
-- DATOS DE PRUEBA
-- ============================================

-- Insertar datos de prueba en Clientes
INSERT INTO Clientes (nombre, NroDocumento, correo) VALUES
('Juan Pérez', '12345678', 'juan.perez@email.com'),
('María García', '87654321', 'maria.garcia@email.com'),
('Carlos López', '11223344', 'carlos.lopez@email.com');

-- Insertar datos de prueba en Productos
INSERT INTO Productos (nombreproducto, precio, descripcion) VALUES
('Laptop HP', 2500.00, 'Laptop HP Core i5 8GB RAM'),
('Mouse Logitech', 45.00, 'Mouse óptico inalámbrico'),
('Teclado Mecánico', 120.00, 'Teclado mecánico retroiluminado');

-- Insertar datos de prueba en Almacen
INSERT INTO Almacen (descripcion, direccion, telefono) VALUES
('Almacén Central', 'Av. Principal 123', '01-234-5678'),
('Sucursal Norte', 'Jr. Los Olivos 456', '01-345-6789');

-- Insertar datos de prueba en Inventario
INSERT INTO Inventario (idProducto, idAlmacen, cantidad, stock_minimo, stock_maximo) VALUES
(1, 1, 50, 5, 100),
(1, 2, 30, 5, 50),
(2, 1, 200, 20, 500),
(2, 2, 150, 20, 300),
(3, 1, 80, 10, 200);

-- ============================================
-- VISTAS ÚTILES
-- ============================================

-- Vista de inventario con detalles
CREATE VIEW v_inventario_detallado AS
SELECT 
    i.idInventario,
    p.nombreproducto,
    a.descripcion AS almacen,
    i.cantidad,
    i.stock_minimo,
    i.stock_maximo,
    p.precio,
    (i.cantidad * p.precio) AS valor_inventario,
    CASE 
        WHEN i.cantidad <= i.stock_minimo THEN 'BAJO'
        WHEN i.cantidad >= i.stock_maximo THEN 'ALTO'
        ELSE 'NORMAL'
    END AS estado_stock
FROM Inventario i
JOIN Productos p ON i.idProducto = p.idProducto
JOIN Almacen a ON i.idAlmacen = a.idAlmacen
WHERE i.activo = TRUE;

-- Vista de ventas con detalles
CREATE VIEW v_ventas_detalladas AS
SELECT 
    v.idVentas,
    v.fecha,
    c.nombre AS cliente,
    c.NroDocumento,
    v.total,
    v.estado,
    COUNT(dv.idDetalleVenta) AS total_items
FROM Ventas v
JOIN Clientes c ON v.idCliente = c.idCliente
LEFT JOIN DetalleVenta dv ON v.idVentas = dv.idVentas
GROUP BY v.idVentas, v.fecha, c.nombre, c.NroDocumento, v.total, v.estado;