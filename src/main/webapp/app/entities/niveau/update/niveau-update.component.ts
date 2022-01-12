import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { INiveau, Niveau } from '../niveau.model';
import { NiveauService } from '../service/niveau.service';
import { ISalle } from 'app/entities/salle/salle.model';
import { SalleService } from 'app/entities/salle/service/salle.service';

@Component({
  selector: 'jhi-niveau-update',
  templateUrl: './niveau-update.component.html',
})
export class NiveauUpdateComponent implements OnInit {
  isSaving = false;

  sallesSharedCollection: ISalle[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    libelle: [],
    salle: [],
  });

  constructor(
    protected niveauService: NiveauService,
    protected salleService: SalleService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ niveau }) => {
      this.updateForm(niveau);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const niveau = this.createFromForm();
    if (niveau.id !== undefined) {
      this.subscribeToSaveResponse(this.niveauService.update(niveau));
    } else {
      this.subscribeToSaveResponse(this.niveauService.create(niveau));
    }
  }

  trackSalleById(index: number, item: ISalle): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INiveau>>): void {
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

  protected updateForm(niveau: INiveau): void {
    this.editForm.patchValue({
      id: niveau.id,
      nom: niveau.nom,
      libelle: niveau.libelle,
      salle: niveau.salle,
    });

    this.sallesSharedCollection = this.salleService.addSalleToCollectionIfMissing(this.sallesSharedCollection, niveau.salle);
  }

  protected loadRelationshipsOptions(): void {
    this.salleService
      .query()
      .pipe(map((res: HttpResponse<ISalle[]>) => res.body ?? []))
      .pipe(map((salles: ISalle[]) => this.salleService.addSalleToCollectionIfMissing(salles, this.editForm.get('salle')!.value)))
      .subscribe((salles: ISalle[]) => (this.sallesSharedCollection = salles));
  }

  protected createFromForm(): INiveau {
    return {
      ...new Niveau(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      salle: this.editForm.get(['salle'])!.value,
    };
  }
}
