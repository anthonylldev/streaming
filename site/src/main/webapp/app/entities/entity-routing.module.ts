import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'film',
        data: { pageTitle: 'streamingApp.film.home.title' },
        loadChildren: () => import('./film/film.module').then(m => m.FilmModule),
      },
      {
        path: 'episode',
        data: { pageTitle: 'streamingApp.episode.home.title' },
        loadChildren: () => import('./episode/episode.module').then(m => m.EpisodeModule),
      },
      {
        path: 'person',
        data: { pageTitle: 'streamingApp.person.home.title' },
        loadChildren: () => import('./person/person.module').then(m => m.PersonModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
