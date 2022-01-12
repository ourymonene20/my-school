import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TuteursDetailComponent } from './tuteurs-detail.component';

describe('Tuteurs Management Detail Component', () => {
  let comp: TuteursDetailComponent;
  let fixture: ComponentFixture<TuteursDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TuteursDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tuteurs: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TuteursDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TuteursDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tuteurs on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tuteurs).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
