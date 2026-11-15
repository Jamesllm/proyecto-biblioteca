import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ETIQUETAS_ROL } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';

interface Enlace {
  ruta: string;
  texto: string;
}

interface GrupoMenu {
  titulo: string;
  enlaces: Enlace[];
}

/** Barra lateral con sesión iniciada (grupos según el rol); sin sesión, franja superior. */
@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly menuAbierto = signal(false);
  protected readonly etiquetaRol = computed(() => {
    const sesion = this.auth.sesion();
    return sesion ? ETIQUETAS_ROL[sesion.rol] : '';
  });

  protected readonly grupos = computed<GrupoMenu[]>(() => {
    const catalogo: Enlace[] = [{ ruta: '/libros', texto: 'Libros' }];
    const grupos: GrupoMenu[] = [{ titulo: 'Catálogo', enlaces: catalogo }];

    if (this.auth.esLector()) {
      grupos.push({
        titulo: 'Mi cuenta',
        enlaces: [
          { ruta: '/mis-prestamos', texto: 'Mis préstamos' },
          { ruta: '/mis-reservas', texto: 'Mis reservas' },
          { ruta: '/mis-multas', texto: 'Mis multas' },
        ],
      });
    }
    if (this.auth.esPersonal()) {
      catalogo.push(
        { ruta: '/autores', texto: 'Autores' },
        { ruta: '/categorias', texto: 'Categorías' },
        { ruta: '/editoriales', texto: 'Editoriales' },
      );
      grupos.push(
        {
          titulo: 'Circulación',
          enlaces: [
            { ruta: '/prestamos', texto: 'Préstamos' },
            { ruta: '/reservas', texto: 'Reservas' },
          ],
        },
        {
          titulo: 'Multas y pagos',
          enlaces: [
            { ruta: '/multas', texto: 'Multas' },
            { ruta: '/pagos', texto: 'Pagos' },
          ],
        },
      );
    }
    if (this.auth.esAdmin()) {
      grupos.push({
        titulo: 'Administración',
        enlaces: [
          { ruta: '/usuarios', texto: 'Usuarios' },
          { ruta: '/reportes', texto: 'Reportes' },
        ],
      });
    }
    return grupos;
  });

  protected alternarMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  protected cerrarMenu(): void {
    this.menuAbierto.set(false);
  }

  protected salir(): void {
    this.auth.cerrarSesion();
    this.cerrarMenu();
    void this.router.navigate(['/login']);
  }
}
