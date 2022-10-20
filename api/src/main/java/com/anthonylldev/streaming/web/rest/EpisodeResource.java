package com.anthonylldev.streaming.web.rest;

import com.anthonylldev.streaming.repository.EpisodeRepository;
import com.anthonylldev.streaming.service.EpisodeQueryService;
import com.anthonylldev.streaming.service.EpisodeService;
import com.anthonylldev.streaming.service.criteria.EpisodeCriteria;
import com.anthonylldev.streaming.service.dto.EpisodeDTO;
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
 * REST controller for managing {@link com.anthonylldev.streaming.domain.Episode}.
 */
@RestController
@RequestMapping("/api")
public class EpisodeResource {

    private final Logger log = LoggerFactory.getLogger(EpisodeResource.class);

    private static final String ENTITY_NAME = "episode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EpisodeService episodeService;

    private final EpisodeRepository episodeRepository;

    private final EpisodeQueryService episodeQueryService;

    public EpisodeResource(EpisodeService episodeService, EpisodeRepository episodeRepository, EpisodeQueryService episodeQueryService) {
        this.episodeService = episodeService;
        this.episodeRepository = episodeRepository;
        this.episodeQueryService = episodeQueryService;
    }

    /**
     * {@code POST  /episodes} : Create a new episode.
     *
     * @param episodeDTO the episodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new episodeDTO, or with status {@code 400 (Bad Request)} if the episode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/episodes")
    public ResponseEntity<EpisodeDTO> createEpisode(@Valid @RequestBody EpisodeDTO episodeDTO) throws URISyntaxException {
        log.debug("REST request to save Episode : {}", episodeDTO);
        if (episodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new episode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EpisodeDTO result = episodeService.save(episodeDTO);
        return ResponseEntity
            .created(new URI("/api/episodes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /episodes/:id} : Updates an existing episode.
     *
     * @param id the id of the episodeDTO to save.
     * @param episodeDTO the episodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated episodeDTO,
     * or with status {@code 400 (Bad Request)} if the episodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the episodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/episodes/{id}")
    public ResponseEntity<EpisodeDTO> updateEpisode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EpisodeDTO episodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Episode : {}, {}", id, episodeDTO);
        if (episodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, episodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!episodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EpisodeDTO result = episodeService.update(episodeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, episodeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /episodes/:id} : Partial updates given fields of an existing episode, field will ignore if it is null
     *
     * @param id the id of the episodeDTO to save.
     * @param episodeDTO the episodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated episodeDTO,
     * or with status {@code 400 (Bad Request)} if the episodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the episodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the episodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/episodes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EpisodeDTO> partialUpdateEpisode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EpisodeDTO episodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Episode partially : {}, {}", id, episodeDTO);
        if (episodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, episodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!episodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EpisodeDTO> result = episodeService.partialUpdate(episodeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, episodeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /episodes} : get all the episodes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of episodes in body.
     */
    @GetMapping("/episodes")
    public ResponseEntity<List<EpisodeDTO>> getAllEpisodes(
        EpisodeCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Episodes by criteria: {}", criteria);
        Page<EpisodeDTO> page = episodeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /episodes/count} : count all the episodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/episodes/count")
    public ResponseEntity<Long> countEpisodes(EpisodeCriteria criteria) {
        log.debug("REST request to count Episodes by criteria: {}", criteria);
        return ResponseEntity.ok().body(episodeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /episodes/:id} : get the "id" episode.
     *
     * @param id the id of the episodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the episodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/episodes/{id}")
    public ResponseEntity<EpisodeDTO> getEpisode(@PathVariable Long id) {
        log.debug("REST request to get Episode : {}", id);
        Optional<EpisodeDTO> episodeDTO = episodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(episodeDTO);
    }

    /**
     * {@code DELETE  /episodes/:id} : delete the "id" episode.
     *
     * @param id the id of the episodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/episodes/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable Long id) {
        log.debug("REST request to delete Episode : {}", id);
        episodeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
