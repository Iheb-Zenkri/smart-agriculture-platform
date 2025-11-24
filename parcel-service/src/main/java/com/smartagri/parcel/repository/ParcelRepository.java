package com.smartagri.parcel.repository;
import com.smartagri.parcel.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    List<Parcel> findByLocation(String location);

    List<Parcel> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Parcel p LEFT JOIN FETCH p.crops WHERE p.id = :id")
    Optional<Parcel> findByIdWithCrops(@Param("id") Long id);

    @Query("SELECT p FROM Parcel p WHERE p.soilType = :soilType")
    List<Parcel> findBySoilType(@Param("soilType") String soilType);
}

