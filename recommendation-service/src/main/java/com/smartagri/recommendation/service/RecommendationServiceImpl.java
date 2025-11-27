package com.smartagri.recommendation.service;

import com.smartagri.recommendation.client.ParcelServiceClient;
import com.smartagri.recommendation.client.WeatherServiceClient;
import com.smartagri.recommendation.dto.*;
import com.smartagri.recommendation.exception.RecommendationNotFoundException;
import com.smartagri.recommendation.model.*;
import com.smartagri.recommendation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    private final IrrigationRecommendationRepository irrigationRepository;
    private final FertilizationRecommendationRepository fertilizationRepository;
    private final TreatmentRecommendationRepository treatmentRepository;
    private final CropPlanRepository cropPlanRepository;
    private final ParcelServiceClient parcelServiceClient;
    private final WeatherServiceClient weatherServiceClient;

    // ========================================================================
    // IRRIGATION RECOMMENDATIONS
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public IrrigationRecommendationDTO getIrrigationRecommendation(Long id) {
        log.info("Getting irrigation recommendation with ID: {}", id);
        IrrigationRecommendation recommendation = irrigationRepository.findById(id)
                .orElseThrow(() -> new RecommendationNotFoundException("Irrigation recommendation not found"));
        return convertToIrrigationDTO(recommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IrrigationRecommendationDTO> getIrrigationRecommendationsByParcel(Long parcelId) {
        log.info("Getting irrigation recommendations for parcel: {}", parcelId);
        return irrigationRepository.findByParcelId(parcelId).stream()
                .map(this::convertToIrrigationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IrrigationRecommendationDTO getLatestIrrigationRecommendation(Long parcelId) {
        log.info("Getting latest irrigation recommendation for parcel: {}", parcelId);
        List<IrrigationRecommendation> recommendations = irrigationRepository.findLatestByParcelId(parcelId);
        if (recommendations.isEmpty()) {
            return generateIrrigationRecommendation(parcelId);
        }
        return convertToIrrigationDTO(recommendations.get(0));
    }

    @Override
    public IrrigationRecommendationDTO generateIrrigationRecommendation(Long parcelId) {
        log.info("Generating irrigation recommendation for parcel: {}", parcelId);

        // Fetch parcel info
        ParcelInfoDTO parcel = parcelServiceClient.getParcelById(parcelId);

        // Fetch weather data
        WeatherInfoDTO weather = weatherServiceClient.getWeatherByLocation(
                parcel.getLocation(), LocalDate.now());

        // Calculate recommendation
        IrrigationRecommendation recommendation = new IrrigationRecommendation();
        recommendation.setParcelId(parcelId);
        recommendation.setRecommendationDate(LocalDate.now());

        // Calculate water amount based on weather
        BigDecimal waterAmount = calculateWaterAmount(weather, parcel);
        recommendation.setWaterAmount(waterAmount);

        // Determine frequency
        String frequency = determineIrrigationFrequency(weather);
        recommendation.setIrrigationFrequency(frequency);

        // Optimal time
        recommendation.setOptimalTime(determineOptimalIrrigationTime(weather));

        // Weather factors
        recommendation.setBasedOnTemperature(weather.getTemperatureAvg());
        recommendation.setBasedOnHumidity(weather.getHumidity());
        recommendation.setBasedOnPrecipitation(weather.getPrecipitation());

        // Reasoning
        String reasoning = String.format(
                "Based on current weather: Temp %.1f°C, Humidity %.1f%%, Precipitation %.1fmm. " +
                        "Recommended %s irrigation with %.1fmm water.",
                weather.getTemperatureAvg(), weather.getHumidity(), weather.getPrecipitation(),
                frequency, waterAmount);
        recommendation.setReasoning(reasoning);

        // Confidence score
        recommendation.setConfidenceScore(new BigDecimal("0.85"));

        IrrigationRecommendation saved = irrigationRepository.save(recommendation);
        return convertToIrrigationDTO(saved);
    }

    @Override
    public void deleteIrrigationRecommendation(Long id) {
        log.info("Deleting irrigation recommendation: {}", id);
        irrigationRepository.deleteById(id);
    }

    // ========================================================================
    // FERTILIZATION RECOMMENDATIONS
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public FertilizationRecommendationDTO getFertilizationRecommendation(Long id) {
        log.info("Getting fertilization recommendation with ID: {}", id);
        FertilizationRecommendation recommendation = fertilizationRepository.findById(id)
                .orElseThrow(() -> new RecommendationNotFoundException("Fertilization recommendation not found"));
        return convertToFertilizationDTO(recommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FertilizationRecommendationDTO> getFertilizationRecommendationsByCrop(Long cropId) {
        log.info("Getting fertilization recommendations for crop: {}", cropId);
        return fertilizationRepository.findByCropId(cropId).stream()
                .map(this::convertToFertilizationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FertilizationRecommendationDTO generateFertilizationRecommendation(Long cropId) {
        log.info("Generating fertilization recommendation for crop: {}", cropId);

        // Fetch crop info
        CropInfoDTO crop = parcelServiceClient.getCropById(cropId);
        ParcelInfoDTO parcel = parcelServiceClient.getParcelById(crop.getParcelId());

        // Generate recommendation
        FertilizationRecommendation recommendation = new FertilizationRecommendation();
        recommendation.setCropId(cropId);
        recommendation.setParcelId(crop.getParcelId());
        recommendation.setRecommendationDate(LocalDate.now());

        // Determine fertilizer based on crop and growth stage
        String fertilizerType = determineFertilizerType(crop);
        String npkRatio = determineNPKRatio(crop);
        BigDecimal quantity = calculateFertilizerQuantity(crop, parcel);

        recommendation.setFertilizerType(fertilizerType);
        recommendation.setNpkRatio(npkRatio);
        recommendation.setQuantity(quantity);
        recommendation.setApplicationMethod("Broadcast Application");
        recommendation.setGrowthStage(crop.getGrowthStage());
        recommendation.setSoilType(parcel.getSoilType());

        String reasoning = String.format(
                "For %s in %s stage on %s soil, recommended %s with NPK ratio %s at %.1f kg/ha.",
                crop.getCropType(), crop.getGrowthStage(), parcel.getSoilType(),
                fertilizerType, npkRatio, quantity);
        recommendation.setReasoning(reasoning);

        FertilizationRecommendation saved = fertilizationRepository.save(recommendation);
        return convertToFertilizationDTO(saved);
    }

    @Override
    public void deleteFertilizationRecommendation(Long id) {
        log.info("Deleting fertilization recommendation: {}", id);
        fertilizationRepository.deleteById(id);
    }

    // ========================================================================
    // TREATMENT RECOMMENDATIONS
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public TreatmentRecommendationDTO getTreatmentRecommendation(Long id) {
        log.info("Getting treatment recommendation with ID: {}", id);
        TreatmentRecommendation recommendation = treatmentRepository.findById(id)
                .orElseThrow(() -> new RecommendationNotFoundException("Treatment recommendation not found"));
        return convertToTreatmentDTO(recommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentRecommendationDTO> getTreatmentRecommendationsByCrop(Long cropId) {
        log.info("Getting treatment recommendations for crop: {}", cropId);
        return treatmentRepository.findByCropId(cropId).stream()
                .map(this::convertToTreatmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentRecommendationDTO> getUpcomingTreatments(Long cropId) {
        log.info("Getting upcoming treatments for crop: {}", cropId);
        return treatmentRepository.findUpcomingByCropId(cropId, LocalDate.now()).stream()
                .map(this::convertToTreatmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TreatmentRecommendationDTO generateTreatmentRecommendation(Long cropId) {
        log.info("Generating treatment recommendation for crop: {}", cropId);

        CropInfoDTO crop = parcelServiceClient.getCropById(cropId);

        TreatmentRecommendation recommendation = new TreatmentRecommendation();
        recommendation.setCropId(cropId);
        recommendation.setParcelId(crop.getParcelId());
        recommendation.setRecommendationDate(LocalDate.now());
        recommendation.setTreatmentType("Preventive");
        recommendation.setProductName("Organic Pesticide");
        recommendation.setDosage("2L/ha");
        recommendation.setTargetPest("General Pests");
        recommendation.setApplicationTiming("Early Morning");
        recommendation.setWeatherConditions("Dry, calm conditions");
        recommendation.setReasoning("Preventive treatment recommended for " + crop.getCropType());

        TreatmentRecommendation saved = treatmentRepository.save(recommendation);
        return convertToTreatmentDTO(saved);
    }

    @Override
    public void deleteTreatmentRecommendation(Long id) {
        log.info("Deleting treatment recommendation: {}", id);
        treatmentRepository.deleteById(id);
    }

    // ========================================================================
    // CROP PLANS
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public CropPlanDTO getCropPlan(Long id) {
        log.info("Getting crop plan with ID: {}", id);
        CropPlan plan = cropPlanRepository.findById(id)
                .orElseThrow(() -> new RecommendationNotFoundException("Crop plan not found"));
        return convertToCropPlanDTO(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropPlanDTO> getCropPlansByParcel(Long parcelId) {
        log.info("Getting crop plans for parcel: {}", parcelId);
        return cropPlanRepository.findByParcelIdOrderByConfidence(parcelId).stream()
                .map(this::convertToCropPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CropPlanDTO getBestCropPlan(Long parcelId) {
        log.info("Getting best crop plan for parcel: {}", parcelId);
        return cropPlanRepository.findTopByParcelIdOrderByConfidenceScoreDescCreatedAtDesc(parcelId)
                .map(this::convertToCropPlanDTO)
                .orElseGet(() -> generateCropPlan(parcelId));
    }

    @Override
    public CropPlanDTO generateCropPlan(Long parcelId) {
        log.info("Generating crop plan for parcel: {}", parcelId);

        ParcelInfoDTO parcel = parcelServiceClient.getParcelById(parcelId);

        CropPlan plan = new CropPlan();
        plan.setParcelId(parcelId);
        plan.setRecommendedCrop(recommendCropForSoil(parcel.getSoilType()));
        plan.setRecommendedVariety("Standard");
        plan.setPlantingDate(LocalDate.now().plusMonths(1));
        plan.setExpectedHarvestDate(LocalDate.now().plusMonths(5));
        plan.setExpectedYield(new BigDecimal("8.5"));
        plan.setConfidenceScore(new BigDecimal("0.80"));
        plan.setSoilSuitability("Good");
        plan.setClimateSuitability("Excellent");
        plan.setReasoning(String.format("Recommended %s for %s soil type",
                plan.getRecommendedCrop(), parcel.getSoilType()));

        CropPlan saved = cropPlanRepository.save(plan);
        return convertToCropPlanDTO(saved);
    }

    @Override
    public void deleteCropPlan(Long id) {
        log.info("Deleting crop plan: {}", id);
        cropPlanRepository.deleteById(id);
    }

    @Override
    public List<Object> getAllRecommendationsForParcel(Long parcelId) {
        log.info("Getting all recommendations for parcel: {}", parcelId);
        List<Object> all = new ArrayList<>();
        all.addAll(getIrrigationRecommendationsByParcel(parcelId));
        all.addAll(getCropPlansByParcel(parcelId));
        return all;
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    private BigDecimal calculateWaterAmount(WeatherInfoDTO weather, ParcelInfoDTO parcel) {
        BigDecimal baseAmount = new BigDecimal("10");
        BigDecimal tempFactor = weather.getTemperatureAvg().subtract(new BigDecimal("20"))
                .multiply(new BigDecimal("0.5"));
        BigDecimal precipitationReduction = weather.getPrecipitation().multiply(new BigDecimal("0.5"));

        return baseAmount.add(tempFactor).subtract(precipitationReduction)
                .max(new BigDecimal("5"))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private String determineIrrigationFrequency(WeatherInfoDTO weather) {
        if (weather.getPrecipitation().compareTo(new BigDecimal("10")) > 0) {
            return "None needed";
        } else if (weather.getTemperatureAvg().compareTo(new BigDecimal("25")) > 0) {
            return "Daily";
        } else {
            return "Every 2-3 days";
        }
    }

    private String determineOptimalIrrigationTime(WeatherInfoDTO weather) {
        return "Early morning (6-8 AM) or evening (6-8 PM)";
    }

    private String determineFertilizerType(CropInfoDTO crop) {
        return switch (crop.getCropType().toLowerCase()) {
            case "wheat", "barley" -> "Nitrogen-rich fertilizer";
            case "tomato", "potato" -> "Balanced NPK fertilizer";
            case "olive" -> "Potassium-rich fertilizer";
            default -> "Organic compost";
        };
    }

    private String determineNPKRatio(CropInfoDTO crop) {
        return switch (crop.getCropType().toLowerCase()) {
            case "wheat", "barley" -> "20-10-10";
            case "tomato" -> "10-20-20";
            case "olive" -> "10-10-20";
            default -> "10-10-10";
        };
    }

    private BigDecimal calculateFertilizerQuantity(CropInfoDTO crop, ParcelInfoDTO parcel) {
        BigDecimal baseQuantity = new BigDecimal("100");
        BigDecimal areaFactor = parcel.getSurfaceArea().multiply(new BigDecimal("20"));
        return baseQuantity.add(areaFactor).setScale(1, RoundingMode.HALF_UP);
    }

    private String recommendCropForSoil(String soilType) {
        return switch (soilType.toLowerCase()) {
            case "clay" -> "Wheat";
            case "sandy" -> "Carrot";
            case "loamy" -> "Tomato";
            default -> "Corn";
        };
    }

    private IrrigationRecommendationDTO convertToIrrigationDTO(IrrigationRecommendation rec) {
        IrrigationRecommendationDTO dto = new IrrigationRecommendationDTO();
        dto.setId(rec.getId());
        dto.setParcelId(rec.getParcelId());
        dto.setRecommendationDate(rec.getRecommendationDate());
        dto.setWaterAmount(rec.getWaterAmount());
        dto.setIrrigationFrequency(rec.getIrrigationFrequency());
        dto.setOptimalTime(rec.getOptimalTime());
        dto.setReasoning(rec.getReasoning());
        dto.setConfidenceScore(rec.getConfidenceScore());

        IrrigationRecommendationDTO.WeatherFactors factors = new IrrigationRecommendationDTO.WeatherFactors();
        factors.setTemperature(rec.getBasedOnTemperature());
        factors.setHumidity(rec.getBasedOnHumidity());
        factors.setPrecipitation(rec.getBasedOnPrecipitation());
        dto.setWeatherFactors(factors);

        return dto;
    }

    private FertilizationRecommendationDTO convertToFertilizationDTO(FertilizationRecommendation rec) {
        FertilizationRecommendationDTO dto = new FertilizationRecommendationDTO();
        dto.setId(rec.getId());
        dto.setParcelId(rec.getParcelId());
        dto.setCropId(rec.getCropId());
        dto.setRecommendationDate(rec.getRecommendationDate());
        dto.setFertilizerType(rec.getFertilizerType());
        dto.setNpkRatio(rec.getNpkRatio());
        dto.setQuantity(rec.getQuantity());
        dto.setApplicationMethod(rec.getApplicationMethod());
        dto.setReasoning(rec.getReasoning());
        dto.setGrowthStage(rec.getGrowthStage());
        dto.setSoilType(rec.getSoilType());
        return dto;
    }

    private TreatmentRecommendationDTO convertToTreatmentDTO(TreatmentRecommendation rec) {
        TreatmentRecommendationDTO dto = new TreatmentRecommendationDTO();
        dto.setId(rec.getId());
        dto.setParcelId(rec.getParcelId());
        dto.setCropId(rec.getCropId());
        dto.setRecommendationDate(rec.getRecommendationDate());
        dto.setTreatmentType(rec.getTreatmentType());
        dto.setProductName(rec.getProductName());
        dto.setDosage(rec.getDosage());
        dto.setTargetPest(rec.getTargetPest());
        dto.setApplicationTiming(rec.getApplicationTiming());
        dto.setReasoning(rec.getReasoning());
        dto.setWeatherConditions(rec.getWeatherConditions());
        return dto;
    }

    private CropPlanDTO convertToCropPlanDTO(CropPlan plan) {
        CropPlanDTO dto = new CropPlanDTO();
        dto.setId(plan.getId());
        dto.setParcelId(plan.getParcelId());
        dto.setRecommendedCrop(plan.getRecommendedCrop());
        dto.setRecommendedVariety(plan.getRecommendedVariety());
        dto.setPlantingDate(plan.getPlantingDate());
        dto.setExpectedHarvestDate(plan.getExpectedHarvestDate());
        dto.setExpectedYield(plan.getExpectedYield());
        dto.setConfidenceScore(plan.getConfidenceScore());
        dto.setReasoning(plan.getReasoning());
        dto.setSoilSuitability(plan.getSoilSuitability());
        dto.setClimateSuitability(plan.getClimateSuitability());
        return dto;
    }
}