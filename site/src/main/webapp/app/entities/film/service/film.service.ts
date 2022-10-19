import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFilm, NewFilm } from '../film.model';

export type PartialUpdateFilm = Partial<IFilm> & Pick<IFilm, 'id'>;

export type EntityResponseType = HttpResponse<IFilm>;
export type EntityArrayResponseType = HttpResponse<IFilm[]>;

@Injectable({ providedIn: 'root' })
export class FilmService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/films');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(film: NewFilm): Observable<EntityResponseType> {
    return this.http.post<IFilm>(this.resourceUrl, film, { observe: 'response' });
  }

  update(film: IFilm): Observable<EntityResponseType> {
    return this.http.put<IFilm>(`${this.resourceUrl}/${this.getFilmIdentifier(film)}`, film, { observe: 'response' });
  }

  partialUpdate(film: PartialUpdateFilm): Observable<EntityResponseType> {
    return this.http.patch<IFilm>(`${this.resourceUrl}/${this.getFilmIdentifier(film)}`, film, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFilm>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFilm[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFilmIdentifier(film: Pick<IFilm, 'id'>): number {
    return film.id;
  }

  compareFilm(o1: Pick<IFilm, 'id'> | null, o2: Pick<IFilm, 'id'> | null): boolean {
    return o1 && o2 ? this.getFilmIdentifier(o1) === this.getFilmIdentifier(o2) : o1 === o2;
  }

  addFilmToCollectionIfMissing<Type extends Pick<IFilm, 'id'>>(
    filmCollection: Type[],
    ...filmsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const films: Type[] = filmsToCheck.filter(isPresent);
    if (films.length > 0) {
      const filmCollectionIdentifiers = filmCollection.map(filmItem => this.getFilmIdentifier(filmItem)!);
      const filmsToAdd = films.filter(filmItem => {
        const filmIdentifier = this.getFilmIdentifier(filmItem);
        if (filmCollectionIdentifiers.includes(filmIdentifier)) {
          return false;
        }
        filmCollectionIdentifiers.push(filmIdentifier);
        return true;
      });
      return [...filmsToAdd, ...filmCollection];
    }
    return filmCollection;
  }
}
