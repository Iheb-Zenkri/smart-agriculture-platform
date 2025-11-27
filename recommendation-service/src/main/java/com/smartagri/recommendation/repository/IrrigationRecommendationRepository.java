package com.smartagri.recommendation.repository;

import com.smartagri.recommendation.model.IrrigationRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IrrigationRecommendationRepository extends JpaRepository<IrrigationRecommendation, Long> {

    List<IrrigationRecommendation> findByParcelId(Long parcelId);

    Optional<IrrigationRecommendation> findByParcelIdAndRecommendationDate(Long parcelId, LocalDate date);

    @Query("SELECT i FROM IrrigationRecommendation i WHERE i.parcelId = :parcelId " +
            "AND i.recommendationDate BETWEEN :startDate AND :endDate ORDER BY i.recommendationDate DESC")
    List<IrrigationRecommendation> findByParcelIdAndDateRange(
            @Param("parcelId") Long parcelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT i FROM IrrigationRecommendation i WHERE i.parcelId = :parcelId " +
            "ORDER BY i.recommendationDate DESC")
    List<IrrigationRecommendation> findLatestByParcelId(@Param("parcelId") Long parcelId);
}
