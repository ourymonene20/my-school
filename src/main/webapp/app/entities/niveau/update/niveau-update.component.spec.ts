import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NiveauService } from '../service/niveau.service';
import { INiveau, Niveau } from '../niveau.model';
import { ISalle } from 'app/entities/salle/salle.model';
import { SalleService } from 'app/entities/salle/service/salle.service';

import { NiveauUpdateComponent } from './niveau-update.component';

describe('Niveau Management Update Component', () => {
  let comp: NiveauUpdateComponent;
  let fixture: ComponentFixture<NiveauUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let niveauService: NiveauService;
  let salleService: SalleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NiveauUpdateComponent],
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
      .overrideTemplate(NiveauUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NiveauUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    niveauService = TestBed.inject(NiveauService);
    salleService = TestBed.inject(SalleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Salle query and add missing value', () => {
      const niveau: INiveau = { id: 456 };
      const salle: ISalle = { id: 4171 };
      niveau.salle = salle;

      const salleCollection: ISalle[] = [{ id: 20403 }];
      jest.spyOn(salleService, 'query').mockReturnValue(of(new HttpResponse({ body: salleCollection })));
      const additionalSalles = [salle];
      const expectedCollection: ISalle[] = [...additionalSalles, ...salleCollection];
      jest.spyOn(salleService, 'addSalleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ niveau });
      comp.ngOnInit();

      expect(salleService.query).toHaveBeenCalled();
      expect(salleService.addSalleToCollectionIfMissing).toHaveBeenCalledWith(salleCollection, ...additionalSalles);
      expect(comp.sallesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const niveau: INiveau = { id: 456 };
      const salle: ISalle = { id: 37720 };
      niveau.salle = salle;

      activatedRoute.data = of({ niveau });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(niveau));
      expect(comp.sallesSharedCollection).toContain(salle);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Niveau>>();
      const niveau = { id: 123 };
      jest.spyOn(niveauService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ niveau });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: niveau }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(niveauService.update).toHaveBeenCalledWith(niveau);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Niveau>>();
      const niveau = new Niveau();
      jest.spyOn(niveauService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ niveau });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: niveau }));
      saveSubject.complete();

      // THEN
      expect(niveauService.create).toHaveBeenCalledWith(niveau);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Niveau>>();
      const niveau = { id: 123 };
      jest.spyOn(niveauService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ niveau });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(niveauService.update).toHaveBeenCalledWith(niveau);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackSalleById', () => {
      it('Should return tracked Salle primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackSalleById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
