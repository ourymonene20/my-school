import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EnseignantService } from '../service/enseignant.service';
import { IEnseignant, Enseignant } from '../enseignant.model';
import { INiveau } from 'app/entities/niveau/niveau.model';
import { NiveauService } from 'app/entities/niveau/service/niveau.service';

import { EnseignantUpdateComponent } from './enseignant-update.component';

describe('Enseignant Management Update Component', () => {
  let comp: EnseignantUpdateComponent;
  let fixture: ComponentFixture<EnseignantUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let enseignantService: EnseignantService;
  let niveauService: NiveauService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EnseignantUpdateComponent],
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
      .overrideTemplate(EnseignantUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnseignantUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    enseignantService = TestBed.inject(EnseignantService);
    niveauService = TestBed.inject(NiveauService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Niveau query and add missing value', () => {
      const enseignant: IEnseignant = { id: 456 };
      const niveaus: INiveau[] = [{ id: 97485 }];
      enseignant.niveaus = niveaus;

      const niveauCollection: INiveau[] = [{ id: 49607 }];
      jest.spyOn(niveauService, 'query').mockReturnValue(of(new HttpResponse({ body: niveauCollection })));
      const additionalNiveaus = [...niveaus];
      const expectedCollection: INiveau[] = [...additionalNiveaus, ...niveauCollection];
      jest.spyOn(niveauService, 'addNiveauToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ enseignant });
      comp.ngOnInit();

      expect(niveauService.query).toHaveBeenCalled();
      expect(niveauService.addNiveauToCollectionIfMissing).toHaveBeenCalledWith(niveauCollection, ...additionalNiveaus);
      expect(comp.niveausSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const enseignant: IEnseignant = { id: 456 };
      const niveaus: INiveau = { id: 82559 };
      enseignant.niveaus = [niveaus];

      activatedRoute.data = of({ enseignant });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(enseignant));
      expect(comp.niveausSharedCollection).toContain(niveaus);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Enseignant>>();
      const enseignant = { id: 123 };
      jest.spyOn(enseignantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enseignant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: enseignant }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(enseignantService.update).toHaveBeenCalledWith(enseignant);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Enseignant>>();
      const enseignant = new Enseignant();
      jest.spyOn(enseignantService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enseignant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: enseignant }));
      saveSubject.complete();

      // THEN
      expect(enseignantService.create).toHaveBeenCalledWith(enseignant);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Enseignant>>();
      const enseignant = { id: 123 };
      jest.spyOn(enseignantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enseignant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(enseignantService.update).toHaveBeenCalledWith(enseignant);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackNiveauById', () => {
      it('Should return tracked Niveau primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackNiveauById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedNiveau', () => {
      it('Should return option if no Niveau is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedNiveau(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Niveau for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedNiveau(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Niveau is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedNiveau(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
