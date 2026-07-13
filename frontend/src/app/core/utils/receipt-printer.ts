import { jsPDF } from 'jspdf';
import { numberToLetters } from './number-to-letters';
import { VentaResponse } from '../models';

export function imprimirBoleta(venta: VentaResponse): void {
  const doc = new jsPDF({ unit: 'mm', format: [80, 200] });
  const fecha = new Date(venta.fecha);
  const fechaStr = fecha.toLocaleDateString('es-PE');
  const horaStr = fecha.toLocaleString('es-PE');

  let y = 8;
  const line = (text: string, size = 8, bold = false) => {
    doc.setFont('courier', bold ? 'bold' : 'normal');
    doc.setFontSize(size);
    doc.text(text, 40, y, { align: 'center' });
    y += size === 7 ? 3.5 : 4;
  };

  line('ABARROTES ELEUTERIO', 9, true);
  line('Sector 6, Grupo 5-A, Mz. k, lote 24', 7);
  line('LIMA - LIMA - VILLA EL SALVADOR', 7);
  line('RUC: 10089803361', 7);
  y += 2;
  line('BOLETA DE VENTA ELECTRONICA', 8, true);
  line(venta.numeroBoleta, 8, true);
  line('FECHA EMISION: ' + fechaStr, 7);
  line('=====================================', 7);
  line('UDS DESCRIPCION P.U. IMPORTE', 7, true);
  line('=====================================', 7);

  venta.items.forEach(item => {
    const desc = item.productoNombre.substring(0, 18);
    const row = `${item.cantidad} ${desc} ${item.precioUnitario.toFixed(2)} ${item.subtotal.toFixed(2)}`;
    doc.setFont('courier', 'normal');
    doc.setFontSize(7);
    doc.text(row, 4, y);
    y += 3.5;
  });

  line('=====================================', 7);
  line('BASE IMPONIBLE : S/. ' + venta.baseImponible.toFixed(2), 7);
  line('IGV (18%) : S/. ' + venta.igv.toFixed(2), 7);
  line('=====================================', 7);
  line('TOTAL S/ : S/. ' + venta.total.toFixed(2), 8, true);
  line(numberToLetters(venta.total), 6);
  line('Condición: Contado', 7);
  line('Impresión: ' + horaStr, 7);
  line('Representación del Comprobante Electrónico, ingrese a:', 6);
  line('www.abaroteseleuterio.com/cpe/comprobante', 6);

  doc.autoPrint();
  doc.save('boleta-' + venta.numeroBoleta + '.pdf');
  window.open(doc.output('bloburl'), '_blank');
}
