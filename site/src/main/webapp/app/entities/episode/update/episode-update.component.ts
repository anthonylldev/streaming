import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EpisodeFormService, EpisodeFormGroup } from './episode-form.service';
import { IEpisode } from '../episode.model';
import { EpisodeService } from '../service/episode.service';
import { IFilm } from 'app/entities/film/film.model';
import { FilmService } from 'app/entities/film/service/film.service';

@Component({
  selector: 'jhi-episode-update',
  templateUrl: './episode-update.component.html',
})
export class EpisodeUpdateComponent implements OnInit {
  isSaving = false;
  episode: IEpisode | null = null;

  filmsSharedCollection: IFilm[] = [];

  editForm: EpisodeFormGroup = this.episodeFormService.createEpisodeFormGroup();

  constructor(
    protected episodeService: EpisodeService,
    protected episodeFormService: EpisodeFormService,
    protected filmService: FilmService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareFilm = (o1: IFilm | null, o2: IFilm | null): boolean => this.filmService.compareFilm(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ episode }) => {
      this.episode = episode;
      if (episode) {
        this.updateForm(episode);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const episode = this.episodeFormService.getEpisode(this.editForm);
    if (episode.id !== null) {
      this.subscribeToSaveResponse(this.episodeService.update(episode));
    } else {
      this.subscribeToSaveResponse(this.episodeService.create(episode));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEpisode>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(episode: IEpisode): void {
    this.episode = episode;
    this.episodeFormService.resetForm(this.editForm, episode);

    this.filmsSharedCollection = this.filmService.addFilmToCollectionIfMissing<IFilm>(this.filmsSharedCollection, episode.film);
  }

  protected loadRelationshipsOptions(): void {
    this.filmService
      .query()
      .pipe(map((res: HttpResponse<IFilm[]>) => res.body ?? []))
      .pipe(map((films: IFilm[]) => this.filmService.addFilmToCollectionIfMissing<IFilm>(films, this.episode?.film)))
      .subscribe((films: IFilm[]) => (this.filmsSharedCollection = films));
  }
}
