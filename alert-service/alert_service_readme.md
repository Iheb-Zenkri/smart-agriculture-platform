# Alert Service

Smart Agriculture Alert and Notification Service with gRPC support.

## Overview

The Alert Service is a microservice responsible for managing alerts, notifications, and subscriptions in the Smart Agriculture platform. It provides real-time alert streaming, notification management, and user subscriptions through gRPC APIs.

## Features

- **Real-time Alert Streaming**: Stream alerts to clients using gRPC server-side streaming
- **Alert Management**: Create, retrieve, acknowledge, and dismiss alerts
- **Multiple Alert Types**: Weather, Pest, Disease, Threshold, Irrigation, Fertilization, Harvest, System
- **Severity Levels**: LOW, MEDIUM, HIGH, CRITICAL
- **Subscription Management**: Users can subscribe to specific alert types and parcels
- **Multi-channel Notifications**: EMAIL, SMS, PUSH, IN_APP
- **Alert History**: Complete audit trail of all alert actions
- **Auto-expiration**: Automatic expiration of time-sensitive alerts
- **PostgreSQL Database**: Persistent storage with optimized indexes
- **Metrics & Monitoring**: Prometheus metrics and health checks

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **gRPC 1.60.0**
- **PostgreSQL 15**
- **Protocol Buffers 3**
- **Lombok**
- **Micrometer (Prometheus)**
- **Docker & Docker Compose**

## Project Structure

```
alert-service/
├── src/
│   ├── main/
│   │   ├── java/com/smartagri/alert/
│   │   │   ├── AlertServiceApplication.java
│   │   │   ├── config/
│   │   │   │   ├── AlertConfiguration.java
│   │   │   │   ├── GrpcServerConfiguration.java
│   │   │   │   ├── DatabaseConfiguration.java
│   │   │   │   └── MetricsConfiguration.java
│   │   │   ├── grpc/
│   │   │   │   └── AlertGrpcService.java
│   │   │   ├── model/
│   │   │   │   ├── Alert.java
│   │   │   │   ├── AlertSubscription.java
│   │   │   │   └── AlertHistory.java
│   │   │   ├── repository/
│   │   │   │   ├── AlertRepository.java
│   │   │   │   ├── AlertSubscriptionRepository.java
│   │   │   │   └── AlertHistoryRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AlertService.java
│   │   │   │   └── AlertServiceImpl.java
│   │   │   ├── scheduler/
│   │   │   │   └── AlertScheduler.java
│   │   │   └── exception/
│   │   │       ├── AlertNotFoundException.java
│   │   │       ├── AlertServiceException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   ├── proto/
│   │   │   └── alert.proto
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── schema.sql
│   └── test/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (for containerized deployment)
- PostgreSQL 15+ (if running locally)

## Building the Project

### Generate gRPC Code

```bash
mvn clean compile
```

This will generate Java classes from the `.proto` file in `target/generated-sources/protobuf/`.

### Build JAR

```bash
mvn clean package
```

## Running Locally

### 1. Start PostgreSQL

```bash
docker run -d \
  --name alert-postgres \
  -e POSTGRES_DB=smartagri_alerts \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2. Initialize Database

```bash
psql -h localhost -U postgres -d smartagri_alerts -f src/main/resources/db/schema.sql
```

### 3. Run Application

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/alert-service-1.0.0.jar
```

## Running with Docker Compose

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f alert-service

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## API Documentation

### gRPC Service Endpoints

The service exposes the following gRPC methods on port **9095**:

#### 1. Stream Alerts (Server Streaming)
```protobuf
rpc StreamAlerts(StreamAlertsRequest) returns (stream AlertResponse);
```

#### 2. Create Alert
```protobuf
rpc CreateAlert(CreateAlertRequest) returns (AlertResponse);
```

#### 3. Get Alert by ID
```protobuf
rpc GetAlert(GetAlertRequest) returns (AlertResponse);
```

#### 4. Get Active Alerts
```protobuf
rpc GetActiveAlerts(GetActiveAlertsRequest) returns (AlertListResponse);
```

#### 5. Acknowledge Alert
```protobuf
rpc AcknowledgeAlert(AcknowledgeAlertRequest) returns (AlertResponse);
```

#### 6. Dismiss Alert
```protobuf
rpc DismissAlert(DismissAlertRequest) returns (DismissAlertResponse);
```

#### 7. Subscribe to Alerts
```protobuf
rpc SubscribeToAlerts(SubscribeRequest) returns (SubscribeResponse);
```

#### 8. Get Subscription
```protobuf
rpc GetSubscription(GetSubscriptionRequest) returns (SubscriptionResponse);
```

### REST Endpoints (Actuator)

- **Health Check**: `GET http://localhost:8095/actuator/health`
- **Metrics**: `GET http://localhost:8095/actuator/metrics`
- **Prometheus**: `GET http://localhost:8095/actuator/prometheus`

## Testing with grpcurl

### Install grpcurl
```bash
# macOS
brew install grpcurl

# Linux
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest
```

### List Services
```bash
grpcurl -plaintext localhost:9095 list
```

### Create Alert
```bash
grpcurl -plaintext -d '{
  "alert_type": "WEATHER",
  "severity": "HIGH",
  "parcel_id": 1,
  "title": "Heavy Rain Warning",
  "message": "Heavy rainfall expected in the next 6 hours",
  "expiry_seconds": 21600
}' localhost:9095 alert.AlertService/CreateAlert
```

### Get Active Alerts
```bash
grpcurl -plaintext -d '{
  "parcel_id": 1
}' localhost:9095 alert.AlertService/GetActiveAlerts
```

### Stream Alerts
```bash
grpcurl -plaintext -d '{
  "parcel_id": 1
}' localhost:9095 alert.AlertService/StreamAlerts
```

### Acknowledge Alert
```bash
grpcurl -plaintext -d '{
  "alert_id": 1,
  "acknowledged_by": "farmer001"
}' localhost:9095 alert.AlertService/AcknowledgeAlert
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
# Database
spring.datasource.url: jdbc:postgresql://localhost:5432/smartagri_alerts

# gRPC Server
grpc.server.port: 9095

# Alert Configuration
alert.expiry.check-interval: 300000  # 5 minutes
alert.stream.update-interval: 5000   # 5 seconds
```

## Database Schema

### Tables

1. **alerts**: Main alert storage
2. **alert_subscriptions**: User notification subscriptions
3. **alert_history**: Audit trail for alert actions

### Views

1. **v_active_unacknowledged_alerts**: Active alerts needing attention
2. **v_alert_statistics**: Alert statistics by type
3. **v_subscription_statistics**: Subscription statistics

## Monitoring

### Prometheus Metrics

Available at `http://localhost:8095/actuator/prometheus`:

- `alert_created_total`: Total alerts created by type and severity
- `alert_acknowledged_total`: Total alerts acknowledged
- `alert_dismissed_total`: Total alerts dismissed
- `grpc_call_duration`: gRPC call duration by method

### Grafana Dashboard

Access Grafana at `http://localhost:3000` (admin/admin)

Import the provided dashboard JSON for alert service visualization.

## Scheduled Tasks

The service runs the following scheduled tasks:

1. **Expire Old Alerts**: Every 5 minutes
2. **Log Alert Statistics**: Every hour

## Error Handling

The service provides comprehensive error handling:

- `AlertNotFoundException`: When alert is not found (gRPC: NOT_FOUND)
- `AlertServiceException`: General service errors (gRPC: INTERNAL)
- Global exception handler for all unhandled exceptions

## Development

### Adding a New Alert Type

1. Add the type to `Alert.AlertType` enum
2. Update database constraint in `schema.sql`
3. Implement specific notification logic if needed

### Adding a New Notification Channel

1. Add method to `AlertSubscription.NotificationMethod` enum
2. Implement notification sender in `AlertServiceImpl`
3. Update subscription logic

## Production Considerations

1. **Security**:
    - Enable TLS for gRPC
    - Use SSL for PostgreSQL connections
    - Implement authentication/authorization
    - Secure sensitive credentials

2. **Performance**:
    - Configure connection pooling
    - Optimize database indexes
    - Implement caching for frequently accessed data
    - Use async processing for notifications

3. **Reliability**:
    - Implement retry logic for failed notifications
    - Use message queues for notification delivery
    - Set up database replication
    - Configure automatic backups

4. **Monitoring**:
    - Set up alerting rules in Prometheus
    - Configure log aggregation
    - Monitor gRPC metrics
    - Track notification delivery rates

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

Copyright © 2024 Smart Agriculture Platform. All rights reserved.

## Support

For issues and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## Changelog

### Version 1.0.0
- Initial release
- gRPC API implementation
- Real-time alert streaming
- Subscription management
- Multi-channel notifications
- Database schema and indexes
- Docker support
- Prometheus metrics