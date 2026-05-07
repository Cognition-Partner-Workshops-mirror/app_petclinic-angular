package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiology;
    private SpecialtyDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void listAll_returnsAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
        when(specialtyMapper.toDtoList(any())).thenReturn(List.of(radiologyDto));

        List<SpecialtyDto> result = specialtyService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
        verify(specialtyRepository).findAll();
    }

    @Test
    void getById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toDto(radiology)).thenReturn(radiologyDto);

        SpecialtyDto result = specialtyService.getById(1);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getById_nonExistingId_throwsNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_validSpecialty_returnsCreated() {
        SpecialtyDto inputDto = new SpecialtyDto(null, "oncology");
        Specialty newEntity = new Specialty(null, "oncology");
        Specialty savedEntity = new Specialty(4, "oncology");
        SpecialtyDto savedDto = new SpecialtyDto(4, "oncology");

        when(specialtyRepository.existsByNameIgnoreCase("oncology")).thenReturn(false);
        when(specialtyMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(specialtyRepository.save(newEntity)).thenReturn(savedEntity);
        when(specialtyMapper.toDto(savedEntity)).thenReturn(savedDto);

        SpecialtyDto result = specialtyService.create(inputDto);

        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("oncology");
    }

    @Test
    void create_duplicateName_throwsDuplicateException() {
        SpecialtyDto inputDto = new SpecialtyDto(null, "radiology");
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.create(inputDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("radiology");
    }

    @Test
    void update_existingSpecialty_returnsUpdated() {
        SpecialtyDto updateDto = new SpecialtyDto(null, "updated");
        Specialty updatedEntity = new Specialty(1, "updated");
        SpecialtyDto updatedDto = new SpecialtyDto(1, "updated");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("updated")).thenReturn(Optional.empty());
        when(specialtyRepository.save(radiology)).thenReturn(updatedEntity);
        when(specialtyMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        SpecialtyDto result = specialtyService.update(1, updateDto);

        assertThat(result.getName()).isEqualTo("updated");
        verify(specialtyMapper).updateEntity(updateDto, radiology);
    }

    @Test
    void update_nonExistingId_throwsNotFoundException() {
        SpecialtyDto updateDto = new SpecialtyDto(null, "updated");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, updateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_duplicateName_differentId_throwsDuplicateException() {
        Specialty other = new Specialty(2, "surgery");
        SpecialtyDto updateDto = new SpecialtyDto(null, "surgery");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("surgery")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> specialtyService.update(1, updateDto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_sameName_sameId_succeeds() {
        SpecialtyDto updateDto = new SpecialtyDto(null, "radiology");
        Specialty updatedEntity = new Specialty(1, "radiology");
        SpecialtyDto updatedDto = new SpecialtyDto(1, "radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updatedEntity);
        when(specialtyMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        SpecialtyDto result = specialtyService.update(1, updateDto);

        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void delete_existingSpecialty_returnsDeleted() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toDto(radiology)).thenReturn(radiologyDto);

        SpecialtyDto result = specialtyService.delete(1);

        assertThat(result.getName()).isEqualTo("radiology");
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingSpecialties() {
        when(specialtyRepository.searchByName("rad")).thenReturn(List.of(radiology));
        when(specialtyMapper.toDtoList(any())).thenReturn(List.of(radiologyDto));

        List<SpecialtyDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
    }
}
