package com.anthonylldev.streaming.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.anthonylldev.streaming.IntegrationTest;
import com.anthonylldev.streaming.domain.Episode;
import com.anthonylldev.streaming.domain.Film;
import com.anthonylldev.streaming.repository.EpisodeRepository;
import com.anthonylldev.streaming.service.EpisodeService;
import com.anthonylldev.streaming.service.criteria.EpisodeCriteria;
import com.anthonylldev.streaming.service.dto.EpisodeDTO;
import com.anthonylldev.streaming.service.mapper.EpisodeMapper;
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

/**
 * Integration tests for the {@link EpisodeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EpisodeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SYNOPSIS = "AAAAAAAAAA";
    private static final String UPDATED_SYNOPSIS = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;
    private static final Integer SMALLER_ORDER = 1 - 1;

    private static final String ENTITY_API_URL = "/api/episodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EpisodeRepository episodeRepository;

    @Mock
    private EpisodeRepository episodeRepositoryMock;

    @Autowired
    private EpisodeMapper episodeMapper;

    @Mock
    private EpisodeService episodeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEpisodeMockMvc;

    private Episode episode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Episode createEntity(EntityManager em) {
        Episode episode = new Episode().title(DEFAULT_TITLE).synopsis(DEFAULT_SYNOPSIS).order(DEFAULT_ORDER);
        return episode;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Episode createUpdatedEntity(EntityManager em) {
        Episode episode = new Episode().title(UPDATED_TITLE).synopsis(UPDATED_SYNOPSIS).order(UPDATED_ORDER);
        return episode;
    }

    @BeforeEach
    public void initTest() {
        episode = createEntity(em);
    }

    @Test
    @Transactional
    void createEpisode() throws Exception {
        int databaseSizeBeforeCreate = episodeRepository.findAll().size();
        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeCreate + 1);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEpisode.getSynopsis()).isEqualTo(DEFAULT_SYNOPSIS);
        assertThat(testEpisode.getOrder()).isEqualTo(DEFAULT_ORDER);
    }

    @Test
    @Transactional
    void createEpisodeWithExistingId() throws Exception {
        // Create the Episode with an existing ID
        episode.setId(1L);
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        int databaseSizeBeforeCreate = episodeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEpisodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = episodeRepository.findAll().size();
        // set the field null
        episode.setTitle(null);

        // Create the Episode, which fails.
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        restEpisodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isBadRequest());

        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEpisodes() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList
        restEpisodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(episode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].synopsis").value(hasItem(DEFAULT_SYNOPSIS)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEpisodesWithEagerRelationshipsIsEnabled() throws Exception {
        when(episodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEpisodeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(episodeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEpisodesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(episodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEpisodeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(episodeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEpisode() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get the episode
        restEpisodeMockMvc
            .perform(get(ENTITY_API_URL_ID, episode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(episode.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.synopsis").value(DEFAULT_SYNOPSIS))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    void getEpisodesByIdFiltering() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        Long id = episode.getId();

        defaultEpisodeShouldBeFound("id.equals=" + id);
        defaultEpisodeShouldNotBeFound("id.notEquals=" + id);

        defaultEpisodeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEpisodeShouldNotBeFound("id.greaterThan=" + id);

        defaultEpisodeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEpisodeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEpisodesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title equals to DEFAULT_TITLE
        defaultEpisodeShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the episodeList where title equals to UPDATED_TITLE
        defaultEpisodeShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEpisodesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultEpisodeShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the episodeList where title equals to UPDATED_TITLE
        defaultEpisodeShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEpisodesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title is not null
        defaultEpisodeShouldBeFound("title.specified=true");

        // Get all the episodeList where title is null
        defaultEpisodeShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllEpisodesByTitleContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title contains DEFAULT_TITLE
        defaultEpisodeShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the episodeList where title contains UPDATED_TITLE
        defaultEpisodeShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEpisodesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title does not contain DEFAULT_TITLE
        defaultEpisodeShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the episodeList where title does not contain UPDATED_TITLE
        defaultEpisodeShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEpisodesBySynopsisIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where synopsis equals to DEFAULT_SYNOPSIS
        defaultEpisodeShouldBeFound("synopsis.equals=" + DEFAULT_SYNOPSIS);

        // Get all the episodeList where synopsis equals to UPDATED_SYNOPSIS
        defaultEpisodeShouldNotBeFound("synopsis.equals=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllEpisodesBySynopsisIsInShouldWork() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where synopsis in DEFAULT_SYNOPSIS or UPDATED_SYNOPSIS
        defaultEpisodeShouldBeFound("synopsis.in=" + DEFAULT_SYNOPSIS + "," + UPDATED_SYNOPSIS);

        // Get all the episodeList where synopsis equals to UPDATED_SYNOPSIS
        defaultEpisodeShouldNotBeFound("synopsis.in=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllEpisodesBySynopsisIsNullOrNotNull() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where synopsis is not null
        defaultEpisodeShouldBeFound("synopsis.specified=true");

        // Get all the episodeList where synopsis is null
        defaultEpisodeShouldNotBeFound("synopsis.specified=false");
    }

    @Test
    @Transactional
    void getAllEpisodesBySynopsisContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where synopsis contains DEFAULT_SYNOPSIS
        defaultEpisodeShouldBeFound("synopsis.contains=" + DEFAULT_SYNOPSIS);

        // Get all the episodeList where synopsis contains UPDATED_SYNOPSIS
        defaultEpisodeShouldNotBeFound("synopsis.contains=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllEpisodesBySynopsisNotContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where synopsis does not contain DEFAULT_SYNOPSIS
        defaultEpisodeShouldNotBeFound("synopsis.doesNotContain=" + DEFAULT_SYNOPSIS);

        // Get all the episodeList where synopsis does not contain UPDATED_SYNOPSIS
        defaultEpisodeShouldBeFound("synopsis.doesNotContain=" + UPDATED_SYNOPSIS);
    }

    @Test
    @Transactional
    void getAllEpisodesByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where order equals to DEFAULT_ORDER
        defaultEpisodeShouldBeFound("order.equals=" + DEFAULT_ORDER);

        // Get all the episodeList where order equals to UPDATED_ORDER
        defaultEpisodeShouldNotBeFound("order.equals=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEpisodesByOrderIsInShouldWork() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where order in DEFAULT_ORDER or UPDATED_ORDER
        defaultEpisodeShouldBeFound("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER);

        // Get all the episodeList where order equals to UPDATED_ORDER
        defaultEpisodeShouldNotBeFound("order.in=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEpisodesByOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where order is not null
        defaultEpisodeShouldBeFound("order.specified=true");

        // Get all the episodeList where order is null
        defaultEpisodeShouldNotBeFound("order.specified=false");
    }

    @Test
    @Transactional
    void getAllEpisodesByOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where order is greater than or equal to DEFAULT_ORDER
        defaultEpisodeShouldBeFound("order.greaterThanOrEqual=" + DEFAULT_ORDER);

        // Get all the episodeList where order is greater than or equal to UPDATED_ORDER
        defaultEpisodeShouldNotBeFound("order.greaterThanOrEqual=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEpisodesByOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where order is less than or equal to DEFAULT_ORDER
        defaultEpisodeShouldBeFound("order.lessThanOrEqual=" + DEFAULT_ORDER);

        // Get all the episodeList where order is less than or equal to SMALLER_ORDER
        defaultEpisodeShouldNotBeFound("order.lessThanOrEqual=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllEpisodesByOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where order is less than DEFAULT_ORDER
        defaultEpisodeShouldNotBeFound("order.lessThan=" + DEFAULT_ORDER);

        // Get all the episodeList where order is less than UPDATED_ORDER
        defaultEpisodeShouldBeFound("order.lessThan=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEpisodesByOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where order is greater than DEFAULT_ORDER
        defaultEpisodeShouldNotBeFound("order.greaterThan=" + DEFAULT_ORDER);

        // Get all the episodeList where order is greater than SMALLER_ORDER
        defaultEpisodeShouldBeFound("order.greaterThan=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllEpisodesByFilmIsEqualToSomething() throws Exception {
        Film film;
        if (TestUtil.findAll(em, Film.class).isEmpty()) {
            episodeRepository.saveAndFlush(episode);
            film = FilmResourceIT.createEntity(em);
        } else {
            film = TestUtil.findAll(em, Film.class).get(0);
        }
        em.persist(film);
        em.flush();
        episode.setFilm(film);
        episodeRepository.saveAndFlush(episode);
        Long filmId = film.getId();

        // Get all the episodeList where film equals to filmId
        defaultEpisodeShouldBeFound("filmId.equals=" + filmId);

        // Get all the episodeList where film equals to (filmId + 1)
        defaultEpisodeShouldNotBeFound("filmId.equals=" + (filmId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEpisodeShouldBeFound(String filter) throws Exception {
        restEpisodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(episode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].synopsis").value(hasItem(DEFAULT_SYNOPSIS)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));

        // Check, that the count call also returns 1
        restEpisodeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEpisodeShouldNotBeFound(String filter) throws Exception {
        restEpisodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEpisodeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEpisode() throws Exception {
        // Get the episode
        restEpisodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEpisode() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();

        // Update the episode
        Episode updatedEpisode = episodeRepository.findById(episode.getId()).get();
        // Disconnect from session so that the updates on updatedEpisode are not directly saved in db
        em.detach(updatedEpisode);
        updatedEpisode.title(UPDATED_TITLE).synopsis(UPDATED_SYNOPSIS).order(UPDATED_ORDER);
        EpisodeDTO episodeDTO = episodeMapper.toDto(updatedEpisode);

        restEpisodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, episodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(episodeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEpisode.getSynopsis()).isEqualTo(UPDATED_SYNOPSIS);
        assertThat(testEpisode.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    void putNonExistingEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();
        episode.setId(count.incrementAndGet());

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEpisodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, episodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(episodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();
        episode.setId(count.incrementAndGet());

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEpisodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(episodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();
        episode.setId(count.incrementAndGet());

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEpisodeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEpisodeWithPatch() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();

        // Update the episode using partial update
        Episode partialUpdatedEpisode = new Episode();
        partialUpdatedEpisode.setId(episode.getId());

        partialUpdatedEpisode.title(UPDATED_TITLE).order(UPDATED_ORDER);

        restEpisodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEpisode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEpisode))
            )
            .andExpect(status().isOk());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEpisode.getSynopsis()).isEqualTo(DEFAULT_SYNOPSIS);
        assertThat(testEpisode.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    void fullUpdateEpisodeWithPatch() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();

        // Update the episode using partial update
        Episode partialUpdatedEpisode = new Episode();
        partialUpdatedEpisode.setId(episode.getId());

        partialUpdatedEpisode.title(UPDATED_TITLE).synopsis(UPDATED_SYNOPSIS).order(UPDATED_ORDER);

        restEpisodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEpisode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEpisode))
            )
            .andExpect(status().isOk());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEpisode.getSynopsis()).isEqualTo(UPDATED_SYNOPSIS);
        assertThat(testEpisode.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    void patchNonExistingEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();
        episode.setId(count.incrementAndGet());

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEpisodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, episodeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(episodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();
        episode.setId(count.incrementAndGet());

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEpisodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(episodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();
        episode.setId(count.incrementAndGet());

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEpisodeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(episodeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEpisode() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        int databaseSizeBeforeDelete = episodeRepository.findAll().size();

        // Delete the episode
        restEpisodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, episode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
