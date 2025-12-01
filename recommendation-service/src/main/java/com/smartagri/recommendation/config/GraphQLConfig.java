package com.smartagri.recommendation.config;

import com.smartagri.recommendation.dto.CropPlanDTO;
import com.smartagri.recommendation.dto.FertilizationRecommendationDTO;
import com.smartagri.recommendation.dto.IrrigationRecommendationDTO;
import com.smartagri.recommendation.dto.TreatmentRecommendationDTO;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .type("Recommendation", typeWiring -> typeWiring.typeResolver(env -> {
                    Object obj = env.getObject();
                    if (obj instanceof IrrigationRecommendationDTO) {
                        return env.getSchema().getObjectType("IrrigationRecommendation");
                    }
                    if (obj instanceof FertilizationRecommendationDTO) {
                        return env.getSchema().getObjectType("FertilizationRecommendation");
                    }
                    if (obj instanceof TreatmentRecommendationDTO) {
                        return env.getSchema().getObjectType("TreatmentRecommendation");
                    }
                    if (obj instanceof CropPlanDTO) {
                        return env.getSchema().getObjectType("CropPlan");
                    }
                    return null;
                }))
                .scalar(ExtendedScalars.Date)
                .scalar(DecimalScalar.DECIMAL);
    }
}