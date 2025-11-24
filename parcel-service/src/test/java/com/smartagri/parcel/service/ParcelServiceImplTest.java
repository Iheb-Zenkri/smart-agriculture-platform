package com.smartagri.parcel.service;


import com.smartagri.parcel.dto.ParcelDTO;
import com.smartagri.parcel.dto.ParcelRequest;
import com.smartagri.parcel.exception.ResourceNotFoundException;
import com.smartagri.parcel.model.Parcel;
import com.smartagri.parcel.repository.ParcelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceImplTest {

    @Mock
    private ParcelRepository parcelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ParcelServiceImpl parcelService;

    private Parcel parcel;
    private ParcelDTO parcelDTO;
    private ParcelRequest parcelRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        parcel = new Parcel();
        parcel.setId(1L);
        parcel.setName("Test Parcel");
        parcel.setLocation("Test Location");
        parcel.setLatitude(new BigDecimal("36.8065"));
        parcel.setLongitude(new BigDecimal("10.1815"));
        parcel.setSurfaceArea(new BigDecimal("5.5"));
        parcel.setSoilType("Clay");
        parcel.setIrrigationSystem("Drip");

        parcelDTO = new ParcelDTO();
        parcelDTO.setId(1L);
        parcelDTO.setName("Test Parcel");
        parcelDTO.setLocation("Test Location");

        parcelRequest = new ParcelRequest();
        parcelRequest.setName("Test Parcel");
        parcelRequest.setLocation("Test Location");
        parcelRequest.setLatitude(new BigDecimal("36.8065"));
        parcelRequest.setLongitude(new BigDecimal("10.1815"));
        parcelRequest.setSurfaceArea(new BigDecimal("5.5"));
        parcelRequest.setSoilType("Clay");
    }

    @Test
    void createParcel_Success() {
        // Arrange
        when(modelMapper.map(any(ParcelRequest.class), eq(Parcel.class))).thenReturn(parcel);
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcel);
        when(modelMapper.map(any(Parcel.class), eq(ParcelDTO.class))).thenReturn(parcelDTO);

        // Act
        ParcelDTO result = parcelService.createParcel(parcelRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Parcel", result.getName());
        verify(parcelRepository, times(1)).save(any(Parcel.class));
    }

    @Test
    void getParcelById_Success() {
        // Arrange
        when(parcelRepository.findByIdWithCrops(1L)).thenReturn(Optional.of(parcel));
        when(modelMapper.map(any(Parcel.class), eq(ParcelDTO.class))).thenReturn(parcelDTO);

        // Act
        ParcelDTO result = parcelService.getParcelById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(parcelRepository, times(1)).findByIdWithCrops(1L);
    }

    @Test
    void getParcelById_NotFound() {
        // Arrange
        when(parcelRepository.findByIdWithCrops(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            parcelService.getParcelById(1L);
        });
    }

    @Test
    void getAllParcels_Success() {
        // Arrange
        List<Parcel> parcels = Arrays.asList(parcel, new Parcel());
        when(parcelRepository.findAll()).thenReturn(parcels);
        when(modelMapper.map(any(Parcel.class), eq(ParcelDTO.class))).thenReturn(parcelDTO);

        // Act
        List<ParcelDTO> result = parcelService.getAllParcels();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(parcelRepository, times(1)).findAll();
    }

    @Test
    void deleteParcel_Success() {
        // Arrange
        when(parcelRepository.existsById(1L)).thenReturn(true);

        // Act
        parcelService.deleteParcel(1L);

        // Assert
        verify(parcelRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteParcel_NotFound() {
        // Arrange
        when(parcelRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            parcelService.deleteParcel(1L);
        });
    }
}