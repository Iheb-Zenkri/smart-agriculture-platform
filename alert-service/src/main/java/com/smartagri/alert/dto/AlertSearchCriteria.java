package com.smartagri.alert.dto;

import com.smartagri.alert.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Criteria for searching and filtering alerts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSearchCriteria {

    /**
     * Filter by parcel ID
     */
    private Long parcelId;

    /**
     * Filter by alert type
     */
    private Alert.AlertType alertType;

    /**
     * Filter by severity level
     */
    private Alert.AlertSeverity severity;

    /**
     * Filter by active status
     */
    private Boolean isActive;

    /**
     * Filter by acknowledgement status
     */
    private Boolean acknowledged;

    /**
     * Filter alerts created after this date
     */
    private LocalDateTime startDate;

    /**
     * Filter alerts created before this date
     */
    private LocalDateTime endDate;

    /**
     * Search in title and message
     */
    private String searchText;

    /**
     * Filter by location
     */
    private String location;

    @Override
    public String toString() {
        return "AlertSearchCriteria{" +
                "parcelId=" + parcelId +
                ", alertType=" + alertType +
                ", severity=" + severity +
                ", isActive=" + isActive +
                ", acknowledged=" + acknowledged +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", searchText='" + searchText + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}