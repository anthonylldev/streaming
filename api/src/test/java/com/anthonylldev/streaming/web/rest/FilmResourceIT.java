package com.anthonylldev.streaming.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.anthonylldev.streaming.IntegrationTest;
import com.anthonylldev.streaming.domain.Episode;
import com.anthonylldev.streaming.domain.Film;
import com.anthonylldev.streaming.domain.Person;
import com.anthonylldev.streaming.domain.enumeration.FilmType;
import com.anthonylldev.streaming.domain.enumeration.Gender;
import com.anthonylldev.streaming.repository.FilmRepository;
import com.anthonylldev.streaming.service.FilmService;
import com.anthonylldev.streaming.service.criteria.FilmCriteria;
import com.anthonylldev.streaming.service.dto.FilmDTO;
import com.anthonylldev.streaming.service.mapper.FilmMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link FilmResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FilmResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SYNOPSIS = "AAAAAAAAAA";
    private static final String UPDATED_SYNOPSIS = "BBBBBBBBBB";

    private static final Integer DEFAULT_VIEWS = 0;
    private static final Integer UPDATED_VIEWS = 1;
    private static final Integer SMALLER_VIEWS = 0 - 1;

    private static final byte[] DEFAULT_COVER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COVER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COVER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COVER_CONTENT_TYPE = "image/png";

    private static final Long DEFAULT_REVIEWS = 1L;
    private static final Long UPDATED_REVIEWS = 2L;
    private static final Long SMALLER_REVIEWS = 1L - 1L;

    private static final Gender DEFAULT_GENDER = Gender.COMEDY;
    private static final Gender UPDATED_GENDER = Gender.DRAMA;

    private static final FilmType DEFAULT_FILM_TYPE = FilmType.MOVIE;
    private static final FilmType UPDATED_FILM_TYPE = FilmType.DOCUMENTARY;

    private static final Integer DEFAULT_ORDER = 0;
    private static final Integer UPDATED_ORDER = 1;
    private static final Integer SMALLER_ORDER = 0 - 1;

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/films";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FilmRepository filmRepository;

    @Mock
    private FilmRepository filmRepositoryMock;

    @Autowired
    private FilmMapper filmMapper;

    @Mock
    private FilmService filmServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFilmMockMvc;

    private Film film;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Film createEntity(EntityManager em) {
        Film film = new Film()
            .title(DEFAULT_TITLE)
            .synopsis(DEFAULT_SYNOPSIS)
            .views(DEFAULT_VIEWS)
            .cover(DEFAULT_COVER)
            .coverContentType(DEFAULT_COVER_CONTENT_TYPE)
            .reviews(DEFAULT_REVIEWS)
            .gender(DEFAULT_GENDER)
            .filmType(DEFAULT_FILM_TYPE)
            .order(DEFAULT_ORDER)
            .url(DEFAULT_URL);
        return film;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Film createUpdatedEntity(EntityManager em) {
        Film film = new Film()
            .title(UPDATED_TITLE)
            .synopsis(UPDATED_SYNOPSIS)
            .views(UPDATED_VIEWS)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .reviews(UPDATED_REVIEWS)
            .gender(UPDATED_GENDER)
            .filmType(UPDATED_FILM_TYPE)
            .order(UPDATED_ORDER)
            .url(UPDATED_URL);
        return film;
    }

    @BeforeEach
    public void initTest() {
        film = createEntity(em);
    }

    @Test
    @Transactional
    void createFilm() throws Exception {
        int databaseSizeBeforeCreate = filmRepository.findAll().size();
        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);
        restFilmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(filmDTO)))
            .andExpect(status().isCreated());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeCreate + 1);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFilm.getSynopsis()).isEqualTo(DEFAULT_SYNOPSIS);
        assertThat(testFilm.getViews()).isEqualTo(DEFAULT_VIEWS);
        assertThat(testFilm.getCover()).isEqualTo(DEFAULT_COVER);
        assertThat(testFilm.getCoverContentType()).isEqualTo(DEFAULT_COVER_CONTENT_TYPE);
        assertThat(testFilm.getReviews()).isEqualTo(DEFAULT_REVIEWS);
        assertThat(testFilm.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testFilm.getFilmType()).isEqualTo(DEFAULT_FILM_TYPE);
        assertThat(testFilm.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testFilm.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void createFilmWithExistingId() throws Exception {
        // Create the Film with an existing ID
        film.setId(1L);
        FilmDTO filmDTO = filmMapper.toDto(film);

        int databaseSizeBeforeCreate = filmRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFilmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(filmDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().size();
        // set the field null
        film.setTitle(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        restFilmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(filmDTO)))
            .andExpect(status().isBadRequest());

        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().size();
        // set the field null
        film.setUrl(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        restFilmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(filmDTO)))
            .andExpect(status().isBadRequest());

        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFilms() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList
        restFilmMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(film.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].synopsis").value(hasItem(DEFAULT_SYNOPSIS)))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))))
            .andExpect(jsonPath("$.[*].reviews").value(hasItem(DEFAULT_REVIEWS.intValue())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].filmType").value(hasItem(DEFAULT_FILM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFilmsWithEagerRelationshipsIsEnabled() throws Exception {
        when(filmServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFilmMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(filmServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFilmsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(filmServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFilmMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(filmRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFilm() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get the film
        restFilmMockMvc
            .perform(get(ENTITY_API_URL_ID, film.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(film.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.synopsis").value(DEFAULT_SYNOPSIS))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS))
            .andExpect(jsonPath("$.coverContentType").value(DEFAULT_COVER_CONTENT_TYPE))
            .andExpect(jsonPath("$.cover").value(Base64Utils.encodeToString(DEFAULT_COVER)))
            .andExpect(jsonPath("$.reviews").value(DEFAULT_REVIEWS.intValue()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.filmType").value(DEFAULT_FILM_TYPE.toString()))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getFilmsByIdFiltering() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        Long id = film.getId();

        defaultFilmShouldBeFound("id.equals=" + id);
        defaultFilmShouldNotBeFound("id.notEquals=" + id);

        defaultFilmShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFilmShouldNotBeFound("id.greaterThan=" + id);

        defaultFilmShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFilmShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFilmsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where title equals to DEFAULT_TITLE
        defaultFilmShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the filmList where title equals to UPDATED_TITLE
        defaultFilmShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFilmsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultFilmShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the filmList where title equals to UPDATED_TITLE
        defaultFilmShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFilmsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where title is not null
        defaultFilmShouldBeFound("title.specified=true");

        // Get all the filmList where title is null
        defaultFilmShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsByTitleContainsSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where title contains DEFAULT_TITLE
        defaultFilmShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the filmList where title contains UPDATED_TITLE
        defaultFilmShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFilmsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where title does not contain DEFAULT_TITLE
        defaultFilmShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the filmList where title does not contain UPDATED_TITLE
        defaultFilmShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFilmsBySynopsisIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where synopsis equals to DEFAULT_SYNOPSIS
        defaultFilmShouldBeFound("synopsis.equals=" + DEFAULT_SYNOPSIS);

        // Get all the filmList where synopsis equals to UPDATED_SYNOPSIS
        defaultFilmShouldNotBeFound("synopsis.equals=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllFilmsBySynopsisIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where synopsis in DEFAULT_SYNOPSIS or UPDATED_SYNOPSIS
        defaultFilmShouldBeFound("synopsis.in=" + DEFAULT_SYNOPSIS + "," + UPDATED_SYNOPSIS);

        // Get all the filmList where synopsis equals to UPDATED_SYNOPSIS
        defaultFilmShouldNotBeFound("synopsis.in=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllFilmsBySynopsisIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where synopsis is not null
        defaultFilmShouldBeFound("synopsis.specified=true");

        // Get all the filmList where synopsis is null
        defaultFilmShouldNotBeFound("synopsis.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsBySynopsisContainsSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where synopsis contains DEFAULT_SYNOPSIS
        defaultFilmShouldBeFound("synopsis.contains=" + DEFAULT_SYNOPSIS);

        // Get all the filmList where synopsis contains UPDATED_SYNOPSIS
        defaultFilmShouldNotBeFound("synopsis.contains=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllFilmsBySynopsisNotContainsSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where synopsis does not contain DEFAULT_SYNOPSIS
        defaultFilmShouldNotBeFound("synopsis.doesNotContain=" + DEFAULT_SYNOPSIS);

        // Get all the filmList where synopsis does not contain UPDATED_SYNOPSIS
        defaultFilmShouldBeFound("synopsis.doesNotContain=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllFilmsByViewsIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where views equals to DEFAULT_VIEWS
        defaultFilmShouldBeFound("views.equals=" + DEFAULT_VIEWS);

        // Get all the filmList where views equals to UPDATED_VIEWS
        defaultFilmShouldNotBeFound("views.equals=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByViewsIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where views in DEFAULT_VIEWS or UPDATED_VIEWS
        defaultFilmShouldBeFound("views.in=" + DEFAULT_VIEWS + "," + UPDATED_VIEWS);

        // Get all the filmList where views equals to UPDATED_VIEWS
        defaultFilmShouldNotBeFound("views.in=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByViewsIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where views is not null
        defaultFilmShouldBeFound("views.specified=true");

        // Get all the filmList where views is null
        defaultFilmShouldNotBeFound("views.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsByViewsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where views is greater than or equal to DEFAULT_VIEWS
        defaultFilmShouldBeFound("views.greaterThanOrEqual=" + DEFAULT_VIEWS);

        // Get all the filmList where views is greater than or equal to UPDATED_VIEWS
        defaultFilmShouldNotBeFound("views.greaterThanOrEqual=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByViewsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where views is less than or equal to DEFAULT_VIEWS
        defaultFilmShouldBeFound("views.lessThanOrEqual=" + DEFAULT_VIEWS);

        // Get all the filmList where views is less than or equal to SMALLER_VIEWS
        defaultFilmShouldNotBeFound("views.lessThanOrEqual=" + SMALLER_VIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByViewsIsLessThanSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where views is less than DEFAULT_VIEWS
        defaultFilmShouldNotBeFound("views.lessThan=" + DEFAULT_VIEWS);

        // Get all the filmList where views is less than UPDATED_VIEWS
        defaultFilmShouldBeFound("views.lessThan=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByViewsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where views is greater than DEFAULT_VIEWS
        defaultFilmShouldNotBeFound("views.greaterThan=" + DEFAULT_VIEWS);

        // Get all the filmList where views is greater than SMALLER_VIEWS
        defaultFilmShouldBeFound("views.greaterThan=" + SMALLER_VIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByReviewsIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where reviews equals to DEFAULT_REVIEWS
        defaultFilmShouldBeFound("reviews.equals=" + DEFAULT_REVIEWS);

        // Get all the filmList where reviews equals to UPDATED_REVIEWS
        defaultFilmShouldNotBeFound("reviews.equals=" + UPDATED_REVIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByReviewsIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where reviews in DEFAULT_REVIEWS or UPDATED_REVIEWS
        defaultFilmShouldBeFound("reviews.in=" + DEFAULT_REVIEWS + "," + UPDATED_REVIEWS);

        // Get all the filmList where reviews equals to UPDATED_REVIEWS
        defaultFilmShouldNotBeFound("reviews.in=" + UPDATED_REVIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByReviewsIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where reviews is not null
        defaultFilmShouldBeFound("reviews.specified=true");

        // Get all the filmList where reviews is null
        defaultFilmShouldNotBeFound("reviews.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsByReviewsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where reviews is greater than or equal to DEFAULT_REVIEWS
        defaultFilmShouldBeFound("reviews.greaterThanOrEqual=" + DEFAULT_REVIEWS);

        // Get all the filmList where reviews is greater than or equal to UPDATED_REVIEWS
        defaultFilmShouldNotBeFound("reviews.greaterThanOrEqual=" + UPDATED_REVIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByReviewsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where reviews is less than or equal to DEFAULT_REVIEWS
        defaultFilmShouldBeFound("reviews.lessThanOrEqual=" + DEFAULT_REVIEWS);

        // Get all the filmList where reviews is less than or equal to SMALLER_REVIEWS
        defaultFilmShouldNotBeFound("reviews.lessThanOrEqual=" + SMALLER_REVIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByReviewsIsLessThanSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where reviews is less than DEFAULT_REVIEWS
        defaultFilmShouldNotBeFound("reviews.lessThan=" + DEFAULT_REVIEWS);

        // Get all the filmList where reviews is less than UPDATED_REVIEWS
        defaultFilmShouldBeFound("reviews.lessThan=" + UPDATED_REVIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByReviewsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where reviews is greater than DEFAULT_REVIEWS
        defaultFilmShouldNotBeFound("reviews.greaterThan=" + DEFAULT_REVIEWS);

        // Get all the filmList where reviews is greater than SMALLER_REVIEWS
        defaultFilmShouldBeFound("reviews.greaterThan=" + SMALLER_REVIEWS);
    }

    @Test
    @Transactional
    void getAllFilmsByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where gender equals to DEFAULT_GENDER
        defaultFilmShouldBeFound("gender.equals=" + DEFAULT_GENDER);

        // Get all the filmList where gender equals to UPDATED_GENDER
        defaultFilmShouldNotBeFound("gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllFilmsByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where gender in DEFAULT_GENDER or UPDATED_GENDER
        defaultFilmShouldBeFound("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER);

        // Get all the filmList where gender equals to UPDATED_GENDER
        defaultFilmShouldNotBeFound("gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllFilmsByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where gender is not null
        defaultFilmShouldBeFound("gender.specified=true");

        // Get all the filmList where gender is null
        defaultFilmShouldNotBeFound("gender.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsByFilmTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where filmType equals to DEFAULT_FILM_TYPE
        defaultFilmShouldBeFound("filmType.equals=" + DEFAULT_FILM_TYPE);

        // Get all the filmList where filmType equals to UPDATED_FILM_TYPE
        defaultFilmShouldNotBeFound("filmType.equals=" + UPDATED_FILM_TYPE);
    }

    @Test
    @Transactional
    void getAllFilmsByFilmTypeIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where filmType in DEFAULT_FILM_TYPE or UPDATED_FILM_TYPE
        defaultFilmShouldBeFound("filmType.in=" + DEFAULT_FILM_TYPE + "," + UPDATED_FILM_TYPE);

        // Get all the filmList where filmType equals to UPDATED_FILM_TYPE
        defaultFilmShouldNotBeFound("filmType.in=" + UPDATED_FILM_TYPE);
    }

    @Test
    @Transactional
    void getAllFilmsByFilmTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where filmType is not null
        defaultFilmShouldBeFound("filmType.specified=true");

        // Get all the filmList where filmType is null
        defaultFilmShouldNotBeFound("filmType.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where order equals to DEFAULT_ORDER
        defaultFilmShouldBeFound("order.equals=" + DEFAULT_ORDER);

        // Get all the filmList where order equals to UPDATED_ORDER
        defaultFilmShouldNotBeFound("order.equals=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllFilmsByOrderIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where order in DEFAULT_ORDER or UPDATED_ORDER
        defaultFilmShouldBeFound("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER);

        // Get all the filmList where order equals to UPDATED_ORDER
        defaultFilmShouldNotBeFound("order.in=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllFilmsByOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where order is not null
        defaultFilmShouldBeFound("order.specified=true");

        // Get all the filmList where order is null
        defaultFilmShouldNotBeFound("order.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsByOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where order is greater than or equal to DEFAULT_ORDER
        defaultFilmShouldBeFound("order.greaterThanOrEqual=" + DEFAULT_ORDER);

        // Get all the filmList where order is greater than or equal to UPDATED_ORDER
        defaultFilmShouldNotBeFound("order.greaterThanOrEqual=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllFilmsByOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where order is less than or equal to DEFAULT_ORDER
        defaultFilmShouldBeFound("order.lessThanOrEqual=" + DEFAULT_ORDER);

        // Get all the filmList where order is less than or equal to SMALLER_ORDER
        defaultFilmShouldNotBeFound("order.lessThanOrEqual=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllFilmsByOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where order is less than DEFAULT_ORDER
        defaultFilmShouldNotBeFound("order.lessThan=" + DEFAULT_ORDER);

        // Get all the filmList where order is less than UPDATED_ORDER
        defaultFilmShouldBeFound("order.lessThan=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllFilmsByOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where order is greater than DEFAULT_ORDER
        defaultFilmShouldNotBeFound("order.greaterThan=" + DEFAULT_ORDER);

        // Get all the filmList where order is greater than SMALLER_ORDER
        defaultFilmShouldBeFound("order.greaterThan=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllFilmsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where url equals to DEFAULT_URL
        defaultFilmShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the filmList where url equals to UPDATED_URL
        defaultFilmShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllFilmsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where url in DEFAULT_URL or UPDATED_URL
        defaultFilmShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the filmList where url equals to UPDATED_URL
        defaultFilmShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllFilmsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where url is not null
        defaultFilmShouldBeFound("url.specified=true");

        // Get all the filmList where url is null
        defaultFilmShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllFilmsByUrlContainsSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where url contains DEFAULT_URL
        defaultFilmShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the filmList where url contains UPDATED_URL
        defaultFilmShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllFilmsByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList where url does not contain DEFAULT_URL
        defaultFilmShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the filmList where url does not contain UPDATED_URL
        defaultFilmShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllFilmsByPersonIsEqualToSomething() throws Exception {
        Person person;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            filmRepository.saveAndFlush(film);
            person = PersonResourceIT.createEntity(em);
        } else {
            person = TestUtil.findAll(em, Person.class).get(0);
        }
        em.persist(person);
        em.flush();
        film.addPerson(person);
        filmRepository.saveAndFlush(film);
        Long personId = person.getId();

        // Get all the filmList where person equals to personId
        defaultFilmShouldBeFound("personId.equals=" + personId);

        // Get all the filmList where person equals to (personId + 1)
        defaultFilmShouldNotBeFound("personId.equals=" + (personId + 1));
    }

    @Test
    @Transactional
    void getAllFilmsByEpisodesIsEqualToSomething() throws Exception {
        Episode episodes;
        if (TestUtil.findAll(em, Episode.class).isEmpty()) {
            filmRepository.saveAndFlush(film);
            episodes = EpisodeResourceIT.createEntity(em);
        } else {
            episodes = TestUtil.findAll(em, Episode.class).get(0);
        }
        em.persist(episodes);
        em.flush();
        film.addEpisodes(episodes);
        filmRepository.saveAndFlush(film);
        Long episodesId = episodes.getId();

        // Get all the filmList where episodes equals to episodesId
        defaultFilmShouldBeFound("episodesId.equals=" + episodesId);

        // Get all the filmList where episodes equals to (episodesId + 1)
        defaultFilmShouldNotBeFound("episodesId.equals=" + (episodesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFilmShouldBeFound(String filter) throws Exception {
        restFilmMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(film.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].synopsis").value(hasItem(DEFAULT_SYNOPSIS)))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))))
            .andExpect(jsonPath("$.[*].reviews").value(hasItem(DEFAULT_REVIEWS.intValue())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].filmType").value(hasItem(DEFAULT_FILM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));

        // Check, that the count call also returns 1
        restFilmMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFilmShouldNotBeFound(String filter) throws Exception {
        restFilmMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFilmMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFilm() throws Exception {
        // Get the film
        restFilmMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFilm() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeUpdate = filmRepository.findAll().size();

        // Update the film
        Film updatedFilm = filmRepository.findById(film.getId()).get();
        // Disconnect from session so that the updates on updatedFilm are not directly saved in db
        em.detach(updatedFilm);
        updatedFilm
            .title(UPDATED_TITLE)
            .synopsis(UPDATED_SYNOPSIS)
            .views(UPDATED_VIEWS)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .reviews(UPDATED_REVIEWS)
            .gender(UPDATED_GENDER)
            .filmType(UPDATED_FILM_TYPE)
            .order(UPDATED_ORDER)
            .url(UPDATED_URL);
        FilmDTO filmDTO = filmMapper.toDto(updatedFilm);

        restFilmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, filmDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(filmDTO))
            )
            .andExpect(status().isOk());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFilm.getSynopsis()).isEqualTo(UPDATED_SYNOPSIS);
        assertThat(testFilm.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testFilm.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testFilm.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testFilm.getReviews()).isEqualTo(UPDATED_REVIEWS);
        assertThat(testFilm.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testFilm.getFilmType()).isEqualTo(UPDATED_FILM_TYPE);
        assertThat(testFilm.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testFilm.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void putNonExistingFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, filmDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(filmDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(filmDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(filmDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFilmWithPatch() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeUpdate = filmRepository.findAll().size();

        // Update the film using partial update
        Film partialUpdatedFilm = new Film();
        partialUpdatedFilm.setId(film.getId());

        partialUpdatedFilm.views(UPDATED_VIEWS).reviews(UPDATED_REVIEWS).gender(UPDATED_GENDER).order(UPDATED_ORDER).url(UPDATED_URL);

        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFilm.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFilm))
            )
            .andExpect(status().isOk());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFilm.getSynopsis()).isEqualTo(DEFAULT_SYNOPSIS);
        assertThat(testFilm.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testFilm.getCover()).isEqualTo(DEFAULT_COVER);
        assertThat(testFilm.getCoverContentType()).isEqualTo(DEFAULT_COVER_CONTENT_TYPE);
        assertThat(testFilm.getReviews()).isEqualTo(UPDATED_REVIEWS);
        assertThat(testFilm.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testFilm.getFilmType()).isEqualTo(DEFAULT_FILM_TYPE);
        assertThat(testFilm.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testFilm.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void fullUpdateFilmWithPatch() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeUpdate = filmRepository.findAll().size();

        // Update the film using partial update
        Film partialUpdatedFilm = new Film();
        partialUpdatedFilm.setId(film.getId());

        partialUpdatedFilm
            .title(UPDATED_TITLE)
            .synopsis(UPDATED_SYNOPSIS)
            .views(UPDATED_VIEWS)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .reviews(UPDATED_REVIEWS)
            .gender(UPDATED_GENDER)
            .filmType(UPDATED_FILM_TYPE)
            .order(UPDATED_ORDER)
            .url(UPDATED_URL);

        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFilm.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFilm))
            )
            .andExpect(status().isOk());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFilm.getSynopsis()).isEqualTo(UPDATED_SYNOPSIS);
        assertThat(testFilm.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testFilm.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testFilm.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testFilm.getReviews()).isEqualTo(UPDATED_REVIEWS);
        assertThat(testFilm.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testFilm.getFilmType()).isEqualTo(UPDATED_FILM_TYPE);
        assertThat(testFilm.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testFilm.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, filmDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(filmDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(filmDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(filmDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFilm() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeDelete = filmRepository.findAll().size();

        // Delete the film
        restFilmMockMvc
            .perform(delete(ENTITY_API_URL_ID, film.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
