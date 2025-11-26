package com.smartagri.weather.client;


import com.smartagri.weather.model.Location;
import com.smartagri.weather.model.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalWeatherClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${weather.api.openmeteo.base-url:https://api.open-meteo.com/v1}")
    private String baseUrl;

    public WeatherData fetchWeatherData(Location location, LocalDate date) {
        log.info("Fetching weather data from Open-Meteo API for location: {} and date: {}", location.getName(), date);

        try {
            String url = String.format("%s/forecast?latitude=%s&longitude=%s&daily=temperature_2m_max," +
                            "temperature_2m_min,precipitation_sum,windspeed_10m_max,relative_humidity_2m_mean," +
                            "cloud_cover_mean,temperature_2m_mean,pressure_msl_mean,weather_code&"+
                            "timezone=auto&start_date=%s&end_date=%s",
                    baseUrl,
                    location.getLatitude(),
                    location.getLongitude(),
                    date.format(DateTimeFormatter.ISO_DATE),
                    date.format(DateTimeFormatter.ISO_DATE));

            WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();

            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return parseWeatherResponse(response, location, date);

        } catch (Exception e) {
            log.error("Error fetching weather data from API: {}", e.getMessage());
            return createDummyWeatherData(location, date);
        }
    }

    public List<WeatherData> fetchHistoricalWeatherData(Location location, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching historical weather data for location: {} from {} to {}",
                location.getName(), startDate, endDate);

        List<WeatherData> weatherDataList = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            weatherDataList.add(fetchWeatherData(location, currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return weatherDataList;
    }

    private WeatherData parseWeatherResponse(Map<String, Object> response,
                                             Location location,LocalDate date) {
        WeatherData weatherData = new WeatherData();
        weatherData.setLocation(location);
        weatherData.setDate(date);

        try {
            Map<String, Object> daily = (Map<String, Object>) response.get("daily");
            List<Double> tempMax = (List<Double>) daily.get("temperature_2m_max");
            List<Double> tempMin = (List<Double>) daily.get("temperature_2m_min");
            List<Double> tempAvg = (List<Double>) daily.get("temperature_2m_mean");
            List<Double> precipitation = (List<Double>) daily.get("precipitation_sum");
            List<Double> windSpeed = (List<Double>) daily.get("windspeed_10m_max");
            List<Double> pressure = (List<Double>) daily.get("pressure_msl_mean");
            List<Integer> humidity = (List<Integer>) daily.get("relative_humidity_2m_mean");
            List<Integer> weatherCode = (List<Integer>) daily.get("weather_code");
            List<Integer> cloudCover = (List<Integer>) daily.get("cloud_cover_mean");

            weatherData.setTemperatureMax(BigDecimal.valueOf(tempMax.get(0)));
            weatherData.setTemperatureMin(BigDecimal.valueOf(tempMin.get(0)));
            weatherData.setTemperatureAvg(BigDecimal.valueOf(tempAvg.get(0)));
            weatherData.setPrecipitation(BigDecimal.valueOf(precipitation.get(0)));
            weatherData.setWindSpeed(BigDecimal.valueOf(windSpeed.get(0)));
            weatherData.setHumidity(BigDecimal.valueOf(humidity.get(0)));
            weatherData.setPressure(BigDecimal.valueOf(pressure.get(0)));
            weatherData.setWeatherCondition(determineWeatherCondition(weatherCode.get(0)));
            weatherData.setCloudCover(cloudCover.get(0));

        } catch (Exception e) {
            log.error("Error parsing weather response: {}", e.getMessage());
            return createDummyWeatherData(location, date);
        }

        return weatherData;
    }

    private String determineWeatherCondition(int weatherCode) {
        return switch (weatherCode) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Mainly clear, partly cloudy, overcast";
            case 45, 48 -> "Fog or depositing rime fog";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing rain";
            case 71, 73, 75 -> "Snow fall";
            case 77 -> "Snow grains";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with hail";
            default -> "Unknown";
        };
    }

    private WeatherData createDummyWeatherData(Location location, LocalDate date) {
        log.warn("Creating dummy weather data for location: {} and date: {}", location, date);

        WeatherData weatherData = new WeatherData();
        weatherData.setLocation(location);
        weatherData.setDate(date);
        weatherData.setTemperatureMin(new BigDecimal("15"));
        weatherData.setTemperatureMax(new BigDecimal("25"));
        weatherData.setTemperatureAvg(new BigDecimal("20"));
        weatherData.setHumidity(new BigDecimal("60"));
        weatherData.setPrecipitation(new BigDecimal("2"));
        weatherData.setWindSpeed(new BigDecimal("10"));
        weatherData.setPressure(new BigDecimal("1013"));
        weatherData.setWeatherCondition("Clear");
        weatherData.setCloudCover(30);

        return weatherData;
    }
}