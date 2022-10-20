package com.anthonylldev.streaming.service;

import com.anthonylldev.streaming.domain.*; // for static metamodels
import com.anthonylldev.streaming.domain.Film;
import com.anthonylldev.streaming.repository.FilmRepository;
import com.anthonylldev.streaming.service.criteria.FilmCriteria;
import com.anthonylldev.streaming.service.dto.FilmDTO;
import com.anthonylldev.streaming.service.mapper.FilmMapper;
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
 * Service for executing complex queries for {@link Film} entities in the database.
 * The main input is a {@link FilmCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FilmDTO} or a {@link Page} of {@link FilmDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FilmQueryService extends QueryService<Film> {

    private final Logger log = LoggerFactory.getLogger(FilmQueryService.class);

    private final FilmRepository filmRepository;

    private final FilmMapper filmMapper;

    public FilmQueryService(FilmRepository filmRepository, FilmMapper filmMapper) {
        this.filmRepository = filmRepository;
        this.filmMapper = filmMapper;
    }

    /**
     * Return a {@link List} of {@link FilmDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FilmDTO> findByCriteria(FilmCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Film> specification = createSpecification(criteria);
        return filmMapper.toDto(filmRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FilmDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FilmDTO> findByCriteria(FilmCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Film> specification = createSpecification(criteria);
        return filmRepository.findAll(specification, page).map(filmMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FilmCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Film> specification = createSpecification(criteria);
        return filmRepository.count(specification);
    }

    /**
     * Function to convert {@link FilmCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Film> createSpecification(FilmCriteria criteria) {
        Specification<Film> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Film_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Film_.title));
            }
            if (criteria.getSynopsis() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSynopsis(), Film_.synopsis));
            }
            if (criteria.getViews() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getViews(), Film_.views));
            }
            if (criteria.getReviews() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReviews(), Film_.reviews));
            }
            if (criteria.getGender() != null) {
                specification = specification.and(buildSpecification(criteria.getGender(), Film_.gender));
            }
            if (criteria.getFilmType() != null) {
                specification = specification.and(buildSpecification(criteria.getFilmType(), Film_.filmType));
            }
            if (criteria.getOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrder(), Film_.order));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Film_.url));
            }
            if (criteria.getPersonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPersonId(), root -> root.join(Film_.people, JoinType.LEFT).get(Person_.id))
                    );
            }
            if (criteria.getEpisodesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEpisodesId(), root -> root.join(Film_.episodes, JoinType.LEFT).get(Episode_.id))
                    );
            }
        }
        return specification;
    }
}
