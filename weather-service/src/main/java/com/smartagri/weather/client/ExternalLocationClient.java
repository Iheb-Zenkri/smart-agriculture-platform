package com.smartagri.weather.client;

import com.smartagri.weather.model.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalLocationClient {

    private final WebClient.Builder webClientBuilder;

    public Location fetchCoordinates(String location) {
        try {
            WebClient webClient = webClientBuilder.build();
            Map<String, Object>[] response = webClient.get()
                    .uri("https://nominatim.openstreetmap.org/search?format=json&q={location}", location)
                    .retrieve()
                    .bodyToMono(Map[].class)
                    .block();

            if (response != null && response.length > 0) {
                Location fetchedLocation = new Location();
                fetchedLocation.setName((String) response[0].get("name"));
                fetchedLocation.setLatitude(new BigDecimal((String) response[0].get("lat")));
                fetchedLocation.setLongitude(new BigDecimal((String) response[0].get("lon")));
                return fetchedLocation;
            }

        } catch (Exception e) {
            log.error("Failed to fetch coordinates for {}: {}", location, e.getMessage());
        }

        return new Location();
    }
}
