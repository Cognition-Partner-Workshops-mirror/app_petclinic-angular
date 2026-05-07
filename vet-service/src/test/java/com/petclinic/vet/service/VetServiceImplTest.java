package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet james;
    private VetDto jamesDto;
    private Specialty radiology;
    private SpecialtyDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyDto(1, "radiology");

        james = new Vet(1, "James", "Carter");
        james.setSpecialties(Set.of(radiology));

        jamesDto = new VetDto(1, "James", "Carter", List.of(radiologyDto));
    }

    @Test
    void listAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toDtoList(anyList())).thenReturn(List.of(jamesDto));

        List<VetDto> result = vetService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = vetService.getById(1);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void getById_nonExistingId_throwsNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_validVet_returnsCreated() {
        VetDto inputDto = new VetDto(null, "Test", "Vet", List.of(radiologyDto));
        Vet newEntity = new Vet(null, "Test", "Vet");
        Vet savedEntity = new Vet(7, "Test", "Vet");
        savedEntity.setSpecialties(Set.of(radiology));
        VetDto savedDto = new VetDto(7, "Test", "Vet", List.of(radiologyDto));

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(savedDto);

        VetDto result = vetService.create(inputDto);

        assertThat(result.getId()).isEqualTo(7);
        assertThat(result.getFirstName()).isEqualTo("Test");
    }

    @Test
    void create_vetWithNonExistingSpecialty_throwsNotFoundException() {
        SpecialtyDto nonExistentSpecialty = new SpecialtyDto(999, "nonexistent");
        VetDto inputDto = new VetDto(null, "Test", "Vet", List.of(nonExistentSpecialty));
        Vet newEntity = new Vet(null, "Test", "Vet");

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(inputDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_vetWithSpecialtyByName_resolvesSpecialty() {
        SpecialtyDto byNameDto = new SpecialtyDto(null, "radiology");
        VetDto inputDto = new VetDto(null, "Test", "Vet", List.of(byNameDto));
        Vet newEntity = new Vet(null, "Test", "Vet");
        Vet savedEntity = new Vet(7, "Test", "Vet");
        savedEntity.setSpecialties(Set.of(radiology));
        VetDto savedDto = new VetDto(7, "Test", "Vet", List.of(radiologyDto));

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(savedDto);

        VetDto result = vetService.create(inputDto);

        assertThat(result.getId()).isEqualTo(7);
    }

    @Test
    void create_vetWithSpecialtyByName_notFound_throwsNotFoundException() {
        SpecialtyDto byNameDto = new SpecialtyDto(null, "nonexistent");
        VetDto inputDto = new VetDto(null, "Test", "Vet", List.of(byNameDto));
        Vet newEntity = new Vet(null, "Test", "Vet");

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(specialtyRepository.findByNameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(inputDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    void create_vetWithEmptySpecialties_succeeds() {
        VetDto inputDto = new VetDto(null, "Test", "Vet", List.of());
        Vet newEntity = new Vet(null, "Test", "Vet");
        Vet savedEntity = new Vet(7, "Test", "Vet");
        savedEntity.setSpecialties(new HashSet<>());
        VetDto savedDto = new VetDto(7, "Test", "Vet", List.of());

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(savedDto);

        VetDto result = vetService.create(inputDto);

        assertThat(result.getId()).isEqualTo(7);
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_vetWithNullSpecialties_succeeds() {
        VetDto inputDto = new VetDto(null, "Test", "Vet", null);
        Vet newEntity = new Vet(null, "Test", "Vet");
        Vet savedEntity = new Vet(7, "Test", "Vet");
        savedEntity.setSpecialties(new HashSet<>());
        VetDto savedDto = new VetDto(7, "Test", "Vet", List.of());

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(savedDto);

        VetDto result = vetService.create(inputDto);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void update_existingVet_returnsUpdated() {
        VetDto updateDto = new VetDto(null, "Updated", "Name", List.of(radiologyDto));
        Vet updatedEntity = new Vet(1, "Updated", "Name");
        updatedEntity.setSpecialties(Set.of(radiology));
        VetDto updatedDto = new VetDto(1, "Updated", "Name", List.of(radiologyDto));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(james)).thenReturn(updatedEntity);
        when(vetMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        VetDto result = vetService.update(1, updateDto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        verify(vetMapper).updateEntity(updateDto, james);
    }

    @Test
    void update_nonExistingId_throwsNotFoundException() {
        VetDto updateDto = new VetDto(null, "Updated", "Name", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, updateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingVet_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = vetService.delete(1);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).delete(james);
    }

    @Test
    void delete_nonExistingId_throwsNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.searchByName("James")).thenReturn(List.of(james));
        when(vetMapper.toDtoList(anyList())).thenReturn(List.of(jamesDto));

        List<VetDto> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(james));
        when(vetMapper.toDtoList(anyList())).thenReturn(List.of(jamesDto));

        List<VetDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toDtoList(anyList())).thenReturn(List.of(jamesDto));

        List<VetDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
