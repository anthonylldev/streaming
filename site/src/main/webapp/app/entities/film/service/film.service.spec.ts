import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFilm } from '../film.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../film.test-samples';

import { FilmService } from './film.service';

const requireRestSample: IFilm = {
  ...sampleWithRequiredData,
};

describe('Film Service', () => {
  let service: FilmService;
  let httpMock: HttpTestingController;
  let expectedResult: IFilm | IFilm[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FilmService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Film', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const film = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(film).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Film', () => {
      const film = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(film).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Film', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Film', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Film', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFilmToCollectionIfMissing', () => {
      it('should add a Film to an empty array', () => {
        const film: IFilm = sampleWithRequiredData;
        expectedResult = service.addFilmToCollectionIfMissing([], film);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(film);
      });

      it('should not add a Film to an array that contains it', () => {
        const film: IFilm = sampleWithRequiredData;
        const filmCollection: IFilm[] = [
          {
            ...film,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, film);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Film to an array that doesn't contain it", () => {
        const film: IFilm = sampleWithRequiredData;
        const filmCollection: IFilm[] = [sampleWithPartialData];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, film);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(film);
      });

      it('should add only unique Film to an array', () => {
        const filmArray: IFilm[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const filmCollection: IFilm[] = [sampleWithRequiredData];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, ...filmArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const film: IFilm = sampleWithRequiredData;
        const film2: IFilm = sampleWithPartialData;
        expectedResult = service.addFilmToCollectionIfMissing([], film, film2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(film);
        expect(expectedResult).toContain(film2);
      });

      it('should accept null and undefined values', () => {
        const film: IFilm = sampleWithRequiredData;
        expectedResult = service.addFilmToCollectionIfMissing([], null, film, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(film);
      });

      it('should return initial array if no Film is added', () => {
        const filmCollection: IFilm[] = [sampleWithRequiredData];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, undefined, null);
        expect(expectedResult).toEqual(filmCollection);
      });
    });

    describe('compareFilm', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFilm(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFilm(entity1, entity2);
        const compareResult2 = service.compareFilm(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFilm(entity1, entity2);
        const compareResult2 = service.compareFilm(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFilm(entity1, entity2);
        const compareResult2 = service.compareFilm(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
