package com.anthonylldev.streaming.service.criteria;

import com.anthonylldev.streaming.domain.enumeration.FilmType;
import com.anthonylldev.streaming.domain.enumeration.Gender;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.anthonylldev.streaming.domain.Film} entity. This class is used
 * in {@link com.anthonylldev.streaming.web.rest.FilmResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /films?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FilmCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Gender
     */
    public static class GenderFilter extends Filter<Gender> {

        public GenderFilter() {}

        public GenderFilter(GenderFilter filter) {
            super(filter);
        }

        @Override
        public GenderFilter copy() {
            return new GenderFilter(this);
        }
    }

    /**
     * Class for filtering FilmType
     */
    public static class FilmTypeFilter extends Filter<FilmType> {

        public FilmTypeFilter() {}

        public FilmTypeFilter(FilmTypeFilter filter) {
            super(filter);
        }

        @Override
        public FilmTypeFilter copy() {
            return new FilmTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter synopsis;

    private IntegerFilter views;

    private LongFilter reviews;

    private GenderFilter gender;

    private FilmTypeFilter filmType;

    private IntegerFilter order;

    private StringFilter url;

    private LongFilter personId;

    private LongFilter episodesId;

    private Boolean distinct;

    public FilmCriteria() {}

    public FilmCriteria(FilmCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.synopsis = other.synopsis == null ? null : other.synopsis.copy();
        this.views = other.views == null ? null : other.views.copy();
        this.reviews = other.reviews == null ? null : other.reviews.copy();
        this.gender = other.gender == null ? null : other.gender.copy();
        this.filmType = other.filmType == null ? null : other.filmType.copy();
        this.order = other.order == null ? null : other.order.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.personId = other.personId == null ? null : other.personId.copy();
        this.episodesId = other.episodesId == null ? null : other.episodesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FilmCriteria copy() {
        return new FilmCriteria(this);
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

    public IntegerFilter getViews() {
        return views;
    }

    public IntegerFilter views() {
        if (views == null) {
            views = new IntegerFilter();
        }
        return views;
    }

    public void setViews(IntegerFilter views) {
        this.views = views;
    }

    public LongFilter getReviews() {
        return reviews;
    }

    public LongFilter reviews() {
        if (reviews == null) {
            reviews = new LongFilter();
        }
        return reviews;
    }

    public void setReviews(LongFilter reviews) {
        this.reviews = reviews;
    }

    public GenderFilter getGender() {
        return gender;
    }

    public GenderFilter gender() {
        if (gender == null) {
            gender = new GenderFilter();
        }
        return gender;
    }

    public void setGender(GenderFilter gender) {
        this.gender = gender;
    }

    public FilmTypeFilter getFilmType() {
        return filmType;
    }

    public FilmTypeFilter filmType() {
        if (filmType == null) {
            filmType = new FilmTypeFilter();
        }
        return filmType;
    }

    public void setFilmType(FilmTypeFilter filmType) {
        this.filmType = filmType;
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

    public StringFilter getUrl() {
        return url;
    }

    public StringFilter url() {
        if (url == null) {
            url = new StringFilter();
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public LongFilter getPersonId() {
        return personId;
    }

    public LongFilter personId() {
        if (personId == null) {
            personId = new LongFilter();
        }
        return personId;
    }

    public void setPersonId(LongFilter personId) {
        this.personId = personId;
    }

    public LongFilter getEpisodesId() {
        return episodesId;
    }

    public LongFilter episodesId() {
        if (episodesId == null) {
            episodesId = new LongFilter();
        }
        return episodesId;
    }

    public void setEpisodesId(LongFilter episodesId) {
        this.episodesId = episodesId;
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
        final FilmCriteria that = (FilmCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(synopsis, that.synopsis) &&
            Objects.equals(views, that.views) &&
            Objects.equals(reviews, that.reviews) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(filmType, that.filmType) &&
            Objects.equals(order, that.order) &&
            Objects.equals(url, that.url) &&
            Objects.equals(personId, that.personId) &&
            Objects.equals(episodesId, that.episodesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, synopsis, views, reviews, gender, filmType, order, url, personId, episodesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilmCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (synopsis != null ? "synopsis=" + synopsis + ", " : "") +
            (views != null ? "views=" + views + ", " : "") +
            (reviews != null ? "reviews=" + reviews + ", " : "") +
            (gender != null ? "gender=" + gender + ", " : "") +
            (filmType != null ? "filmType=" + filmType + ", " : "") +
            (order != null ? "order=" + order + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (personId != null ? "personId=" + personId + ", " : "") +
            (episodesId != null ? "episodesId=" + episodesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
