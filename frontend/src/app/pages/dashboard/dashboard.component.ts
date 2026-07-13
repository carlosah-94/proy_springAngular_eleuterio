import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { VentaService } from '../../core/services/venta.service';
import { ProductoService } from '../../core/services/producto.service';
import { InventarioStats } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  ventasHoy = 0;
  transaccionesHoy = 0;
  stats: InventarioStats | null = null;
  showModalProducto = false;

  constructor(private ventaService: VentaService, private productoService: ProductoService) {}

  ngOnInit(): void {
    this.refresh();
    setInterval(() => this.refresh(), 60000);
  }

  refresh(): void {
    this.ventaService.ventasHoy().subscribe(r => {
      this.ventasHoy = Number(r.total);
      this.transaccionesHoy = r.transacciones;
    });
    this.productoService.stats().subscribe(s => this.stats = s);
  }
}
