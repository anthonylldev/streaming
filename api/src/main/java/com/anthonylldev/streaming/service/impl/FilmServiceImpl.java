package com.anthonylldev.streaming.service.impl;

import com.anthonylldev.streaming.domain.Film;
import com.anthonylldev.streaming.repository.FilmRepository;
import com.anthonylldev.streaming.service.FilmService;
import com.anthonylldev.streaming.service.dto.FilmDTO;
import com.anthonylldev.streaming.service.mapper.FilmMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Film}.
 */
@Service
@Transactional
public class FilmServiceImpl implements FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmServiceImpl.class);

    private final FilmRepository filmRepository;

    private final FilmMapper filmMapper;

    public FilmServiceImpl(FilmRepository filmRepository, FilmMapper filmMapper) {
        this.filmRepository = filmRepository;
        this.filmMapper = filmMapper;
    }

    @Override
    public FilmDTO save(FilmDTO filmDTO) {
        log.debug("Request to save Film : {}", filmDTO);
        Film film = filmMapper.toEntity(filmDTO);
        film = filmRepository.save(film);
        return filmMapper.toDto(film);
    }

    @Override
    public FilmDTO update(FilmDTO filmDTO) {
        log.debug("Request to update Film : {}", filmDTO);
        Film film = filmMapper.toEntity(filmDTO);
        film = filmRepository.save(film);
        return filmMapper.toDto(film);
    }

    @Override
    public Optional<FilmDTO> partialUpdate(FilmDTO filmDTO) {
        log.debug("Request to partially update Film : {}", filmDTO);

        return filmRepository
            .findById(filmDTO.getId())
            .map(existingFilm -> {
                filmMapper.partialUpdate(existingFilm, filmDTO);

                return existingFilm;
            })
            .map(filmRepository::save)
            .map(filmMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FilmDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Films");
        return filmRepository.findAll(pageable).map(filmMapper::toDto);
    }

    public Page<FilmDTO> findAllWithEagerRelationships(Pageable pageable) {
        return filmRepository.findAllWithEagerRelationships(pageable).map(filmMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FilmDTO> findOne(Long id) {
        log.debug("Request to get Film : {}", id);
        return filmRepository.findOneWithEagerRelationships(id).map(filmMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Film : {}", id);
        filmRepository.deleteById(id);
    }
}
