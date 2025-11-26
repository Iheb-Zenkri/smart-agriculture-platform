package com.smartagri.weather.endpoint;

import com.smartagri.weather.endpoint.generated.*;
import com.smartagri.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
@RequiredArgsConstructor
@Slf4j
public class WeatherEndpoint {


    private static final String NAMESPACE = "http://smartagri.com/weather";
    private final WeatherService weatherService;

    @PayloadRoot(namespace = NAMESPACE, localPart = "getWeatherRequest")
    @ResponsePayload
    public GetWeatherResponse getWeather(@RequestPayload GetWeatherRequest request) {
        log.info("[SOAP] getWeather called for location={} date={}", request.getLocation(), request.getDate());


        WeatherInfo weatherInfo = weatherService.getWeatherByLocationAndDate(
                request.getLocation(),
                request.getDate()
        );


        GetWeatherResponse response = new GetWeatherResponse();
        response.setWeatherInfo(weatherInfo);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE, localPart = "getHistoricalWeatherRequest")
    @ResponsePayload
    public GetHistoricalWeatherResponse getHistoricalWeather(@RequestPayload GetHistoricalWeatherRequest request) {
        log.info("[SOAP] getHistoricalWeather called for location={} from {} to {}",
                request.getLocation(), request.getStartDate(), request.getEndDate());


        GetHistoricalWeatherResponse response = new GetHistoricalWeatherResponse();
        response.getWeatherInfoList().addAll(
                weatherService.getHistoricalWeather(request.getLocation(), request.getStartDate(), request.getEndDate())
        );
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "compareWeatherRequest")
    @ResponsePayload
    public CompareWeatherResponse compareWeather(@RequestPayload CompareWeatherRequest request) {
        log.info("[SOAP] compareWeather called for {} vs {} on {}",
                request.getLocation1(), request.getLocation2(), request.getDate());


        return weatherService.compareWeather(
                request.getLocation1(),
                request.getLocation2(),
                request.getDate()
        );
    }


   @PayloadRoot(namespace = NAMESPACE, localPart = "getClimateIndexRequest")
    @ResponsePayload
    public GetClimateIndexResponse getClimateIndex(@RequestPayload GetClimateIndexRequest request) {
        log.info("[SOAP] getClimateIndex called for location={} date={}", request.getLocation(), request.getDate());


        return weatherService.getClimateIndex(
                request.getLocation(),
                request.getDate()
        );
    }
}
