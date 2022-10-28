import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEpisode } from '../episode.model';
import { EpisodeService } from '../service/episode.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './episode-delete-dialog.component.html',
})
export class EpisodeDeleteDialogComponent {
  episodes?: IEpisode[];

  constructor(protected episodeService: EpisodeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(episodes: IEpisode[]): void {

    if (episodes.length === 1) {
      this.episodeService.delete(episodes[0].id).subscribe(() => {
        this.activeModal.close(ITEM_DELETED_EVENT);
      });
    } else {
      episodes.forEach(episode => {
        this.episodeService.delete(episode.id).subscribe(() => {
          this.activeModal.close(ITEM_DELETED_EVENT);
        });
      });

      this.activeModal.close(ITEM_DELETED_EVENT);
    }
  }
}
