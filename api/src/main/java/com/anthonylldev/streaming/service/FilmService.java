package com.anthonylldev.streaming.service;

import com.anthonylldev.streaming.service.dto.FilmDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.anthonylldev.streaming.domain.Film}.
 */
public interface FilmService {
    /**
     * Save a film.
     *
     * @param filmDTO the entity to save.
     * @return the persisted entity.
     */
    FilmDTO save(FilmDTO filmDTO);

    /**
     * Updates a film.
     *
     * @param filmDTO the entity to update.
     * @return the persisted entity.
     */
    FilmDTO update(FilmDTO filmDTO);

    /**
     * Partially updates a film.
     *
     * @param filmDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FilmDTO> partialUpdate(FilmDTO filmDTO);

    /**
     * Get all the films.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FilmDTO> findAll(Pageable pageable);

    /**
     * Get all the films with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FilmDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" film.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FilmDTO> findOne(Long id);

    /**
     * Delete the "id" film.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
