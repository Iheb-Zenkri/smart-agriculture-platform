package com.smartagri.recommendation.resolver;

import com.smartagri.recommendation.dto.*;
import com.smartagri.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryResolver {

    private final RecommendationService recommendationService;

    // Irrigation Recommendations
    @QueryMapping
    public IrrigationRecommendationDTO irrigationRecommendation(@Argument Long id) {
        log.info("GraphQL Query: irrigationRecommendation(id: {})", id);
        return recommendationService.getIrrigationRecommendation(id);
    }

    @QueryMapping
    public List<IrrigationRecommendationDTO> irrigationRecommendationsByParcel(@Argument Long parcelId) {
        log.info("GraphQL Query: irrigationRecommendationsByParcel(parcelId: {})", parcelId);
        return recommendationService.getIrrigationRecommendationsByParcel(parcelId);
    }

    @QueryMapping
    public IrrigationRecommendationDTO latestIrrigationRecommendation(@Argument Long parcelId) {
        log.info("GraphQL Query: latestIrrigationRecommendation(parcelId: {})", parcelId);
        return recommendationService.getLatestIrrigationRecommendation(parcelId);
    }

    // Fertilization Recommendations
    @QueryMapping
    public FertilizationRecommendationDTO fertilizationRecommendation(@Argument Long id) {
        log.info("GraphQL Query: fertilizationRecommendation(id: {})", id);
        return recommendationService.getFertilizationRecommendation(id);
    }

    @QueryMapping
    public List<FertilizationRecommendationDTO> fertilizationRecommendationsByCrop(@Argument Long cropId) {
        log.info("GraphQL Query: fertilizationRecommendationsByCrop(cropId: {})", cropId);
        return recommendationService.getFertilizationRecommendationsByCrop(cropId);
    }

    // Treatment Recommendations
    @QueryMapping
    public TreatmentRecommendationDTO treatmentRecommendation(@Argument Long id) {
        log.info("GraphQL Query: treatmentRecommendation(id: {})", id);
        return recommendationService.getTreatmentRecommendation(id);
    }

    @QueryMapping
    public List<TreatmentRecommendationDTO> treatmentRecommendationsByCrop(@Argument Long cropId) {
        log.info("GraphQL Query: treatmentRecommendationsByCrop(cropId: {})", cropId);
        return recommendationService.getTreatmentRecommendationsByCrop(cropId);
    }

    @QueryMapping
    public List<TreatmentRecommendationDTO> upcomingTreatments(@Argument Long cropId) {
        log.info("GraphQL Query: upcomingTreatments(cropId: {})", cropId);
        return recommendationService.getUpcomingTreatments(cropId);
    }

    // Crop Plans
    @QueryMapping
    public CropPlanDTO cropPlan(@Argument Long id) {
        log.info("GraphQL Query: cropPlan(id: {})", id);
        return recommendationService.getCropPlan(id);
    }

    @QueryMapping
    public List<CropPlanDTO> cropPlansByParcel(@Argument Long parcelId) {
        log.info("GraphQL Query: cropPlansByParcel(parcelId: {})", parcelId);
        return recommendationService.getCropPlansByParcel(parcelId);
    }

    @QueryMapping
    public CropPlanDTO bestCropPlan(@Argument Long parcelId) {
        log.info("GraphQL Query: bestCropPlan(parcelId: {})", parcelId);
        return recommendationService.getBestCropPlan(parcelId);
    }

    // Complex Queries
    @QueryMapping
    public List<Object> allRecommendationsForParcel(@Argument Long parcelId) {
        log.info("GraphQL Query: allRecommendationsForParcel(parcelId: {})", parcelId);
        return recommendationService.getAllRecommendationsForParcel(parcelId);
    }
}
