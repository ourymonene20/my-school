import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEleve } from '../eleve.model';

@Component({
  selector: 'jhi-eleve-detail',
  templateUrl: './eleve-detail.component.html',
})
export class EleveDetailComponent implements OnInit {
  eleve: IEleve | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eleve }) => {
      this.eleve = eleve;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
