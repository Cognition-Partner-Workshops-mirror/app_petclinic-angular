package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyDto>> listSpecialties(
            @RequestParam(required = false) String name) {
        List<SpecialtyDto> specialties;
        if (name != null && !name.isBlank()) {
            specialties = specialtyService.searchByName(name);
        } else {
            specialties = specialtyService.listAll();
        }
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.getById(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyDto> addSpecialty(@Valid @RequestBody SpecialtyDto specialtyDto) {
        return ResponseEntity.ok(specialtyService.create(specialtyDto));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> updateSpecialty(@PathVariable Integer specialtyId,
                                                        @Valid @RequestBody SpecialtyDto specialtyDto) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, specialtyDto));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.delete(specialtyId));
    }
}
