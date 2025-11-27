# 🌾 Recommendation Service - Smart Agriculture Platform

## 📋 Overview

GraphQL-based microservice providing intelligent agricultural recommendations including irrigation schedules, fertilization plans, treatment suggestions, and crop planning.

## 🚀 Quick Start

### Using Docker Compose
```bash
docker-compose -f docker-compose-recommendation.yml up --build
```

### Local Development
```bash
# Start PostgreSQL
docker run --name postgres-recommendation \
  -e POSTGRES_DB=recommendation_db \
  -e POSTGRES_USER=recommendation_user \
  -e POSTGRES_PASSWORD=recommendation_pass \
  -p 5434:5432 -d postgres:15-alpine

# Build & Run
mvn clean install
mvn spring-boot:run
```

## 📊 GraphQL Operations

### Queries

| Query | Description |
|-------|-------------|
| irrigationRecommendation | Get irrigation recommendation by ID |
| irrigationRecommendationsByParcel | Get all irrigation recommendations for a parcel |
| latestIrrigationRecommendation | Get latest irrigation recommendation |
| fertilizationRecommendationsByCrop | Get fertilization recommendations for a crop |
| treatmentRecommendationsByCrop | Get treatment recommendations |
| cropPlansByParcel | Get crop plans for a parcel |
| bestCropPlan | Get best crop plan for a parcel |

### Mutations

| Mutation | Description |
|----------|-------------|
| generateIrrigationRecommendation | Generate new irrigation recommendation |
| generateFertilizationRecommendation | Generate fertilization plan |
| generateTreatmentRecommendation | Generate treatment recommendation |
| generateCropPlan | Generate crop planting plan |

## 🌐 Endpoints

- **GraphQL**: http://localhost:8083/graphql
- **Health**: http://localhost:8083/actuator/health

## 🧪 Example GraphQL Queries

### Get Irrigation Recommendation
```graphql
query {
  latestIrrigationRecommendation(parcelId: 1) {
    id
    waterAmount
    irrigationFrequency
    optimalTime
    reasoning
    confidenceScore
    weatherFactors {
      temperature
      humidity
      precipitation
    }
  }
}
```

### Generate Recommendation
```graphql
mutation {
  generateIrrigationRecommendation(parcelId: 1) {
    id
    waterAmount
    irrigationFrequency
    reasoning
  }
}
```

### Get Crop Plan
```graphql
query {
  bestCropPlan(parcelId: 1) {
    recommendedCrop
    recommendedVariety
    plantingDate
    expectedHarvestDate
    expectedYield
    confidenceScore
    reasoning
  }
}
```

### Get All Recommendations
```graphql
query {
  irrigationRecommendationsByParcel(parcelId: 1) {
    id
    waterAmount
    irrigationFrequency
  }
  
  cropPlansByParcel(parcelId: 1) {
    recommendedCrop
    confidenceScore
  }
}
```

## 🔧 Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| SERVER_PORT | 8083 | Application port |
| SPRING_DATASOURCE_URL | jdbc:postgresql://localhost:5434/recommendation_db | Database URL |
| PARCEL_SERVICE_URL | http://localhost:8081 | Parcel Service URL |
| WEATHER_SERVICE_URL | http://localhost:8082/ws | Weather Service URL |

## 📦 Database Schema

### Irrigation Recommendations
- parcel_id, recommendation_date, water_amount
- irrigation_frequency, optimal_time
- based_on_temperature, humidity, precipitation
- reasoning, confidence_score

### Fertilization Recommendations
- parcel_id, crop_id, recommendation_date
- fertilizer_type, npk_ratio, quantity
- application_method, growth_stage

### Treatment Recommendations
- parcel_id, crop_id, recommendation_date
- treatment_type, product_name, dosage
- target_pest, application_timing

### Crop Plans
- parcel_id, recommended_crop, variety
- planting_date, expected_harvest_date
- expected_yield, confidence_score
- soil_suitability, climate_suitability

## 🧪 Running Tests

```bash
mvn test
mvn test -Dtest=RecommendationServiceImplTest
```

## 🐳 Docker Commands

```bash
# Build
docker build -t recommendation-service:latest .

# Run
docker run -d --name recommendation-service -p 8083:8083 recommendation-service:latest

# Logs
docker logs -f recommendation-service
```

## 🔗 Integration

This service integrates with:
- **Parcel Service (REST)**: Fetches parcel and crop information
- **Weather Service (SOAP)**: Fetches weather data for recommendations

## ✅ Verification

- [ ] Service starts: `mvn spring-boot:run`
- [ ] GraphiQL accessible: http://localhost:8083/graphiql
- [ ] Health check: http://localhost:8083/actuator/health
- [ ] Can query recommendations
- [ ] Can generate new recommendations

## 📞 Support

- Port: 8083
- Logs: logs/recommendation-service.log
- GraphiQL: /graphiql

---

**Recommendation Service Ready! 🌾💡**

<!-- ============================================================================
     .GITIGNORE
     File: .gitignore
     ============================================================================ -->

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup

# IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# Logs
logs/
*.log

# OS
.DS_Store
Thumbs.db

# Application
application-local.yml