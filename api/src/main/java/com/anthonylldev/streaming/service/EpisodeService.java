package com.anthonylldev.streaming.service;

import com.anthonylldev.streaming.service.dto.EpisodeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.anthonylldev.streaming.domain.Episode}.
 */
public interface EpisodeService {
    /**
     * Save a episode.
     *
     * @param episodeDTO the entity to save.
     * @return the persisted entity.
     */
    EpisodeDTO save(EpisodeDTO episodeDTO);

    /**
     * Updates a episode.
     *
     * @param episodeDTO the entity to update.
     * @return the persisted entity.
     */
    EpisodeDTO update(EpisodeDTO episodeDTO);

    /**
     * Partially updates a episode.
     *
     * @param episodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EpisodeDTO> partialUpdate(EpisodeDTO episodeDTO);

    /**
     * Get all the episodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EpisodeDTO> findAll(Pageable pageable);

    /**
     * Get all the episodes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EpisodeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" episode.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EpisodeDTO> findOne(Long id);

    /**
     * Delete the "id" episode.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
