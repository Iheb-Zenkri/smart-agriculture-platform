package com.smartagri.parcel.controller;


import com.smartagri.parcel.dto.HarvestDTO;
import com.smartagri.parcel.dto.HarvestRequest;
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
@RequestMapping("/api/harvests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Harvest Management", description = "APIs for managing harvests")
public class HarvestController {

    private final ParcelService parcelService;

    @PostMapping
    @Operation(summary = "Create a new harvest record")
    public ResponseEntity<HarvestDTO> createHarvest(@Valid @RequestBody HarvestRequest request) {
        log.info("REST request to create harvest for crop: {}", request.getCropId());
        HarvestDTO harvest = parcelService.createHarvest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(harvest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get harvest by ID")
    public ResponseEntity<HarvestDTO> getHarvestById(@PathVariable Long id) {
        log.info("REST request to get harvest with ID: {}", id);
        HarvestDTO harvest = parcelService.getHarvestById(id);
        return ResponseEntity.ok(harvest);
    }

    @GetMapping("/crop/{cropId}")
    @Operation(summary = "Get harvests by crop ID")
    public ResponseEntity<List<HarvestDTO>> getHarvestsByCropId(@PathVariable Long cropId) {
        log.info("REST request to get harvests for crop: {}", cropId);
        List<HarvestDTO> harvests = parcelService.getHarvestsByCropId(cropId);
        return ResponseEntity.ok(harvests);
    }

    @GetMapping("/parcel/{parcelId}")
    @Operation(summary = "Get harvests by parcel ID")
    public ResponseEntity<List<HarvestDTO>> getHarvestsByParcelId(@PathVariable Long parcelId) {
        log.info("REST request to get harvests for parcel: {}", parcelId);
        List<HarvestDTO> harvests = parcelService.getHarvestsByParcelId(parcelId);
        return ResponseEntity.ok(harvests);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a harvest")
    public ResponseEntity<Void> deleteHarvest(@PathVariable Long id) {
        log.info("REST request to delete harvest with ID: {}", id);
        parcelService.deleteHarvest(id);
        return ResponseEntity.noContent().build();
    }
}
