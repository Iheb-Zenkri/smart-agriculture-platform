package com.smartagri.weather.service;

import com.smartagri.weather.endpoint.generated.CompareWeatherResponse;
import com.smartagri.weather.endpoint.generated.GetClimateIndexResponse;
import com.smartagri.weather.endpoint.generated.WeatherInfo;
import com.smartagri.weather.model.WeatherData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeatherService {
    @Transactional
    WeatherInfo getWeatherByLocationAndDate(String location, LocalDate date);

    @Transactional
    List<WeatherInfo> getHistoricalWeather(String location, LocalDate startDate, LocalDate endDate);

    @Transactional
    CompareWeatherResponse compareWeather(String location1, String location2, LocalDate date);

    @Transactional
    GetClimateIndexResponse getClimateIndex(String location, LocalDate date);
}
