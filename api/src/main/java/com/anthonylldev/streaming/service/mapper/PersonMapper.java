package com.anthonylldev.streaming.service.mapper;

import com.anthonylldev.streaming.domain.Person;
import com.anthonylldev.streaming.service.dto.PersonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Person} and its DTO {@link PersonDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {}
