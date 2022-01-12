import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEleve } from '../eleve.model';
import { EleveService } from '../service/eleve.service';

@Component({
  templateUrl: './eleve-delete-dialog.component.html',
})
export class EleveDeleteDialogComponent {
  eleve?: IEleve;

  constructor(protected eleveService: EleveService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.eleveService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
