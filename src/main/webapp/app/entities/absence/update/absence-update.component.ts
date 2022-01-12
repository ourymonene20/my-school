import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IAbsence, Absence } from '../absence.model';
import { AbsenceService } from '../service/absence.service';
import { IEleve } from 'app/entities/eleve/eleve.model';
import { EleveService } from 'app/entities/eleve/service/eleve.service';

@Component({
  selector: 'jhi-absence-update',
  templateUrl: './absence-update.component.html',
})
export class AbsenceUpdateComponent implements OnInit {
  isSaving = false;

  elevesSharedCollection: IEleve[] = [];

  editForm = this.fb.group({
    id: [],
    dateAbsence: [],
    eleve: [],
  });

  constructor(
    protected absenceService: AbsenceService,
    protected eleveService: EleveService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ absence }) => {
      if (absence.id === undefined) {
        const today = dayjs().startOf('day');
        absence.dateAbsence = today;
      }

      this.updateForm(absence);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const absence = this.createFromForm();
    if (absence.id !== undefined) {
      this.subscribeToSaveResponse(this.absenceService.update(absence));
    } else {
      this.subscribeToSaveResponse(this.absenceService.create(absence));
    }
  }

  trackEleveById(index: number, item: IEleve): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAbsence>>): void {
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

  protected updateForm(absence: IAbsence): void {
    this.editForm.patchValue({
      id: absence.id,
      dateAbsence: absence.dateAbsence ? absence.dateAbsence.format(DATE_TIME_FORMAT) : null,
      eleve: absence.eleve,
    });

    this.elevesSharedCollection = this.eleveService.addEleveToCollectionIfMissing(this.elevesSharedCollection, absence.eleve);
  }

  protected loadRelationshipsOptions(): void {
    this.eleveService
      .query()
      .pipe(map((res: HttpResponse<IEleve[]>) => res.body ?? []))
      .pipe(map((eleves: IEleve[]) => this.eleveService.addEleveToCollectionIfMissing(eleves, this.editForm.get('eleve')!.value)))
      .subscribe((eleves: IEleve[]) => (this.elevesSharedCollection = eleves));
  }

  protected createFromForm(): IAbsence {
    return {
      ...new Absence(),
      id: this.editForm.get(['id'])!.value,
      dateAbsence: this.editForm.get(['dateAbsence'])!.value
        ? dayjs(this.editForm.get(['dateAbsence'])!.value, DATE_TIME_FORMAT)
        : undefined,
      eleve: this.editForm.get(['eleve'])!.value,
    };
  }
}
