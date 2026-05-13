# ms-tickets - Microservicio de Tickets de Soporte

Microservicio de la plataforma InnovaTech para la gestion de tickets de soporte al cliente.

## Funcionalidades (RF-5)

- **RF-5.1 Crear ticket**: Registrar el problema del usuario
- **RF-5.2 Estado del ticket**: Otorgar el estado para permitir seguimiento
- **RF-5.3 Prioridad**: Definir nivel de urgencia (BAJA, MEDIA, ALTA)
- **RF-5.4 Historial**: Registro de tickets accesible por usuario

## Arquitectura

- **Framework**: Spring Boot 4.0.6
- **Java**: 21
- **Arquitectura**: Event-Driven con RabbitMQ
- **Base de Datos**: H2 (desarrollo) / MySQL (produccion)
- **Documentacion**: Swagger/OpenAPI

## Endpoints API

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | `/api/v1/tickets` | Crear nuevo ticket |
| GET | `/api/v1/tickets` | Listar todos los tickets |
| GET | `/api/v1/tickets/{id}` | Obtener ticket por ID |
| GET | `/api/v1/tickets/usuario/{usuarioId}` | Historial por usuario |
| PUT | `/api/v1/tickets/{id}` | Actualizar estado y prioridad |

## Documentacion Swagger

- **Swagger UI**: http://localhost:8085/swagger-ui.html
- **API Docs**: http://localhost:8085/api-docs

## Eventos RabbitMQ

Cuando se crea un ticket, se publica el evento `Ticket_Creado` con:
- Exchange: `tickets.exchange`
- Queue: `tickets.queue`
- Routing Key: `tickets.routing.key`

## Perfiles de Ejecucion

### Desarrollo (H2 - por defecto)
```bash
mvn spring-boot:run
```
- Consola H2: http://localhost:8085/h2-console
- JDBC URL: `jdbc:h2:mem:tickets_db`

### Produccion (MySQL)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Actuator

- Health: http://localhost:8085/actuator/health
- Info: http://localhost:8085/actuator/info
- Metrics: http://localhost:8085/actuator/metrics

## Estructura de Paquetes

```
com.innovatech.ms_tickets/
├── config/          # Configuracion RabbitMQ
├── controller/      # REST Controllers
├── dto/
│   ├── request/     # DTOs de entrada
│   └── response/    # DTOs de salida
├── exception/       # Manejo de excepciones
├── model/           # Entidades JPA y Enums
├── repository/      # Repositorios JPA
└── service/         # Logica de negocio
```

## Dependencias Principales

- Spring Web
- Spring Data JPA
- Spring AMQP (RabbitMQ)
- Spring Validation
- Spring Boot Actuator
- Springdoc OpenAPI
- H2 Database
- MySQL Driver
- Lombok
- Spring Boot DevTools

## Integraci�n con BFF
- Ruta base consumida: /api/v1/tickets
- Filtros esperados: estado=ABIERTO, prioridad=CRITICA
- Healthcheck: /actuator/health

