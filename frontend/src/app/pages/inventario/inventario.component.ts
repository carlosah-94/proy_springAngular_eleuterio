import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../../core/services/producto.service';
import { Categoria, InventarioStats, Producto } from '../../core/models';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.component.html',
  styleUrl: './inventario.component.scss'
})
export class InventarioComponent implements OnInit {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  stats: InventarioStats | null = null;
  busqueda = '';
  pagina = 1;
  limite = 5;
  total = 0;

  showModal = false;
  editMode = false;
  selectedId: number | null = null;
  form = this.emptyForm();
  error = '';

  constructor(private productoService: ProductoService) {}

  ngOnInit(): void {
    this.cargar();
    this.productoService.categorias().subscribe(c => this.categorias = c);
  }

  emptyForm() {
    return {
      nombre: '',
      presentacion: '',
      tipo: '',
      precio: null as number | null,
      stockInicial: 0,
      categoria: '',
      fechaVencimiento: ''
    };
  }

  cargar(): void {
    this.productoService.listar(this.busqueda, this.pagina, this.limite).subscribe(r => {
      this.productos = r.data;
      this.total = r.total;
    });
    this.productoService.stats().subscribe(s => this.stats = s);
  }

  buscar(): void {
    this.pagina = 1;
    this.cargar();
  }

  abrirCrear(): void {
    this.editMode = false;
    this.selectedId = null;
    this.form = this.emptyForm();
    this.showModal = true;
    this.error = '';
  }

  abrirEditar(p: Producto): void {
    this.editMode = true;
    this.selectedId = p.id;
    this.form = {
      nombre: p.nombre,
      presentacion: p.presentacion || '',
      tipo: p.tipo || '',
      precio: p.precio,
      stockInicial: p.stockActual,
      categoria: p.categoriaNombre,
      fechaVencimiento: p.fechaVencimiento || ''
    };
    this.showModal = true;
    this.error = '';
  }

  guardar(): void {
    const body = {
      nombre: this.form.nombre,
      presentacion: this.form.presentacion || null,
      tipo: this.form.tipo || null,
      precio: this.form.precio,
      stock_inicial: this.form.stockInicial,
      categoria: this.form.categoria,
      fecha_vencimiento: this.form.fechaVencimiento || null
    };

    const req = this.editMode && this.selectedId
      ? this.productoService.actualizar(this.selectedId, body)
      : this.productoService.crear(body);

    req.subscribe({
      next: () => {
        this.showModal = false;
        this.cargar();
        this.productoService.categorias().subscribe(c => this.categorias = c);
      },
      error: err => this.error = err.error?.message || 'Error al guardar'
    });
  }

  eliminar(p: Producto): void {
    if (!confirm(`¿Eliminar "${p.nombre}"?`)) return;
    this.productoService.eliminar(p.id).subscribe(() => this.cargar());
  }

  paginas(): number[] {
    return Array.from({ length: Math.ceil(this.total / this.limite) }, (_, i) => i + 1);
  }

  irPagina(p: number): void {
    this.pagina = p;
    this.cargar();
  }
}
