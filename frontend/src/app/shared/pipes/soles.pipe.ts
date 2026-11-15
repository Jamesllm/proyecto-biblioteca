import { Pipe, PipeTransform } from '@angular/core';

/** Formatea un importe en soles, por ejemplo: S/ 12.50 */
@Pipe({ name: 'soles' })
export class SolesPipe implements PipeTransform {
  transform(valor: number | null | undefined): string {
    return `S/ ${(valor ?? 0).toFixed(2)}`;
  }
}
