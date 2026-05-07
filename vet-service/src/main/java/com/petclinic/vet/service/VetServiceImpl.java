package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository,
                          SpecialtyRepository specialtyRepository,
                          VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> listAll() {
        List<Vet> vets = vetRepository.findAll();
        return vetMapper.toDtoList(vets);
    }

    @Override
    @Transactional(readOnly = true)
    public VetDto getById(Integer id) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto create(VetDto dto) {
        Vet vet = vetMapper.toEntity(dto);
        vet.setId(null);
        Set<Specialty> resolvedSpecialties = resolveSpecialties(dto.getSpecialties());
        vet.setSpecialties(resolvedSpecialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toDto(saved);
    }

    @Override
    public VetDto update(Integer id, VetDto dto) {
        Vet existing = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntity(dto, existing);
        Set<Specialty> resolvedSpecialties = resolveSpecialties(dto.getSpecialties());
        existing.setSpecialties(resolvedSpecialties);
        Vet saved = vetRepository.save(existing);
        return vetMapper.toDto(saved);
    }

    @Override
    public VetDto delete(Integer id) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetDto dto = vetMapper.toDto(vet);
        vetRepository.delete(vet);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> searchByName(String name) {
        return vetMapper.toDtoList(vetRepository.searchByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialtyId(Integer specialtyId) {
        return vetMapper.toDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialtyName(String specialtyName) {
        return vetMapper.toDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos == null) {
            return specialties;
        }
        for (SpecialtyDto dto : specialtyDtos) {
            if (dto.getId() != null) {
                Specialty specialty = specialtyRepository.findById(dto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getId()));
                specialties.add(specialty);
            } else if (dto.getName() != null) {
                Specialty specialty = specialtyRepository.findByNameIgnoreCase(dto.getName())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Specialty not found with name: " + dto.getName()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
