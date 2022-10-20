import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFilm, NewFilm } from '../film.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFilm for edit and NewFilmFormGroupInput for create.
 */
type FilmFormGroupInput = IFilm | PartialWithRequiredKeyOf<NewFilm>;

type FilmFormDefaults = Pick<NewFilm, 'id' | 'people'>;

type FilmFormGroupContent = {
  id: FormControl<IFilm['id'] | NewFilm['id']>;
  title: FormControl<IFilm['title']>;
  synopsis: FormControl<IFilm['synopsis']>;
  views: FormControl<IFilm['views']>;
  cover: FormControl<IFilm['cover']>;
  coverContentType: FormControl<IFilm['coverContentType']>;
  reviews: FormControl<IFilm['reviews']>;
  gender: FormControl<IFilm['gender']>;
  filmType: FormControl<IFilm['filmType']>;
  order: FormControl<IFilm['order']>;
  url: FormControl<IFilm['url']>;
  people: FormControl<IFilm['people']>;
};

export type FilmFormGroup = FormGroup<FilmFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FilmFormService {
  createFilmFormGroup(film: FilmFormGroupInput = { id: null }): FilmFormGroup {
    const filmRawValue = {
      ...this.getFormDefaults(),
      ...film,
    };
    return new FormGroup<FilmFormGroupContent>({
      id: new FormControl(
        { value: filmRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(filmRawValue.title, {
        validators: [Validators.required, Validators.minLength(3)],
      }),
      synopsis: new FormControl(filmRawValue.synopsis),
      views: new FormControl(filmRawValue.views, {
        validators: [Validators.min(0)],
      }),
      cover: new FormControl(filmRawValue.cover),
      coverContentType: new FormControl(filmRawValue.coverContentType),
      reviews: new FormControl(filmRawValue.reviews),
      gender: new FormControl(filmRawValue.gender),
      filmType: new FormControl(filmRawValue.filmType),
      order: new FormControl(filmRawValue.order, {
        validators: [Validators.min(0)],
      }),
      url: new FormControl(filmRawValue.url, {
        validators: [Validators.required],
      }),
      people: new FormControl(filmRawValue.people ?? []),
    });
  }

  getFilm(form: FilmFormGroup): IFilm | NewFilm {
    return form.getRawValue() as IFilm | NewFilm;
  }

  resetForm(form: FilmFormGroup, film: FilmFormGroupInput): void {
    const filmRawValue = { ...this.getFormDefaults(), ...film };
    form.reset(
      {
        ...filmRawValue,
        id: { value: filmRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FilmFormDefaults {
    return {
      id: null,
      people: [],
    };
  }
}
