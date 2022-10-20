package com.anthonylldev.streaming.repository;

import com.anthonylldev.streaming.domain.Episode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Episode entity.
 */
@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long>, JpaSpecificationExecutor<Episode> {
    default Optional<Episode> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Episode> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Episode> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct episode from Episode episode left join fetch episode.film",
        countQuery = "select count(distinct episode) from Episode episode"
    )
    Page<Episode> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct episode from Episode episode left join fetch episode.film")
    List<Episode> findAllWithToOneRelationships();

    @Query("select episode from Episode episode left join fetch episode.film where episode.id =:id")
    Optional<Episode> findOneWithToOneRelationships(@Param("id") Long id);
}
