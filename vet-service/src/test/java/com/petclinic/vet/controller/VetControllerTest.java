package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private final SpecialtyDto radiologyDto = new SpecialtyDto(1, "radiology");
    private final VetDto jamesDto = new VetDto(1, "James", "Carter", List.of(radiologyDto));

    @Test
    void listVets_returnsOkWithVets() throws Exception {
        when(vetService.listAll()).thenReturn(List.of(jamesDto));

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("James")))
                .andExpect(jsonPath("$[0].lastName", is("Carter")))
                .andExpect(jsonPath("$[0].specialties", hasSize(1)));
    }

    @Test
    void listVets_withNameParam_searchesByName() throws Exception {
        when(vetService.searchByName("James")).thenReturn(List.of(jamesDto));

        mockMvc.perform(get("/vets").param("name", "James"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_withSpecialtyParam_filtersBySpecialty() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(jamesDto));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        when(vetService.getById(1)).thenReturn(jamesDto);

        mockMvc.perform(get("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("James")))
                .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addVet_validBody_returnsOk() throws Exception {
        VetDto inputDto = new VetDto(null, "New", "Vet", List.of(radiologyDto));
        VetDto savedDto = new VetDto(7, "New", "Vet", List.of(radiologyDto));
        when(vetService.create(any(VetDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.firstName", is("New")));
    }

    @Test
    void addVet_missingFirstName_returns400() throws Exception {
        VetDto invalidDto = new VetDto(null, null, "Vet", List.of());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addVet_missingLastName_returns400() throws Exception {
        VetDto invalidDto = new VetDto(null, "New", null, List.of());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialties_returns400() throws Exception {
        String json = "{\"firstName\":\"New\",\"lastName\":\"Vet\",\"specialties\":null}";

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_firstNameTooLong_returns400() throws Exception {
        String longName = "A".repeat(31);
        VetDto invalidDto = new VetDto(null, longName, "Vet", List.of());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validBody_returnsOk() throws Exception {
        VetDto updateDto = new VetDto(null, "Updated", "Vet", List.of(radiologyDto));
        VetDto updatedDto = new VetDto(1, "Updated", "Vet", List.of(radiologyDto));
        when(vetService.update(eq(1), any(VetDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/vets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetDto updateDto = new VetDto(null, "Updated", "Vet", List.of());
        when(vetService.update(eq(999), any(VetDto.class)))
                .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsOk() throws Exception {
        when(vetService.delete(1)).thenReturn(jamesDto);

        mockMvc.perform(delete("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addVet_invalidFirstNamePattern_returns400() throws Exception {
        VetDto invalidDto = new VetDto(null, "James123", "Carter", List.of());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
