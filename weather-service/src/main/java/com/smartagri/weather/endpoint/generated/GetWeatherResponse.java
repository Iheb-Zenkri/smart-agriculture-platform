package com.smartagri.weather.endpoint.generated;


import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "weatherInfo"
})
@XmlRootElement(name = "getWeatherResponse", namespace = "http://smartagri.com/weather")
@Data
public class GetWeatherResponse {

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    private WeatherInfo weatherInfo;
}
