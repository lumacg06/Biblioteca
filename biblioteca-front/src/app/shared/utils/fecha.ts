/** Fecha local de hoy en formato 'yyyy-MM-dd', el mismo que usa el backend (LocalDate). */
export function hoyIso(): string {
  const hoy = new Date();
  const mes = String(hoy.getMonth() + 1).padStart(2, '0');
  const dia = String(hoy.getDate()).padStart(2, '0');
  return `${hoy.getFullYear()}-${mes}-${dia}`;
}

/** Días desde `desde` hasta `hasta` (ambas 'yyyy-MM-dd'). */
export function diasEntre(desde: string, hasta: string): number {
  const MS_POR_DIA = 86_400_000;
  return Math.round((Date.parse(hasta) - Date.parse(desde)) / MS_POR_DIA);
}
