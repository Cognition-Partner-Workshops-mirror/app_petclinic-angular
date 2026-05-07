package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDto> listAll();

    SpecialtyDto getById(Integer id);

    SpecialtyDto create(SpecialtyDto dto);

    SpecialtyDto update(Integer id, SpecialtyDto dto);

    SpecialtyDto delete(Integer id);

    List<SpecialtyDto> searchByName(String name);
}
