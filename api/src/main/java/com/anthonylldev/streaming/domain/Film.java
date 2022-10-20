package com.anthonylldev.streaming.domain;

import com.anthonylldev.streaming.domain.enumeration.FilmType;
import com.anthonylldev.streaming.domain.enumeration.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Film.
 */
@Entity
@Table(name = "film")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Film implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "synopsis")
    private String synopsis;

    @Min(value = 0)
    @Column(name = "views")
    private Integer views;

    @Lob
    @Column(name = "cover")
    private byte[] cover;

    @Column(name = "cover_content_type")
    private String coverContentType;

    @Column(name = "reviews")
    private Long reviews;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "film_type")
    private FilmType filmType;

    @Min(value = 0)
    @Column(name = "jhi_order")
    private Integer order;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @ManyToMany
    @JoinTable(name = "rel_film__person", joinColumns = @JoinColumn(name = "film_id"), inverseJoinColumns = @JoinColumn(name = "person_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "films" }, allowSetters = true)
    private Set<Person> people = new HashSet<>();

    @OneToMany(mappedBy = "film")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "film" }, allowSetters = true)
    private Set<Episode> episodes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Film id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Film title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public Film synopsis(String synopsis) {
        this.setSynopsis(synopsis);
        return this;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Integer getViews() {
        return this.views;
    }

    public Film views(Integer views) {
        this.setViews(views);
        return this;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public byte[] getCover() {
        return this.cover;
    }

    public Film cover(byte[] cover) {
        this.setCover(cover);
        return this;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return this.coverContentType;
    }

    public Film coverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
        return this;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public Long getReviews() {
        return this.reviews;
    }

    public Film reviews(Long reviews) {
        this.setReviews(reviews);
        return this;
    }

    public void setReviews(Long reviews) {
        this.reviews = reviews;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Film gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public FilmType getFilmType() {
        return this.filmType;
    }

    public Film filmType(FilmType filmType) {
        this.setFilmType(filmType);
        return this;
    }

    public void setFilmType(FilmType filmType) {
        this.filmType = filmType;
    }

    public Integer getOrder() {
        return this.order;
    }

    public Film order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getUrl() {
        return this.url;
    }

    public Film url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Person> getPeople() {
        return this.people;
    }

    public void setPeople(Set<Person> people) {
        this.people = people;
    }

    public Film people(Set<Person> people) {
        this.setPeople(people);
        return this;
    }

    public Film addPerson(Person person) {
        this.people.add(person);
        person.getFilms().add(this);
        return this;
    }

    public Film removePerson(Person person) {
        this.people.remove(person);
        person.getFilms().remove(this);
        return this;
    }

    public Set<Episode> getEpisodes() {
        return this.episodes;
    }

    public void setEpisodes(Set<Episode> episodes) {
        if (this.episodes != null) {
            this.episodes.forEach(i -> i.setFilm(null));
        }
        if (episodes != null) {
            episodes.forEach(i -> i.setFilm(this));
        }
        this.episodes = episodes;
    }

    public Film episodes(Set<Episode> episodes) {
        this.setEpisodes(episodes);
        return this;
    }

    public Film addEpisodes(Episode episode) {
        this.episodes.add(episode);
        episode.setFilm(this);
        return this;
    }

    public Film removeEpisodes(Episode episode) {
        this.episodes.remove(episode);
        episode.setFilm(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Film)) {
            return false;
        }
        return id != null && id.equals(((Film) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Film{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", synopsis='" + getSynopsis() + "'" +
            ", views=" + getViews() +
            ", cover='" + getCover() + "'" +
            ", coverContentType='" + getCoverContentType() + "'" +
            ", reviews=" + getReviews() +
            ", gender='" + getGender() + "'" +
            ", filmType='" + getFilmType() + "'" +
            ", order=" + getOrder() +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
