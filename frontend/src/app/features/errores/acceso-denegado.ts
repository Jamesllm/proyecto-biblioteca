import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-acceso-denegado',
  imports: [RouterLink],
  template: `
    <div class="mensaje-estado">
      <h1>Acceso denegado</h1>
      <p>Tu rol no tiene permiso para ver esta página.</p>
      <a class="btn" routerLink="/libros">Volver al catálogo</a>
    </div>
  `,
})
export class AccesoDenegado {}
