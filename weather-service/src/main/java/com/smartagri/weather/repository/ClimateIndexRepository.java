package com.smartagri.weather.repository;


import com.smartagri.weather.model.ClimateIndex;
import com.smartagri.weather.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClimateIndexRepository extends JpaRepository<ClimateIndex, Long> {

    Optional<ClimateIndex> findByLocationAndDate(Location location, LocalDate date);
    @Query("SELECT c FROM ClimateIndex c JOIN c.location l " +
            "WHERE l.name = :name AND c.date BETWEEN :startDate AND :endDate " +
            "ORDER BY c.date")
    List<ClimateIndex> findByLocationNameAndDateBetween(
            @Param("name") String name,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}