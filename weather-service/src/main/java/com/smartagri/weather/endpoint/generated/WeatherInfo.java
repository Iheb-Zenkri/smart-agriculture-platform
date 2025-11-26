package com.smartagri.weather.endpoint.generated;


import com.smartagri.weather.config.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "weatherInfo", propOrder = {
        "location",
        "date",
        "temperatureMin",
        "temperatureMax",
        "temperatureAvg",
        "humidity",
        "precipitation",
        "windSpeed",
        "pressure",
        "weatherCondition",
        "cloudCover"
})
@Data
public class WeatherInfo {

    @XmlElement(required = true)
    private String location;

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate date;

    @XmlElement(required = true)
    private BigDecimal temperatureMin;

    @XmlElement(required = true)
    private BigDecimal temperatureMax;

    @XmlElement(required = true)
    private BigDecimal temperatureAvg;

    @XmlElement(required = true)
    private BigDecimal humidity;

    @XmlElement(required = true)
    private BigDecimal precipitation;

    @XmlElement(required = true)
    private BigDecimal windSpeed;

    @XmlElement(required = true)
    private BigDecimal pressure;

    @XmlElement(required = true)
    private String weatherCondition;

    private Integer cloudCover;
}
