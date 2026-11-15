import { Component, input } from '@angular/core';

/** Encabezado de página; las acciones se proyectan con ng-content. */
@Component({
  selector: 'app-page-header',
  template: `
    <div>
      <h1>{{ titulo() }}</h1>
      @if (subtitulo()) {
        <p class="texto-suave">{{ subtitulo() }}</p>
      }
    </div>
    <div class="acciones"><ng-content /></div>
  `,
  styles: `
    @use 'variables' as *;

    :host {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: $espacio-m;
      margin-bottom: $espacio-l;
    }

    h1 {
      font-size: $texto-xl;
    }

    p {
      margin: $espacio-xs 0 0;
    }
  `,
})
export class PageHeader {
  readonly titulo = input.required<string>();
  readonly subtitulo = input('');
}
