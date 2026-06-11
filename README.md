# innovatech-ms-tickets

## Estado de evidencia

| Categoria | Estado |
|---|---|
| Implementado | CRUD de tickets, productor RabbitMQ, Actuator |
| Configurado | MySQL/H2, RabbitMQ, perfiles, Docker |
| Validado | compilacion |
| Pendiente de validacion runtime | publicacion real del evento y stack completo |
| No evidenciado | consumidor oficial de `Ticket_Creado` |

## 1. Descripcion general
`innovatech-ms-tickets` es el microservicio responsable de registrar y consultar tickets de soporte dentro del ecosistema InnovaTech. Expone operaciones REST para crear tickets, consultar por identificador, listar tickets por usuario y actualizar su estado o prioridad.

## 2. Rol dentro de la arquitectura
El servicio es consumido a traves del API Gateway en la ruta `/api/v1/tickets/**`. Persiste informacion en base de datos propia y publica un evento RabbitMQ cuando se crea un ticket.

Flujo simple:

`Cliente/Frontend -> API Gateway -> innovatech-ms-tickets -> Base de datos / RabbitMQ`

Relaciones evidenciadas:

- API Gateway enruta hacia este servicio en `http://tickets:8085` en perfil Docker.
- BFF consulta este servicio mediante `MS_TICKETS_URL`.
- El servicio usa H2 en desarrollo y MySQL en produccion.
- El servicio publica mensajes en RabbitMQ mediante `RabbitTemplate`.

## 3. Stack tecnico
- Java 21
- Spring Boot 3.5.14
- Maven Wrapper
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Spring AMQP / RabbitMQ
- H2 Database
- MySQL Driver
- Spring Boot Actuator
- Springdoc OpenAPI
- Docker

## 4. Puerto del servicio

| Concepto | Valor |
|---|---|
| Puerto esperado | 8085 |
| Puerto configurado | `${SERVER_PORT:8085}` |
| Archivo donde se define | `src/main/resources/application.properties`, `src/main/resources/application-prod.properties`, `Dockerfile` |
| Variable de entorno asociada | `SERVER_PORT` |

## 5. Variables de entorno

| Variable | Descripcion | Valor por defecto | Obligatoria | Riesgo/observacion |
|---|---|---|---|---|
| `SERVER_PORT` | Puerto HTTP del microservicio | `8085` | No | Debe mantenerse alineado con Gateway y Docker. |
| `SPRING_PROFILES_ACTIVE` | Perfil activo de Spring | `dev` | No | `prod` cambia datasource, Swagger y Actuator. |
| `JWT_SECRET` | Secreto para validacion JWT | Sin valor por defecto | Si en entornos no locales | Si falta, el servicio no valida JWT correctamente. |
| `APP_SECURITY_DOCS_PUBLIC` | Habilita acceso publico a Swagger/OpenAPI | `true` en dev, `false` en prod | No | No deberia exponerse libremente en produccion. |
| `TICKETS_MYSQL_HOST` | Host MySQL en produccion | `mysql-tickets` | Si en prod | No aplica en desarrollo H2. |
| `TICKETS_MYSQL_PORT` | Puerto MySQL en produccion | `3306` | Si en prod | Pendiente de verificacion externa en otro despliegue. |
| `TICKETS_MYSQL_DATABASE` | Nombre de la base MySQL | `tickets_db` | Si en prod | El servicio asume base dedicada. |
| `TICKETS_MYSQL_USERNAME` | Usuario de aplicacion MySQL | Sin valor por defecto | Si en prod | No se evidencia uso de `root` en prod. |
| `TICKETS_MYSQL_PASSWORD` | Password de aplicacion MySQL | Sin valor por defecto | Si en prod | No incluir en repositorio. |
| `RABBITMQ_HOST` | Host RabbitMQ | `localhost` en dev | Si cuando RabbitMQ esta integrado | En prod se define sin fallback inseguro. |
| `RABBITMQ_PORT` | Puerto RabbitMQ | `5672` | No | Debe coincidir con la infraestructura. |
| `RABBITMQ_USERNAME` | Usuario RabbitMQ | `rabbit_local_user` en dev | Si en prod | No usar `guest` en produccion. |
| `RABBITMQ_PASSWORD` | Password RabbitMQ | `rabbit_local_password` en dev | Si en prod | No incluir secretos reales. |

## 6. Base de datos
El servicio usa H2 en desarrollo y MySQL en produccion. Se evidencia una entidad principal y un repositorio JPA dedicado.

| Elemento | Valor |
|---|---|
| Motor | H2 en dev, MySQL en prod |
| Base de datos | `tickets_db` |
| Entidades | `Ticket` |
| Repositories | `TicketRepository` |
| ddl-auto | `update` en dev, `validate` en prod |
| show-sql | `true` en dev, `false` en prod |

Riesgos o pendientes:

- En produccion el servicio depende de que el esquema exista previamente, porque usa `validate`.
- La consola H2 solo se evidencia habilitada en desarrollo.

## 7. Endpoints principales

| Metodo | Endpoint | Descripcion | Auth requerida | Request | Response |
|---|---|---|---|---|---|
| `POST` | `/api/v1/tickets` | Crea un ticket de soporte | Si | `TicketRequestDTO` | `TicketResponseDTO` |
| `GET` | `/api/v1/tickets/{id}` | Obtiene un ticket por identificador | Si | No aplica | `TicketResponseDTO` |
| `GET` | `/api/v1/tickets/usuario/{usuarioId}` | Lista tickets por usuario | Si | No aplica | `List<TicketResponseDTO>` |
| `GET` | `/api/v1/tickets` | Lista todos los tickets | Si | No aplica | `List<TicketResponseDTO>` |
| `PUT` | `/api/v1/tickets/{id}` | Actualiza ticket existente | Si | `TicketUpdateRequestDTO` | `TicketResponseDTO` |

## 8. Seguridad
- Usa Spring Security: si.
- Valida JWT: si.
- Depende del Gateway: el flujo oficial es via Gateway, aunque el servicio tambien protege endpoints si recibe trafico directo.
- Endpoints publicos: `GET /actuator/health`, `GET /actuator/info`, Swagger/OpenAPI solo cuando `APP_SECURITY_DOCS_PUBLIC=true`.
- Endpoints protegidos: los endpoints `/api/v1/tickets/**`.
- Riesgos detectados:
  - Si se expone directamente fuera de la red interna, el control ya no dependeria solo del Gateway.
  - La apertura de Swagger depende de perfil y variable; debe controlarse en prod.

## 9. Integraciones

| Origen | Destino | Tipo | URL/variable | Estado |
|---|---|---|---|---|
| API Gateway | `innovatech-ms-tickets` | HTTP | `http://tickets:8085` en Docker | Evidenciado |
| BFF | `innovatech-ms-tickets` | HTTP | `MS_TICKETS_URL` | Evidenciado |
| `innovatech-ms-tickets` | Base de datos propia | JDBC/JPA | H2 dev / `TICKETS_MYSQL_*` prod | Evidenciado |
| `innovatech-ms-tickets` | RabbitMQ | AMQP | `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` | Evidenciado |

## 10. Eventos RabbitMQ

| Evento | Exchange | Routing key | Queue | Productor/Consumidor | Estado |
|---|---|---|---|---|---|
| `Ticket_Creado` | `tickets.exchange` | `tickets.routing.key` | `tickets.queue` | Productor | Evidenciado |

No se evidencia consumidor oficial para este evento dentro de este repositorio.

## 11. Ejecucion local

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Perfil de produccion:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Puntos utiles:

- Swagger UI: `http://localhost:8085/swagger-ui.html` cuando la documentacion publica esta habilitada.
- OpenAPI: `http://localhost:8085/api-docs`
- Health: `http://localhost:8085/actuator/health`
- Info: `http://localhost:8085/actuator/info`
