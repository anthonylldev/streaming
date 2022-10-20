package com.anthonylldev.streaming.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.anthonylldev.streaming.domain.Episode} entity. This class is used
 * in {@link com.anthonylldev.streaming.web.rest.EpisodeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /episodes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EpisodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter synopsis;

    private IntegerFilter order;

    private LongFilter filmId;

    private Boolean distinct;

    public EpisodeCriteria() {}

    public EpisodeCriteria(EpisodeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.synopsis = other.synopsis == null ? null : other.synopsis.copy();
        this.order = other.order == null ? null : other.order.copy();
        this.filmId = other.filmId == null ? null : other.filmId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EpisodeCriteria copy() {
        return new EpisodeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getSynopsis() {
        return synopsis;
    }

    public StringFilter synopsis() {
        if (synopsis == null) {
            synopsis = new StringFilter();
        }
        return synopsis;
    }

    public void setSynopsis(StringFilter synopsis) {
        this.synopsis = synopsis;
    }

    public IntegerFilter getOrder() {
        return order;
    }

    public IntegerFilter order() {
        if (order == null) {
            order = new IntegerFilter();
        }
        return order;
    }

    public void setOrder(IntegerFilter order) {
        this.order = order;
    }

    public LongFilter getFilmId() {
        return filmId;
    }

    public LongFilter filmId() {
        if (filmId == null) {
            filmId = new LongFilter();
        }
        return filmId;
    }

    public void setFilmId(LongFilter filmId) {
        this.filmId = filmId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EpisodeCriteria that = (EpisodeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(synopsis, that.synopsis) &&
            Objects.equals(order, that.order) &&
            Objects.equals(filmId, that.filmId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, synopsis, order, filmId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EpisodeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (synopsis != null ? "synopsis=" + synopsis + ", " : "") +
            (order != null ? "order=" + order + ", " : "") +
            (filmId != null ? "filmId=" + filmId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
