import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TuteursComponent } from '../list/tuteurs.component';
import { TuteursDetailComponent } from '../detail/tuteurs-detail.component';
import { TuteursUpdateComponent } from '../update/tuteurs-update.component';
import { TuteursRoutingResolveService } from './tuteurs-routing-resolve.service';

const tuteursRoute: Routes = [
  {
    path: '',
    component: TuteursComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TuteursDetailComponent,
    resolve: {
      tuteurs: TuteursRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TuteursUpdateComponent,
    resolve: {
      tuteurs: TuteursRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TuteursUpdateComponent,
    resolve: {
      tuteurs: TuteursRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tuteursRoute)],
  exports: [RouterModule],
})
export class TuteursRoutingModule {}
