import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEleve, Eleve } from '../eleve.model';

import { EleveService } from './eleve.service';

describe('Eleve Service', () => {
  let service: EleveService;
  let httpMock: HttpTestingController;
  let elemDefault: IEleve;
  let expectedResult: IEleve | IEleve[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EleveService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      prenom: 'AAAAAAA',
      email: 'AAAAAAA',
      dateNaiss: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateNaiss: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Eleve', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dateNaiss: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateNaiss: currentDate,
        },
        returnedFromService
      );

      service.create(new Eleve()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Eleve', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          prenom: 'BBBBBB',
          email: 'BBBBBB',
          dateNaiss: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateNaiss: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Eleve', () => {
      const patchObject = Object.assign(
        {
          prenom: 'BBBBBB',
        },
        new Eleve()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateNaiss: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Eleve', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          prenom: 'BBBBBB',
          email: 'BBBBBB',
          dateNaiss: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateNaiss: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Eleve', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEleveToCollectionIfMissing', () => {
      it('should add a Eleve to an empty array', () => {
        const eleve: IEleve = { id: 123 };
        expectedResult = service.addEleveToCollectionIfMissing([], eleve);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eleve);
      });

      it('should not add a Eleve to an array that contains it', () => {
        const eleve: IEleve = { id: 123 };
        const eleveCollection: IEleve[] = [
          {
            ...eleve,
          },
          { id: 456 },
        ];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, eleve);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Eleve to an array that doesn't contain it", () => {
        const eleve: IEleve = { id: 123 };
        const eleveCollection: IEleve[] = [{ id: 456 }];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, eleve);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eleve);
      });

      it('should add only unique Eleve to an array', () => {
        const eleveArray: IEleve[] = [{ id: 123 }, { id: 456 }, { id: 7790 }];
        const eleveCollection: IEleve[] = [{ id: 123 }];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, ...eleveArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const eleve: IEleve = { id: 123 };
        const eleve2: IEleve = { id: 456 };
        expectedResult = service.addEleveToCollectionIfMissing([], eleve, eleve2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eleve);
        expect(expectedResult).toContain(eleve2);
      });

      it('should accept null and undefined values', () => {
        const eleve: IEleve = { id: 123 };
        expectedResult = service.addEleveToCollectionIfMissing([], null, eleve, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eleve);
      });

      it('should return initial array if no Eleve is added', () => {
        const eleveCollection: IEleve[] = [{ id: 123 }];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, undefined, null);
        expect(expectedResult).toEqual(eleveCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
