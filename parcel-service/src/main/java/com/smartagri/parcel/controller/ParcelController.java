package com.smartagri.parcel.controller;


import com.smartagri.parcel.dto.ParcelDTO;
import com.smartagri.parcel.dto.ParcelRequest;
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
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Parcel Management", description = "APIs for managing agricultural parcels")
public class ParcelController {

    private final ParcelService parcelService;

    @PostMapping
    @Operation(summary = "Create a new parcel")
    public ResponseEntity<ParcelDTO> createParcel(@Valid @RequestBody ParcelRequest request) {
        log.info("REST request to create parcel: {}", request.getName());
        ParcelDTO parcel = parcelService.createParcel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(parcel);
    }

    @GetMapping
    @Operation(summary = "Get all parcels")
    public ResponseEntity<List<ParcelDTO>> getAllParcels() {
        log.info("REST request to get all parcels");
        List<ParcelDTO> parcels = parcelService.getAllParcels();
        return ResponseEntity.ok(parcels);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parcel by ID")
    public ResponseEntity<ParcelDTO> getParcelById(@PathVariable Long id) {
        log.info("REST request to get parcel with ID: {}", id);
        ParcelDTO parcel = parcelService.getParcelById(id);
        return ResponseEntity.ok(parcel);
    }

    @GetMapping("/location/{location}")
    @Operation(summary = "Get parcels by location")
    public ResponseEntity<List<ParcelDTO>> getParcelsByLocation(@PathVariable String location) {
        log.info("REST request to get parcels by location: {}", location);
        List<ParcelDTO> parcels = parcelService.getParcelsByLocation(location);
        return ResponseEntity.ok(parcels);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a parcel")
    public ResponseEntity<ParcelDTO> updateParcel(
            @PathVariable Long id,
            @Valid @RequestBody ParcelRequest request) {
        log.info("REST request to update parcel with ID: {}", id);
        ParcelDTO parcel = parcelService.updateParcel(id, request);
        return ResponseEntity.ok(parcel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a parcel")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        log.info("REST request to delete parcel with ID: {}", id);
        parcelService.deleteParcel(id);
        return ResponseEntity.noContent().build();
    }
}