package com.smartagri.recommendation;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FullRecommendationGraphQLTest {

    @Autowired
    private GraphQlTester graphQlTester;

    private static Long irrigationId;
    private static Long fertilizationId;
    private static Long treatmentId;
    private static Long cropPlanId;

    private static final Long TEST_PARCEL_ID = 1L;
    private static final Long TEST_CROP_ID = 1L;

    // ============================
    // 1. CREATE / GENERATE DATA
    // ============================

    @Test
    @Order(1)
    void generateIrrigationRecommendation() {
        graphQlTester.document("""
                            mutation {
                              irrigation: generateIrrigationRecommendation(parcelId: %d) {
                                id
                                parcelId
                                waterAmount
                              }
                            }
                        """.formatted(TEST_PARCEL_ID))
                .execute()
                .path("irrigation.id").entity(Long.class).satisfies(id -> irrigationId = id)
                .path("irrigation.parcelId").entity(Long.class).isEqualTo(TEST_PARCEL_ID);
    }

    @Test
    @Order(2)
    void generateFertilizationRecommendation() {
        graphQlTester.document("""
                            mutation {
                              fertilization: generateFertilizationRecommendation(cropId: %d) {
                                id
                                cropId
                                fertilizerType
                              }
                            }
                        """.formatted(TEST_CROP_ID))
                .execute()
                .path("fertilization.id").entity(Long.class).satisfies(id -> fertilizationId = id)
                .path("fertilization.cropId").entity(Long.class).isEqualTo(TEST_CROP_ID);
    }

    @Test
    @Order(3)
    void generateTreatmentRecommendation() {
        graphQlTester.document("""
                            mutation {
                              treatment: generateTreatmentRecommendation(cropId: %d) {
                                id
                                cropId
                                treatmentType
                              }
                            }
                        """.formatted(TEST_CROP_ID))
                .execute()
                .path("treatment.id").entity(Long.class).satisfies(id -> treatmentId = id)
                .path("treatment.cropId").entity(Long.class).isEqualTo(TEST_CROP_ID);
    }

    @Test
    @Order(4)
    void generateCropPlan() {
        graphQlTester.document("""
                            mutation {
                              cropPlan: generateCropPlan(parcelId: %d) {
                                id
                                parcelId
                                recommendedCrop
                              }
                            }
                        """.formatted(TEST_PARCEL_ID))
                .execute()
                .path("cropPlan.id").entity(Long.class).satisfies(id -> cropPlanId = id)
                .path("cropPlan.parcelId").entity(Long.class).isEqualTo(TEST_PARCEL_ID);
    }

    // ============================
    // 2. READ / QUERY DATA
    // ============================

    @Test
    @Order(5)
    void testIrrigationQueries() {
        // By ID
        graphQlTester.document("""
                            query {
                              irrigationRecommendation(id: %d) {
                                id
                                waterAmount
                              }
                            }
                        """.formatted(irrigationId)).execute()
                .path("irrigationRecommendation.id").entity(Long.class).isEqualTo(irrigationId);

        // By Parcel
        graphQlTester.document("""
                            query {
                              irrigationRecommendationsByParcel(parcelId: %d) {
                                id
                                waterAmount
                              }
                            }
                        """.formatted(TEST_PARCEL_ID)).execute()
                .path("irrigationRecommendationsByParcel").entityList(Object.class).hasSizeGreaterThan(0);

        // Latest
        graphQlTester.document("""
                            query {
                              latestIrrigationRecommendation(parcelId: %d) {
                                id
                              }
                            }
                        """.formatted(TEST_PARCEL_ID)).execute()
                .path("latestIrrigationRecommendation.id").entity(Long.class).isEqualTo(irrigationId);
    }

    @Test
    @Order(6)
    void testFertilizationQueries() {
        // By ID
        graphQlTester.document("""
                            query {
                              fertilizationRecommendation(id: %d) {
                                id
                                fertilizerType
                              }
                            }
                        """.formatted(fertilizationId)).execute()
                .path("fertilizationRecommendation.id").entity(Long.class).isEqualTo(fertilizationId);

        // By Crop
        graphQlTester.document("""
                            query {
                              fertilizationRecommendationsByCrop(cropId: %d) {
                                id
                              }
                            }
                        """.formatted(TEST_CROP_ID)).execute()
                .path("fertilizationRecommendationsByCrop").entityList(Object.class).hasSizeGreaterThan(0);
    }

    @Test
    @Order(7)
    void testTreatmentQueries() {
        // By ID
        graphQlTester.document("""
                            query {
                              treatmentRecommendation(id: %d) {
                                id
                                treatmentType
                              }
                            }
                        """.formatted(treatmentId)).execute()
                .path("treatmentRecommendation.id").entity(Long.class).isEqualTo(treatmentId);

        // By Crop
        graphQlTester.document("""
                            query {
                              treatmentRecommendationsByCrop(cropId: %d) {
                                id
                              }
                            }
                        """.formatted(TEST_CROP_ID)).execute()
                .path("treatmentRecommendationsByCrop").entityList(Object.class).hasSizeGreaterThan(0);

        // Upcoming Treatments
        graphQlTester.document("""
                            query {
                              upcomingTreatments(cropId: %d) {
                                id
                              }
                            }
                        """.formatted(TEST_CROP_ID)).execute()
                .path("upcomingTreatments").entityList(Object.class).hasSizeGreaterThan(0);
    }

    @Test
    @Order(8)
    void testCropPlanQueries() {
        // By ID
        graphQlTester.document("""
                            query {
                              cropPlan(id: %d) {
                                id
                                recommendedCrop
                              }
                            }
                        """.formatted(cropPlanId)).execute()
                .path("cropPlan.id").entity(Long.class).isEqualTo(cropPlanId);

        // By Parcel
        graphQlTester.document("""
                            query {
                              cropPlansByParcel(parcelId: %d) {
                                id
                              }
                            }
                        """.formatted(TEST_PARCEL_ID)).execute()
                .path("cropPlansByParcel").entityList(Object.class).hasSizeGreaterThan(0);

        // Best Crop Plan
        graphQlTester.document("""
                            query {
                              bestCropPlan(parcelId: %d) {
                                id
                              }
                            }
                        """.formatted(TEST_PARCEL_ID)).execute()
                .path("bestCropPlan.id").entity(Long.class).isEqualTo(cropPlanId);
    }

    @Test
    @Order(9)
    void testAllRecommendationsForParcel() {
        graphQlTester.document("""
                            query {
                              allRecommendationsForParcel(parcelId: %d) {
                                ... on IrrigationRecommendation { id }
                                ... on FertilizationRecommendation { id }
                                ... on TreatmentRecommendation { id }
                                ... on CropPlan { id }
                              }
                            }
                        """.formatted(TEST_PARCEL_ID)).execute()
                .path("allRecommendationsForParcel").entityList(Object.class).hasSizeGreaterThan(0);
    }

    // ============================
    // 3. DELETE DATA
    // ============================

    @Test
    @Order(10)
    void deleteAllRecommendations() {
        graphQlTester.document("""
                    mutation { deleteIrrigationRecommendation(id: %d) }
                """.formatted(irrigationId)).execute().path("deleteIrrigationRecommendation").entity(Boolean.class).isEqualTo(true);

        graphQlTester.document("""
                    mutation { deleteFertilizationRecommendation(id: %d) }
                """.formatted(fertilizationId)).execute().path("deleteFertilizationRecommendation").entity(Boolean.class).isEqualTo(true);

        graphQlTester.document("""
                    mutation { deleteTreatmentRecommendation(id: %d) }
                """.formatted(treatmentId)).execute().path("deleteTreatmentRecommendation").entity(Boolean.class).isEqualTo(true);

        graphQlTester.document("""
                    mutation { deleteCropPlan(id: %d) }
                """.formatted(cropPlanId)).execute().path("deleteCropPlan").entity(Boolean.class).isEqualTo(true);
    }
}
