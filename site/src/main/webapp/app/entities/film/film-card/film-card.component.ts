import { Component, Input, OnInit } from '@angular/core';
import { IFilm } from '../film.model';

@Component({
  selector: 'jhi-film-card',
  templateUrl: './film-card.component.html',
  styleUrls: ['./film-card.component.scss']
})
export class FilmCardComponent implements OnInit {
  @Input() film?: IFilm;
  showSpecifications = false;

  constructor() { }

  ngOnInit(): void {
  }

  show(op: any, $event: any): void {
    op.toggle($event);
  }

  hide(op: any): void {
    setTimeout(() => {
      op.hide();
    }, 500);
  }

}
