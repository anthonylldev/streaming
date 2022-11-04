import { Component, OnInit, ElementRef, ViewChild, AfterViewInit, AfterViewChecked, ChangeDetectorRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FilmFormService, FilmFormGroup } from './film-form.service';
import { IFilm } from '../film.model';
import { FilmService } from '../service/film.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { Gender } from 'app/entities/enumerations/gender.model';
import { FilmType } from 'app/entities/enumerations/film-type.model';
import { Dropdown } from 'primeng/dropdown';
import { FileUpload } from 'primeng/fileupload';

@Component({
  selector: 'jhi-film-update',
  templateUrl: './film-update.component.html',
  styleUrls: ['./film-update.component.scss']
})
export class FilmUpdateComponent implements OnInit, AfterViewInit, AfterViewChecked {
  isSaving = false;
  film: IFilm | null = null;

  genderValues = Object.keys(Gender);
  selectedGender?: string;

  filmTypeValues = Object.keys(FilmType);
  selectedFilmType?: string;

  order?: number;

  peopleSharedCollection: IPerson[] = [];
  selectedPeople: IPerson[] = [];

  editForm: FilmFormGroup = this.filmFormService.createFilmFormGroup();

  fileUploadInit = true;

  @ViewChild('genderDropdown') genderDropdown?: Dropdown;
  @ViewChild('coverUpload') coverUpload?: FileUpload;

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected filmService: FilmService,
    protected filmFormService: FilmFormService,
    protected personService: PersonService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private changeDetector: ChangeDetectorRef
  ) {}

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ film }) => {
      this.film = film;
      if (film) {
        this.updateForm(film);
      }

      this.loadRelationshipsOptions();
    });
  }

  ngAfterViewInit(): void {
    if (this.genderDropdown) {
      (this.genderDropdown.filterBy as any) = {
        split: (_: any) => [(item: any) => item],
      };
    }


    if (this.film && this.fileUploadInit) {
      this.dataUtils.loadFileInFileUpload(this.film.cover!, this.film.coverContentType!, this.coverUpload).subscribe({
        error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('streamingApp.error', { ...err, key: 'error.file.' + err.key })),
      })
    }
  }

  ngAfterViewChecked(): void {
    this.changeDetector.detectChanges();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: any, field: string): void {
    console.log(this.coverUpload);
    this.dataUtils.loadFile(event, this.editForm, field).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('streamingApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const film = this.filmFormService.getFilm(this.editForm);
    if (film.id !== null) {
      this.subscribeToSaveResponse(this.filmService.update(film));
    } else {
      this.subscribeToSaveResponse(this.filmService.create(film));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFilm>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(film: IFilm): void {
    this.film = film;
    this.filmFormService.resetForm(this.editForm, film);

    this.selectedGender = film.gender?.toString();
    this.selectedFilmType = film.filmType?.toString();
    this.order = film.order ?? 0;

    this.peopleSharedCollection = this.personService.addPersonToCollectionIfMissing<IPerson>(
      this.peopleSharedCollection,
      ...(film.people ?? [])
    );
  }

  protected selectedPerson(): void {
    //
  }

  protected loadRelationshipsOptions(): void {
    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, ...(this.film?.people ?? []))))
      .subscribe((people: IPerson[]) => {
        this.peopleSharedCollection = people;
        this.film?.people?.forEach(selectedPerson => {
          this.peopleSharedCollection.forEach(person => {
            if (person.id === selectedPerson.id) {
              this.selectedPeople.push(person);
            }
          });
        });
      });
  }
}
