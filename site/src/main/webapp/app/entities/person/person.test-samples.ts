import { IPerson, NewPerson } from './person.model';

export const sampleWithRequiredData: IPerson = {
  id: 31094,
  name: 'Pound Buckinghamshire',
};

export const sampleWithPartialData: IPerson = {
  id: 14129,
  name: 'Rojo',
};

export const sampleWithFullData: IPerson = {
  id: 6974,
  name: 'facilitate',
  cover: '../fake-data/blob/hipster.png',
  coverContentType: 'unknown',
};

export const sampleWithNewData: NewPerson = {
  name: 'withdrawal complejidad',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
