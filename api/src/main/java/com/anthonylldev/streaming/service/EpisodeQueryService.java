package com.anthonylldev.streaming.service;

import com.anthonylldev.streaming.domain.*; // for static metamodels
import com.anthonylldev.streaming.domain.Episode;
import com.anthonylldev.streaming.repository.EpisodeRepository;
import com.anthonylldev.streaming.service.criteria.EpisodeCriteria;
import com.anthonylldev.streaming.service.dto.EpisodeDTO;
import com.anthonylldev.streaming.service.mapper.EpisodeMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Episode} entities in the database.
 * The main input is a {@link EpisodeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EpisodeDTO} or a {@link Page} of {@link EpisodeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EpisodeQueryService extends QueryService<Episode> {

    private final Logger log = LoggerFactory.getLogger(EpisodeQueryService.class);

    private final EpisodeRepository episodeRepository;

    private final EpisodeMapper episodeMapper;

    public EpisodeQueryService(EpisodeRepository episodeRepository, EpisodeMapper episodeMapper) {
        this.episodeRepository = episodeRepository;
        this.episodeMapper = episodeMapper;
    }

    /**
     * Return a {@link List} of {@link EpisodeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EpisodeDTO> findByCriteria(EpisodeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Episode> specification = createSpecification(criteria);
        return episodeMapper.toDto(episodeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EpisodeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EpisodeDTO> findByCriteria(EpisodeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Episode> specification = createSpecification(criteria);
        return episodeRepository.findAll(specification, page).map(episodeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EpisodeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Episode> specification = createSpecification(criteria);
        return episodeRepository.count(specification);
    }

    /**
     * Function to convert {@link EpisodeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Episode> createSpecification(EpisodeCriteria criteria) {
        Specification<Episode> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Episode_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Episode_.title));
            }
            if (criteria.getSynopsis() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSynopsis(), Episode_.synopsis));
            }
            if (criteria.getOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrder(), Episode_.order));
            }
            if (criteria.getFilmId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getFilmId(), root -> root.join(Episode_.film, JoinType.LEFT).get(Film_.id))
                    );
            }
        }
        return specification;
    }
}
