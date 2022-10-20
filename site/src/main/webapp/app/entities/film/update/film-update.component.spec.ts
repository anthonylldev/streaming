import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FilmFormService } from './film-form.service';
import { FilmService } from '../service/film.service';
import { IFilm } from '../film.model';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';

import { FilmUpdateComponent } from './film-update.component';

describe('Film Management Update Component', () => {
  let comp: FilmUpdateComponent;
  let fixture: ComponentFixture<FilmUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let filmFormService: FilmFormService;
  let filmService: FilmService;
  let personService: PersonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FilmUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FilmUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FilmUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    filmFormService = TestBed.inject(FilmFormService);
    filmService = TestBed.inject(FilmService);
    personService = TestBed.inject(PersonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Person query and add missing value', () => {
      const film: IFilm = { id: 456 };
      const people: IPerson[] = [{ id: 12176 }];
      film.people = people;

      const personCollection: IPerson[] = [{ id: 47390 }];
      jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const additionalPeople = [...people];
      const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
      jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ film });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(
        personCollection,
        ...additionalPeople.map(expect.objectContaining)
      );
      expect(comp.peopleSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const film: IFilm = { id: 456 };
      const person: IPerson = { id: 28267 };
      film.people = [person];

      activatedRoute.data = of({ film });
      comp.ngOnInit();

      expect(comp.peopleSharedCollection).toContain(person);
      expect(comp.film).toEqual(film);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFilm>>();
      const film = { id: 123 };
      jest.spyOn(filmFormService, 'getFilm').mockReturnValue(film);
      jest.spyOn(filmService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ film });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: film }));
      saveSubject.complete();

      // THEN
      expect(filmFormService.getFilm).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(filmService.update).toHaveBeenCalledWith(expect.objectContaining(film));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFilm>>();
      const film = { id: 123 };
      jest.spyOn(filmFormService, 'getFilm').mockReturnValue({ id: null });
      jest.spyOn(filmService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ film: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: film }));
      saveSubject.complete();

      // THEN
      expect(filmFormService.getFilm).toHaveBeenCalled();
      expect(filmService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFilm>>();
      const film = { id: 123 };
      jest.spyOn(filmService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ film });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(filmService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePerson', () => {
      it('Should forward to personService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(personService, 'comparePerson');
        comp.comparePerson(entity, entity2);
        expect(personService.comparePerson).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
