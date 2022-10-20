import { Component, Input, OnInit } from '@angular/core';
import { IPerson } from '../person.model';

@Component({
  selector: 'jhi-person-card',
  templateUrl: './person-card.component.html',
  styleUrls: ['./person-card.component.scss']
})
export class PersonCardComponent implements OnInit {
  @Input() person?: IPerson;

  constructor() { }

  ngOnInit(): void {
  }

}
