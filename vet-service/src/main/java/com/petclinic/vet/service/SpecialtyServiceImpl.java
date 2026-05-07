package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDto> listAll() {
        return specialtyMapper.toDtoList(specialtyRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyDto getById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toDto(specialty);
    }

    @Override
    public SpecialtyDto create(SpecialtyDto dto) {
        if (specialtyRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Specialty with name '" + dto.getName() + "' already exists");
        }
        Specialty specialty = specialtyMapper.toEntity(dto);
        specialty.setId(null);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toDto(saved);
    }

    @Override
    public SpecialtyDto update(Integer id, SpecialtyDto dto) {
        Specialty existing = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        specialtyRepository.findByNameIgnoreCase(dto.getName())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new DuplicateResourceException("Specialty with name '" + dto.getName() + "' already exists");
                });

        specialtyMapper.updateEntity(dto, existing);
        Specialty saved = specialtyRepository.save(existing);
        return specialtyMapper.toDto(saved);
    }

    @Override
    public SpecialtyDto delete(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyDto dto = specialtyMapper.toDto(specialty);
        specialtyRepository.delete(specialty);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDto> searchByName(String name) {
        return specialtyMapper.toDtoList(specialtyRepository.searchByName(name));
    }
}
