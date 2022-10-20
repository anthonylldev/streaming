import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../episode.test-samples';

import { EpisodeFormService } from './episode-form.service';

describe('Episode Form Service', () => {
  let service: EpisodeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EpisodeFormService);
  });

  describe('Service methods', () => {
    describe('createEpisodeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEpisodeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            synopsis: expect.any(Object),
            order: expect.any(Object),
            film: expect.any(Object),
          })
        );
      });

      it('passing IEpisode should create a new form with FormGroup', () => {
        const formGroup = service.createEpisodeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            synopsis: expect.any(Object),
            order: expect.any(Object),
            film: expect.any(Object),
          })
        );
      });
    });

    describe('getEpisode', () => {
      it('should return NewEpisode for default Episode initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEpisodeFormGroup(sampleWithNewData);

        const episode = service.getEpisode(formGroup) as any;

        expect(episode).toMatchObject(sampleWithNewData);
      });

      it('should return NewEpisode for empty Episode initial value', () => {
        const formGroup = service.createEpisodeFormGroup();

        const episode = service.getEpisode(formGroup) as any;

        expect(episode).toMatchObject({});
      });

      it('should return IEpisode', () => {
        const formGroup = service.createEpisodeFormGroup(sampleWithRequiredData);

        const episode = service.getEpisode(formGroup) as any;

        expect(episode).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEpisode should not enable id FormControl', () => {
        const formGroup = service.createEpisodeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEpisode should disable id FormControl', () => {
        const formGroup = service.createEpisodeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
