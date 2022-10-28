import { Component, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEpisode } from '../episode.model';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { EntityArrayResponseType, EpisodeService } from '../service/episode.service';
import { EpisodeDeleteDialogComponent } from '../delete/episode-delete-dialog.component';
import { FilterOptions, IFilterOptions, IFilterOption } from 'app/shared/filter/filter.model';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-episode',
  templateUrl: './episode.component.html',
})
export class EpisodeComponent implements OnInit {
  episodes?: IEpisode[];
  selectedEpisodes?: IEpisode[];
  isLoading = false;

  predicate = 'id';
  ascending = true;
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  titleFilter = '';

  constructor(
    protected episodeService: EpisodeService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected modalService: NgbModal,
    protected accountService: AccountService
  ) {}

  trackId = (_index: number, item: IEpisode): number => this.episodeService.getEpisodeIdentifier(item);

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(auth => {
      if (auth?.authorities[0] === 'ROLE_ADMIN') {
        this.load();
        this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.predicate, this.ascending, filterOptions));
      } else {
        // load when user have role user
      }
    });
  }

  clearFilters(): void {
    this.filters.clear();
    this.titleFilter = '';
  }

  filterByTitle($event: string): void {
    const titleFilterOption = this.filters.getFilterOptionByName('title.contains');

    if (titleFilterOption) {
      this.filters.removeFilter('title.contains');
    }

    if ($event.length >= 3) {
      this.filters.addFilter('title.contains', ...[$event]);
    }
  }

  delete(episode: IEpisode): void {
    const modalRef = this.modalService.open(EpisodeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });

    // ? The episode is wrapped in a list
    const episodeAux: IEpisode[] = [episode];

    modalRef.componentInstance.episodes = episodeAux;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations())
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  deleteSelectedFilms(): void {
    if (this.selectedEpisodes?.length === 1) {
      this.delete(this.selectedEpisodes[0]);
    } else {
      const modalRef = this.modalService.open(EpisodeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
      modalRef.componentInstance.episodes = this.selectedEpisodes;
      // unsubscribe not needed because closed completes on modal close
      modalRef.closed
        .pipe(
          filter(reason => reason === ITEM_DELETED_EVENT),
          switchMap(() => this.loadFromBackendWithRouteInformations())
        )
        .subscribe({
          next: (res: EntityArrayResponseType) => {
            this.onResponseSuccess(res);
          },
        });
    }
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page, this.predicate, this.ascending, this.filters.filterOptions);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending, this.filters.filterOptions);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending, this.filters.filterOptions))
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    this.filters.initializeFromParams(params);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.episodes = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IEpisode[] | null): IEpisode[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(
    page?: number,
    predicate?: string,
    ascending?: boolean,
    filterOptions?: IFilterOption[]
  ): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      eagerload: true,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    filterOptions?.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    return this.episodeService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
