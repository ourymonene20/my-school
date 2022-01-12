import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AbsenceService } from '../service/absence.service';
import { IAbsence, Absence } from '../absence.model';
import { IEleve } from 'app/entities/eleve/eleve.model';
import { EleveService } from 'app/entities/eleve/service/eleve.service';

import { AbsenceUpdateComponent } from './absence-update.component';

describe('Absence Management Update Component', () => {
  let comp: AbsenceUpdateComponent;
  let fixture: ComponentFixture<AbsenceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let absenceService: AbsenceService;
  let eleveService: EleveService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AbsenceUpdateComponent],
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
      .overrideTemplate(AbsenceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AbsenceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    absenceService = TestBed.inject(AbsenceService);
    eleveService = TestBed.inject(EleveService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Eleve query and add missing value', () => {
      const absence: IAbsence = { id: 456 };
      const eleve: IEleve = { id: 19243 };
      absence.eleve = eleve;

      const eleveCollection: IEleve[] = [{ id: 86130 }];
      jest.spyOn(eleveService, 'query').mockReturnValue(of(new HttpResponse({ body: eleveCollection })));
      const additionalEleves = [eleve];
      const expectedCollection: IEleve[] = [...additionalEleves, ...eleveCollection];
      jest.spyOn(eleveService, 'addEleveToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      expect(eleveService.query).toHaveBeenCalled();
      expect(eleveService.addEleveToCollectionIfMissing).toHaveBeenCalledWith(eleveCollection, ...additionalEleves);
      expect(comp.elevesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const absence: IAbsence = { id: 456 };
      const eleve: IEleve = { id: 92249 };
      absence.eleve = eleve;

      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(absence));
      expect(comp.elevesSharedCollection).toContain(eleve);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Absence>>();
      const absence = { id: 123 };
      jest.spyOn(absenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: absence }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(absenceService.update).toHaveBeenCalledWith(absence);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Absence>>();
      const absence = new Absence();
      jest.spyOn(absenceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: absence }));
      saveSubject.complete();

      // THEN
      expect(absenceService.create).toHaveBeenCalledWith(absence);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Absence>>();
      const absence = { id: 123 };
      jest.spyOn(absenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(absenceService.update).toHaveBeenCalledWith(absence);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackEleveById', () => {
      it('Should return tracked Eleve primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEleveById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
