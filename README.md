# ecom-notification-service

Notification management microservice for the e-commerce platform. Handles order event notifications, custom notifications, and notification history.

## Tech Stack

- Java 21, Spring Boot 3.4.1, Spring Cloud 2024.0.0
- Spring Data JPA with H2 (in-memory)
- Eureka Client for service discovery
- SpringDoc OpenAPI for API documentation

## Port

**8086**

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/notifications` | Send a custom notification |
| POST | `/api/notifications/order-event` | Handle an order event |
| GET | `/api/notifications` | Get all notifications |
| GET | `/api/notifications/user/{userId}` | Get notifications for user |
| GET | `/api/notifications/order/{orderId}` | Get notifications for order |

## Integration

The Notification Service receives order events from the Order Service via REST. Events include:
- Order created/confirmed
- Order shipped
- Order delivered
- Order cancelled

## Build and Run

```bash
mvn clean package
java -jar target/ecom-notification-service-0.0.1-SNAPSHOT.jar
```

## Access Points

- Swagger UI: http://localhost:8086/swagger-ui.html
- H2 Console: http://localhost:8086/h2-console
- Actuator Health: http://localhost:8086/actuator/health
