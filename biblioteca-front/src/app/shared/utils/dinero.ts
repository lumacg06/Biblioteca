const FORMATO_PESOS = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
});

/** 3000 → "$ 3.000" */
export function formatearPesos(valor: number): string {
  return FORMATO_PESOS.format(valor);
}
