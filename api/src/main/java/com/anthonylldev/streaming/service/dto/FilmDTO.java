package com.anthonylldev.streaming.service.dto;

import com.anthonylldev.streaming.domain.enumeration.FilmType;
import com.anthonylldev.streaming.domain.enumeration.Gender;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.anthonylldev.streaming.domain.Film} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FilmDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3)
    private String title;

    private String synopsis;

    @Min(value = 0)
    private Integer views;

    @Lob
    private byte[] cover;

    private String coverContentType;
    private Long reviews;

    private Gender gender;

    private FilmType filmType;

    @Min(value = 0)
    private Integer order;

    @NotNull
    private String url;

    private Set<PersonDTO> people = new HashSet<>();

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

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return coverContentType;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public Long getReviews() {
        return reviews;
    }

    public void setReviews(Long reviews) {
        this.reviews = reviews;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public FilmType getFilmType() {
        return filmType;
    }

    public void setFilmType(FilmType filmType) {
        this.filmType = filmType;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<PersonDTO> getPeople() {
        return people;
    }

    public void setPeople(Set<PersonDTO> people) {
        this.people = people;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilmDTO)) {
            return false;
        }

        FilmDTO filmDTO = (FilmDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, filmDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilmDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", synopsis='" + getSynopsis() + "'" +
            ", views=" + getViews() +
            ", cover='" + getCover() + "'" +
            ", reviews=" + getReviews() +
            ", gender='" + getGender() + "'" +
            ", filmType='" + getFilmType() + "'" +
            ", order=" + getOrder() +
            ", url='" + getUrl() + "'" +
            ", people=" + getPeople() +
            "}";
    }
}
