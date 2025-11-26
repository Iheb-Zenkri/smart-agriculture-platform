package com.smartagri.weather.endpoint.generated;


import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "location",
        "date",
        "growingDegreeDays",
        "evapotranspiration",
        "droughtIndex",
        "heatStressIndex"
})
@XmlRootElement(name = "getClimateIndexResponse", namespace = "http://smartagri.com/weather")
@Data
public class GetClimateIndexResponse {

    @XmlElement(required = true)
    private String location;

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    private LocalDate date;

    @XmlElement(required = true)
    private BigDecimal growingDegreeDays;

    @XmlElement(required = true)
    private BigDecimal evapotranspiration;

    @XmlElement(required = true)
    private BigDecimal droughtIndex;

    @XmlElement(required = true)
    private BigDecimal heatStressIndex;
}
