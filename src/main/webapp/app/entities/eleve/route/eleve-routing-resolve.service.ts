import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEleve, Eleve } from '../eleve.model';
import { EleveService } from '../service/eleve.service';

@Injectable({ providedIn: 'root' })
export class EleveRoutingResolveService implements Resolve<IEleve> {
  constructor(protected service: EleveService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEleve> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((eleve: HttpResponse<Eleve>) => {
          if (eleve.body) {
            return of(eleve.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Eleve());
  }
}
