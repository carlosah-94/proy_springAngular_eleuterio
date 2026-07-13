import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Alerta, Categoria, InventarioStats, Producto } from '../models';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  constructor(private http: HttpClient) {}

  listar(busqueda = '', pagina = 1, limite = 5, categoria = '') {
    let params = new HttpParams()
      .set('pagina', pagina)
      .set('limite', limite);
    if (busqueda) params = params.set('busqueda', busqueda);
    if (categoria) params = params.set('categoria', categoria);
    return this.http.get<{ data: Producto[]; total: number }>(`${environment.apiUrl}/productos`, { params });
  }

  stats() {
    return this.http.get<InventarioStats>(`${environment.apiUrl}/productos/stats`);
  }

  frecuentes() {
    return this.http.get<Producto[]>(`${environment.apiUrl}/productos/frecuentes`);
  }

  alertas() {
    return this.http.get<Alerta[]>(`${environment.apiUrl}/productos/alertas`);
  }

  crear(body: Record<string, unknown>) {
    return this.http.post<Producto>(`${environment.apiUrl}/productos`, body);
  }

  actualizar(id: number, body: Record<string, unknown>) {
    return this.http.put<Producto>(`${environment.apiUrl}/productos/${id}`, body);
  }

  eliminar(id: number) {
    return this.http.delete(`${environment.apiUrl}/productos/${id}`);
  }

  categorias() {
    return this.http.get<Categoria[]>(`${environment.apiUrl}/categorias`);
  }
}
