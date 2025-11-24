# 🌾 Parcel Service - Smart Agriculture Platform

## 📋 Overview

The Parcel Service is a RESTful microservice for managing agricultural parcels, crops, and harvest data in the Smart Agriculture Platform. It provides comprehensive CRUD operations with full validation, error handling, and OpenAPI documentation.

## 🚀 Features

- ✅ Complete REST API for Parcels, Crops, and Harvests
- ✅ PostgreSQL database with JPA/Hibernate
- ✅ Input validation with Jakarta Validation
- ✅ Global exception handling
- ✅ OpenAPI/Swagger documentation
- ✅ Docker containerization
- ✅ Comprehensive unit and integration tests
- ✅ Actuator endpoints for monitoring
- ✅ CORS configuration
- ✅ Logging with SLF4J

## 🛠️ Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Database**: PostgreSQL 15
- **Build Tool**: Maven 3.9+
- **Containerization**: Docker & Docker Compose
- **Documentation**: SpringDoc OpenAPI 3
- **Testing**: JUnit 5, Mockito, TestContainers

## 📁 Project Structure

```
parcel-service/
├── src/
│   ├── main/
│   │   ├── java/com/smartagri/parcel/
│   │   │   ├── ParcelServiceApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── ModelMapperConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── ParcelController.java
│   │   │   │   ├── CropController.java
│   │   │   │   └── HarvestController.java
│   │   │   ├── dto/
│   │   │   │   ├── ParcelDTO.java
│   │   │   │   ├── ParcelRequest.java
│   │   │   │   ├── CropDTO.java
│   │   │   │   ├── CropRequest.java
│   │   │   │   ├── HarvestDTO.java
│   │   │   │   └── HarvestRequest.java
│   │   │   ├── exception/
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── Parcel.java
│   │   │   │   ├── Crop.java
│   │   │   │   └── Harvest.java
│   │   │   ├── repository/
│   │   │   │   ├── ParcelRepository.java
│   │   │   │   ├── CropRepository.java
│   │   │   │   └── HarvestRepository.java
│   │   │   └── service/
│   │   │       ├── ParcelService.java
│   │   │       └── ParcelServiceImpl.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── data.sql
│   └── test/
│       ├── java/com/smartagri/parcel/
│       │   ├── controller/
│       │   │   └── ParcelControllerIntegrationTest.java
│       │   ├── repository/
│       │   │   └── ParcelRepositoryTest.java
│       │   └── service/
│       │       └── ParcelServiceImplTest.java
│       └── resources/
│           └── application-test.yml
├── Dockerfile
├── docker-compose-parcel.yml
├── pom.xml
└── README.md
```

## 🔧 Prerequisites

- Java 17 or higher
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 15 (if running locally without Docker)

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd parcel-service

# Build and run with Docker Compose
docker-compose -f docker-compose-parcel.yml up --build

# Service will be available at http://localhost:8081
```

### Option 2: Local Development

```bash
# 1. Start PostgreSQL
docker run --name postgres-parcel \
  -e POSTGRES_DB=parcel_db \
  -e POSTGRES_USER=parcel_user \
  -e POSTGRES_PASSWORD=parcel_pass \
  -p 5432:5432 \
  -d postgres:15-alpine

# 2. Build the project
mvn clean install

# 3. Run the service
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 3: Using JAR

```bash
# Build
mvn clean package

# Run
java -jar target/parcel-service-1.0.0.jar

# Or with profile
java -jar target/parcel-service-1.0.0.jar --spring.profiles.active=dev
```

## 📊 API Endpoints

### Parcel Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/parcels` | Create a new parcel |
| GET | `/api/parcels` | Get all parcels |
| GET | `/api/parcels/{id}` | Get parcel by ID |
| GET | `/api/parcels/location/{location}` | Get parcels by location |
| PUT | `/api/parcels/{id}` | Update a parcel |
| DELETE | `/api/parcels/{id}` | Delete a parcel |

### Crop Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/crops` | Create a new crop |
| GET | `/api/crops` | Get all crops |
| GET | `/api/crops/{id}` | Get crop by ID |
| GET | `/api/crops/parcel/{parcelId}` | Get crops by parcel ID |
| PUT | `/api/crops/{id}` | Update a crop |
| DELETE | `/api/crops/{id}` | Delete a crop |

### Harvest Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/harvests` | Create a new harvest record |
| GET | `/api/harvests/{id}` | Get harvest by ID |
| GET | `/api/harvests/crop/{cropId}` | Get harvests by crop ID |
| GET | `/api/harvests/parcel/{parcelId}` | Get harvests by parcel ID |
| DELETE | `/api/harvests/{id}` | Delete a harvest |

## 📝 API Examples

### Create a Parcel

```bash
curl -X POST http://localhost:8081/api/parcels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "North Field",
    "location": "Tunis North",
    "latitude": 36.8065,
    "longitude": 10.1815,
    "surfaceArea": 5.5,
    "soilType": "Clay",
    "irrigationSystem": "Drip Irrigation"
  }'
```

### Get All Parcels

```bash
curl http://localhost:8081/api/parcels
```

### Create a Crop

```bash
curl -X POST http://localhost:8081/api/crops \
  -H "Content-Type: application/json" \
  -d '{
    "parcelId": 1,
    "cropType": "Wheat",
    "variety": "Durum",
    "plantingDate": "2024-11-01",
    "expectedHarvestDate": "2025-06-15",
    "growthStage": "Vegetative",
    "status": "Active"
  }'
```

### Create a Harvest

```bash
curl -X POST http://localhost:8081/api/harvests \
  -H "Content-Type: application/json" \
  -d '{
    "cropId": 1,
    "harvestDate": "2025-06-15",
    "quantity": 8.5,
    "qualityGrade": "A",
    "notes": "Excellent harvest"
  }'
```

## 📚 Documentation

### Swagger UI

Access the interactive API documentation at:
```
http://localhost:8081/swagger-ui.html
```

### OpenAPI JSON

Get the OpenAPI specification:
```
http://localhost:8081/api-docs
```

### Actuator Endpoints

Health check and monitoring:
```
http://localhost:8081/actuator/health
http://localhost:8081/actuator/info
http://localhost:8081/actuator/metrics
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=ParcelServiceImplTest
```

### Run Integration Tests Only

```bash
mvn test -Dtest=*IntegrationTest
```

### Test Coverage

```bash
mvn clean test jacoco:report
# Report will be in target/site/jacoco/index.html
```

## 🐳 Docker Commands

### Build Docker Image

```bash
docker build -t parcel-service:latest .
```

### Run Container

```bash
docker run -d \
  --name parcel-service \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/parcel_db \
  -e SPRING_DATASOURCE_USERNAME=parcel_user \
  -e SPRING_DATASOURCE_PASSWORD=parcel_pass \
  parcel-service:latest
```

### View Logs

```bash
docker logs -f parcel-service
```

### Stop and Remove

```bash
docker stop parcel-service
docker rm parcel-service
```

## 🔒 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8081 |
| `SPRING_DATASOURCE_URL` | Database URL | jdbc:postgresql://localhost:5432/parcel_db |
| `SPRING_DATASOURCE_USERNAME` | Database username | parcel_user |
| `SPRING_DATASOURCE_PASSWORD` | Database password | parcel_pass |
| `SPRING_PROFILES_ACTIVE` | Active profile | dev |

## 📈 Database Schema

### Parcels Table

```sql
CREATE TABLE parcels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    surface_area DECIMAL(10,2),
    soil_type VARCHAR(100),
    irrigation_system VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Crops Table

```sql
CREATE TABLE crops (
    id BIGSERIAL PRIMARY KEY,
    parcel_id BIGINT REFERENCES parcels(id),
    crop_type VARCHAR(100) NOT NULL,
    variety VARCHAR(100),
    planting_date DATE NOT NULL,
    expected_harvest_date DATE,
    growth_stage VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Harvests Table

```sql
CREATE TABLE harvests (
    id BIGSERIAL PRIMARY KEY,
    crop_id BIGINT REFERENCES crops(id),
    harvest_date DATE NOT NULL,
    quantity DECIMAL(10,2),
    quality_grade VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP
);
```

## 🔍 Troubleshooting

### Database Connection Issues

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check database logs
docker logs postgres-parcel

# Test connection
psql -h localhost -p 5432 -U parcel_user -d parcel_db
```

### Application Won't Start

```bash
# Check application logs
tail -f logs/parcel-service.log

# Check if port 8081 is in use
lsof -i :8081

# Run with debug logging
mvn spring-boot:run -Dlogging.level.root=DEBUG
```

### Docker Build Issues

```bash
# Clear Maven cache
mvn clean

# Rebuild without cache
docker build --no-cache -t parcel-service:latest .
```

## 📦 Dependencies

Key dependencies used in this project:

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- Spring Boot Starter Actuator
- PostgreSQL Driver
- Lombok
- ModelMapper
- SpringDoc OpenAPI
- JUnit 5 & Mockito

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is part of the Smart Agriculture Platform educational project.

## 👥 Team

Smart Agriculture Development Team - 3rd Year Engineering Students

## 📞 Support

For issues and questions:
- Create an issue in the repository

---

**Happy Coding! 🌾**