import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../../core/services/producto.service';
import { VentaService } from '../../core/services/venta.service';
import { CartItem, Producto, VentaResponse } from '../../core/models';
import { imprimirBoleta } from '../../core/utils/receipt-printer';

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.component.html',
  styleUrl: './ventas.component.scss'
})
export class VentasComponent implements OnInit {
  frecuentes: Producto[] = [];
  busquedaResultados: Producto[] = [];
  busqueda = '';
  cart: CartItem[] = [];
  ultimaVenta: VentaResponse | null = null;
  mensaje = '';

  constructor(private productoService: ProductoService, private ventaService: VentaService) {}

  ngOnInit(): void {
    this.productoService.frecuentes().subscribe(p => this.frecuentes = p);
    const saved = localStorage.getItem('cart');
    if (saved) this.cart = JSON.parse(saved);
  }

  get productosVisibles(): Producto[] {
    if (this.busqueda.trim()) return this.busquedaResultados;
    return this.frecuentes;
  }

  buscar(): void {
    if (!this.busqueda.trim()) {
      this.busquedaResultados = [];
      return;
    }
    this.productoService.listar(this.busqueda, 1, 50).subscribe(r => this.busquedaResultados = r.data);
  }

  agregar(p: Producto): void {
    const existing = this.cart.find(c => c.productoId === p.id);
    if (existing) {
      this.cambiarCantidad(existing, existing.cantidad + 1);
      return;
    }
    this.cart.push({
      productoId: p.id,
      nombre: p.nombre,
      precio: p.precio,
      cantidad: 1,
      stockMax: p.stockActual,
      tipo: p.tipo
    });
    this.persistCart();
  }

  cambiarCantidad(item: CartItem, qty: number): void {
    if (qty <= 0) {
      this.cart = this.cart.filter(c => c !== item);
    } else if (qty <= item.stockMax) {
      item.cantidad = qty;
    }
    this.persistCart();
  }

  get total(): number {
    return this.cart.reduce((s, i) => s + i.precio * i.cantidad, 0);
  }

  persistCart(): void {
    localStorage.setItem('cart', JSON.stringify(this.cart));
  }

  finalizarVenta(): void {
    if (!this.cart.length) return;
    const items = this.cart.map(c => ({
      productoId: c.productoId,
      cantidad: c.cantidad,
      precioUnitario: c.precio
    }));
    this.ventaService.registrar(items).subscribe({
      next: venta => {
        this.ultimaVenta = venta;
        this.cart = [];
        this.persistCart();
        this.mensaje = 'Venta registrada correctamente';
        this.productoService.frecuentes().subscribe(p => this.frecuentes = p);
      },
      error: err => this.mensaje = err.error?.message || 'Error al registrar venta'
    });
  }

  descargarComprobante(): void {
    if (!this.ultimaVenta) {
      this.mensaje = 'Primero finalice una venta';
      return;
    }
    imprimirBoleta(this.ultimaVenta);
  }

  defaultImg = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'><rect width='100%' height='100%' fill='%23f1f5f9'/><text x='100' y='105' text-anchor='middle' fill='%2364748b'>Sin imagen</text></svg>";
}
