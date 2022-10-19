package com.anthonylldev.streaming.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.anthonylldev.streaming.domain.Episode} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EpisodeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3)
    private String title;

    private String synopsis;

    private Integer order;

    private FilmDTO film;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public FilmDTO getFilm() {
        return film;
    }

    public void setFilm(FilmDTO film) {
        this.film = film;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EpisodeDTO)) {
            return false;
        }

        EpisodeDTO episodeDTO = (EpisodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, episodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EpisodeDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", synopsis='" + getSynopsis() + "'" +
            ", order=" + getOrder() +
            ", film=" + getFilm() +
            "}";
    }
}
