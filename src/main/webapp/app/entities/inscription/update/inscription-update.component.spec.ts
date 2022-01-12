import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InscriptionService } from '../service/inscription.service';
import { IInscription, Inscription } from '../inscription.model';
import { IEleve } from 'app/entities/eleve/eleve.model';
import { EleveService } from 'app/entities/eleve/service/eleve.service';
import { INiveau } from 'app/entities/niveau/niveau.model';
import { NiveauService } from 'app/entities/niveau/service/niveau.service';

import { InscriptionUpdateComponent } from './inscription-update.component';

describe('Inscription Management Update Component', () => {
  let comp: InscriptionUpdateComponent;
  let fixture: ComponentFixture<InscriptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inscriptionService: InscriptionService;
  let eleveService: EleveService;
  let niveauService: NiveauService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [InscriptionUpdateComponent],
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
      .overrideTemplate(InscriptionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InscriptionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inscriptionService = TestBed.inject(InscriptionService);
    eleveService = TestBed.inject(EleveService);
    niveauService = TestBed.inject(NiveauService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call eleve query and add missing value', () => {
      const inscription: IInscription = { id: 456 };
      const eleve: IEleve = { id: 4422 };
      inscription.eleve = eleve;

      const eleveCollection: IEleve[] = [{ id: 2428 }];
      jest.spyOn(eleveService, 'query').mockReturnValue(of(new HttpResponse({ body: eleveCollection })));
      const expectedCollection: IEleve[] = [eleve, ...eleveCollection];
      jest.spyOn(eleveService, 'addEleveToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inscription });
      comp.ngOnInit();

      expect(eleveService.query).toHaveBeenCalled();
      expect(eleveService.addEleveToCollectionIfMissing).toHaveBeenCalledWith(eleveCollection, eleve);
      expect(comp.elevesCollection).toEqual(expectedCollection);
    });

    it('Should call Niveau query and add missing value', () => {
      const inscription: IInscription = { id: 456 };
      const niveau: INiveau = { id: 9989 };
      inscription.niveau = niveau;

      const niveauCollection: INiveau[] = [{ id: 82408 }];
      jest.spyOn(niveauService, 'query').mockReturnValue(of(new HttpResponse({ body: niveauCollection })));
      const additionalNiveaus = [niveau];
      const expectedCollection: INiveau[] = [...additionalNiveaus, ...niveauCollection];
      jest.spyOn(niveauService, 'addNiveauToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inscription });
      comp.ngOnInit();

      expect(niveauService.query).toHaveBeenCalled();
      expect(niveauService.addNiveauToCollectionIfMissing).toHaveBeenCalledWith(niveauCollection, ...additionalNiveaus);
      expect(comp.niveausSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const inscription: IInscription = { id: 456 };
      const eleve: IEleve = { id: 83307 };
      inscription.eleve = eleve;
      const niveau: INiveau = { id: 85238 };
      inscription.niveau = niveau;

      activatedRoute.data = of({ inscription });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(inscription));
      expect(comp.elevesCollection).toContain(eleve);
      expect(comp.niveausSharedCollection).toContain(niveau);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Inscription>>();
      const inscription = { id: 123 };
      jest.spyOn(inscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inscription }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(inscriptionService.update).toHaveBeenCalledWith(inscription);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Inscription>>();
      const inscription = new Inscription();
      jest.spyOn(inscriptionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inscription }));
      saveSubject.complete();

      // THEN
      expect(inscriptionService.create).toHaveBeenCalledWith(inscription);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Inscription>>();
      const inscription = { id: 123 };
      jest.spyOn(inscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inscriptionService.update).toHaveBeenCalledWith(inscription);
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

    describe('trackNiveauById', () => {
      it('Should return tracked Niveau primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackNiveauById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
