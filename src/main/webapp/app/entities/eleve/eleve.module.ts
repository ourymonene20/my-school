import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EleveComponent } from './list/eleve.component';
import { EleveDetailComponent } from './detail/eleve-detail.component';
import { EleveUpdateComponent } from './update/eleve-update.component';
import { EleveDeleteDialogComponent } from './delete/eleve-delete-dialog.component';
import { EleveRoutingModule } from './route/eleve-routing.module';

@NgModule({
  imports: [SharedModule, EleveRoutingModule],
  declarations: [EleveComponent, EleveDetailComponent, EleveUpdateComponent, EleveDeleteDialogComponent],
  entryComponents: [EleveDeleteDialogComponent],
})
export class EleveModule {}
