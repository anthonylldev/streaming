import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EpisodeFormService } from './episode-form.service';
import { EpisodeService } from '../service/episode.service';
import { IEpisode } from '../episode.model';
import { IFilm } from 'app/entities/film/film.model';
import { FilmService } from 'app/entities/film/service/film.service';

import { EpisodeUpdateComponent } from './episode-update.component';

describe('Episode Management Update Component', () => {
  let comp: EpisodeUpdateComponent;
  let fixture: ComponentFixture<EpisodeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let episodeFormService: EpisodeFormService;
  let episodeService: EpisodeService;
  let filmService: FilmService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EpisodeUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(EpisodeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EpisodeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    episodeFormService = TestBed.inject(EpisodeFormService);
    episodeService = TestBed.inject(EpisodeService);
    filmService = TestBed.inject(FilmService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Film query and add missing value', () => {
      const episode: IEpisode = { id: 456 };
      const film: IFilm = { id: 11907 };
      episode.film = film;

      const filmCollection: IFilm[] = [{ id: 65124 }];
      jest.spyOn(filmService, 'query').mockReturnValue(of(new HttpResponse({ body: filmCollection })));
      const additionalFilms = [film];
      const expectedCollection: IFilm[] = [...additionalFilms, ...filmCollection];
      jest.spyOn(filmService, 'addFilmToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ episode });
      comp.ngOnInit();

      expect(filmService.query).toHaveBeenCalled();
      expect(filmService.addFilmToCollectionIfMissing).toHaveBeenCalledWith(
        filmCollection,
        ...additionalFilms.map(expect.objectContaining)
      );
      expect(comp.filmsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const episode: IEpisode = { id: 456 };
      const film: IFilm = { id: 25938 };
      episode.film = film;

      activatedRoute.data = of({ episode });
      comp.ngOnInit();

      expect(comp.filmsSharedCollection).toContain(film);
      expect(comp.episode).toEqual(episode);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEpisode>>();
      const episode = { id: 123 };
      jest.spyOn(episodeFormService, 'getEpisode').mockReturnValue(episode);
      jest.spyOn(episodeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ episode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: episode }));
      saveSubject.complete();

      // THEN
      expect(episodeFormService.getEpisode).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(episodeService.update).toHaveBeenCalledWith(expect.objectContaining(episode));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEpisode>>();
      const episode = { id: 123 };
      jest.spyOn(episodeFormService, 'getEpisode').mockReturnValue({ id: null });
      jest.spyOn(episodeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ episode: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: episode }));
      saveSubject.complete();

      // THEN
      expect(episodeFormService.getEpisode).toHaveBeenCalled();
      expect(episodeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEpisode>>();
      const episode = { id: 123 };
      jest.spyOn(episodeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ episode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(episodeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFilm', () => {
      it('Should forward to filmService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(filmService, 'compareFilm');
        comp.compareFilm(entity, entity2);
        expect(filmService.compareFilm).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
