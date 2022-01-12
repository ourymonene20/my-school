import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TuteursComponent } from './list/tuteurs.component';
import { TuteursDetailComponent } from './detail/tuteurs-detail.component';
import { TuteursUpdateComponent } from './update/tuteurs-update.component';
import { TuteursDeleteDialogComponent } from './delete/tuteurs-delete-dialog.component';
import { TuteursRoutingModule } from './route/tuteurs-routing.module';

@NgModule({
  imports: [SharedModule, TuteursRoutingModule],
  declarations: [TuteursComponent, TuteursDetailComponent, TuteursUpdateComponent, TuteursDeleteDialogComponent],
  entryComponents: [TuteursDeleteDialogComponent],
})
export class TuteursModule {}
