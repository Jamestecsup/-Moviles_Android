-- =============================================================================
--  CafeCampus - Esquema relacional y datos iniciales
--  Laboratorio MVVM + Room  |  TECSUP - Programacion en Moviles - Semana 08
--
--  Este archivo es la FUENTE DE VERDAD del esquema relacional. El codigo Room
--  (data/local/entity/*.kt) genera exactamente estas tablas en
--  cafecampus.db. Verifica el resultado con:
--     Android Studio > View > Tool Windows > App Inspection > Database Inspector
--
--  Todos los datos son FICTICIOS (Ley 29733 - Proteccion de Datos Personales).
--  Las imagenes de producto viven en app/src/main/assets/img/productos/
--  (ver ATRIBUCIONES.md). Este script NO guarda imagenes: solo el nombre de
--  la columna `icono` (cafe|plato|jugo|pan|botella|dulce).
-- =============================================================================


-- =============================================================================
--  1. RESTRICCIONES DE CLAVES
-- =============================================================================
--
--  CLAVES PRIMARIAS (PK) - identifican univocamente cada fila
--    usuarios.id              PK autoincremental
--    productos.id             PK autoincremental
--    pedidos.id               PK autoincremental
--    detalle_pedido.id        PK autoincremental
--
--  CLAVES FORANEAS (FK) - integridad referencial
--    pedidos.clienteId        -> usuarios.id
--                               N pedidos : 1 usuario
--    detalle_pedido.pedidoId  -> pedidos.id   ON DELETE CASCADE
--                               N lineas   : 1 pedido
--                               Si se borra el pedido, sus lineas se borran solas.
--
--  CLAVES CANDIDATAS (alternate keys) - columnas o grupos de columnas que
--  tambien podrian ser PK porque identifican una fila de forma unica:
--    usuarios.correo                        UNIQUE  -> login del usuario
--    productos.nombre                       UNIQUE  -> no hay dos productos iguales
--    detalle_pedido (pedidoId, productoId)   UNIQUE  <- CLAVE COMPUESTA
--                                               Un producto aparece como maximo
--                                               una vez por pedido. Por eso el
--                                               carrito SUMA cantidades en vez de
--                                               duplicar la linea.
--
--  detalle_pedido.productoId NO es clave foranea a proposito: es una foto del
--  momento del pedido. Si el administrador borra o renombra un producto, los
--  pedidos antiguos NO se rompen, por eso la linea copia tambien
--  nombreProducto y precioUnitario.
-- =============================================================================


-- =============================================================================
--  2. TABLAS
--  (nombres de columnas identicos a los @ColumnInfo de las entidades Room)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- usuarios : personas que usan la app (estudiante y administrador)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `usuarios` (
    `id`      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `nombre`  TEXT    NOT NULL,
    `correo`  TEXT    NOT NULL,
    `clave`   TEXT    NOT NULL,
    `rol`     TEXT    NOT NULL          -- Rol.name : CLIENTE | ADMIN
);

-- clave candidata: el correo identifica al usuario y es la clave de login
CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`);

-- -----------------------------------------------------------------------------
-- productos : carta de la cafeteria
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `productos` (
    `id`          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `nombre`      TEXT    NOT NULL,
    `descripcion` TEXT    NOT NULL,
    `categoria`   TEXT    NOT NULL,      -- Desayunos | Almuerzos | Bebidas | Postres
    `precio`      REAL    NOT NULL,      -- en soles (S/)
    `disponible`  INTEGER NOT NULL,      -- 1 = se puede pedir, 0 = agotado
    `icono`       TEXT    NOT NULL       -- cafe|plato|jugo|pan|botella|dulce
);

-- clave candidata
CREATE UNIQUE INDEX IF NOT EXISTS `index_productos_nombre` ON `productos` (`nombre`);

-- -----------------------------------------------------------------------------
-- pedidos : cabecera del pedido (1 pedido = N lineas)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pedidos` (
    `id`         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `clienteId`  INTEGER NOT NULL,
    `estado`     TEXT    NOT NULL,      -- EstadoPedido.name
    `horaRecojo` TEXT    NOT NULL,      -- "10:30"
    `pago`       TEXT    NOT NULL,      -- CAJA | YAPE
    `nota`       TEXT    NOT NULL,
    `total`      REAL    NOT NULL,
    `fecha`      INTEGER NOT NULL,      -- System.currentTimeMillis()
    FOREIGN KEY (`clienteId`) REFERENCES `usuarios` (`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- indice sobre la clave foranea (evita advertencias de Room y acelera la consulta)
CREATE INDEX IF NOT EXISTS `index_pedidos_clienteId` ON `pedidos` (`clienteId`);

-- -----------------------------------------------------------------------------
-- detalle_pedido : lineas del pedido (1 linea = 1 producto del pedido)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `detalle_pedido` (
    `id`              INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `pedidoId`        INTEGER NOT NULL,
    `productoId`      INTEGER NOT NULL,
    `nombreProducto`  TEXT    NOT NULL,  -- foto del nombre al momento de pedir
    `cantidad`        INTEGER NOT NULL,
    `precioUnitario`  REAL    NOT NULL,  -- foto del precio al momento de pedir
    FOREIGN KEY (`pedidoId`) REFERENCES `pedidos` (`id`)
        ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS `index_detalle_pedido_pedidoId` ON `detalle_pedido` (`pedidoId`);

-- CLAVE COMPUESTA / clave candidata: (pedidoId, productoId)
-- Un producto no puede repetirse como dos lineas del mismo pedido.
CREATE UNIQUE INDEX IF NOT EXISTS `index_detalle_pedido_pedidoId_productoId`
    ON `detalle_pedido` (`pedidoId`, `productoId`);


-- =============================================================================
--  3. DATOS INICIALES  (los mismos que siembra data/local/DatosIniciales.kt)
-- =============================================================================

-- 3.1 Productos de la carta
INSERT INTO `productos` (`id`, `nombre`, `descripcion`, `categoria`, `precio`, `disponible`, `icono`) VALUES
    (1, 'Cafe americano',    'Cafe de grano percolado, sin azucar.',        'Bebidas',  4.00, 1, 'cafe'),
    (2, 'Sandwich de pollo', 'Pan de molde, pollo a la plancha y lechuga.',  'Almuerzos', 9.00, 1, 'pan'),
    (3, 'Menu del dia',      'Entrada + segundo + refresco. Cambia cada dia segun el menu de cocina.', 'Almuerzos', 12.00, 1, 'plato'),
    (4, 'Jugo de naranja',   'Exprimido natural, 400 ml.',                  'Bebidas',  5.00, 1, 'jugo'),
    (5, 'Empanada',          'Masa horneada rellena de carne.',             'Desayunos', 4.50, 1, 'pan'),
    (6, 'Chicha morada',     'Maiz morado con canela y pina.',              'Bebidas',  3.50, 1, 'botella');

-- 3.2 Usuarios
--     clave = 1234 (estudiante)  |  clave = admin (administrador)
INSERT INTO `usuarios` (`id`, `nombre`, `correo`, `clave`, `rol`) VALUES
    (1, 'Juan Perez',  'juan@campus.test', '1234',  'CLIENTE'),
    (2, 'Ana Torres',  'admin@cafe.test',  'admin', 'ADMIN');


-- =============================================================================
--  4. DATOS DE DEMOSTRACION (OPCIONAL)
--  reproducing los pedidos que aparecen en los mockups.
--  NO se ejecuta al abrir la app: `sembrar()` solo inserta 3.1 y 3.2.
--  Copia este bloque en el Database Inspector para ver las listas con historial.
-- =============================================================================
--
-- INSERT INTO `pedidos` (`id`, `clienteId`, `estado`, `horaRecojo`, `pago`, `nota`, `total`, `fecha`) VALUES
--     (39, 1, 'CANCELADO',  '16:10', 'CAJA', '',                    3.00,  date('now','-3 days') * 1000),
--     (41, 1, 'ENTREGADO',  '08:40', 'CAJA', '',                    8.00,  date('now','-2 days') * 1000),
--     (45, 1, 'LISTO',      '10:15', 'YAPE', '',                    9.00,  date('now','-1 day')  * 1000),
--     (46, 1, 'PREPARANDO', '10:30', 'CAJA', 'Sin aji, por favor', 29.00,  date('now')           * 1000);
--
-- INSERT INTO `detalle_pedido` (`id`, `pedidoId`, `productoId`, `nombreProducto`, `cantidad`, `precioUnitario`) VALUES
--     (1, 39, 6, 'Chicha morada',    1,  3.00),
--     (2, 41, 1, 'Cafe americano',   2,  4.00),
--     (3, 45, 2, 'Sandwich de pollo',1,  9.00),
--     (4, 46, 3, 'Menu del dia',     2, 12.00),
--     (5, 46, 4, 'Jugo de naranja',  1,  5.00);


-- =============================================================================
--  5. CONSULTAS DE VERIFICACION
-- =============================================================================

-- 5.1 Claves foraneas de la base (PRAGMA)
-- PRAGMA foreign_key_list('pedidos');
-- PRAGMA foreign_key_list('detalle_pedido');

-- 5.2 Indices (PK, FK, clave compuesta)
-- PRAGMA index_list('detalle_pedido');

-- 5.3 Pedido completo con su cliente y sus lineas
-- SELECT p.id, u.nombre, p.estado, p.horaRecojo, p.total,
--        d.nombreProducto, d.cantidad, d.precioUnitario
-- FROM pedidos p
-- JOIN usuarios u        ON u.id = p.clienteId
-- JOIN detalle_pedido d  ON d.pedidoId = p.id
-- ORDER BY p.fecha DESC;

-- 5.4 Ventas del dia, sin contar cancelados (COALESCE evita nulos)
-- SELECT COALESCE(SUM(total), 0.0) AS ventas_hoy
-- FROM pedidos
-- WHERE fecha >= :inicioDelDia AND estado <> 'CANCELADO';

-- 5.5 Cantidad de pedidos por estado (para el panel de control)
-- SELECT estado, COUNT(*) FROM pedidos GROUP BY estado;