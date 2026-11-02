import { Pipe, PipeTransform } from '@angular/core';

import { formatearPesos } from '../utils/dinero';

/** Uso en plantillas: {{ multa.valor | pesos }} */
@Pipe({ name: 'pesos' })
export class PesosPipe implements PipeTransform {
  transform(valor: number): string {
    return formatearPesos(valor);
  }
}
