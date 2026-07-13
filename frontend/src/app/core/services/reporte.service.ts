import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { VentaResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  constructor(private http: HttpClient) {}

  resumen() {
    return this.http.get<{
      totalVentas: number;
      totalGastos: number;
      totalTransacciones: number;
      totalOrdenes: number;
    }>(`${environment.apiUrl}/reportes/resumen`);
  }

  ventasSemana() {
    return this.http.get<{ ventas: VentaResponse[]; total: number; transacciones: number }>(
      `${environment.apiUrl}/reportes/ventas-semana`
    );
  }

  gastosSemana() {
    return this.http.get<{ ordenes: unknown[]; total: number; totalOrdenes: number }>(
      `${environment.apiUrl}/reportes/gastos-semana`
    );
  }

  proveedoresMes() {
    return this.http.get<{ proveedor: string; gastoMensual: number; totalOrdenes: number; categoria: string }[]>(
      `${environment.apiUrl}/reportes/proveedores-mes`
    );
  }
}
