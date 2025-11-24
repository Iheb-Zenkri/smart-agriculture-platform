package com.smartagri.parcel.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartagri.parcel.dto.ParcelRequest;
import com.smartagri.parcel.model.Parcel;
import com.smartagri.parcel.repository.ParcelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ParcelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParcelRepository parcelRepository;

    private Parcel testParcel;

    @BeforeEach
    void setUp() {
        parcelRepository.deleteAll();

        testParcel = new Parcel();
        testParcel.setName("Integration Test Parcel");
        testParcel.setLocation("Test Location");
        testParcel.setLatitude(new BigDecimal("36.8065"));
        testParcel.setLongitude(new BigDecimal("10.1815"));
        testParcel.setSurfaceArea(new BigDecimal("5.5"));
        testParcel.setSoilType("Clay");
        testParcel.setIrrigationSystem("Drip");
        testParcel = parcelRepository.save(testParcel);
    }

    @Test
    void createParcel_Success() throws Exception {
        ParcelRequest request = new ParcelRequest();
        request.setName("New Test Parcel");
        request.setLocation("New Location");
        request.setLatitude(new BigDecimal("36.8065"));
        request.setLongitude(new BigDecimal("10.1815"));
        request.setSurfaceArea(new BigDecimal("3.5"));
        request.setSoilType("Sandy");
        request.setIrrigationSystem("Sprinkler");

        mockMvc.perform(post("/api/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Test Parcel")))
                .andExpect(jsonPath("$.location", is("New Location")))
                .andExpect(jsonPath("$.soilType", is("Sandy")));
    }

    @Test
    void createParcel_ValidationError() throws Exception {
        ParcelRequest request = new ParcelRequest();
        request.setName(""); // Invalid: empty name

        mockMvc.perform(post("/api/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void getAllParcels_Success() throws Exception {
        mockMvc.perform(get("/api/parcels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Integration Test Parcel")));
    }

    @Test
    void getParcelById_Success() throws Exception {
        mockMvc.perform(get("/api/parcels/{id}", testParcel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testParcel.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Parcel")))
                .andExpect(jsonPath("$.location", is("Test Location")));
    }

    @Test
    void getParcelById_NotFound() throws Exception {
        mockMvc.perform(get("/api/parcels/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void updateParcel_Success() throws Exception {
        ParcelRequest request = new ParcelRequest();
        request.setName("Updated Parcel");
        request.setLocation("Updated Location");
        request.setLatitude(new BigDecimal("36.8065"));
        request.setLongitude(new BigDecimal("10.1815"));
        request.setSurfaceArea(new BigDecimal("6.5"));
        request.setSoilType("Loamy");
        request.setIrrigationSystem("Center Pivot");

        mockMvc.perform(put("/api/parcels/{id}", testParcel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Parcel")))
                .andExpect(jsonPath("$.location", is("Updated Location")))
                .andExpect(jsonPath("$.soilType", is("Loamy")));
    }

    @Test
    void deleteParcel_Success() throws Exception {
        mockMvc.perform(delete("/api/parcels/{id}", testParcel.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/parcels/{id}", testParcel.getId()))
                .andExpect(status().isNotFound());
    }
}