import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITuteurs, getTuteursIdentifier } from '../tuteurs.model';

export type EntityResponseType = HttpResponse<ITuteurs>;
export type EntityArrayResponseType = HttpResponse<ITuteurs[]>;

@Injectable({ providedIn: 'root' })
export class TuteursService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tuteurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tuteurs: ITuteurs): Observable<EntityResponseType> {
    return this.http.post<ITuteurs>(this.resourceUrl, tuteurs, { observe: 'response' });
  }

  update(tuteurs: ITuteurs): Observable<EntityResponseType> {
    return this.http.put<ITuteurs>(`${this.resourceUrl}/${getTuteursIdentifier(tuteurs) as number}`, tuteurs, { observe: 'response' });
  }

  partialUpdate(tuteurs: ITuteurs): Observable<EntityResponseType> {
    return this.http.patch<ITuteurs>(`${this.resourceUrl}/${getTuteursIdentifier(tuteurs) as number}`, tuteurs, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITuteurs>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITuteurs[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTuteursToCollectionIfMissing(tuteursCollection: ITuteurs[], ...tuteursToCheck: (ITuteurs | null | undefined)[]): ITuteurs[] {
    const tuteurs: ITuteurs[] = tuteursToCheck.filter(isPresent);
    if (tuteurs.length > 0) {
      const tuteursCollectionIdentifiers = tuteursCollection.map(tuteursItem => getTuteursIdentifier(tuteursItem)!);
      const tuteursToAdd = tuteurs.filter(tuteursItem => {
        const tuteursIdentifier = getTuteursIdentifier(tuteursItem);
        if (tuteursIdentifier == null || tuteursCollectionIdentifiers.includes(tuteursIdentifier)) {
          return false;
        }
        tuteursCollectionIdentifiers.push(tuteursIdentifier);
        return true;
      });
      return [...tuteursToAdd, ...tuteursCollection];
    }
    return tuteursCollection;
  }
}
