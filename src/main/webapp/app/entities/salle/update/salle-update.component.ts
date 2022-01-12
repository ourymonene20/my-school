import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISalle, Salle } from '../salle.model';
import { SalleService } from '../service/salle.service';

@Component({
  selector: 'jhi-salle-update',
  templateUrl: './salle-update.component.html',
})
export class SalleUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [],
    capacite: [],
  });

  constructor(protected salleService: SalleService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ salle }) => {
      this.updateForm(salle);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const salle = this.createFromForm();
    if (salle.id !== undefined) {
      this.subscribeToSaveResponse(this.salleService.update(salle));
    } else {
      this.subscribeToSaveResponse(this.salleService.create(salle));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISalle>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(salle: ISalle): void {
    this.editForm.patchValue({
      id: salle.id,
      libelle: salle.libelle,
      capacite: salle.capacite,
    });
  }

  protected createFromForm(): ISalle {
    return {
      ...new Salle(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      capacite: this.editForm.get(['capacite'])!.value,
    };
  }
}
