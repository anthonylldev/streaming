import { Component, Input, OnInit } from '@angular/core';
import { IFilm } from '../film.model';

@Component({
  selector: 'jhi-film-card',
  templateUrl: './film-card.component.html',
  styleUrls: ['./film-card.component.scss']
})
export class FilmCardComponent implements OnInit {
  @Input() film?: IFilm;

  constructor() { }

  ngOnInit(): void {
  }

}
