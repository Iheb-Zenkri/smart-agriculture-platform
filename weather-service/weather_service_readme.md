# 🌤️ Weather Service - Smart Agriculture Platform

## 📋 Overview

The Weather Service is a SOAP-based microservice that provides weather data, historical weather information, climate indices, and weather comparisons for agricultural purposes.

## 🚀 Features

- ✅ SOAP Web Service with WSDL
- ✅ Integration with Open-Meteo API (free weather data)
- ✅ Historical weather data storage
- ✅ Climate indices calculation (GDD, ET, Drought Index, Heat Stress)
- ✅ Weather comparison between locations
- ✅ PostgreSQL database with JPA/Hibernate
- ✅ Comprehensive unit and integration tests
- ✅ Docker containerization

## 🛠️ Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **SOAP**: Spring Web Services
- **Database**: PostgreSQL 15
- **External API**: Open-Meteo (Free Weather API)
- **Build Tool**: Maven 3.9+
- **Containerization**: Docker & Docker Compose

## 📁 Project Structure

```
weather-service/
├── src/
│   ├── main/
│   │   ├── java/com/smartagri/weather/
│   │   │   ├── WeatherServiceApplication.java
│   │   │   ├── client/
│   │   │   │   └── ExternalWeatherClient.java
│   │   │   ├── config/
│   │   │   │   ├── WebServiceConfig.java
│   │   │   │   ├── WebClientConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── LocalDateAdapter.java
│   │   │   ├── endpoint/
│   │   │   │   ├── WeatherEndpoint.java
│   │   │   │   └── generated/ (JAXB generated classes)
│   │   │   ├── exception/
│   │   │   │   ├── WeatherDataNotFoundException.java
│   │   │   │   └── WeatherServiceException.java
│   │   │   ├── model/
│   │   │   │   ├── WeatherData.java
│   │   │   │   └── ClimateIndex.java
│   │   │   ├── repository/
│   │   │   │   ├── WeatherDataRepository.java
│   │   │   │   └── ClimateIndexRepository.java
│   │   │   └── service/
│   │   │       ├── WeatherService.java
│   │   │       └── WeatherServiceImpl.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── weather.xsd
│   │       └── data.sql
│   └── test/
├── Dockerfile
├── docker-compose-weather.yml
├── pom.xml
└── README.md
```

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and run
docker-compose -f docker-compose-weather.yml up --build

# Service will be available at http://localhost:8082
```

### Option 2: Local Development

```bash
# 1. Start PostgreSQL
docker run --name postgres-weather \
  -e POSTGRES_DB=weather_db \
  -e POSTGRES_USER=weather_user \
  -e POSTGRES_PASSWORD=weather_pass \
  -p 5433:5432 \
  -d postgres:15-alpine

# 2. Build
mvn clean install

# 3. Run
mvn spring-boot:run
```

## 📊 SOAP Operations

### Available Operations

1. **getWeather** - Get current weather for a location and date
2. **getHistoricalWeather** - Get weather data for a date range
3. **compareWeather** - Compare weather between two locations
4. **getClimateIndex** - Get agricultural climate indices

## 🧪 Testing with SOAP UI / Postman

### WSDL Location
```
http://localhost:8082/ws/weather.wsdl
```

### Example SOAP Requests

#### 1. Get Weather Request

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:wea="http://smartagri.com/weather">
   <soapenv:Header/>
   <soapenv:Body>
      <wea:getWeatherRequest>
         <wea:location>Tunis North</wea:location>
         <wea:date>2024-11-20</wea:date>
      </wea:getWeatherRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:getWeatherResponse xmlns:ns2="http://smartagri.com/weather">
         <ns2:weatherInfo>
            <ns2:location>Tunis North</ns2:location>
            <ns2:date>2024-11-20</ns2:date>
            <ns2:temperatureMin>15.5</ns2:temperatureMin>
            <ns2:temperatureMax>24.3</ns2:temperatureMax>
            <ns2:temperatureAvg>19.9</ns2:temperatureAvg>
            <ns2:humidity>65.0</ns2:humidity>
            <ns2:precipitation>0.0</ns2:precipitation>
            <ns2:windSpeed>12.5</ns2:windSpeed>
            <ns2:pressure>1013.2</ns2:pressure>
            <ns2:weatherCondition>Clear</ns2:weatherCondition>
            <ns2:cloudCover>20</ns2:cloudCover>
         </ns2:weatherInfo>
      </ns2:getWeatherResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

#### 2. Get Historical Weather Request

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:wea="http://smartagri.com/weather">
   <soapenv:Header/>
   <soapenv:Body>
      <wea:getHistoricalWeatherRequest>
         <wea:location>Tunis North</wea:location>
         <wea:startDate>2024-11-01</wea:startDate>
         <wea:endDate>2024-11-07</wea:endDate>
      </wea:getHistoricalWeatherRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

#### 3. Compare Weather Request

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:wea="http://smartagri.com/weather">
   <soapenv:Header/>
   <soapenv:Body>
      <wea:compareWeatherRequest>
         <wea:location1>Tunis North</wea:location1>
         <wea:location2>Ariana</wea:location2>
         <wea:date>2024-11-20</wea:date>
      </wea:compareWeatherRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:compareWeatherResponse xmlns:ns2="http://smartagri.com/weather">
         <ns2:location1Weather>
            <!-- Weather data for Tunis North -->
         </ns2:location1Weather>
         <ns2:location2Weather>
            <!-- Weather data for Ariana -->
         </ns2:location2Weather>
         <ns2:comparison>
            Temperature difference: Tunis North is 0.80°C warmer than Ariana. 
            Humidity difference: Tunis North is 3.00% less humid than Ariana. 
            Precipitation difference: Tunis North received 0.00mm same as Ariana.
         </ns2:comparison>
      </ns2:compareWeatherResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

#### 4. Get Climate Index Request

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:wea="http://smartagri.com/weather">
   <soapenv:Header/>
   <soapenv:Body>
      <wea:getClimateIndexRequest>
         <wea:location>Tunis North</wea:location>
         <wea:date>2024-11-20</wea:date>
      </wea:getClimateIndexRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:getClimateIndexResponse xmlns:ns2="http://smartagri.com/weather">
         <ns2:location>Tunis North</ns2:location>
         <ns2:date>2024-11-20</ns2:date>
         <ns2:growingDegreeDays>9.90</ns2:growingDegreeDays>
         <ns2:evapotranspiration>3.20</ns2:evapotranspiration>
         <ns2:droughtIndex>0.15</ns2:droughtIndex>
         <ns2:heatStressIndex>25.50</ns2:heatStressIndex>
      </ns2:getClimateIndexResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

## 🧪 Testing with cURL

```bash
# Get Weather
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml" \
  -d '
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:wea="http://smartagri.com/weather">
   <soapenv:Body>
      <wea:getWeatherRequest>
         <wea:location>Tunis North</wea:location>
         <wea:date>2024-11-20</wea:date>
      </wea:getWeatherRequest>
   </soapenv:Body>
</soapenv:Envelope>
'
```

## 📈 Climate Indices Explained

### Growing Degree Days (GDD)
- Measures heat accumulation for crop growth
- Formula: (Tmax + Tmin) / 2 - Base Temperature (10°C)
- Higher values = more heat for crop development

### Evapotranspiration (ET)
- Water loss from soil and plants
- Important for irrigation planning
- Measured in mm/day

### Drought Index
- Indicates water deficit
- Range: 0 (no drought) to 1 (severe drought)
- Based on precipitation vs. expected values

### Heat Stress Index
- Combines temperature and humidity
- Indicates plant stress conditions
- Range: 0-100 (higher = more stress)

## 📊 Database Schema

### Weather Data Table
```sql
CREATE TABLE weather_data (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    date DATE NOT NULL,
    temperature_min DECIMAL(5,2),
    temperature_max DECIMAL(5,2),
    temperature_avg DECIMAL(5,2),
    humidity DECIMAL(5,2),
    precipitation DECIMAL(6,2),
    wind_speed DECIMAL(5,2),
    pressure DECIMAL(6,2),
    weather_condition VARCHAR(100),
    cloud_cover INTEGER,
    created_at TIMESTAMP,
    UNIQUE(location, date)
);
```

### Climate Indices Table
```sql
CREATE TABLE climate_indices (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    growing_degree_days DECIMAL(6,2),
    evapotranspiration DECIMAL(6,2),
    drought_index DECIMAL(5,2),
    heat_stress_index DECIMAL(5,2),
    created_at TIMESTAMP
);
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8082 |
| `SPRING_DATASOURCE_URL` | Database URL | jdbc:postgresql://localhost:5433/weather_db |
| `SPRING_DATASOURCE_USERNAME` | Database username | weather_user |
| `SPRING_DATASOURCE_PASSWORD` | Database password | weather_pass |
| `WEATHER_API_OPENMETEO_BASE_URL` | Open-Meteo API URL | https://api.open-meteo.com/v1 |

## 🐳 Docker Commands

```bash
# Build
docker build -t weather-service:latest .

# Run
docker run -d \
  --name weather-service \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/weather_db \
  weather-service:latest

# Logs
docker logs -f weather-service

# Stop
docker stop weather-service && docker rm weather-service
```

## 🧪 Running Tests

```bash
# All tests
mvn test

# Specific test
mvn test -Dtest=WeatherServiceImplTest

# With coverage
mvn clean test jacoco:report
```

## 📚 External Weather API

This service uses **Open-Meteo** API (completely free, no API key required):
- Documentation: https://open-meteo.com/
- Features: Weather forecasts, historical data, climate data
- No rate limits for reasonable use

## 🔍 Troubleshooting

### SOAP Endpoint Not Found
```bash
# Verify WSDL is accessible
curl http://localhost:8082/ws/weather.wsdl

# Check if service is running
curl http://localhost:8082/actuator/health
```

### Database Connection Issues
```bash
# Test PostgreSQL connection
docker exec -it postgres-weather psql -U weather_user -d weather_db

# Check if tables exist
\dt
```

### JAXB Generation Issues
```bash
# Clean and regenerate classes
mvn clean
mvn jaxb2:xjc
mvn compile
```

## 📦 Dependencies

Key dependencies:
- Spring Boot Starter Web Services
- Spring Boot Starter Data JPA
- Spring Boot Starter WebFlux
- PostgreSQL Driver
- WSDL4J
- JAXB Runtime

## 🤝 Integration with Other Services

### From Recommendation Service (GraphQL)
The Recommendation Service can call Weather Service to get weather data for intelligent crop recommendations.

### From Orchestrator Service
The Orchestrator can coordinate calls between Parcel Service and Weather Service.

## 🎯 Next Steps

After Weather Service, implement:
1. **Recommendation Service** (GraphQL) - Uses weather data for recommendations
2. **Alert Service** (gRPC) - Sends alerts based on weather conditions

## 📞 Support

- WSDL: http://localhost:8082/ws/weather.wsdl
- Health: http://localhost:8082/actuator/health
- Logs: logs/weather-service.log

---

**Weather Service Ready! ☀️🌧️**