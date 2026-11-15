import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-no-encontrado',
  imports: [RouterLink],
  template: `
    <div class="mensaje-estado">
      <h1>Página no encontrada</h1>
      <p>La dirección que buscas no existe.</p>
      <a class="btn" routerLink="/libros">Volver al catálogo</a>
    </div>
  `,
})
export class NoEncontrado {}
