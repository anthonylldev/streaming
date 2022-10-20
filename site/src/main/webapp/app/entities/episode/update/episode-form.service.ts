import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEpisode, NewEpisode } from '../episode.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEpisode for edit and NewEpisodeFormGroupInput for create.
 */
type EpisodeFormGroupInput = IEpisode | PartialWithRequiredKeyOf<NewEpisode>;

type EpisodeFormDefaults = Pick<NewEpisode, 'id'>;

type EpisodeFormGroupContent = {
  id: FormControl<IEpisode['id'] | NewEpisode['id']>;
  title: FormControl<IEpisode['title']>;
  synopsis: FormControl<IEpisode['synopsis']>;
  order: FormControl<IEpisode['order']>;
  film: FormControl<IEpisode['film']>;
};

export type EpisodeFormGroup = FormGroup<EpisodeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EpisodeFormService {
  createEpisodeFormGroup(episode: EpisodeFormGroupInput = { id: null }): EpisodeFormGroup {
    const episodeRawValue = {
      ...this.getFormDefaults(),
      ...episode,
    };
    return new FormGroup<EpisodeFormGroupContent>({
      id: new FormControl(
        { value: episodeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(episodeRawValue.title, {
        validators: [Validators.required, Validators.minLength(3)],
      }),
      synopsis: new FormControl(episodeRawValue.synopsis),
      order: new FormControl(episodeRawValue.order),
      film: new FormControl(episodeRawValue.film),
    });
  }

  getEpisode(form: EpisodeFormGroup): IEpisode | NewEpisode {
    return form.getRawValue() as IEpisode | NewEpisode;
  }

  resetForm(form: EpisodeFormGroup, episode: EpisodeFormGroupInput): void {
    const episodeRawValue = { ...this.getFormDefaults(), ...episode };
    form.reset(
      {
        ...episodeRawValue,
        id: { value: episodeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EpisodeFormDefaults {
    return {
      id: null,
    };
  }
}
