package com.smartagri.recommendation.repository;

import com.smartagri.recommendation.model.CropPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropPlanRepository extends JpaRepository<CropPlan, Long> {

    List<CropPlan> findByParcelId(Long parcelId);

    @Query("SELECT c FROM CropPlan c WHERE c.parcelId = :parcelId " +
            "ORDER BY c.confidenceScore DESC, c.createdAt DESC")
    List<CropPlan> findByParcelIdOrderByConfidence(@Param("parcelId") Long parcelId);

    Optional<CropPlan> findTopByParcelIdOrderByConfidenceScoreDescCreatedAtDesc(Long parcelId);
}