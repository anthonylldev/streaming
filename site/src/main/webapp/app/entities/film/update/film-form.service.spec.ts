import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../film.test-samples';

import { FilmFormService } from './film-form.service';

describe('Film Form Service', () => {
  let service: FilmFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FilmFormService);
  });

  describe('Service methods', () => {
    describe('createFilmFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFilmFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            synopsis: expect.any(Object),
            views: expect.any(Object),
            cover: expect.any(Object),
            reviews: expect.any(Object),
            gender: expect.any(Object),
            filmType: expect.any(Object),
            order: expect.any(Object),
            url: expect.any(Object),
            people: expect.any(Object),
          })
        );
      });

      it('passing IFilm should create a new form with FormGroup', () => {
        const formGroup = service.createFilmFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            synopsis: expect.any(Object),
            views: expect.any(Object),
            cover: expect.any(Object),
            reviews: expect.any(Object),
            gender: expect.any(Object),
            filmType: expect.any(Object),
            order: expect.any(Object),
            url: expect.any(Object),
            people: expect.any(Object),
          })
        );
      });
    });

    describe('getFilm', () => {
      it('should return NewFilm for default Film initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFilmFormGroup(sampleWithNewData);

        const film = service.getFilm(formGroup) as any;

        expect(film).toMatchObject(sampleWithNewData);
      });

      it('should return NewFilm for empty Film initial value', () => {
        const formGroup = service.createFilmFormGroup();

        const film = service.getFilm(formGroup) as any;

        expect(film).toMatchObject({});
      });

      it('should return IFilm', () => {
        const formGroup = service.createFilmFormGroup(sampleWithRequiredData);

        const film = service.getFilm(formGroup) as any;

        expect(film).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFilm should not enable id FormControl', () => {
        const formGroup = service.createFilmFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFilm should disable id FormControl', () => {
        const formGroup = service.createFilmFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
