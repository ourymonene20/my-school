import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EleveService } from '../service/eleve.service';
import { IEleve, Eleve } from '../eleve.model';
import { ITuteurs } from 'app/entities/tuteurs/tuteurs.model';
import { TuteursService } from 'app/entities/tuteurs/service/tuteurs.service';

import { EleveUpdateComponent } from './eleve-update.component';

describe('Eleve Management Update Component', () => {
  let comp: EleveUpdateComponent;
  let fixture: ComponentFixture<EleveUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eleveService: EleveService;
  let tuteursService: TuteursService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EleveUpdateComponent],
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
      .overrideTemplate(EleveUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EleveUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eleveService = TestBed.inject(EleveService);
    tuteursService = TestBed.inject(TuteursService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tuteurs query and add missing value', () => {
      const eleve: IEleve = { id: 456 };
      const tuteur: ITuteurs = { id: 54799 };
      eleve.tuteur = tuteur;

      const tuteursCollection: ITuteurs[] = [{ id: 39917 }];
      jest.spyOn(tuteursService, 'query').mockReturnValue(of(new HttpResponse({ body: tuteursCollection })));
      const additionalTuteurs = [tuteur];
      const expectedCollection: ITuteurs[] = [...additionalTuteurs, ...tuteursCollection];
      jest.spyOn(tuteursService, 'addTuteursToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      expect(tuteursService.query).toHaveBeenCalled();
      expect(tuteursService.addTuteursToCollectionIfMissing).toHaveBeenCalledWith(tuteursCollection, ...additionalTuteurs);
      expect(comp.tuteursSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const eleve: IEleve = { id: 456 };
      const tuteur: ITuteurs = { id: 26636 };
      eleve.tuteur = tuteur;

      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(eleve));
      expect(comp.tuteursSharedCollection).toContain(tuteur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Eleve>>();
      const eleve = { id: 123 };
      jest.spyOn(eleveService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eleve }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(eleveService.update).toHaveBeenCalledWith(eleve);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Eleve>>();
      const eleve = new Eleve();
      jest.spyOn(eleveService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eleve }));
      saveSubject.complete();

      // THEN
      expect(eleveService.create).toHaveBeenCalledWith(eleve);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Eleve>>();
      const eleve = { id: 123 };
      jest.spyOn(eleveService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eleveService.update).toHaveBeenCalledWith(eleve);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTuteursById', () => {
      it('Should return tracked Tuteurs primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTuteursById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
