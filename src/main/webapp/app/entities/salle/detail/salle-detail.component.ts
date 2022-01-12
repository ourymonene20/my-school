import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISalle } from '../salle.model';

@Component({
  selector: 'jhi-salle-detail',
  templateUrl: './salle-detail.component.html',
})
export class SalleDetailComponent implements OnInit {
  salle: ISalle | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ salle }) => {
      this.salle = salle;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
