package com.anthonylldev.streaming.service.mapper;

import com.anthonylldev.streaming.domain.Film;
import com.anthonylldev.streaming.domain.Person;
import com.anthonylldev.streaming.service.dto.FilmDTO;
import com.anthonylldev.streaming.service.dto.PersonDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Film} and its DTO {@link FilmDTO}.
 */
@Mapper(componentModel = "spring")
public interface FilmMapper extends EntityMapper<FilmDTO, Film> {

    @Mapping(target = "removePerson", ignore = true)
    @Mapping(target = "episodes", ignore = true)
    @Mapping(target = "removeEpisodes", ignore = true)
    @Mapping(target = "people", source = "people", qualifiedByName = "personEntityId")
    Film toEntity(FilmDTO filmDTO);

    @Override
    @Mapping(target = "removePerson", ignore = true)
    @Mapping(target = "episodes", ignore = true)
    @Mapping(target = "removeEpisodes", ignore = true)
    @Mapping(target = "people", source = "people", qualifiedByName = "personEntityId")
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Film entity, FilmDTO dto);

    @Mapping(target = "people", source = "people", qualifiedByName = "personIdSet")
    FilmDTO toDto(Film s);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("personEntityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Person toDtoPersonId(PersonDTO personDTO);

    @Named("personIdSet")
    default Set<PersonDTO> toDtoPersonIdSet(Set<Person> person) {
        return person.stream().map(this::toDtoPersonId).collect(Collectors.toSet());
    }
}
