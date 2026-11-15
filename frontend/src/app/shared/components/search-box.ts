import { Component, input, output } from '@angular/core';

/** Campo de búsqueda que comunica cada cambio al componente padre. */
@Component({
  selector: 'app-search-box',
  template: `
    <label class="campo">
      <span>{{ etiqueta() }}</span>
      <input
        type="search"
        [placeholder]="placeholder()"
        (input)="buscar.emit($any($event.target).value)"
      />
    </label>
  `,
})
export class SearchBox {
  readonly etiqueta = input('Buscar');
  readonly placeholder = input('');
  readonly buscar = output<string>();
}
