package com.smartagri.weather.endpoint.generated;


import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "location1Weather",
        "location2Weather",
        "comparison"
})
@XmlRootElement(name = "compareWeatherResponse", namespace = "http://smartagri.com/weather")
@Data
public class CompareWeatherResponse {

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    private WeatherInfo location1Weather;

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    private WeatherInfo location2Weather;

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    private String comparison;
}