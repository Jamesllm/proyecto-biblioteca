import { Component, computed, input } from '@angular/core';

const EXITO = ['ACTIVO', 'DISPONIBLE', 'PAGADA', 'DEVUELTO', 'ATENDIDA'];
const PELIGRO = ['SANCIONADO', 'VENCIDO', 'DADO_DE_BAJA', 'INACTIVO'];
const AVISO = ['PENDIENTE', 'PRESTADO', 'EN_MANTENIMIENTO'];

/** Muestra un estado del sistema con el color que le corresponde. */
@Component({
  selector: 'app-estado-badge',
  template: `<span class="badge" [class]="clase()">{{ texto() }}</span>`,
})
export class EstadoBadge {
  readonly estado = input.required<string>();

  protected readonly texto = computed(() => this.estado().replaceAll('_', ' '));
  protected readonly clase = computed(() => {
    const estado = this.estado().toUpperCase();
    if (EXITO.includes(estado)) return 'badge--exito';
    if (PELIGRO.includes(estado)) return 'badge--peligro';
    if (AVISO.includes(estado)) return 'badge--aviso';
    return '';
  });
}
