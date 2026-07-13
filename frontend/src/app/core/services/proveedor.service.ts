import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { OrdenItemForm } from '../models';

@Injectable({ providedIn: 'root' })
export class ProveedorService {
  constructor(private http: HttpClient) {}

  listar() {
    return this.http.get<{ id: number; nombreDisplay: string }[]>(`${environment.apiUrl}/proveedores`);
  }

  registrarOrden(nombreProveedor: string, fechaRecepcion: string, items: OrdenItemForm[]) {
    return this.http.post(`${environment.apiUrl}/ordenes`, { nombreProveedor, fechaRecepcion, items });
  }
}
