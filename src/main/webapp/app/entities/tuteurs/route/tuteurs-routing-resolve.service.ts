import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITuteurs, Tuteurs } from '../tuteurs.model';
import { TuteursService } from '../service/tuteurs.service';

@Injectable({ providedIn: 'root' })
export class TuteursRoutingResolveService implements Resolve<ITuteurs> {
  constructor(protected service: TuteursService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITuteurs> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tuteurs: HttpResponse<Tuteurs>) => {
          if (tuteurs.body) {
            return of(tuteurs.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Tuteurs());
  }
}
