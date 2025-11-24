package com.smartagri.parcel.repository;


import com.smartagri.parcel.model.Harvest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HarvestRepository extends JpaRepository<Harvest, Long> {

    List<Harvest> findByCropId(Long cropId);

    @Query("SELECT h FROM Harvest h WHERE h.crop.parcel.id = :parcelId")
    List<Harvest> findByParcelId(@Param("parcelId") Long parcelId);

    @Query("SELECT h FROM Harvest h WHERE h.harvestDate BETWEEN :startDate AND :endDate")
    List<Harvest> findByHarvestDateBetween(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}
