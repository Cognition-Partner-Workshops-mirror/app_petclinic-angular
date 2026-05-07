package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;

import java.util.List;

public interface VetService {

    List<VetDto> listAll();

    VetDto getById(Integer id);

    VetDto create(VetDto dto);

    VetDto update(Integer id, VetDto dto);

    VetDto delete(Integer id);

    List<VetDto> searchByName(String name);

    List<VetDto> findBySpecialtyId(Integer specialtyId);

    List<VetDto> findBySpecialtyName(String specialtyName);
}
