import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITuteurs, Tuteurs } from '../tuteurs.model';

import { TuteursService } from './tuteurs.service';

describe('Tuteurs Service', () => {
  let service: TuteursService;
  let httpMock: HttpTestingController;
  let elemDefault: ITuteurs;
  let expectedResult: ITuteurs | ITuteurs[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TuteursService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      prenom: 'AAAAAAA',
      telephone: 'AAAAAAA',
      email: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Tuteurs', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Tuteurs()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tuteurs', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          prenom: 'BBBBBB',
          telephone: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tuteurs', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
          prenom: 'BBBBBB',
        },
        new Tuteurs()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tuteurs', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          prenom: 'BBBBBB',
          telephone: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Tuteurs', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTuteursToCollectionIfMissing', () => {
      it('should add a Tuteurs to an empty array', () => {
        const tuteurs: ITuteurs = { id: 123 };
        expectedResult = service.addTuteursToCollectionIfMissing([], tuteurs);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tuteurs);
      });

      it('should not add a Tuteurs to an array that contains it', () => {
        const tuteurs: ITuteurs = { id: 123 };
        const tuteursCollection: ITuteurs[] = [
          {
            ...tuteurs,
          },
          { id: 456 },
        ];
        expectedResult = service.addTuteursToCollectionIfMissing(tuteursCollection, tuteurs);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tuteurs to an array that doesn't contain it", () => {
        const tuteurs: ITuteurs = { id: 123 };
        const tuteursCollection: ITuteurs[] = [{ id: 456 }];
        expectedResult = service.addTuteursToCollectionIfMissing(tuteursCollection, tuteurs);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tuteurs);
      });

      it('should add only unique Tuteurs to an array', () => {
        const tuteursArray: ITuteurs[] = [{ id: 123 }, { id: 456 }, { id: 23228 }];
        const tuteursCollection: ITuteurs[] = [{ id: 123 }];
        expectedResult = service.addTuteursToCollectionIfMissing(tuteursCollection, ...tuteursArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tuteurs: ITuteurs = { id: 123 };
        const tuteurs2: ITuteurs = { id: 456 };
        expectedResult = service.addTuteursToCollectionIfMissing([], tuteurs, tuteurs2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tuteurs);
        expect(expectedResult).toContain(tuteurs2);
      });

      it('should accept null and undefined values', () => {
        const tuteurs: ITuteurs = { id: 123 };
        expectedResult = service.addTuteursToCollectionIfMissing([], null, tuteurs, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tuteurs);
      });

      it('should return initial array if no Tuteurs is added', () => {
        const tuteursCollection: ITuteurs[] = [{ id: 123 }];
        expectedResult = service.addTuteursToCollectionIfMissing(tuteursCollection, undefined, null);
        expect(expectedResult).toEqual(tuteursCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
