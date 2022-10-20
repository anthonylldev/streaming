import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFilm } from '../film.model';
import { FilmService } from '../service/film.service';

@Injectable({ providedIn: 'root' })
export class FilmRoutingResolveService implements Resolve<IFilm | null> {
  constructor(protected service: FilmService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFilm | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((film: HttpResponse<IFilm>) => {
          if (film.body) {
            return of(film.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
