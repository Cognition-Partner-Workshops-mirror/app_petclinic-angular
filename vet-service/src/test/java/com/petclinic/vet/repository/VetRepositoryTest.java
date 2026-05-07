package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.petclinic.vet.config.JpaAuditingConfig;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findByLastNameIgnoreCase_returnsMatchingVets() {
        List<Vet> vets = vetRepository.findByLastNameIgnoreCase("carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findByLastNameIgnoreCase("unknown");
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyId_noVetsWithSpecialty_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void searchByName_matchesFirstName() {
        List<Vet> vets = vetRepository.searchByName("James");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void searchByName_matchesLastName() {
        List<Vet> vets = vetRepository.searchByName("Carter");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void searchByName_caseInsensitive() {
        List<Vet> vets = vetRepository.searchByName("carter");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.searchByName("zzzzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_newVetWithSpecialties_persists() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removes() {
        Vet vet = vetRepository.findById(1).orElseThrow();
        vetRepository.delete(vet);
        vetRepository.flush();
        assertThat(vetRepository.findById(1)).isEmpty();
    }

    @Test
    void findById_vetWithSpecialties_lazyLoadsSpecialties() {
        Vet vet = vetRepository.findById(2).orElseThrow();
        assertThat(vet.getSpecialties()).isNotEmpty();
    }
}
