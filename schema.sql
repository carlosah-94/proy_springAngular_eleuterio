-- ============================================================
-- ABARROTES ELEUTERIO — Base de Datos PostgreSQL (Supabase)
-- Sprint 4 — BD LIMPIA SIN DATOS DE PRUEBA
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS categoria (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    nombre_norm VARCHAR(100) NOT NULL UNIQUE,
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS producto (
    id              SERIAL PRIMARY KEY,
    categoria_id    INTEGER NOT NULL REFERENCES categoria(id) ON DELETE RESTRICT,
    nombre          VARCHAR(200) NOT NULL,
    presentacion    VARCHAR(100),
    tipo            VARCHAR(100),
    precio          NUMERIC(10,2) NOT NULL CHECK (precio > 0),
    stock_actual    INTEGER NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    stock_minimo    INTEGER NOT NULL DEFAULT 10,
    imagen_url      TEXT,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    contador_ventas INTEGER NOT NULL DEFAULT 0,
    creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_producto_nombre ON producto USING GIN (to_tsvector('spanish', nombre));
CREATE INDEX IF NOT EXISTS idx_producto_categoria ON producto(categoria_id);
CREATE INDEX IF NOT EXISTS idx_producto_activo ON producto(activo);

CREATE TABLE IF NOT EXISTS lote_producto (
    id              SERIAL PRIMARY KEY,
    producto_id     INTEGER NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    cantidad        INTEGER NOT NULL DEFAULT 0 CHECK (cantidad >= 0),
    fecha_venc      DATE,
    costo_unitario  NUMERIC(10,2),
    creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_lote_producto ON lote_producto(producto_id);
CREATE INDEX IF NOT EXISTS idx_lote_fecha_venc ON lote_producto(fecha_venc);

CREATE TABLE IF NOT EXISTS usuario (
    id              SERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   TEXT NOT NULL,
    nombre          VARCHAR(100) NOT NULL DEFAULT 'Don Eleuterio',
    rol             VARCHAR(50) NOT NULL DEFAULT 'ROLE_ADMIN',
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ultimo_acceso   TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS venta (
    id              SERIAL PRIMARY KEY,
    usuario_id      INTEGER NOT NULL REFERENCES usuario(id),
    total           NUMERIC(10,2) NOT NULL CHECK (total >= 0),
    base_imponible  NUMERIC(10,2) NOT NULL,
    igv             NUMERIC(10,2) NOT NULL,
    numero_boleta   VARCHAR(20) NOT NULL UNIQUE,
    fecha           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    impreso         BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha);
CREATE INDEX IF NOT EXISTS idx_venta_usuario ON venta(usuario_id);

CREATE TABLE IF NOT EXISTS venta_item (
    id              SERIAL PRIMARY KEY,
    venta_id        INTEGER NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
    producto_id     INTEGER NOT NULL REFERENCES producto(id),
    cantidad        INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL,
    subtotal        NUMERIC(10,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_venta_item_venta ON venta_item(venta_id);
CREATE INDEX IF NOT EXISTS idx_venta_item_producto ON venta_item(producto_id);

CREATE TABLE IF NOT EXISTS proveedor (
    id              SERIAL PRIMARY KEY,
    nombre_norm     VARCHAR(200) NOT NULL UNIQUE,
    nombre_display  VARCHAR(200) NOT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS orden_proveedor (
    id              SERIAL PRIMARY KEY,
    proveedor_id    INTEGER NOT NULL REFERENCES proveedor(id),
    usuario_id      INTEGER NOT NULL REFERENCES usuario(id),
    fecha_recepcion DATE NOT NULL DEFAULT CURRENT_DATE,
    costo_total     NUMERIC(10,2) NOT NULL CHECK (costo_total >= 0),
    archivado       BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_orden_proveedor_fecha ON orden_proveedor(fecha_recepcion);
CREATE INDEX IF NOT EXISTS idx_orden_proveedor_proveedor ON orden_proveedor(proveedor_id);

CREATE TABLE IF NOT EXISTS item_orden_proveedor (
    id                      SERIAL PRIMARY KEY,
    orden_id                INTEGER NOT NULL REFERENCES orden_proveedor(id) ON DELETE CASCADE,
    producto_id             INTEGER NOT NULL REFERENCES producto(id),
    cantidad_recibida       INTEGER NOT NULL CHECK (cantidad_recibida > 0),
    fecha_vencimiento_lote  DATE,
    costo_total             NUMERIC(10,2) NOT NULL CHECK (costo_total > 0)
);

CREATE INDEX IF NOT EXISTS idx_item_orden ON item_orden_proveedor(orden_id);

CREATE OR REPLACE FUNCTION normalizar_texto(texto TEXT)
RETURNS TEXT AS $$
BEGIN
    RETURN lower(
        translate(texto,
            'áéíóúÁÉÍÓÚàèìòùÀÈÌÒÙäëïöüÄËÏÖÜâêîôûÂÊÎÔÛñÑ',
            'aeiouAEIOUaeiouAEIOUaeiouAEIOUaeiouAEIOUnN'
        )
    );
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.actualizado_en = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_timestamp_producto ON producto;
CREATE TRIGGER set_timestamp_producto
BEFORE UPDATE ON producto
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

CREATE OR REPLACE FUNCTION descontar_stock_fefo(
    p_producto_id   INTEGER,
    p_cantidad      INTEGER
)
RETURNS BOOLEAN AS $$
DECLARE
    v_lote          RECORD;
    v_restante      INTEGER := p_cantidad;
BEGIN
    IF (SELECT stock_actual FROM producto WHERE id = p_producto_id) < p_cantidad THEN
        RETURN FALSE;
    END IF;

    FOR v_lote IN
        SELECT id, cantidad
        FROM lote_producto
        WHERE producto_id = p_producto_id AND cantidad > 0
        ORDER BY
            CASE WHEN fecha_venc IS NULL THEN 1 ELSE 0 END,
            fecha_venc ASC
    LOOP
        EXIT WHEN v_restante <= 0;

        IF v_lote.cantidad >= v_restante THEN
            UPDATE lote_producto SET cantidad = cantidad - v_restante WHERE id = v_lote.id;
            v_restante := 0;
        ELSE
            v_restante := v_restante - v_lote.cantidad;
            UPDATE lote_producto SET cantidad = 0 WHERE id = v_lote.id;
        END IF;
    END LOOP;

    DELETE FROM lote_producto WHERE producto_id = p_producto_id AND cantidad = 0;

    UPDATE producto
    SET stock_actual = (
        SELECT COALESCE(SUM(cantidad), 0)
        FROM lote_producto
        WHERE producto_id = p_producto_id
    )
    WHERE id = p_producto_id;

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generar_numero_boleta()
RETURNS VARCHAR AS $$
DECLARE
    v_ultimo    INTEGER;
    v_nuevo     VARCHAR(20);
BEGIN
    SELECT COALESCE(MAX(
        CAST(SUBSTRING(numero_boleta FROM 6) AS INTEGER)
    ), 524000)
    INTO v_ultimo
    FROM venta
    WHERE numero_boleta LIKE 'B002-%';

    v_nuevo := 'B002-' || LPAD((v_ultimo + 1)::TEXT, 6, '0');
    RETURN v_nuevo;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vista_productos_inventario AS
SELECT
    p.id,
    p.nombre,
    p.presentacion,
    p.tipo,
    c.nombre AS categoria,
    p.stock_actual,
    p.stock_minimo,
    p.precio,
    p.imagen_url,
    p.contador_ventas,
    p.activo,
    (SELECT MIN(l.fecha_venc)
     FROM lote_producto l
     WHERE l.producto_id = p.id AND l.cantidad > 0 AND l.fecha_venc IS NOT NULL
    ) AS fecha_vencimiento,
    CASE
        WHEN p.stock_actual = 0 THEN 'AGOTADO'
        WHEN p.stock_actual <= p.stock_minimo THEN 'CRITICO'
        ELSE 'OK'
    END AS estado_stock
FROM producto p
JOIN categoria c ON p.categoria_id = c.id
WHERE p.activo = TRUE
ORDER BY p.nombre;

CREATE OR REPLACE VIEW vista_gastos_proveedor_mes AS
SELECT
    pr.nombre_display AS proveedor,
    COUNT(op.id) AS total_ordenes,
    COALESCE(SUM(op.costo_total), 0) AS gasto_mensual
FROM proveedor pr
LEFT JOIN orden_proveedor op ON pr.id = op.proveedor_id
    AND DATE_TRUNC('month', op.fecha_recepcion) = DATE_TRUNC('month', CURRENT_DATE)
WHERE pr.activo = TRUE
GROUP BY pr.id, pr.nombre_display
ORDER BY gasto_mensual DESC;

ALTER TABLE usuario          ENABLE ROW LEVEL SECURITY;
ALTER TABLE producto         ENABLE ROW LEVEL SECURITY;
ALTER TABLE categoria        ENABLE ROW LEVEL SECURITY;
ALTER TABLE lote_producto    ENABLE ROW LEVEL SECURITY;
ALTER TABLE venta            ENABLE ROW LEVEL SECURITY;
ALTER TABLE venta_item       ENABLE ROW LEVEL SECURITY;
ALTER TABLE proveedor        ENABLE ROW LEVEL SECURITY;
ALTER TABLE orden_proveedor  ENABLE ROW LEVEL SECURITY;
ALTER TABLE item_orden_proveedor ENABLE ROW LEVEL SECURITY;

DO $$ BEGIN
    CREATE POLICY "service_role_all" ON producto FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON categoria FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON lote_producto FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON venta FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON venta_item FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON proveedor FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON orden_proveedor FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON item_orden_proveedor FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN
    CREATE POLICY "service_role_all" ON usuario FOR ALL USING (true) WITH CHECK (true);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
