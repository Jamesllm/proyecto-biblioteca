import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { ConfirmDialog } from './shared/components/confirm-dialog';
import { Navbar } from './shared/components/navbar';
import { Toasts } from './shared/components/toasts';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Toasts, ConfirmDialog],
  host: { '[class.con-lateral]': 'auth.autenticado()' },
  template: `
    <app-navbar />
    <main class="contenedor">
      <router-outlet />
    </main>
    <app-toasts />
    <app-confirm-dialog />
  `,
  styles: `
    @use 'variables' as *;
    @use 'mixins' as *;

    :host {
      display: grid;
      grid-template-columns: minmax(0, 1fr);
      grid-template-rows: auto 1fr;
      min-height: 100vh;
    }

    // La columna lateral solo existe cuando hay una sesión iniciada.
    :host(.con-lateral) {
      grid-template-columns: $ancho-lateral minmax(0, 1fr);
      grid-template-rows: none;

      @include movil {
        grid-template-columns: minmax(0, 1fr);
        grid-template-rows: auto 1fr;
      }
    }
  `,
})
export class App {
  protected readonly auth = inject(AuthService);
}
