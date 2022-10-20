import { IFilm } from 'app/entities/film/film.model';

export interface IEpisode {
  id: number;
  title?: string | null;
  synopsis?: string | null;
  order?: number | null;
  film?: Pick<IFilm, 'id' | 'title'> | null;
}

export type NewEpisode = Omit<IEpisode, 'id'> & { id: null };
