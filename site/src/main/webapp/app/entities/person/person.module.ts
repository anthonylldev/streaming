import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PersonComponent } from './list/person.component';
import { PersonDetailComponent } from './detail/person-detail.component';
import { PersonUpdateComponent } from './update/person-update.component';
import { PersonDeleteDialogComponent } from './delete/person-delete-dialog.component';
import { PersonRoutingModule } from './route/person-routing.module';
import { PersonCardComponent } from './person-card/person-card.component';
import { TableModule } from 'primeng/table';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';

@NgModule({
  imports: [SharedModule, PersonRoutingModule, TableModule, ToolbarModule, ButtonModule, InputTextModule],
  declarations: [PersonComponent, PersonDetailComponent, PersonUpdateComponent, PersonDeleteDialogComponent, PersonCardComponent],
})
export class PersonModule {}
