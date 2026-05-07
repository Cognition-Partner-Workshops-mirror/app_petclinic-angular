package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.service.VetService;
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
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetDto>> listVets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty) {
        List<VetDto> vets;
        if (name != null && !name.isBlank()) {
            vets = vetService.searchByName(name);
        } else if (specialty != null && !specialty.isBlank()) {
            vets = vetService.findBySpecialtyName(specialty);
        } else {
            vets = vetService.listAll();
        }
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.getById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetDto vetDto) {
        return ResponseEntity.ok(vetService.create(vetDto));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetDto> updateVet(@PathVariable Integer vetId,
                                            @Valid @RequestBody VetDto vetDto) {
        return ResponseEntity.ok(vetService.update(vetId, vetDto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
