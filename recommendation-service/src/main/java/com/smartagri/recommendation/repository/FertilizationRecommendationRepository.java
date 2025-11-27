package com.smartagri.recommendation.repository;

import com.smartagri.recommendation.model.FertilizationRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FertilizationRecommendationRepository extends JpaRepository<FertilizationRecommendation, Long> {

    List<FertilizationRecommendation> findByParcelId(Long parcelId);

    List<FertilizationRecommendation> findByCropId(Long cropId);

    @Query("SELECT f FROM FertilizationRecommendation f WHERE f.cropId = :cropId " +
            "AND f.recommendationDate BETWEEN :startDate AND :endDate ORDER BY f.recommendationDate DESC")
    List<FertilizationRecommendation> findByCropIdAndDateRange(
            @Param("cropId") Long cropId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
