package com.smartagri.recommendation.client;

import com.smartagri.recommendation.dto.CropInfoDTO;
import com.smartagri.recommendation.dto.ParcelInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParcelServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.parcel-service.url:http://localhost:8081}")
    private String parcelServiceUrl;

    public ParcelInfoDTO getParcelById(Long parcelId) {
        log.info("Fetching parcel info from Parcel Service for ID: {}", parcelId);

        try {
            WebClient webClient = webClientBuilder.baseUrl(parcelServiceUrl).build();

            return webClient.get()
                    .uri("/api/parcels/{id}", parcelId)
                    .retrieve()
                    .bodyToMono(ParcelInfoDTO.class)
                    .onErrorResume(e -> {
                        log.error("Error fetching parcel info: {}", e.getMessage());
                        return Mono.just(createDummyParcelInfo(parcelId));
                    })
                    .block();

        } catch (Exception e) {
            log.error("Error calling Parcel Service: {}", e.getMessage());
            return createDummyParcelInfo(parcelId);
        }
    }

    public List<CropInfoDTO> getCropsByParcelId(Long parcelId) {
        log.info("Fetching crops from Parcel Service for parcel ID: {}", parcelId);

        try {
            WebClient webClient = webClientBuilder.baseUrl(parcelServiceUrl).build();

            return webClient.get()
                    .uri("/api/crops/parcel/{parcelId}", parcelId)
                    .retrieve()
                    .bodyToFlux(CropInfoDTO.class)
                    .onErrorResume(e -> {
                        log.error("Error fetching crops: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .collectList()
                    .block();

        } catch (Exception e) {
            log.error("Error calling Parcel Service for crops: {}", e.getMessage());
            return List.of();
        }
    }

    public CropInfoDTO getCropById(Long cropId) {
        log.info("Fetching crop info from Parcel Service for ID: {}", cropId);

        try {
            WebClient webClient = webClientBuilder.baseUrl(parcelServiceUrl).build();

            return webClient.get()
                    .uri("/api/crops/{id}", cropId)
                    .retrieve()
                    .bodyToMono(CropInfoDTO.class)
                    .onErrorResume(e -> {
                        log.error("Error fetching crop info: {}", e.getMessage());
                        return Mono.just(createDummyCropInfo(cropId));
                    })
                    .block();

        } catch (Exception e) {
            log.error("Error calling Parcel Service for crop: {}", e.getMessage());
            return createDummyCropInfo(cropId);
        }
    }

    public boolean isParcelServiceAvailable() {
        try {
            WebClient webClient = webClientBuilder.baseUrl(parcelServiceUrl).build();

            String health = webClient.get()
                    .uri("/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return health != null && health.contains("UP");
        } catch (Exception e) {
            log.warn("Parcel Service is not available: {}", e.getMessage());
            return false;
        }
    }

    private ParcelInfoDTO createDummyParcelInfo(Long parcelId) {
        log.warn("Creating dummy parcel info for ID: {}", parcelId);

        ParcelInfoDTO dto = new ParcelInfoDTO();
        dto.setId(parcelId);
        dto.setName("Parcel " + parcelId);
        dto.setLocation("Unknown Location");
        dto.setLatitude(new BigDecimal("36.8065"));
        dto.setLongitude(new BigDecimal("10.1815"));
        dto.setSurfaceArea(new BigDecimal("5.0"));
        dto.setSoilType("Unknown");
        dto.setIrrigationSystem("Unknown");
        return dto;
    }

    private CropInfoDTO createDummyCropInfo(Long cropId) {
        log.warn("Creating dummy crop info for ID: {}", cropId);

        CropInfoDTO dto = new CropInfoDTO();
        dto.setId(cropId);
        dto.setParcelId(1L);
        dto.setCropType("Unknown");
        dto.setVariety("Unknown");
        dto.setPlantingDate(LocalDate.now());
        dto.setExpectedHarvestDate(LocalDate.now().plusMonths(4));
        dto.setGrowthStage("Unknown");
        dto.setStatus("Active");
        return dto;
    }
}