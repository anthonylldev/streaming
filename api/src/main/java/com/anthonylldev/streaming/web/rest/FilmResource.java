package com.anthonylldev.streaming.web.rest;

import com.anthonylldev.streaming.repository.FilmRepository;
import com.anthonylldev.streaming.service.FilmQueryService;
import com.anthonylldev.streaming.service.FilmService;
import com.anthonylldev.streaming.service.criteria.FilmCriteria;
import com.anthonylldev.streaming.service.dto.FilmDTO;
import com.anthonylldev.streaming.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.anthonylldev.streaming.domain.Film}.
 */
@RestController
@RequestMapping("/api")
public class FilmResource {

    private final Logger log = LoggerFactory.getLogger(FilmResource.class);

    private static final String ENTITY_NAME = "film";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FilmService filmService;

    private final FilmRepository filmRepository;

    private final FilmQueryService filmQueryService;

    public FilmResource(FilmService filmService, FilmRepository filmRepository, FilmQueryService filmQueryService) {
        this.filmService = filmService;
        this.filmRepository = filmRepository;
        this.filmQueryService = filmQueryService;
    }

    /**
     * {@code POST  /films} : Create a new film.
     *
     * @param filmDTO the filmDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new filmDTO, or with status {@code 400 (Bad Request)} if the film has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/films")
    public ResponseEntity<FilmDTO> createFilm(@Valid @RequestBody FilmDTO filmDTO) throws URISyntaxException {
        log.debug("REST request to save Film : {}", filmDTO);
        if (filmDTO.getId() != null) {
            throw new BadRequestAlertException("A new film cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FilmDTO result = filmService.save(filmDTO);
        return ResponseEntity
            .created(new URI("/api/films/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /films/:id} : Updates an existing film.
     *
     * @param id the id of the filmDTO to save.
     * @param filmDTO the filmDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmDTO,
     * or with status {@code 400 (Bad Request)} if the filmDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the filmDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/films/{id}")
    public ResponseEntity<FilmDTO> updateFilm(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FilmDTO filmDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Film : {}, {}", id, filmDTO);
        if (filmDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!filmRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FilmDTO result = filmService.update(filmDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, filmDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /films/:id} : Partial updates given fields of an existing film, field will ignore if it is null
     *
     * @param id the id of the filmDTO to save.
     * @param filmDTO the filmDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmDTO,
     * or with status {@code 400 (Bad Request)} if the filmDTO is not valid,
     * or with status {@code 404 (Not Found)} if the filmDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the filmDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/films/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FilmDTO> partialUpdateFilm(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FilmDTO filmDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Film partially : {}, {}", id, filmDTO);
        if (filmDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!filmRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FilmDTO> result = filmService.partialUpdate(filmDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, filmDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /films} : get all the films.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of films in body.
     */
    @GetMapping("/films")
    public ResponseEntity<List<FilmDTO>> getAllFilms(
        FilmCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Films by criteria: {}", criteria);
        Page<FilmDTO> page = filmQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /films/count} : count all the films.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/films/count")
    public ResponseEntity<Long> countFilms(FilmCriteria criteria) {
        log.debug("REST request to count Films by criteria: {}", criteria);
        return ResponseEntity.ok().body(filmQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /films/:id} : get the "id" film.
     *
     * @param id the id of the filmDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the filmDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/films/{id}")
    public ResponseEntity<FilmDTO> getFilm(@PathVariable Long id) {
        log.debug("REST request to get Film : {}", id);
        Optional<FilmDTO> filmDTO = filmService.findOne(id);
        return ResponseUtil.wrapOrNotFound(filmDTO);
    }

    /**
     * {@code DELETE  /films/:id} : delete the "id" film.
     *
     * @param id the id of the filmDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/films/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        log.debug("REST request to delete Film : {}", id);
        filmService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
