package com.smartagri.parcel.controller;


import com.smartagri.parcel.dto.CropDTO;
import com.smartagri.parcel.dto.CropRequest;
import com.smartagri.parcel.service.ParcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Crop Management", description = "APIs for managing crops")
public class CropController {

    private final ParcelService parcelService;

    @PostMapping
    @Operation(summary = "Create a new crop")
    public ResponseEntity<CropDTO> createCrop(@Valid @RequestBody CropRequest request) {
        log.info("REST request to create crop for parcel: {}", request.getParcelId());
        CropDTO crop = parcelService.createCrop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(crop);
    }

    @GetMapping
    @Operation(summary = "Get all crops")
    public ResponseEntity<List<CropDTO>> getAllCrops() {
        log.info("REST request to get all crops");
        List<CropDTO> crops = parcelService.getAllCrops();
        return ResponseEntity.ok(crops);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get crop by ID")
    public ResponseEntity<CropDTO> getCropById(@PathVariable Long id) {
        log.info("REST request to get crop with ID: {}", id);
        CropDTO crop = parcelService.getCropById(id);
        return ResponseEntity.ok(crop);
    }

    @GetMapping("/parcel/{parcelId}")
    @Operation(summary = "Get crops by parcel ID")
    public ResponseEntity<List<CropDTO>> getCropsByParcelId(@PathVariable Long parcelId) {
        log.info("REST request to get crops for parcel: {}", parcelId);
        List<CropDTO> crops = parcelService.getCropsByParcelId(parcelId);
        return ResponseEntity.ok(crops);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a crop")
    public ResponseEntity<CropDTO> updateCrop(
            @PathVariable Long id,
            @Valid @RequestBody CropRequest request) {
        log.info("REST request to update crop with ID: {}", id);
        CropDTO crop = parcelService.updateCrop(id, request);
        return ResponseEntity.ok(crop);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a crop")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
        log.info("REST request to delete crop with ID: {}", id);
        parcelService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }
}