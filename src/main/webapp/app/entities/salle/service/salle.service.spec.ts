import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISalle, Salle } from '../salle.model';

import { SalleService } from './salle.service';

describe('Salle Service', () => {
  let service: SalleService;
  let httpMock: HttpTestingController;
  let elemDefault: ISalle;
  let expectedResult: ISalle | ISalle[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SalleService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      libelle: 'AAAAAAA',
      capacite: 0,
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

    it('should create a Salle', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Salle()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Salle', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
          capacite: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Salle', () => {
      const patchObject = Object.assign({}, new Salle());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Salle', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
          capacite: 1,
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

    it('should delete a Salle', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSalleToCollectionIfMissing', () => {
      it('should add a Salle to an empty array', () => {
        const salle: ISalle = { id: 123 };
        expectedResult = service.addSalleToCollectionIfMissing([], salle);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(salle);
      });

      it('should not add a Salle to an array that contains it', () => {
        const salle: ISalle = { id: 123 };
        const salleCollection: ISalle[] = [
          {
            ...salle,
          },
          { id: 456 },
        ];
        expectedResult = service.addSalleToCollectionIfMissing(salleCollection, salle);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Salle to an array that doesn't contain it", () => {
        const salle: ISalle = { id: 123 };
        const salleCollection: ISalle[] = [{ id: 456 }];
        expectedResult = service.addSalleToCollectionIfMissing(salleCollection, salle);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(salle);
      });

      it('should add only unique Salle to an array', () => {
        const salleArray: ISalle[] = [{ id: 123 }, { id: 456 }, { id: 91245 }];
        const salleCollection: ISalle[] = [{ id: 123 }];
        expectedResult = service.addSalleToCollectionIfMissing(salleCollection, ...salleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const salle: ISalle = { id: 123 };
        const salle2: ISalle = { id: 456 };
        expectedResult = service.addSalleToCollectionIfMissing([], salle, salle2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(salle);
        expect(expectedResult).toContain(salle2);
      });

      it('should accept null and undefined values', () => {
        const salle: ISalle = { id: 123 };
        expectedResult = service.addSalleToCollectionIfMissing([], null, salle, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(salle);
      });

      it('should return initial array if no Salle is added', () => {
        const salleCollection: ISalle[] = [{ id: 123 }];
        expectedResult = service.addSalleToCollectionIfMissing(salleCollection, undefined, null);
        expect(expectedResult).toEqual(salleCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
