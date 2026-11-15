import { Injectable } from '@angular/core';
import { Ejemplar } from '../models/models';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class EjemplarService extends CrudService<Ejemplar> {
  protected readonly recurso = 'ejemplares';
}
