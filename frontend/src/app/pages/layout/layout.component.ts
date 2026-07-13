import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ProductoService } from '../../core/services/producto.service';
import { Alerta } from '../../core/models';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  alertas: Alerta[] = [];
  showNotificaciones = false;

  constructor(public auth: AuthService, private productoService: ProductoService) {}

  ngOnInit(): void {
    this.cargarAlertas();
    setInterval(() => this.cargarAlertas(), 30000);
  }

  cargarAlertas(): void {
    this.productoService.alertas().subscribe(a => this.alertas = a);
  }

  logout(): void {
    this.auth.logout();
  }
}
