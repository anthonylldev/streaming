package com.anthonylldev.streaming.repository;

import com.anthonylldev.streaming.domain.Film;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface FilmRepositoryWithBagRelationships {
    Optional<Film> fetchBagRelationships(Optional<Film> film);

    List<Film> fetchBagRelationships(List<Film> films);

    Page<Film> fetchBagRelationships(Page<Film> films);
}
