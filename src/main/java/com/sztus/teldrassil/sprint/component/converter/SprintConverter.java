package com.sztus.teldrassil.sprint.component.converter;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author free
 **/
@Mapper
public interface SprintConverter {
    SprintConverter INSTANCE = Mappers.getMapper(SprintConverter.class);

}
