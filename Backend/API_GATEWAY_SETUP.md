# API Gateway setup

The frontend should send API requests to the gateway on port `8080`. The gateway forwards requests to the backend services without changing the `/api/v1/...` path.

## Routes

| Request path | Destination |
| --- | --- |
| `/api/v1/admin/**` | Admin service (`http://localhost:8084`) |
| Other `/api/v1/**` paths | Coaching service (`http://localhost:8081`) |

Student and parent operations currently enter through the Coaching service, which calls the Student and Parent services internally. The gateway does not expose those internal services directly.

## Run locally

Start the services from their module directories (or run each Spring Boot application in the IDE):

1. Eureka (`Backend/Eureka`, port `8761`) and Configuration Server if you use it.
2. Student (`Backend/Student`, port `8082`) and Parent (`Backend/Parent`, port `8083`).
3. Admin (`Backend/Admin`, port `8084`) and Coaching (`Backend/Coaching`, port `8081`).
4. API Gateway (`Backend/Api-Gateway`, port `8080`).

For Maven, run `mvn spring-boot:run` inside each module folder. The gateway can start without the optional Config Server; API requests need the destination service to be running.

Point the frontend API base URL at `http://localhost:8080` (for example, an admin login request goes to `http://localhost:8080/api/v1/admin/login`).

## Configuration overrides

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `GATEWAY_PORT` | `8080` | Gateway listen port |
| `ADMIN_SERVICE_URL` | `http://localhost:8084` | Admin route destination |
| `COACHING_SERVICE_URL` | `http://localhost:8081` | General API route destination |
| `FRONTEND_ORIGIN` | `http://localhost:5173` | Allowed browser origin; use the exact frontend origin |

The gateway allows credentialed requests from the configured frontend origin, with common API methods and the `Authorization` header. It does not accept arbitrary origins when credentials are enabled.

## Check the gateway

- Gateway process health: `GET http://localhost:8080/actuator/health`
- Admin APIs: start with `POST http://localhost:8080/api/v1/admin/login`
- Coaching APIs: use their existing `/api/v1/...` paths under `http://localhost:8080`

The health URL checks the gateway process only; it does not verify that every downstream service is healthy.
