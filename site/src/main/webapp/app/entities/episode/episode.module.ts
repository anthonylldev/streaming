import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EpisodeComponent } from './list/episode.component';
import { EpisodeDetailComponent } from './detail/episode-detail.component';
import { EpisodeUpdateComponent } from './update/episode-update.component';
import { EpisodeDeleteDialogComponent } from './delete/episode-delete-dialog.component';
import { EpisodeRoutingModule } from './route/episode-routing.module';
import { TableModule } from 'primeng/table';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MultiSelectModule } from 'primeng/multiselect';
import { DropdownModule } from 'primeng/dropdown';

@NgModule({
  imports: [
    SharedModule,
    EpisodeRoutingModule,
    TableModule,
    ToolbarModule,
    ButtonModule,
    InputTextModule,
    MultiSelectModule,
    DropdownModule,
  ],
  declarations: [EpisodeComponent, EpisodeDetailComponent, EpisodeUpdateComponent, EpisodeDeleteDialogComponent],
})
export class EpisodeModule {}
