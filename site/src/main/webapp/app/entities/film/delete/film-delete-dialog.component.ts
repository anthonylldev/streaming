import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFilm } from '../film.model';
import { FilmService } from '../service/film.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './film-delete-dialog.component.html',
})
export class FilmDeleteDialogComponent {
  films?: IFilm[];

  constructor(protected filmService: FilmService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(films: IFilm[]): void {
    if (films.length === 1) {
      this.filmService.delete(films[0].id).subscribe(() => {
        this.activeModal.close(ITEM_DELETED_EVENT);
      });
    } else {
      films.forEach(film => {
        this.filmService.delete(film.id).subscribe(() => {
          this.activeModal.close(ITEM_DELETED_EVENT);
        });
      });

      this.activeModal.close(ITEM_DELETED_EVENT);
    }
  }
}
