import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EleveComponent } from '../list/eleve.component';
import { EleveDetailComponent } from '../detail/eleve-detail.component';
import { EleveUpdateComponent } from '../update/eleve-update.component';
import { EleveRoutingResolveService } from './eleve-routing-resolve.service';

const eleveRoute: Routes = [
  {
    path: '',
    component: EleveComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EleveDetailComponent,
    resolve: {
      eleve: EleveRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EleveUpdateComponent,
    resolve: {
      eleve: EleveRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EleveUpdateComponent,
    resolve: {
      eleve: EleveRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(eleveRoute)],
  exports: [RouterModule],
})
export class EleveRoutingModule {}
