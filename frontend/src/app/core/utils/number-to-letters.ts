export function numberToLetters(num: number): string {
  const units = ['', 'UN', 'DOS', 'TRES', 'CUATRO', 'CINCO', 'SEIS', 'SIETE', 'OCHO', 'NUEVE'];
  const tens = ['', 'DIEZ', 'VEINTE', 'TREINTA', 'CUARENTA', 'CINCUENTA', 'SESENTA', 'SETENTA', 'OCHENTA', 'NOVENTA'];
  const teens = ['DIEZ', 'ONCE', 'DOCE', 'TRECE', 'CATORCE', 'QUINCE', 'DIECISEIS', 'DIECISIETE', 'DIECIOCHO', 'DIECINUEVE'];
  const twenties = ['VEINTE', 'VEINTIUNO', 'VEINTIDOS', 'VEINTITRES', 'VEINTICUATRO', 'VEINTICINCO', 'VEINTISEIS', 'VEINTISIETE', 'VEINTIOCHO', 'VEINTINUEVE'];
  const hundreds = ['', 'CIENTO', 'DOSCIENTOS', 'TRESCIENTOS', 'CUATROCIENTOS', 'QUINIENTOS', 'SEISCIENTOS', 'SIETECIENTOS', 'OCHOCIENTOS', 'NOVECIENTOS'];

  const convertGroup = (n: number): string => {
    if (n === 100) return 'CIEN';
    let output = '';
    const h = Math.floor(n / 100);
    const r = n % 100;
    if (h > 0) output += hundreds[h] + ' ';
    if (r > 0) {
      if (r < 10) output += units[r];
      else if (r < 20) output += teens[r - 10];
      else if (r < 30) output += twenties[r - 20];
      else {
        const t = Math.floor(r / 10);
        const u = r % 10;
        output += tens[t];
        if (u > 0) output += ' Y ' + units[u];
      }
    }
    return output.trim();
  };

  const integerPart = Math.floor(num);
  const decimalPart = Math.round((num - integerPart) * 100);
  const decimalStr = String(decimalPart).padStart(2, '0') + '/100 SOLES';
  if (integerPart === 0) return 'CERO CON ' + decimalStr;

  let result = '';
  const thousands = Math.floor(integerPart / 1000);
  const remainder = integerPart % 1000;
  if (thousands > 0) result += (thousands === 1 ? 'MIL ' : convertGroup(thousands) + ' MIL ');
  if (remainder > 0) result += convertGroup(remainder) + ' ';
  return (result.trim() + ' CON ' + decimalStr).toUpperCase();
}
