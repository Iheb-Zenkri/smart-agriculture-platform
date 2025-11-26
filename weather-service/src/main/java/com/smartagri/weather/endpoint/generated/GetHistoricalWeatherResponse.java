package com.smartagri.weather.endpoint.generated;


import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "weatherInfoList"
})
@XmlRootElement(name = "getHistoricalWeatherResponse", namespace = "http://smartagri.com/weather")
@Data
public class GetHistoricalWeatherResponse {

    @XmlElement(namespace = "http://smartagri.com/weather", required = true)
    private List<WeatherInfo> weatherInfoList = new ArrayList<>();
}
