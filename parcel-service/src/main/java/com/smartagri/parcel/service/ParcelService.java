package com.smartagri.parcel.service;


import com.smartagri.parcel.dto.*;
import java.util.List;

public interface ParcelService {
    ParcelDTO createParcel(ParcelRequest request);
    ParcelDTO updateParcel(Long id, ParcelRequest request);
    ParcelDTO getParcelById(Long id);
    List<ParcelDTO> getAllParcels();
    List<ParcelDTO> getParcelsByLocation(String location);
    void deleteParcel(Long id);

    CropDTO createCrop(CropRequest request);
    CropDTO updateCrop(Long id, CropRequest request);
    CropDTO getCropById(Long id);
    List<CropDTO> getCropsByParcelId(Long parcelId);
    List<CropDTO> getAllCrops();
    void deleteCrop(Long id);

    HarvestDTO createHarvest(HarvestRequest request);
    HarvestDTO getHarvestById(Long id);
    List<HarvestDTO> getHarvestsByCropId(Long cropId);
    List<HarvestDTO> getHarvestsByParcelId(Long parcelId);
    void deleteHarvest(Long id);
}