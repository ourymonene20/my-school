import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITuteurs } from '../tuteurs.model';

@Component({
  selector: 'jhi-tuteurs-detail',
  templateUrl: './tuteurs-detail.component.html',
})
export class TuteursDetailComponent implements OnInit {
  tuteurs: ITuteurs | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tuteurs }) => {
      this.tuteurs = tuteurs;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
