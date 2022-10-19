import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEpisode } from '../episode.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../episode.test-samples';

import { EpisodeService } from './episode.service';

const requireRestSample: IEpisode = {
  ...sampleWithRequiredData,
};

describe('Episode Service', () => {
  let service: EpisodeService;
  let httpMock: HttpTestingController;
  let expectedResult: IEpisode | IEpisode[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EpisodeService);
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

    it('should create a Episode', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const episode = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(episode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Episode', () => {
      const episode = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(episode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Episode', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Episode', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Episode', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEpisodeToCollectionIfMissing', () => {
      it('should add a Episode to an empty array', () => {
        const episode: IEpisode = sampleWithRequiredData;
        expectedResult = service.addEpisodeToCollectionIfMissing([], episode);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(episode);
      });

      it('should not add a Episode to an array that contains it', () => {
        const episode: IEpisode = sampleWithRequiredData;
        const episodeCollection: IEpisode[] = [
          {
            ...episode,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEpisodeToCollectionIfMissing(episodeCollection, episode);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Episode to an array that doesn't contain it", () => {
        const episode: IEpisode = sampleWithRequiredData;
        const episodeCollection: IEpisode[] = [sampleWithPartialData];
        expectedResult = service.addEpisodeToCollectionIfMissing(episodeCollection, episode);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(episode);
      });

      it('should add only unique Episode to an array', () => {
        const episodeArray: IEpisode[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const episodeCollection: IEpisode[] = [sampleWithRequiredData];
        expectedResult = service.addEpisodeToCollectionIfMissing(episodeCollection, ...episodeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const episode: IEpisode = sampleWithRequiredData;
        const episode2: IEpisode = sampleWithPartialData;
        expectedResult = service.addEpisodeToCollectionIfMissing([], episode, episode2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(episode);
        expect(expectedResult).toContain(episode2);
      });

      it('should accept null and undefined values', () => {
        const episode: IEpisode = sampleWithRequiredData;
        expectedResult = service.addEpisodeToCollectionIfMissing([], null, episode, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(episode);
      });

      it('should return initial array if no Episode is added', () => {
        const episodeCollection: IEpisode[] = [sampleWithRequiredData];
        expectedResult = service.addEpisodeToCollectionIfMissing(episodeCollection, undefined, null);
        expect(expectedResult).toEqual(episodeCollection);
      });
    });

    describe('compareEpisode', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEpisode(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEpisode(entity1, entity2);
        const compareResult2 = service.compareEpisode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEpisode(entity1, entity2);
        const compareResult2 = service.compareEpisode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEpisode(entity1, entity2);
        const compareResult2 = service.compareEpisode(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
