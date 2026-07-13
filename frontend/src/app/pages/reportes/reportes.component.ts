import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReporteService } from '../../core/services/reporte.service';
import { jsPDF } from 'jspdf';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.scss'
})
export class ReportesComponent implements OnInit, OnDestroy {
  totalVentas = 0;
  totalGastos = 0;
  transacciones = 0;
  ordenes = 0;
  proveedores: { proveedor: string; gastoMensual: number; totalOrdenes: number; categoria: string }[] = [];
  pagina = 1;
  limite = 5;
  cronTimer: any;

  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    this.cargar();
    this.iniciarChequeoDominical();
  }

  ngOnDestroy(): void {
    if (this.cronTimer) clearInterval(this.cronTimer);
  }

  cargar(): void {
    this.reporteService.resumen().subscribe(r => {
      this.totalVentas = Number(r.totalVentas);
      this.totalGastos = Number(r.totalGastos);
      this.transacciones = Number(r.totalTransacciones);
      this.ordenes = Number(r.totalOrdenes);
    });
    this.reporteService.proveedoresMes().subscribe(p => this.proveedores = p);
  }

  /**
   * Genera el PDF con la estructura exacta de Reporte Semanal de Ventas.
   */
  descargarVentasPdf(modo = 'Descarga Manual'): void {
    this.reporteService.ventasSemana().subscribe({
      next: data => {
        const doc = new jsPDF();
        const ahora = new Date().toLocaleString('es-PE');

        // Encabezado
        doc.setFont('Helvetica', 'bold');
        doc.setFontSize(22);
        doc.setTextColor(0, 83, 91); // Teal
        doc.text('Reporte Semanal de Ventas', 14, 25);

        doc.setFont('Helvetica', 'normal');
        doc.setFontSize(10);
        doc.setTextColor(100, 100, 100);
        doc.text(`Generado el: ${ahora}`, 14, 33);
        doc.text(`Modo: ${modo}`, 14, 39);

        // Línea divisoria
        doc.setDrawColor(200, 200, 200);
        doc.line(14, 43, 196, 43);

        // Tarjetas
        // TOTAL VENDIDO
        doc.setFillColor(240, 248, 250);
        doc.rect(14, 48, 85, 30, 'F');
        doc.setFont('Helvetica', 'bold');
        doc.setFontSize(10);
        doc.setTextColor(0, 83, 91);
        doc.text('TOTAL VENDIDO', 20, 56);
        doc.setFontSize(18);
        doc.text(`S/. ${Number(data.total).toFixed(2)}`, 20, 68);

        // TRANSACCIONES
        doc.setFillColor(240, 248, 250);
        doc.rect(111, 48, 85, 30, 'F');
        doc.setFont('Helvetica', 'bold');
        doc.setFontSize(10);
        doc.text('TRANSACCIONES', 117, 56);
        doc.setFontSize(18);
        doc.text(`${data.transacciones} ventas`, 117, 68);

        // Título de la Tabla
        doc.setFontSize(12);
        doc.setTextColor(50, 50, 50);
        doc.text('Detalle de Ventas del Periodo', 14, 94);

        // Encabezado de la tabla
        let y = 104;
        doc.setFontSize(9);
        doc.setFont('Helvetica', 'bold');
        doc.text('Fecha y Hora', 14, y);
        doc.text('Código Boleta', 60, y);
        doc.text('Productos Vendidos', 110, y);
        doc.text('Total', 175, y);
        doc.line(14, y + 2, 196, y + 2);
        y += 8;

        // Filas de la tabla
        doc.setFont('Helvetica', 'normal');
        data.ventas.forEach(v => {
          const fechaStr = new Date(v.fecha).toLocaleString('es-PE');
          
          // Mapear los items comprados
          const itemsStr = v.items.map(it => `${it.cantidad}x ${it.productoNombre}`).join(', ');
          const itemsTruncado = itemsStr.length > 32 ? itemsStr.substring(0, 29) + '...' : itemsStr;

          doc.text(fechaStr, 14, y);
          doc.text(v.numeroBoleta, 60, y);
          doc.text(itemsTruncado, 110, y);
          doc.text(`S/. ${Number(v.total).toFixed(2)}`, 175, y);
          y += 8;
        });

        doc.save(`reporte-ventas-semana-${new Date().toISOString().substring(0,10)}.pdf`);
        
        // Poner a 0 de forma local tras descargar
        this.totalVentas = 0;
        this.transacciones = 0;
      }
    });
  }

  /**
   * Genera el PDF con la estructura exacta de Reporte de Gastos con Proveedores.
   */
  descargarGastosPdf(modo = 'Descarga Manual'): void {
    this.reporteService.gastosSemana().subscribe({
      next: (data: any) => {
        const doc = new jsPDF();
        const ahora = new Date().toLocaleString('es-PE');

        // Encabezado
        doc.setFont('Helvetica', 'bold');
        doc.setFontSize(22);
        doc.setTextColor(0, 83, 91);
        doc.text('Reporte de Gastos con Proveedores', 14, 25);

        doc.setFont('Helvetica', 'normal');
        doc.setFontSize(10);
        doc.setTextColor(100, 100, 100);
        doc.text(`Generado el: ${ahora}`, 14, 33);
        doc.text(`Modo: ${modo}`, 14, 39);

        // Línea divisoria
        doc.setDrawColor(200, 200, 200);
        doc.line(14, 43, 196, 43);

        // Tarjetas
        // TOTAL INVERTIDO
        doc.setFillColor(240, 248, 250);
        doc.rect(14, 48, 85, 30, 'F');
        doc.setFont('Helvetica', 'bold');
        doc.setFontSize(10);
        doc.setTextColor(0, 83, 91);
        doc.text('TOTAL INVERTIDO', 20, 56);
        doc.setFontSize(18);
        doc.text(`S/. ${Number(data.total).toFixed(2)}`, 20, 68);

        // REABASTECIMIENTOS
        doc.setFillColor(240, 248, 250);
        doc.rect(111, 48, 85, 30, 'F');
        doc.setFont('Helvetica', 'bold');
        doc.setFontSize(10);
        doc.text('REABASTECIMIENTOS', 117, 56);
        doc.setFontSize(18);
        doc.text(`${data.totalOrdenes} órdenes`, 117, 68);

        // Título de la Tabla
        doc.setFontSize(12);
        doc.setTextColor(50, 50, 50);
        doc.text('Detalle de Órdenes a Proveedores', 14, 94);

        // Encabezado de la tabla
        let y = 104;
        doc.setFontSize(9);
        doc.setFont('Helvetica', 'bold');
        doc.text('Fecha', 14, y);
        doc.text('Proveedor', 45, y);
        doc.text('Detalle de Lotes Recibidos (Vencimiento)', 80, y);
        doc.text('Costo Total', 170, y);
        doc.line(14, y + 2, 196, y + 2);
        y += 8;

        // Filas de la tabla
        doc.setFont('Helvetica', 'normal');
        data.ordenes.forEach((o: any) => {
          // Detallar ítems o poner descripción
          let loteDetalle = '';
          if (o.items && o.items.length) {
            loteDetalle = o.items.map((it: any) => {
              const venceStr = it.fechaVencimientoLote ? `Vence: ${it.fechaVencimientoLote}` : 'No vence';
              return `${it.cantidadRecibida}x ${it.producto?.nombre || 'Producto'} (${venceStr})`;
            }).join(', ');
          } else {
            loteDetalle = 'Ingreso de lote de mercadería';
          }

          if (loteDetalle.length > 50) {
            loteDetalle = loteDetalle.substring(0, 47) + '...';
          }

          doc.text(o.fechaRecepcion || '', 14, y);
          doc.text(o.proveedor?.nombreDisplay || 'Proveedor', 45, y);
          doc.text(loteDetalle, 80, y);
          doc.text(`S/. ${Number(o.costoTotal).toFixed(2)}`, 170, y);
          y += 8;
        });

        doc.save(`reporte-gastos-proveedores-${new Date().toISOString().substring(0,10)}.pdf`);

        // Poner a 0 de forma local tras descargar
        this.totalGastos = 0;
        this.ordenes = 0;
      }
    });
  }

  /**
   * Chequea periódicamente si es Domingo a la Medianoche (23:59:50 a 00:00:10)
   * para ejecutar la autodescarga automática.
   */
  iniciarChequeoDominical(): void {
    this.cronTimer = setInterval(() => {
      const hoy = new Date();
      // Domingo es día 0
      if (hoy.getDay() === 0 && hoy.getHours() === 23 && hoy.getMinutes() === 59 && hoy.getSeconds() >= 55) {
        this.descargarVentasPdf('Autodescarga Dominical');
        this.descargarGastosPdf('Autodescarga Dominical');
      }
    }, 10000); // Chequea cada 10 segundos
  }

  get proveedoresPagina() {
    const start = (this.pagina - 1) * this.limite;
    return this.proveedores.slice(start, start + this.limite);
  }

  paginas(): number[] {
    return Array.from({ length: Math.ceil(this.proveedores.length / this.limite) }, (_, i) => i + 1);
  }

  irPagina(p: number): void {
    this.pagina = p;
  }
}
