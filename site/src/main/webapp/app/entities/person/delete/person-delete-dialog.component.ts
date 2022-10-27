import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPerson } from '../person.model';
import { PersonService } from '../service/person.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './person-delete-dialog.component.html',
})
export class PersonDeleteDialogComponent {
  people?: IPerson[];

  constructor(protected personService: PersonService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(people: IPerson[]): void {
    if (people.length === 1) {
      this.personService.delete(people[0].id).subscribe(() => {
        this.activeModal.close(ITEM_DELETED_EVENT);
      });
    } else {
      people.forEach(person => {
        this.personService.delete(person.id).subscribe(() => {
          this.activeModal.close(ITEM_DELETED_EVENT);
        });
      });

      this.activeModal.close(ITEM_DELETED_EVENT);
    }
  }
}
