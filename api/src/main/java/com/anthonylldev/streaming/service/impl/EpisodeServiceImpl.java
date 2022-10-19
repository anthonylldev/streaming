package com.anthonylldev.streaming.service.impl;

import com.anthonylldev.streaming.domain.Episode;
import com.anthonylldev.streaming.repository.EpisodeRepository;
import com.anthonylldev.streaming.service.EpisodeService;
import com.anthonylldev.streaming.service.dto.EpisodeDTO;
import com.anthonylldev.streaming.service.mapper.EpisodeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Episode}.
 */
@Service
@Transactional
public class EpisodeServiceImpl implements EpisodeService {

    private final Logger log = LoggerFactory.getLogger(EpisodeServiceImpl.class);

    private final EpisodeRepository episodeRepository;

    private final EpisodeMapper episodeMapper;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository, EpisodeMapper episodeMapper) {
        this.episodeRepository = episodeRepository;
        this.episodeMapper = episodeMapper;
    }

    @Override
    public EpisodeDTO save(EpisodeDTO episodeDTO) {
        log.debug("Request to save Episode : {}", episodeDTO);
        Episode episode = episodeMapper.toEntity(episodeDTO);
        episode = episodeRepository.save(episode);
        return episodeMapper.toDto(episode);
    }

    @Override
    public EpisodeDTO update(EpisodeDTO episodeDTO) {
        log.debug("Request to update Episode : {}", episodeDTO);
        Episode episode = episodeMapper.toEntity(episodeDTO);
        episode = episodeRepository.save(episode);
        return episodeMapper.toDto(episode);
    }

    @Override
    public Optional<EpisodeDTO> partialUpdate(EpisodeDTO episodeDTO) {
        log.debug("Request to partially update Episode : {}", episodeDTO);

        return episodeRepository
            .findById(episodeDTO.getId())
            .map(existingEpisode -> {
                episodeMapper.partialUpdate(existingEpisode, episodeDTO);

                return existingEpisode;
            })
            .map(episodeRepository::save)
            .map(episodeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EpisodeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Episodes");
        return episodeRepository.findAll(pageable).map(episodeMapper::toDto);
    }

    public Page<EpisodeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return episodeRepository.findAllWithEagerRelationships(pageable).map(episodeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EpisodeDTO> findOne(Long id) {
        log.debug("Request to get Episode : {}", id);
        return episodeRepository.findOneWithEagerRelationships(id).map(episodeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Episode : {}", id);
        episodeRepository.deleteById(id);
    }
}
