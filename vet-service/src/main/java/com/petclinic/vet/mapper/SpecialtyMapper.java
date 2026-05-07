package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyDto toDto(Specialty entity);

    Specialty toEntity(SpecialtyDto dto);

    List<SpecialtyDto> toDtoList(List<Specialty> entities);

    void updateEntity(SpecialtyDto dto, @MappingTarget Specialty entity);
}
