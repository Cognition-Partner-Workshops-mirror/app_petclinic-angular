package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", source = "specialties", qualifiedByName = "specialtySetToList")
    VetDto toDto(Vet entity);

    @Mapping(target = "specialties", source = "specialties", qualifiedByName = "specialtyListToSet")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetDto dto);

    List<VetDto> toDtoList(List<Vet> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(VetDto dto, @MappingTarget Vet entity);

    @Named("specialtySetToList")
    default List<SpecialtyDto> specialtySetToList(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        List<SpecialtyDto> list = new ArrayList<>();
        for (Specialty s : specialties) {
            list.add(new SpecialtyDto(s.getId(), s.getName()));
        }
        return list;
    }

    @Named("specialtyListToSet")
    default Set<Specialty> specialtyListToSet(List<SpecialtyDto> dtos) {
        if (dtos == null) {
            return new HashSet<>();
        }
        Set<Specialty> set = new HashSet<>();
        for (SpecialtyDto dto : dtos) {
            set.add(new Specialty(dto.getId(), dto.getName()));
        }
        return set;
    }
}
