import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEpisode, NewEpisode } from '../episode.model';

export type PartialUpdateEpisode = Partial<IEpisode> & Pick<IEpisode, 'id'>;

export type EntityResponseType = HttpResponse<IEpisode>;
export type EntityArrayResponseType = HttpResponse<IEpisode[]>;

@Injectable({ providedIn: 'root' })
export class EpisodeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/episodes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(episode: NewEpisode): Observable<EntityResponseType> {
    return this.http.post<IEpisode>(this.resourceUrl, episode, { observe: 'response' });
  }

  update(episode: IEpisode): Observable<EntityResponseType> {
    return this.http.put<IEpisode>(`${this.resourceUrl}/${this.getEpisodeIdentifier(episode)}`, episode, { observe: 'response' });
  }

  partialUpdate(episode: PartialUpdateEpisode): Observable<EntityResponseType> {
    return this.http.patch<IEpisode>(`${this.resourceUrl}/${this.getEpisodeIdentifier(episode)}`, episode, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEpisode>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEpisode[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEpisodeIdentifier(episode: Pick<IEpisode, 'id'>): number {
    return episode.id;
  }

  compareEpisode(o1: Pick<IEpisode, 'id'> | null, o2: Pick<IEpisode, 'id'> | null): boolean {
    return o1 && o2 ? this.getEpisodeIdentifier(o1) === this.getEpisodeIdentifier(o2) : o1 === o2;
  }

  addEpisodeToCollectionIfMissing<Type extends Pick<IEpisode, 'id'>>(
    episodeCollection: Type[],
    ...episodesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const episodes: Type[] = episodesToCheck.filter(isPresent);
    if (episodes.length > 0) {
      const episodeCollectionIdentifiers = episodeCollection.map(episodeItem => this.getEpisodeIdentifier(episodeItem)!);
      const episodesToAdd = episodes.filter(episodeItem => {
        const episodeIdentifier = this.getEpisodeIdentifier(episodeItem);
        if (episodeCollectionIdentifiers.includes(episodeIdentifier)) {
          return false;
        }
        episodeCollectionIdentifiers.push(episodeIdentifier);
        return true;
      });
      return [...episodesToAdd, ...episodeCollection];
    }
    return episodeCollection;
  }
}
