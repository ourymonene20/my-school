import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EleveDetailComponent } from './eleve-detail.component';

describe('Eleve Management Detail Component', () => {
  let comp: EleveDetailComponent;
  let fixture: ComponentFixture<EleveDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EleveDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ eleve: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EleveDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EleveDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load eleve on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.eleve).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
