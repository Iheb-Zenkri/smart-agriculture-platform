package com.smartagri.weather.service;


import com.smartagri.weather.client.ExternalLocationClient;
import com.smartagri.weather.client.ExternalWeatherClient;
import com.smartagri.weather.endpoint.generated.*;
import com.smartagri.weather.exception.WeatherDataNotFoundException;
import com.smartagri.weather.model.ClimateIndex;
import com.smartagri.weather.model.Location;
import com.smartagri.weather.model.WeatherData;
import com.smartagri.weather.repository.ClimateIndexRepository;
import com.smartagri.weather.repository.LocationRepository;
import com.smartagri.weather.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WeatherServiceImpl implements WeatherService {

    private final WeatherDataRepository weatherDataRepository;
    private final ClimateIndexRepository climateIndexRepository;
    private final LocationRepository locationRepository;
    private final ExternalLocationClient externalLocationClient;
    private final ExternalWeatherClient externalWeatherClient;

    @Override
    @Transactional
    public WeatherInfo getWeatherByLocationAndDate(String location, LocalDate date) {
        log.info("Getting weather data for location: {} and date: {}", location, date);

        Location dbLocation = locationRepository.findByNameIgnoreCase(location)
                .orElseGet(() -> {
                    log.info("Location data not found in database, fetching from external API");
                    Location fetchedLocationFromExternalAPI = externalLocationClient.fetchCoordinates(location);
                    return locationRepository.findByNameIgnoreCase(fetchedLocationFromExternalAPI.getName())
                            .orElseGet(() -> locationRepository.save(fetchedLocationFromExternalAPI));
                });

        WeatherData weatherData = weatherDataRepository.findByLocationAndDate(dbLocation, date)
                .orElseGet(() -> {
                    log.info("Weather data not found in database, fetching from external API");

                    WeatherData fetchedData = externalWeatherClient.fetchWeatherData(dbLocation, date);
                    fetchedData.setLocation(dbLocation);
                    return weatherDataRepository.save(fetchedData);
                });

        return convertToWeatherInfo(weatherData);
    }

    @Override
    @Transactional
    public List<WeatherInfo> getHistoricalWeather(String location, LocalDate startDate, LocalDate endDate) {
        log.info("Getting historical weather for location: {} from {} to {}",
                location, startDate, endDate);

        Location dbLocation = locationRepository.findByNameIgnoreCase(location)
                .orElseGet(() -> {
                    log.info("Location data not found in database, fetching from external API");
                    Location fetchedLocationFromExternalAPI = externalLocationClient.fetchCoordinates(location);
                    return locationRepository.findByNameIgnoreCase(fetchedLocationFromExternalAPI.getName())
                            .orElseGet(() -> locationRepository.save(fetchedLocationFromExternalAPI));
                });

        List<WeatherData> weatherDataList = weatherDataRepository
                .findByLocationAndDateBetween(dbLocation.getName(), startDate, endDate);

        if (weatherDataList.isEmpty()) {
            log.info("No historical data found, fetching from external API");
            weatherDataList = externalWeatherClient.fetchHistoricalWeatherData(dbLocation, startDate, endDate);
            weatherDataList.forEach(weatherData -> weatherData.setLocation(dbLocation));
            weatherDataList = weatherDataRepository.saveAll(weatherDataList);
        }

        return weatherDataList.stream()
                .map(this::convertToWeatherInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompareWeatherResponse compareWeather(String location1, String location2, LocalDate date) {
        log.info("Comparing weather between {} and {} for date: {}", location1, location2, date);

        WeatherInfo weather1 = getWeatherByLocationAndDate(location1, date);
        WeatherInfo weather2 = getWeatherByLocationAndDate(location2, date);

        CompareWeatherResponse response = new CompareWeatherResponse();
        response.setLocation1Weather(weather1);
        response.setLocation2Weather(weather2);
        response.setComparison(generateComparison(weather1, weather2));

        return response;
    }

    @Override
    @Transactional
    public GetClimateIndexResponse getClimateIndex(String location, LocalDate date) {
        log.info("Getting climate index for location: {} and date: {}", location, date);

        Location dbLocation = locationRepository.findByNameIgnoreCase(location)
                .orElseGet(() -> {
                    log.info("Location data not found in database, fetching from external API");
                    Location fetchedLocationFromExternalAPI = externalLocationClient.fetchCoordinates(location);
                    return locationRepository.findByNameIgnoreCase(fetchedLocationFromExternalAPI.getName())
                            .orElseGet(() -> locationRepository.save(fetchedLocationFromExternalAPI));
                });

        ClimateIndex climateIndex = climateIndexRepository.findByLocationAndDate(dbLocation, date)
                .orElseGet(() -> {
                    log.info("Climate index not found, calculating from weather data");
                    return calculateAndSaveClimateIndex(dbLocation, date);
                });

        return convertToClimateIndexResponse(climateIndex);
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    private WeatherInfo convertToWeatherInfo(WeatherData weatherData) {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setLocation(weatherData.getLocation().getName());
        weatherInfo.setDate(weatherData.getDate());
        weatherInfo.setTemperatureMin(weatherData.getTemperatureMin());
        weatherInfo.setTemperatureMax(weatherData.getTemperatureMax());
        weatherInfo.setTemperatureAvg(weatherData.getTemperatureAvg());
        weatherInfo.setHumidity(weatherData.getHumidity());
        weatherInfo.setPrecipitation(weatherData.getPrecipitation());
        weatherInfo.setWindSpeed(weatherData.getWindSpeed());
        weatherInfo.setPressure(weatherData.getPressure());
        weatherInfo.setWeatherCondition(weatherData.getWeatherCondition());
        weatherInfo.setCloudCover(weatherData.getCloudCover());
        return weatherInfo;
    }

    private String generateComparison(WeatherInfo weather1, WeatherInfo weather2) {
        StringBuilder comparison = new StringBuilder();

        BigDecimal tempDiff = weather1.getTemperatureAvg().subtract(weather2.getTemperatureAvg());
        comparison.append(String.format("Temperature difference: %s is %.2f°C %s than %s. ",
                weather1.getLocation(),
                tempDiff.abs(),
                tempDiff.compareTo(BigDecimal.ZERO) > 0 ? "warmer" : "cooler",
                weather2.getLocation()));

        BigDecimal humidityDiff = weather1.getHumidity().subtract(weather2.getHumidity());
        comparison.append(String.format("Humidity difference: %s is %.2f%% %s than %s. ",
                weather1.getLocation(),
                humidityDiff.abs(),
                humidityDiff.compareTo(BigDecimal.ZERO) > 0 ? "more humid" : "less humid",
                weather2.getLocation()));

        BigDecimal precipDiff = weather1.getPrecipitation().subtract(weather2.getPrecipitation());
        comparison.append(String.format("Precipitation difference: %s received %.2fmm %s than %s.",
                weather1.getLocation(),
                precipDiff.abs(),
                precipDiff.compareTo(BigDecimal.ZERO) > 0 ? "more" : "less",
                weather2.getLocation()));

        return comparison.toString();
    }

    private ClimateIndex calculateAndSaveClimateIndex(Location location, LocalDate date) {
        // Get weather data for the location
        WeatherData weatherData = weatherDataRepository.findByLocationAndDate(location, date)
                .orElseThrow(() -> new WeatherDataNotFoundException(
                        "Weather data not found for location: " + location.getName() + " and date: " + date));

        ClimateIndex climateIndex = new ClimateIndex();
        climateIndex.setLocation(location);
        climateIndex.setDate(date);

        // Calculate Growing Degree Days (GDD)
        // Formula: (Tmax + Tmin) / 2 - Base Temperature (usually 10°C for most crops)
        BigDecimal baseTemp = new BigDecimal("10");
        BigDecimal avgTemp = weatherData.getTemperatureAvg();
        BigDecimal gdd = avgTemp.subtract(baseTemp).max(BigDecimal.ZERO);
        climateIndex.setGrowingDegreeDays(gdd.setScale(2, RoundingMode.HALF_UP));

        // Calculate Evapotranspiration (simplified Penman-Monteith)
        BigDecimal et = calculateEvapotranspiration(weatherData);
        climateIndex.setEvapotranspiration(et);

        // Calculate Drought Index
        BigDecimal droughtIndex = calculateDroughtIndex(location.getName(), date);
        climateIndex.setDroughtIndex(droughtIndex);

        // Calculate Heat Stress Index
        BigDecimal heatStressIndex = calculateHeatStressIndex(weatherData);
        climateIndex.setHeatStressIndex(heatStressIndex);

        return climateIndexRepository.save(climateIndex);
    }

    private BigDecimal calculateEvapotranspiration(WeatherData weatherData) {
        // Simplified ET calculation
        // ET = 0.0023 * (Tmax + Tmin)/2 * (Tmax - Tmin)^0.5 * Ra
        // For simplicity, using a basic formula
        BigDecimal temp = weatherData.getTemperatureAvg();
        BigDecimal tempRange = weatherData.getTemperatureMax().subtract(weatherData.getTemperatureMin());

        BigDecimal et = temp.multiply(new BigDecimal("0.1"))
                .multiply(tempRange.add(BigDecimal.ONE))
                .divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP);

        return et.max(BigDecimal.ZERO);
    }

    private BigDecimal calculateDroughtIndex(String location, LocalDate date) {
        // Calculate drought index based on precipitation over last 30 days
        LocalDate startDate = date.minusDays(30);

        List<WeatherData> recentData = weatherDataRepository
                .findByLocationAndDateBetween(location, startDate, date);

        if (recentData.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPrecipitation = recentData.stream()
                .map(WeatherData::getPrecipitation)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Expected precipitation (example: 50mm per month)
        BigDecimal expectedPrecipitation = new BigDecimal("50");

        // Drought index: (Expected - Actual) / Expected
        BigDecimal droughtIndex = expectedPrecipitation.subtract(totalPrecipitation)
                .divide(expectedPrecipitation, 2, RoundingMode.HALF_UP);

        return droughtIndex.max(BigDecimal.ZERO).min(BigDecimal.ONE);
    }

    private BigDecimal calculateHeatStressIndex(WeatherData weatherData) {
        // Heat stress index based on temperature and humidity
        BigDecimal temp = weatherData.getTemperatureAvg();
        BigDecimal humidity = weatherData.getHumidity();

        // Simplified heat index formula
        BigDecimal heatIndex = temp.add(humidity.multiply(new BigDecimal("0.05")));

        // Normalize to 0-100 scale
        return heatIndex.subtract(new BigDecimal("20"))
                .max(BigDecimal.ZERO)
                .min(new BigDecimal("100"));
    }

    private GetClimateIndexResponse convertToClimateIndexResponse(ClimateIndex climateIndex) {
        GetClimateIndexResponse response = new GetClimateIndexResponse();
        response.setLocation(climateIndex.getLocation().getName());
        response.setDate(climateIndex.getDate());
        response.setGrowingDegreeDays(climateIndex.getGrowingDegreeDays());
        response.setEvapotranspiration(climateIndex.getEvapotranspiration());
        response.setDroughtIndex(climateIndex.getDroughtIndex());
        response.setHeatStressIndex(climateIndex.getHeatStressIndex());
        return response;
    }
}