package com.smartagri.weather.endpoint.generated;


import com.smartagri.weather.config.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "location",
        "startDate",
        "endDate"
})
@XmlRootElement(name = "getHistoricalWeatherRequest", namespace = "http://smartagri.com/weather")
@Data
public class GetHistoricalWeatherRequest {

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    private String location;

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    @XmlSchemaType(name = "date")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate startDate;

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    @XmlSchemaType(name = "date")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate endDate;
}