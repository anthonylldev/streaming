import { IPerson } from 'app/entities/person/person.model';
import { Gender } from 'app/entities/enumerations/gender.model';
import { FilmType } from 'app/entities/enumerations/film-type.model';

export interface IFilm {
  id: number;
  title?: string | null;
  synopsis?: string | null;
  views?: number | null;
  cover?: string | null;
  coverContentType?: string | null;
  reviews?: number | null;
  gender?: Gender | null;
  filmType?: FilmType | null;
  order?: number | null;
  url?: string | null;
  people?: Pick<IPerson, 'id'>[] | null;
}

export type NewFilm = Omit<IFilm, 'id'> & { id: null };
