import { IFilm } from 'app/entities/film/film.model';

export interface IPerson {
  id: number;
  name?: string | null;
  cover?: string | null;
  coverContentType?: string | null;
  films?: Pick<IFilm, 'id'>[] | null;
}

export type NewPerson = Omit<IPerson, 'id'> & { id: null };
