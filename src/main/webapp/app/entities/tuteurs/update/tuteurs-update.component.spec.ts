import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TuteursService } from '../service/tuteurs.service';
import { ITuteurs, Tuteurs } from '../tuteurs.model';

import { TuteursUpdateComponent } from './tuteurs-update.component';

describe('Tuteurs Management Update Component', () => {
  let comp: TuteursUpdateComponent;
  let fixture: ComponentFixture<TuteursUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tuteursService: TuteursService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TuteursUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TuteursUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TuteursUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tuteursService = TestBed.inject(TuteursService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tuteurs: ITuteurs = { id: 456 };

      activatedRoute.data = of({ tuteurs });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tuteurs));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tuteurs>>();
      const tuteurs = { id: 123 };
      jest.spyOn(tuteursService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tuteurs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tuteurs }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tuteursService.update).toHaveBeenCalledWith(tuteurs);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tuteurs>>();
      const tuteurs = new Tuteurs();
      jest.spyOn(tuteursService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tuteurs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tuteurs }));
      saveSubject.complete();

      // THEN
      expect(tuteursService.create).toHaveBeenCalledWith(tuteurs);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tuteurs>>();
      const tuteurs = { id: 123 };
      jest.spyOn(tuteursService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tuteurs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tuteursService.update).toHaveBeenCalledWith(tuteurs);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
