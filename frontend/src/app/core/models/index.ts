export interface LoginResponse {
  token: string;
  nombre: string;
  email: string;
  rol: string;
}

export interface Producto {
  id: number;
  nombre: string;
  presentacion?: string;
  tipo?: string;
  precio: number;
  stockActual: number;
  stockMinimo: number;
  imagenUrl?: string;
  contadorVentas: number;
  categoriaId: number;
  categoriaNombre: string;
  fechaVencimiento?: string;
  estadoStock: string;
}

export interface InventarioStats {
  totalProductos: number;
  valorInventario: number;
  stockCritico: number;
  totalCategorias: number;
}

export interface Alerta {
  tipo: 'error' | 'warning';
  titulo: string;
  mensaje: string;
  productoId: number;
}

export interface Categoria {
  id: number;
  nombre: string;
  nombreNorm: string;
}

export interface CartItem {
  productoId: number;
  nombre: string;
  precio: number;
  cantidad: number;
  stockMax: number;
  tipo?: string;
}

export interface VentaResponse {
  id: number;
  total: number;
  baseImponible: number;
  igv: number;
  numeroBoleta: string;
  fecha: string;
  items: {
    cantidad: number;
    precioUnitario: number;
    subtotal: number;
    productoNombre: string;
    productoPresentacion?: string;
  }[];
}

export interface OrdenItemForm {
  nombreProducto: string;
  cantidadRecibida: number;
  fechaVencimientoLote?: string;
  costoTotal: number;
}
