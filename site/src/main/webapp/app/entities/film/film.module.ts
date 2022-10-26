import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FilmComponent } from './list/film.component';
import { FilmDetailComponent } from './detail/film-detail.component';
import { FilmUpdateComponent } from './update/film-update.component';
import { FilmDeleteDialogComponent } from './delete/film-delete-dialog.component';
import { FilmRoutingModule } from './route/film-routing.module';
import { FilmCardComponent } from './film-card/film-card.component';
import { CarouselModule } from 'primeng/carousel';
import { TooltipModule } from 'primeng/tooltip';
import {OverlayPanelModule} from 'primeng/overlaypanel';


@NgModule({
  imports: [SharedModule, FilmRoutingModule, CarouselModule, TooltipModule, OverlayPanelModule],
  declarations: [FilmComponent, FilmDetailComponent, FilmUpdateComponent, FilmDeleteDialogComponent, FilmCardComponent],
})
export class FilmModule {}
