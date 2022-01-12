import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITuteurs } from '../tuteurs.model';
import { TuteursService } from '../service/tuteurs.service';

@Component({
  templateUrl: './tuteurs-delete-dialog.component.html',
})
export class TuteursDeleteDialogComponent {
  tuteurs?: ITuteurs;

  constructor(protected tuteursService: TuteursService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tuteursService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
