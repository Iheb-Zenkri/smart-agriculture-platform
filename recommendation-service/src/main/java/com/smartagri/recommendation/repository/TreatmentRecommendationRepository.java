package com.smartagri.recommendation.repository;


import com.smartagri.recommendation.model.TreatmentRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TreatmentRecommendationRepository extends JpaRepository<TreatmentRecommendation, Long> {

    List<TreatmentRecommendation> findByParcelId(Long parcelId);

    List<TreatmentRecommendation> findByCropId(Long cropId);

    @Query("SELECT t FROM TreatmentRecommendation t WHERE t.cropId = :cropId " +
            "AND t.recommendationDate >= :date ORDER BY t.recommendationDate ASC")
    List<TreatmentRecommendation> findUpcomingByCropId(
            @Param("cropId") Long cropId,
            @Param("date") LocalDate date
    );
}
