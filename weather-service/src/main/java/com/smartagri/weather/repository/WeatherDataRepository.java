package com.smartagri.weather.repository;


import com.smartagri.weather.model.Location;
import com.smartagri.weather.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    Optional<WeatherData> findByLocationAndDate(Location location, LocalDate date);

    @Query("SELECT w FROM WeatherData w JOIN w.location l " +
            "WHERE l.name = :name AND w.date BETWEEN :startDate AND :endDate " +
            "ORDER BY w.date")
    List<WeatherData> findByLocationAndDateBetween(
            @Param("name") String name,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


}
