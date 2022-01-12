import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITuteurs, Tuteurs } from '../tuteurs.model';
import { TuteursService } from '../service/tuteurs.service';

@Component({
  selector: 'jhi-tuteurs-update',
  templateUrl: './tuteurs-update.component.html',
})
export class TuteursUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nom: [],
    prenom: [],
    telephone: [null, [Validators.required]],
    email: [],
  });

  constructor(protected tuteursService: TuteursService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tuteurs }) => {
      this.updateForm(tuteurs);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tuteurs = this.createFromForm();
    if (tuteurs.id !== undefined) {
      this.subscribeToSaveResponse(this.tuteursService.update(tuteurs));
    } else {
      this.subscribeToSaveResponse(this.tuteursService.create(tuteurs));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITuteurs>>): void {
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

  protected updateForm(tuteurs: ITuteurs): void {
    this.editForm.patchValue({
      id: tuteurs.id,
      nom: tuteurs.nom,
      prenom: tuteurs.prenom,
      telephone: tuteurs.telephone,
      email: tuteurs.email,
    });
  }

  protected createFromForm(): ITuteurs {
    return {
      ...new Tuteurs(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      prenom: this.editForm.get(['prenom'])!.value,
      telephone: this.editForm.get(['telephone'])!.value,
      email: this.editForm.get(['email'])!.value,
    };
  }
}
