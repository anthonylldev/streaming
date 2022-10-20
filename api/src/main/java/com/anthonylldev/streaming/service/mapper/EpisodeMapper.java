package com.anthonylldev.streaming.service.mapper;

import com.anthonylldev.streaming.domain.Episode;
import com.anthonylldev.streaming.domain.Film;
import com.anthonylldev.streaming.service.dto.EpisodeDTO;
import com.anthonylldev.streaming.service.dto.FilmDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Episode} and its DTO {@link EpisodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EpisodeMapper extends EntityMapper<EpisodeDTO, Episode> {

    @Override
    @Mapping(target = "film", source = "film", qualifiedByName = "filmEntityTitle")
    Episode toEntity(EpisodeDTO dto);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "film", source = "film", qualifiedByName = "filmEntityTitle")
    void partialUpdate(@MappingTarget Episode entity, EpisodeDTO dto);

    @Mapping(target = "film", source = "film", qualifiedByName = "filmTitle")
    EpisodeDTO toDto(Episode s);

    @Named("filmTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    FilmDTO toDtoFilmTitle(Film film);

    @Named("filmEntityTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    Film toDtoFilmTitle(FilmDTO filmDTO);
}
