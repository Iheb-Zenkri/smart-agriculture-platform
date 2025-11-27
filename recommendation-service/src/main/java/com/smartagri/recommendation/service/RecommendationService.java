package com.smartagri.recommendation.service;


import com.smartagri.recommendation.dto.*;

import java.util.List;

public interface RecommendationService {

    // Irrigation Recommendations
    IrrigationRecommendationDTO getIrrigationRecommendation(Long id);
    List<IrrigationRecommendationDTO> getIrrigationRecommendationsByParcel(Long parcelId);
    IrrigationRecommendationDTO getLatestIrrigationRecommendation(Long parcelId);
    IrrigationRecommendationDTO generateIrrigationRecommendation(Long parcelId);
    void deleteIrrigationRecommendation(Long id);

    // Fertilization Recommendations
    FertilizationRecommendationDTO getFertilizationRecommendation(Long id);
    List<FertilizationRecommendationDTO> getFertilizationRecommendationsByCrop(Long cropId);
    FertilizationRecommendationDTO generateFertilizationRecommendation(Long cropId);
    void deleteFertilizationRecommendation(Long id);

    // Treatment Recommendations
    TreatmentRecommendationDTO getTreatmentRecommendation(Long id);
    List<TreatmentRecommendationDTO> getTreatmentRecommendationsByCrop(Long cropId);
    List<TreatmentRecommendationDTO> getUpcomingTreatments(Long cropId);
    TreatmentRecommendationDTO generateTreatmentRecommendation(Long cropId);
    void deleteTreatmentRecommendation(Long id);

    // Crop Plans
    CropPlanDTO getCropPlan(Long id);
    List<CropPlanDTO> getCropPlansByParcel(Long parcelId);
    CropPlanDTO getBestCropPlan(Long parcelId);
    CropPlanDTO generateCropPlan(Long parcelId);
    void deleteCropPlan(Long id);

    // Complex Operations
    List<Object> getAllRecommendationsForParcel(Long parcelId);
}
