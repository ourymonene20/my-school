import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SalleDetailComponent } from './salle-detail.component';

describe('Salle Management Detail Component', () => {
  let comp: SalleDetailComponent;
  let fixture: ComponentFixture<SalleDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SalleDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ salle: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SalleDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SalleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load salle on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.salle).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
