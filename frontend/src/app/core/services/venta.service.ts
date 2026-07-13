import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { VentaResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class VentaService {
  constructor(private http: HttpClient) {}

  registrar(items: { productoId: number; cantidad: number; precioUnitario: number }[]) {
    return this.http.post<VentaResponse>(`${environment.apiUrl}/ventas`, { items });
  }

  ventasHoy() {
    return this.http.get<{ total: number; transacciones: number }>(`${environment.apiUrl}/ventas/hoy`);
  }

  ventasSemana() {
    return this.http.get<{ ventas: VentaResponse[]; total: number; transacciones: number }>(
      `${environment.apiUrl}/ventas/semana`
    );
  }
}
