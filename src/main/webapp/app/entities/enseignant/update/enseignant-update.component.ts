import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IEnseignant, Enseignant } from '../enseignant.model';
import { EnseignantService } from '../service/enseignant.service';
import { INiveau } from 'app/entities/niveau/niveau.model';
import { NiveauService } from 'app/entities/niveau/service/niveau.service';

@Component({
  selector: 'jhi-enseignant-update',
  templateUrl: './enseignant-update.component.html',
})
export class EnseignantUpdateComponent implements OnInit {
  isSaving = false;

  niveausSharedCollection: INiveau[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    prenom: [],
    email: [],
    dateNaiss: [],
    niveaus: [],
  });

  constructor(
    protected enseignantService: EnseignantService,
    protected niveauService: NiveauService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enseignant }) => {
      if (enseignant.id === undefined) {
        const today = dayjs().startOf('day');
        enseignant.dateNaiss = today;
      }

      this.updateForm(enseignant);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const enseignant = this.createFromForm();
    if (enseignant.id !== undefined) {
      this.subscribeToSaveResponse(this.enseignantService.update(enseignant));
    } else {
      this.subscribeToSaveResponse(this.enseignantService.create(enseignant));
    }
  }

  trackNiveauById(index: number, item: INiveau): number {
    return item.id!;
  }

  getSelectedNiveau(option: INiveau, selectedVals?: INiveau[]): INiveau {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnseignant>>): void {
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

  protected updateForm(enseignant: IEnseignant): void {
    this.editForm.patchValue({
      id: enseignant.id,
      nom: enseignant.nom,
      prenom: enseignant.prenom,
      email: enseignant.email,
      dateNaiss: enseignant.dateNaiss ? enseignant.dateNaiss.format(DATE_TIME_FORMAT) : null,
      niveaus: enseignant.niveaus,
    });

    this.niveausSharedCollection = this.niveauService.addNiveauToCollectionIfMissing(
      this.niveausSharedCollection,
      ...(enseignant.niveaus ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.niveauService
      .query()
      .pipe(map((res: HttpResponse<INiveau[]>) => res.body ?? []))
      .pipe(
        map((niveaus: INiveau[]) =>
          this.niveauService.addNiveauToCollectionIfMissing(niveaus, ...(this.editForm.get('niveaus')!.value ?? []))
        )
      )
      .subscribe((niveaus: INiveau[]) => (this.niveausSharedCollection = niveaus));
  }

  protected createFromForm(): IEnseignant {
    return {
      ...new Enseignant(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      prenom: this.editForm.get(['prenom'])!.value,
      email: this.editForm.get(['email'])!.value,
      dateNaiss: this.editForm.get(['dateNaiss'])!.value ? dayjs(this.editForm.get(['dateNaiss'])!.value, DATE_TIME_FORMAT) : undefined,
      niveaus: this.editForm.get(['niveaus'])!.value,
    };
  }
}
