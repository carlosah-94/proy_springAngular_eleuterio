import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProveedorService } from '../../core/services/proveedor.service';
import { ProductoService } from '../../core/services/producto.service';
import { OrdenItemForm, Producto } from '../../core/models';

@Component({
  selector: 'app-proveedores',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './proveedores.component.html',
  styleUrl: './proveedores.component.scss'
})
export class ProveedoresComponent implements OnInit {
  nombreProveedor = '';
  fechaRecepcion = new Date().toISOString().substring(0, 10);
  productos: Producto[] = [];
  items: OrdenItemForm[] = [];
  formItem = this.emptyItem();
  mensaje = '';
  error = '';

  constructor(private proveedorService: ProveedorService, private productoService: ProductoService) {}

  ngOnInit(): void {
    // Listar todos los productos disponibles en el inventario (límite alto para abarcar todos)
    this.productoService.listar('', 1, 1000).subscribe(r => this.productos = r.data);
  }

  emptyItem(): OrdenItemForm {
    return { nombreProducto: '', cantidadRecibida: 1, fechaVencimientoLote: '', costoTotal: null as any };
  }

  agregarItem(): void {
    this.error = '';
    this.mensaje = '';

    if (!this.formItem.nombreProducto) {
      this.error = 'Debe seleccionar un producto del inventario.';
      return;
    }

    // Verificar si el producto ingresado realmente existe en el inventario
    const existeEnInventario = this.productos.some(
      p => p.nombre.toLowerCase().trim() === this.formItem.nombreProducto.toLowerCase().trim()
    );

    if (!existeEnInventario) {
      this.error = `El producto "${this.formItem.nombreProducto}" no existe en el inventario. Debe crearlo primero en el módulo de Inventario.`;
      return;
    }

    if (!this.formItem.cantidadRecibida || this.formItem.cantidadRecibida <= 0) {
      this.error = 'La cantidad recibida debe ser mayor a 0.';
      return;
    }

    if (this.formItem.costoTotal === null || this.formItem.costoTotal === undefined || this.formItem.costoTotal <= 0) {
      this.error = 'El costo total debe ser un valor mayor a 0.';
      return;
    }

    this.items.push({
      nombreProducto: this.formItem.nombreProducto.trim(),
      cantidadRecibida: Number(this.formItem.cantidadRecibida),
      fechaVencimientoLote: this.formItem.fechaVencimientoLote || undefined,
      costoTotal: Number(this.formItem.costoTotal)
    });

    this.formItem = this.emptyItem();
  }

  quitarItem(i: number): void {
    this.items.splice(i, 1);
  }

  get costoTotalOrden(): number {
    return this.items.reduce((s, i) => s + Number(i.costoTotal), 0);
  }

  registrar(): void {
    this.error = '';
    this.mensaje = '';

    if (!this.nombreProveedor.trim()) {
      this.error = 'Debe ingresar el nombre del proveedor.';
      return;
    }

    if (!this.items.length) {
      this.error = 'Debe agregar al menos un producto a la lista antes de registrar.';
      return;
    }

    this.proveedorService.registrarOrden(this.nombreProveedor.trim(), this.fechaRecepcion, this.items).subscribe({
      next: () => {
        this.mensaje = 'Orden registrada exitosamente y stock actualizado.';
        this.items = [];
        this.nombreProveedor = '';
        this.error = '';
        // Actualizar la lista local de productos para reflejar el nuevo stock
        this.productoService.listar('', 1, 1000).subscribe(r => this.productos = r.data);
      },
      error: err => {
        this.error = err.error?.message || 'Error al registrar orden. Verifique los campos.';
      }
    });
  }
}
