import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IInscription, Inscription } from '../inscription.model';
import { InscriptionService } from '../service/inscription.service';
import { IEleve } from 'app/entities/eleve/eleve.model';
import { EleveService } from 'app/entities/eleve/service/eleve.service';
import { INiveau } from 'app/entities/niveau/niveau.model';
import { NiveauService } from 'app/entities/niveau/service/niveau.service';

@Component({
  selector: 'jhi-inscription-update',
  templateUrl: './inscription-update.component.html',
})
export class InscriptionUpdateComponent implements OnInit {
  isSaving = false;

  elevesCollection: IEleve[] = [];
  niveausSharedCollection: INiveau[] = [];

  editForm = this.fb.group({
    id: [],
    dateInscprion: [],
    anneeScolaire: [],
    eleve: [],
    niveau: [],
  });

  constructor(
    protected inscriptionService: InscriptionService,
    protected eleveService: EleveService,
    protected niveauService: NiveauService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inscription }) => {
      if (inscription.id === undefined) {
        const today = dayjs().startOf('day');
        inscription.dateInscprion = today;
        inscription.anneeScolaire = today;
      }

      this.updateForm(inscription);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inscription = this.createFromForm();
    if (inscription.id !== undefined) {
      this.subscribeToSaveResponse(this.inscriptionService.update(inscription));
    } else {
      this.subscribeToSaveResponse(this.inscriptionService.create(inscription));
    }
  }

  trackEleveById(index: number, item: IEleve): number {
    return item.id!;
  }

  trackNiveauById(index: number, item: INiveau): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInscription>>): void {
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

  protected updateForm(inscription: IInscription): void {
    this.editForm.patchValue({
      id: inscription.id,
      dateInscprion: inscription.dateInscprion ? inscription.dateInscprion.format(DATE_TIME_FORMAT) : null,
      anneeScolaire: inscription.anneeScolaire ? inscription.anneeScolaire.format(DATE_TIME_FORMAT) : null,
      eleve: inscription.eleve,
      niveau: inscription.niveau,
    });

    this.elevesCollection = this.eleveService.addEleveToCollectionIfMissing(this.elevesCollection, inscription.eleve);
    this.niveausSharedCollection = this.niveauService.addNiveauToCollectionIfMissing(this.niveausSharedCollection, inscription.niveau);
  }

  protected loadRelationshipsOptions(): void {
    this.eleveService
      .query({ filter: 'inscription-is-null' })
      .pipe(map((res: HttpResponse<IEleve[]>) => res.body ?? []))
      .pipe(map((eleves: IEleve[]) => this.eleveService.addEleveToCollectionIfMissing(eleves, this.editForm.get('eleve')!.value)))
      .subscribe((eleves: IEleve[]) => (this.elevesCollection = eleves));

    this.niveauService
      .query()
      .pipe(map((res: HttpResponse<INiveau[]>) => res.body ?? []))
      .pipe(map((niveaus: INiveau[]) => this.niveauService.addNiveauToCollectionIfMissing(niveaus, this.editForm.get('niveau')!.value)))
      .subscribe((niveaus: INiveau[]) => (this.niveausSharedCollection = niveaus));
  }

  protected createFromForm(): IInscription {
    return {
      ...new Inscription(),
      id: this.editForm.get(['id'])!.value,
      dateInscprion: this.editForm.get(['dateInscprion'])!.value
        ? dayjs(this.editForm.get(['dateInscprion'])!.value, DATE_TIME_FORMAT)
        : undefined,
      anneeScolaire: this.editForm.get(['anneeScolaire'])!.value
        ? dayjs(this.editForm.get(['anneeScolaire'])!.value, DATE_TIME_FORMAT)
        : undefined,
      eleve: this.editForm.get(['eleve'])!.value,
      niveau: this.editForm.get(['niveau'])!.value,
    };
  }
}
