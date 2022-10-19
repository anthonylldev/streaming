import { Gender } from 'app/entities/enumerations/gender.model';
import { FilmType } from 'app/entities/enumerations/film-type.model';

import { IFilm, NewFilm } from './film.model';

export const sampleWithRequiredData: IFilm = {
  id: 86712,
  title: 'Camiseta Patatas',
  url: 'https://pedro.com.es',
};

export const sampleWithPartialData: IFilm = {
  id: 58556,
  title: 'Nicaragua Camiseta',
  cover: '../fake-data/blob/hipster.png',
  coverContentType: 'unknown',
  gender: Gender['COMEDY'],
  url: 'https://yolanda.com.es',
};

export const sampleWithFullData: IFilm = {
  id: 9640,
  title: 'bluetooth Increible Sabroso',
  synopsis: 'Blanco de Ladrillo',
  views: 30244,
  cover: '../fake-data/blob/hipster.png',
  coverContentType: 'unknown',
  reviews: 68816,
  gender: Gender['ADVENTURE'],
  filmType: FilmType['DOCUMENTARY'],
  order: 97069,
  url: 'http://beatriz.info',
};

export const sampleWithNewData: NewFilm = {
  title: 'Arquitecto Programable Coche',
  url: 'http://caridad.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
