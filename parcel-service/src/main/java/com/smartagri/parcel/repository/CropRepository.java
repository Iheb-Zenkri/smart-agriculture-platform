package com.smartagri.parcel.repository;


import com.smartagri.parcel.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByParcelId(Long parcelId);

    List<Crop> findByCropType(String cropType);

    List<Crop> findByStatus(String status);

    @Query("SELECT c FROM Crop c LEFT JOIN FETCH c.harvests WHERE c.id = :id")
    Optional<Crop> findByIdWithHarvests(@Param("id") Long id);

    @Query("SELECT c FROM Crop c WHERE c.parcel.id = :parcelId AND c.status = :status")
    List<Crop> findByParcelIdAndStatus(@Param("parcelId") Long parcelId, @Param("status") String status);
}