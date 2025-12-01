package com.smartagri.alert;

import com.smartagri.alert.grpc.generated.*;
import com.smartagri.alert.model.Alert;
import com.smartagri.alert.service.AlertGrpcService;
import com.smartagri.alert.service.AlertService;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertGrpcServiceTest {

    @Mock
    private AlertService alertService;

    @Mock
    private StreamObserver<AlertResponse> alertResponseObserver;

    @Mock
    private StreamObserver<AlertListResponse> alertListResponseObserver;

    @Mock
    private StreamObserver<DismissAlertResponse> dismissResponseObserver;

    @Mock
    private StreamObserver<SubscribeResponse> subscribeResponseObserver;

    @Mock
    private StreamObserver<SubscriptionResponse> subscriptionResponseObserver;

    private AlertGrpcService grpcService;

    @BeforeEach
    void setUp() {
        grpcService = new AlertGrpcService(alertService);
    }

    // ==================== CreateAlert Tests ====================

    @Test
    void testCreateAlert_Success() {
        // Given
        CreateAlertRequest request = CreateAlertRequest.newBuilder()
                .setAlertType("WEATHER")
                .setSeverity("HIGH")
                .setParcelId(1L)
                .setTitle("High Temperature Alert")
                .setMessage("Temperature exceeded threshold")
                .setExpirySeconds(3600L)
                .build();

        Alert mockAlert = createMockAlert(1L, Alert.AlertType.WEATHER,
                Alert.AlertSeverity.HIGH, "High Temperature Alert");

        when(alertService.createAlert(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockAlert);

        // When
        grpcService.createAlert(request, alertResponseObserver);

        // Then
        ArgumentCaptor<AlertResponse> captor = ArgumentCaptor.forClass(AlertResponse.class);
        verify(alertResponseObserver).onNext(captor.capture());
        verify(alertResponseObserver).onCompleted();
        verify(alertResponseObserver, never()).onError(any());

        AlertResponse response = captor.getValue();
        assertEquals(1L, response.getId());
        assertEquals("WEATHER", response.getAlertType());
        assertEquals("HIGH", response.getSeverity());
        assertEquals("High Temperature Alert", response.getTitle());
    }

    @Test
    void testCreateAlert_MissingRequiredFields() {
        // Given
        CreateAlertRequest request = CreateAlertRequest.newBuilder()
                .setAlertType("") // Empty type
                .setSeverity("HIGH")
                .setTitle("Test")
                .setMessage("Test message")
                .build();

        // When
        grpcService.createAlert(request, alertResponseObserver);

        // Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(alertResponseObserver).onError(captor.capture());
        verify(alertResponseObserver, never()).onCompleted();

        Throwable error = captor.getValue();
        System.out.println(error.getClass());
        assertTrue(error instanceof StatusException);
        assertEquals(Status.INVALID_ARGUMENT.getCode(),
                ((StatusException) error).getStatus().getCode());
    }

    // ==================== GetAlert Tests ====================

    @Test
    void testGetAlert_Success() {
        // Given
        Long alertId = 1L;
        GetAlertRequest request = GetAlertRequest.newBuilder()
                .setAlertId(alertId)
                .build();

        Alert mockAlert = createMockAlert(alertId, Alert.AlertType.THRESHOLD,
                Alert.AlertSeverity.HIGH, "Test Alert");

        when(alertService.getAlertById(alertId)).thenReturn(mockAlert);

        // When
        grpcService.getAlert(request, alertResponseObserver);

        // Then
        verify(alertResponseObserver).onNext(any(AlertResponse.class));
        verify(alertResponseObserver).onCompleted();
    }

    @Test
    void testGetAlert_NotFound() {
        // Given
        Long alertId = 999L;
        GetAlertRequest request = GetAlertRequest.newBuilder()
                .setAlertId(alertId)
                .build();

        when(alertService.getAlertById(alertId))
                .thenThrow(new com.smartagri.alert.exception.AlertNotFoundException("Not found"));

        // When
        grpcService.getAlert(request, alertResponseObserver);

        // Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(alertResponseObserver).onError(captor.capture());

        Throwable error = captor.getValue();
        assertTrue(error instanceof StatusException);
        assertEquals(Status.NOT_FOUND.getCode(),
                ((StatusException) error).getStatus().getCode());
    }

    // ==================== GetActiveAlerts Tests ====================

    @Test
    void testGetActiveAlerts_AllAlerts() {
        // Given
        GetActiveAlertsRequest request = GetActiveAlertsRequest.newBuilder().build();

        List<Alert> mockAlerts = Arrays.asList(
                createMockAlert(1L, Alert.AlertType.HARVEST, Alert.AlertSeverity.HIGH, "Alert 1"),
                createMockAlert(2L, Alert.AlertType.FERTILIZATION, Alert.AlertSeverity.MEDIUM, "Alert 2")
        );

        when(alertService.getActiveAlerts()).thenReturn(mockAlerts);

        // When
        grpcService.getActiveAlerts(request, alertListResponseObserver);

        // Then
        ArgumentCaptor<AlertListResponse> captor = ArgumentCaptor.forClass(AlertListResponse.class);
        verify(alertListResponseObserver).onNext(captor.capture());
        verify(alertListResponseObserver).onCompleted();

        AlertListResponse response = captor.getValue();
        assertEquals(2, response.getTotalCount());
        assertEquals(2, response.getAlertsCount());
    }

    @Test
    void testGetActiveAlerts_ByParcel() {
        // Given
        Long parcelId = 5L;
        GetActiveAlertsRequest request = GetActiveAlertsRequest.newBuilder()
                .setParcelId(parcelId)
                .build();

        List<Alert> mockAlerts = Arrays.asList(
                createMockAlert(1L, Alert.AlertType.THRESHOLD, Alert.AlertSeverity.HIGH, "Alert 1")
        );

        when(alertService.getActiveAlertsByParcel(parcelId)).thenReturn(mockAlerts);

        // When
        grpcService.getActiveAlerts(request, alertListResponseObserver);

        // Then
        verify(alertService).getActiveAlertsByParcel(parcelId);
        verify(alertListResponseObserver).onNext(any(AlertListResponse.class));
        verify(alertListResponseObserver).onCompleted();
    }

    @Test
    void testGetActiveAlerts_WithLimit() {
        // Given
        GetActiveAlertsRequest request = GetActiveAlertsRequest.newBuilder()
                .setLimit(1)
                .build();

        List<Alert> mockAlerts = Arrays.asList(
                createMockAlert(1L, Alert.AlertType.THRESHOLD, Alert.AlertSeverity.HIGH, "Alert 1"),
                createMockAlert(2L, Alert.AlertType.WEATHER, Alert.AlertSeverity.MEDIUM, "Alert 2")
        );

        when(alertService.getActiveAlerts()).thenReturn(mockAlerts);

        // When
        grpcService.getActiveAlerts(request, alertListResponseObserver);

        // Then
        ArgumentCaptor<AlertListResponse> captor = ArgumentCaptor.forClass(AlertListResponse.class);
        verify(alertListResponseObserver).onNext(captor.capture());

        AlertListResponse response = captor.getValue();
        assertEquals(1, response.getAlertsCount()); // Limited to 1
    }

    // ==================== AcknowledgeAlert Tests ====================

    @Test
    void testAcknowledgeAlert_Success() {
        // Given
        Long alertId = 1L;
        String acknowledgedBy = "user@example.com";

        AcknowledgeAlertRequest request = AcknowledgeAlertRequest.newBuilder()
                .setAlertId(alertId)
                .setAcknowledgedBy(acknowledgedBy)
                .build();

        Alert mockAlert = createMockAlert(alertId, Alert.AlertType.WEATHER,
                Alert.AlertSeverity.HIGH, "Test Alert");
        mockAlert.setAcknowledged(true);
        mockAlert.setAcknowledgedBy(acknowledgedBy);
        mockAlert.setAcknowledgedAt(LocalDateTime.now());

        when(alertService.acknowledgeAlert(alertId, acknowledgedBy)).thenReturn(mockAlert);

        // When
        grpcService.acknowledgeAlert(request, alertResponseObserver);

        // Then
        verify(alertService).acknowledgeAlert(alertId, acknowledgedBy);
        verify(alertResponseObserver).onNext(any(AlertResponse.class));
        verify(alertResponseObserver).onCompleted();
    }

    // ==================== DismissAlert Tests ====================

    @Test
    void testDismissAlert_Success() {
        // Given
        Long alertId = 1L;
        String dismissedBy = "user@example.com";

        DismissAlertRequest request = DismissAlertRequest.newBuilder()
                .setAlertId(alertId)
                .setDismissedBy(dismissedBy)
                .build();

        doNothing().when(alertService).dismissAlert(alertId, dismissedBy);

        // When
        grpcService.dismissAlert(request, dismissResponseObserver);

        // Then
        ArgumentCaptor<DismissAlertResponse> captor =
                ArgumentCaptor.forClass(DismissAlertResponse.class);
        verify(dismissResponseObserver).onNext(captor.capture());
        verify(dismissResponseObserver).onCompleted();

        DismissAlertResponse response = captor.getValue();
        assertTrue(response.getSuccess());
        assertEquals("Alert dismissed successfully", response.getMessage());
    }

    // ==================== SubscribeToAlerts Tests ====================

    @Test
    void testSubscribeToAlerts_Success() {
        // Given
        String userId = "user123";
        Long subscriptionId = 10L;

        SubscribeRequest request = SubscribeRequest.newBuilder()
                .setUserId(userId)
                .setNotificationMethod("EMAIL")
                .setEmail("user@example.com")
                .addAlertTypes("TEMPERATURE_HIGH")
                .build();

        when(alertService.createSubscription(any(), any(), any(), any(), any(), any()))
                .thenReturn(subscriptionId);

        // When
        grpcService.subscribeToAlerts(request, subscribeResponseObserver);

        // Then
        ArgumentCaptor<SubscribeResponse> captor =
                ArgumentCaptor.forClass(SubscribeResponse.class);
        verify(subscribeResponseObserver).onNext(captor.capture());
        verify(subscribeResponseObserver).onCompleted();

        SubscribeResponse response = captor.getValue();
        assertTrue(response.getSuccess());
        assertEquals(subscriptionId, response.getSubscriptionId());
    }

    // ==================== Helper Methods ====================

    private Alert createMockAlert(Long id, Alert.AlertType type,
                                  Alert.AlertSeverity severity, String title) {
        Alert alert = new Alert();
        alert.setId(id);
        alert.setAlertType(type);
        alert.setSeverity(severity);
        alert.setTitle(title);
        alert.setMessage("Test message");
        alert.setAlertTime(LocalDateTime.now());
        alert.setIsActive(true);
        alert.setAcknowledged(false);
        return alert;
    }
}