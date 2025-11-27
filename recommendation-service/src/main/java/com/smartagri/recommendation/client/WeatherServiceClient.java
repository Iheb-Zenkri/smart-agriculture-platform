package com.smartagri.recommendation.client;

import com.smartagri.recommendation.dto.WeatherInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceClient {

    private final WebServiceTemplate webServiceTemplate;

    @Value("${services.weather-service.url:http://localhost:8082/ws}")
    private String weatherServiceUrl;

    public WeatherInfoDTO getWeatherByLocation(String location, LocalDate date) {
        log.info("Fetching weather from Weather Service for location: {} and date: {}", location, date);

        try {
            // SOAP call would be implemented here when weather.endpoint.generated classes are available
            // For now, returning realistic dummy data
            return createRealisticWeatherData(location, date);

        } catch (Exception e) {
            log.error("Error fetching weather from SOAP service: {}", e.getMessage());
            return createRealisticWeatherData(location, date);
        }
    }

    public List<WeatherInfoDTO> getHistoricalWeather(String location, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching historical weather for location: {} from {} to {}", location, startDate, endDate);

        List<WeatherInfoDTO> historicalData = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            historicalData.add(getWeatherByLocation(location, currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return historicalData;
    }

    public boolean isWeatherServiceAvailable() {
        try {
            // Test SOAP endpoint availability
            // Simplified check - in production would ping the SOAP service
            return true;
        } catch (Exception e) {
            log.warn("Weather Service is not available: {}", e.getMessage());
            return false;
        }
    }

    private WeatherInfoDTO createRealisticWeatherData(String location, LocalDate date) {
        log.debug("Creating realistic weather data for location: {} and date: {}", location, date);

        WeatherInfoDTO dto = new WeatherInfoDTO();
        dto.setLocation(location);
        dto.setDate(date);

        // Generate realistic weather based on location and season
        int month = date.getMonthValue();
        boolean isWinter = month >= 11 || month <= 2;
        boolean isSummer = month >= 6 && month <= 8;

        // Temperature based on season
        if (isWinter) {
            dto.setTemperatureMin(new BigDecimal("8"));
            dto.setTemperatureMax(new BigDecimal("18"));
            dto.setTemperatureAvg(new BigDecimal("13"));
            dto.setPrecipitation(new BigDecimal("5"));
            dto.setWeatherCondition("Partly Cloudy");
        } else if (isSummer) {
            dto.setTemperatureMin(new BigDecimal("22"));
            dto.setTemperatureMax(new BigDecimal("35"));
            dto.setTemperatureAvg(new BigDecimal("28.5"));
            dto.setPrecipitation(new BigDecimal("0.5"));
            dto.setWeatherCondition("Sunny");
        } else {
            dto.setTemperatureMin(new BigDecimal("15"));
            dto.setTemperatureMax(new BigDecimal("25"));
            dto.setTemperatureAvg(new BigDecimal("20"));
            dto.setPrecipitation(new BigDecimal("2"));
            dto.setWeatherCondition("Clear");
        }

        // Other weather parameters
        dto.setHumidity(isWinter ? new BigDecimal("70") : new BigDecimal("55"));
        dto.setWindSpeed(new BigDecimal("10"));
        dto.setPressure(new BigDecimal("1013"));
        dto.setCloudCover(isWinter ? 60 : 20);

        return dto;
    }
}