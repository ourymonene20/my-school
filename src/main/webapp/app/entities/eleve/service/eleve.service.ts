import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEleve, getEleveIdentifier } from '../eleve.model';

export type EntityResponseType = HttpResponse<IEleve>;
export type EntityArrayResponseType = HttpResponse<IEleve[]>;

@Injectable({ providedIn: 'root' })
export class EleveService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/eleves');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(eleve: IEleve): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(eleve);
    return this.http
      .post<IEleve>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(eleve: IEleve): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(eleve);
    return this.http
      .put<IEleve>(`${this.resourceUrl}/${getEleveIdentifier(eleve) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(eleve: IEleve): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(eleve);
    return this.http
      .patch<IEleve>(`${this.resourceUrl}/${getEleveIdentifier(eleve) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEleve>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEleve[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEleveToCollectionIfMissing(eleveCollection: IEleve[], ...elevesToCheck: (IEleve | null | undefined)[]): IEleve[] {
    const eleves: IEleve[] = elevesToCheck.filter(isPresent);
    if (eleves.length > 0) {
      const eleveCollectionIdentifiers = eleveCollection.map(eleveItem => getEleveIdentifier(eleveItem)!);
      const elevesToAdd = eleves.filter(eleveItem => {
        const eleveIdentifier = getEleveIdentifier(eleveItem);
        if (eleveIdentifier == null || eleveCollectionIdentifiers.includes(eleveIdentifier)) {
          return false;
        }
        eleveCollectionIdentifiers.push(eleveIdentifier);
        return true;
      });
      return [...elevesToAdd, ...eleveCollection];
    }
    return eleveCollection;
  }

  protected convertDateFromClient(eleve: IEleve): IEleve {
    return Object.assign({}, eleve, {
      dateNaiss: eleve.dateNaiss?.isValid() ? eleve.dateNaiss.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateNaiss = res.body.dateNaiss ? dayjs(res.body.dateNaiss) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((eleve: IEleve) => {
        eleve.dateNaiss = eleve.dateNaiss ? dayjs(eleve.dateNaiss) : undefined;
      });
    }
    return res;
  }
}
