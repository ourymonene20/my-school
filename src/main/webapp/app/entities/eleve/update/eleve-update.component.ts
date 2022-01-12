import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IEleve, Eleve } from '../eleve.model';
import { EleveService } from '../service/eleve.service';
import { ITuteurs } from 'app/entities/tuteurs/tuteurs.model';
import { TuteursService } from 'app/entities/tuteurs/service/tuteurs.service';

@Component({
  selector: 'jhi-eleve-update',
  templateUrl: './eleve-update.component.html',
})
export class EleveUpdateComponent implements OnInit {
  isSaving = false;

  tuteursSharedCollection: ITuteurs[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    prenom: [],
    email: [],
    dateNaiss: [],
    tuteur: [],
  });

  constructor(
    protected eleveService: EleveService,
    protected tuteursService: TuteursService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eleve }) => {
      if (eleve.id === undefined) {
        const today = dayjs().startOf('day');
        eleve.dateNaiss = today;
      }

      this.updateForm(eleve);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const eleve = this.createFromForm();
    if (eleve.id !== undefined) {
      this.subscribeToSaveResponse(this.eleveService.update(eleve));
    } else {
      this.subscribeToSaveResponse(this.eleveService.create(eleve));
    }
  }

  trackTuteursById(index: number, item: ITuteurs): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEleve>>): void {
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

  protected updateForm(eleve: IEleve): void {
    this.editForm.patchValue({
      id: eleve.id,
      nom: eleve.nom,
      prenom: eleve.prenom,
      email: eleve.email,
      dateNaiss: eleve.dateNaiss ? eleve.dateNaiss.format(DATE_TIME_FORMAT) : null,
      tuteur: eleve.tuteur,
    });

    this.tuteursSharedCollection = this.tuteursService.addTuteursToCollectionIfMissing(this.tuteursSharedCollection, eleve.tuteur);
  }

  protected loadRelationshipsOptions(): void {
    this.tuteursService
      .query()
      .pipe(map((res: HttpResponse<ITuteurs[]>) => res.body ?? []))
      .pipe(map((tuteurs: ITuteurs[]) => this.tuteursService.addTuteursToCollectionIfMissing(tuteurs, this.editForm.get('tuteur')!.value)))
      .subscribe((tuteurs: ITuteurs[]) => (this.tuteursSharedCollection = tuteurs));
  }

  protected createFromForm(): IEleve {
    return {
      ...new Eleve(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      prenom: this.editForm.get(['prenom'])!.value,
      email: this.editForm.get(['email'])!.value,
      dateNaiss: this.editForm.get(['dateNaiss'])!.value ? dayjs(this.editForm.get(['dateNaiss'])!.value, DATE_TIME_FORMAT) : undefined,
      tuteur: this.editForm.get(['tuteur'])!.value,
    };
  }
}
