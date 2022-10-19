package com.anthonylldev.streaming.repository;

import com.anthonylldev.streaming.domain.Film;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class FilmRepositoryWithBagRelationshipsImpl implements FilmRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Film> fetchBagRelationships(Optional<Film> film) {
        return film.map(this::fetchPeople);
    }

    @Override
    public Page<Film> fetchBagRelationships(Page<Film> films) {
        return new PageImpl<>(fetchBagRelationships(films.getContent()), films.getPageable(), films.getTotalElements());
    }

    @Override
    public List<Film> fetchBagRelationships(List<Film> films) {
        return Optional.of(films).map(this::fetchPeople).orElse(Collections.emptyList());
    }

    Film fetchPeople(Film result) {
        return entityManager
            .createQuery("select film from Film film left join fetch film.people where film is :film", Film.class)
            .setParameter("film", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Film> fetchPeople(List<Film> films) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, films.size()).forEach(index -> order.put(films.get(index).getId(), index));
        List<Film> result = entityManager
            .createQuery("select distinct film from Film film left join fetch film.people where film in :films", Film.class)
            .setParameter("films", films)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
