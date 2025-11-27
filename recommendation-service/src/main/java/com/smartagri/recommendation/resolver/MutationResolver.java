package com.smartagri.recommendation.resolver;


import com.smartagri.recommendation.dto.*;
import com.smartagri.recommendation.input.*;
import com.smartagri.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MutationResolver {

    private final RecommendationService recommendationService;

    // Generate Recommendations
    @MutationMapping
    public IrrigationRecommendationDTO generateIrrigationRecommendation(@Argument Long parcelId) {
        log.info("GraphQL Mutation: generateIrrigationRecommendation(parcelId: {})", parcelId);
        return recommendationService.generateIrrigationRecommendation(parcelId);
    }

    @MutationMapping
    public FertilizationRecommendationDTO generateFertilizationRecommendation(
            @Argument Long cropId) {
        log.info("GraphQL Mutation: generateFertilizationRecommendation(cropId: {})", cropId);
        return recommendationService.generateFertilizationRecommendation(cropId);
    }

    @MutationMapping
    public TreatmentRecommendationDTO generateTreatmentRecommendation(@Argument Long cropId) {
        log.info("GraphQL Mutation: generateTreatmentRecommendation(cropId: {})", cropId);
        return recommendationService.generateTreatmentRecommendation(cropId);
    }

    @MutationMapping
    public CropPlanDTO generateCropPlan(@Argument Long parcelId) {
        log.info("GraphQL Mutation: generateCropPlan(parcelId: {})", parcelId);
        return recommendationService.generateCropPlan(parcelId);
    }

    // Delete Recommendations
    @MutationMapping
    public Boolean deleteIrrigationRecommendation(@Argument Long id) {
        log.info("GraphQL Mutation: deleteIrrigationRecommendation(id: {})", id);
        recommendationService.deleteIrrigationRecommendation(id);
        return true;
    }

    @MutationMapping
    public Boolean deleteFertilizationRecommendation(@Argument Long id) {
        log.info("GraphQL Mutation: deleteFertilizationRecommendation(id: {})", id);
        recommendationService.deleteFertilizationRecommendation(id);
        return true;
    }

    @MutationMapping
    public Boolean deleteTreatmentRecommendation(@Argument Long id) {
        log.info("GraphQL Mutation: deleteTreatmentRecommendation(id: {})", id);
        recommendationService.deleteTreatmentRecommendation(id);
        return true;
    }

    @MutationMapping
    public Boolean deleteCropPlan(@Argument Long id) {
        log.info("GraphQL Mutation: deleteCropPlan(id: {})", id);
        recommendationService.deleteCropPlan(id);
        return true;
    }
}
