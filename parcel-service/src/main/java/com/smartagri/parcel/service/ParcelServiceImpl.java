package com.smartagri.parcel.service;


import com.smartagri.parcel.dto.*;
import com.smartagri.parcel.exception.ResourceNotFoundException;
import com.smartagri.parcel.model.Crop;
import com.smartagri.parcel.model.Harvest;
import com.smartagri.parcel.model.Parcel;
import com.smartagri.parcel.repository.CropRepository;
import com.smartagri.parcel.repository.HarvestRepository;
import com.smartagri.parcel.repository.ParcelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final CropRepository cropRepository;
    private final HarvestRepository harvestRepository;
    private final ModelMapper modelMapper;

    @Override
    public ParcelDTO createParcel(ParcelRequest request) {
        log.info("Creating new parcel: {}", request.getName());
        Parcel parcel = modelMapper.map(request, Parcel.class);
        Parcel savedParcel = parcelRepository.save(parcel);
        log.info("Parcel created successfully with ID: {}", savedParcel.getId());
        return modelMapper.map(savedParcel, ParcelDTO.class);
    }

    @Override
    public ParcelDTO updateParcel(Long id, ParcelRequest request) {
        log.info("Updating parcel with ID: {}", id);
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));

        modelMapper.map(request, parcel);
        Parcel updatedParcel = parcelRepository.save(parcel);
        log.info("Parcel updated successfully");
        return modelMapper.map(updatedParcel, ParcelDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelDTO getParcelById(Long id) {
        log.info("Fetching parcel with ID: {}", id);
        Parcel parcel = parcelRepository.findByIdWithCrops(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));
        return modelMapper.map(parcel, ParcelDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelDTO> getAllParcels() {
        log.info("Fetching all parcels");
        return parcelRepository.findAll().stream()
                .map(parcel -> modelMapper.map(parcel, ParcelDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelDTO> getParcelsByLocation(String location) {
        log.info("Fetching parcels by location: {}", location);
        return parcelRepository.findByLocation(location).stream()
                .map(parcel -> modelMapper.map(parcel, ParcelDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteParcel(Long id) {
        log.info("Deleting parcel with ID: {}", id);
        if (!parcelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Parcel not found with id: " + id);
        }
        parcelRepository.deleteById(id);
        log.info("Parcel deleted successfully");
    }

    @Override
    public CropDTO createCrop(CropRequest request) {
        log.info("Creating new crop for parcel ID: {}", request.getParcelId());
        Parcel parcel = parcelRepository.findById(request.getParcelId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + request.getParcelId()));

        Crop crop = modelMapper.map(request, Crop.class);
        crop.setParcel(parcel);
        Crop savedCrop = cropRepository.save(crop);
        log.info("Crop created successfully with ID: {}", savedCrop.getId());
        return modelMapper.map(savedCrop, CropDTO.class);
    }

    @Override
    public CropDTO updateCrop(Long id, CropRequest request) {
        log.info("Updating crop with ID: {}", id);
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + id));

        modelMapper.map(request, crop);

        if (!crop.getParcel().getId().equals(request.getParcelId())) {
            Parcel newParcel = parcelRepository.findById(request.getParcelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + request.getParcelId()));
            crop.setParcel(newParcel);
        }

        Crop updatedCrop = cropRepository.save(crop);
        log.info("Crop updated successfully");
        return modelMapper.map(updatedCrop, CropDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CropDTO getCropById(Long id) {
        log.info("Fetching crop with ID: {}", id);
        Crop crop = cropRepository.findByIdWithHarvests(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + id));
        return modelMapper.map(crop, CropDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropDTO> getCropsByParcelId(Long parcelId) {
        log.info("Fetching crops for parcel ID: {}", parcelId);
        return cropRepository.findByParcelId(parcelId).stream()
                .map(crop -> modelMapper.map(crop, CropDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropDTO> getAllCrops() {
        log.info("Fetching all crops");
        return cropRepository.findAll().stream()
                .map(crop -> modelMapper.map(crop, CropDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCrop(Long id) {
        log.info("Deleting crop with ID: {}", id);
        if (!cropRepository.existsById(id)) {
            throw new ResourceNotFoundException("Crop not found with id: " + id);
        }
        cropRepository.deleteById(id);
        log.info("Crop deleted successfully");
    }

    @Override
    public HarvestDTO createHarvest(HarvestRequest request) {
        log.info("Creating new harvest for crop ID: {}", request.getCropId());
        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + request.getCropId()));

        Harvest harvest = modelMapper.map(request, Harvest.class);
        harvest.setCrop(crop);
        Harvest savedHarvest = harvestRepository.save(harvest);
        log.info("Harvest created successfully with ID: {}", savedHarvest.getId());
        return modelMapper.map(savedHarvest, HarvestDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public HarvestDTO getHarvestById(Long id) {
        log.info("Fetching harvest with ID: {}", id);
        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest not found with id: " + id));
        return modelMapper.map(harvest, HarvestDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HarvestDTO> getHarvestsByCropId(Long cropId) {
        log.info("Fetching harvests for crop ID: {}", cropId);
        return harvestRepository.findByCropId(cropId).stream()
                .map(harvest -> modelMapper.map(harvest, HarvestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HarvestDTO> getHarvestsByParcelId(Long parcelId) {
        log.info("Fetching harvests for parcel ID: {}", parcelId);
        return harvestRepository.findByParcelId(parcelId).stream()
                .map(harvest -> modelMapper.map(harvest, HarvestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteHarvest(Long id) {
        log.info("Deleting harvest with ID: {}", id);
        if (!harvestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Harvest not found with id: " + id);
        }
        harvestRepository.deleteById(id);
        log.info("Harvest deleted successfully");
    }
}