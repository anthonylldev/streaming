import { IEpisode, NewEpisode } from './episode.model';

export const sampleWithRequiredData: IEpisode = {
  id: 16158,
  title: 'Senior clicks-and-mortar',
};

export const sampleWithPartialData: IEpisode = {
  id: 83428,
  title: 'Hormigon back Genérico',
  order: 25743,
};

export const sampleWithFullData: IEpisode = {
  id: 2706,
  title: 'contexto Rústico',
  synopsis: 'empower',
  order: 52468,
};

export const sampleWithNewData: NewEpisode = {
  title: 'Rioja',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
