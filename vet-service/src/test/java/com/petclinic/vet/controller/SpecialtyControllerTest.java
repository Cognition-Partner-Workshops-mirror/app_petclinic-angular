package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
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

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    private final SpecialtyDto radiologyDto = new SpecialtyDto(1, "radiology");

    @Test
    void listSpecialties_returnsOkWithSpecialties() throws Exception {
        when(specialtyService.listAll()).thenReturn(List.of(radiologyDto));

        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_withNameParam_searchesByName() throws Exception {
        when(specialtyService.searchByName("rad")).thenReturn(List.of(radiologyDto));

        mockMvc.perform(get("/specialties").param("name", "rad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.getById(1)).thenReturn(radiologyDto);

        mockMvc.perform(get("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.getById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/specialties/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addSpecialty_validBody_returnsOk() throws Exception {
        SpecialtyDto inputDto = new SpecialtyDto(null, "oncology");
        SpecialtyDto savedDto = new SpecialtyDto(4, "oncology");
        when(specialtyService.create(any(SpecialtyDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("oncology")));
    }

    @Test
    void addSpecialty_missingName_returns400() throws Exception {
        SpecialtyDto invalidDto = new SpecialtyDto(null, null);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addSpecialty_blankName_returns400() throws Exception {
        SpecialtyDto invalidDto = new SpecialtyDto(null, "");

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_nameTooLong_returns400() throws Exception {
        String longName = "A".repeat(81);
        SpecialtyDto invalidDto = new SpecialtyDto(null, longName);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_duplicateName_returns400() throws Exception {
        SpecialtyDto inputDto = new SpecialtyDto(null, "radiology");
        when(specialtyService.create(any(SpecialtyDto.class)))
                .thenThrow(new DuplicateResourceException("Specialty with name 'radiology' already exists"));

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void updateSpecialty_validBody_returnsOk() throws Exception {
        SpecialtyDto updateDto = new SpecialtyDto(null, "updated");
        SpecialtyDto updatedDto = new SpecialtyDto(1, "updated");
        when(specialtyService.update(eq(1), any(SpecialtyDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("updated")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyDto updateDto = new SpecialtyDto(null, "updated");
        when(specialtyService.update(eq(999), any(SpecialtyDto.class)))
                .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/specialties/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.delete(1)).thenReturn(radiologyDto);

        mockMvc.perform(delete("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.delete(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/specialties/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listSpecialties_emptyList_returnsOk() throws Exception {
        when(specialtyService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
