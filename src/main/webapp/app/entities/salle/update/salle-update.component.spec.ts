import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SalleService } from '../service/salle.service';
import { ISalle, Salle } from '../salle.model';

import { SalleUpdateComponent } from './salle-update.component';

describe('Salle Management Update Component', () => {
  let comp: SalleUpdateComponent;
  let fixture: ComponentFixture<SalleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let salleService: SalleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SalleUpdateComponent],
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
      .overrideTemplate(SalleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SalleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    salleService = TestBed.inject(SalleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const salle: ISalle = { id: 456 };

      activatedRoute.data = of({ salle });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(salle));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Salle>>();
      const salle = { id: 123 };
      jest.spyOn(salleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: salle }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(salleService.update).toHaveBeenCalledWith(salle);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Salle>>();
      const salle = new Salle();
      jest.spyOn(salleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: salle }));
      saveSubject.complete();

      // THEN
      expect(salleService.create).toHaveBeenCalledWith(salle);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Salle>>();
      const salle = { id: 123 };
      jest.spyOn(salleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(salleService.update).toHaveBeenCalledWith(salle);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
