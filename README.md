
# SWAPI Backend — Java 21 / Spring Boot 3

Servicio backend que integra con **Star Wars API (swapi.tech)** para listar **People, Films, Starships y Vehicles**, con **autenticación JWT**, **paginación**, **filtro por `id` y/o `name`**, documentación OpenAPI y **tests**.  
Todo el proyecto usa **`.yml`** para configuración y respeta **variables de entorno** para overrides.

> Nota: la API pública de SWAPI es inconsistente con `films`. Este backend implementa **paginación local** para `films` (cache en memoria) para cumplir el challenge.

---

## Índice

- [Arquitectura y Stack](#arquitectura-y-stack)
- [Estructura de paquetes](#estructura-de-paquetes)
- [Configuración](#configuración)
    - [Variables de entorno](#variables-de-entorno)
    - [application.yml](#applicationyml)
    - [Usuarios seed (H2)](#usuarios-seed-h2)
- [Cómo ejecutar en local](#cómo-ejecutar-en-local)
- [Documentación OpenAPI](#documentación-openapi)
- [Autenticación](#autenticación)
- [Endpoints](#endpoints)
    - [Auth](#auth)
    - [Listado y filtrado](#listado-y-filtrado)
    - [Por ID](#por-id)
- [Códigos de resultado y errores](#códigos-de-resultado-y-errores)
- [Pruebas](#pruebas)
- [Notas sobre `films` y caché](#notas-sobre-films-y-caché)
- [Problemas comunes](#problemas-comunes)
- [Siguientes pasos (Deploy en Render)](#siguientes-pasos-deploy-en-render)

---

## Arquitectura y Stack

| Componente               | Tecnología                                            |
|--------------------------|-------------------------------------------------------|
| Lenguaje                 | Java 21                                               |
| Framework                | Spring Boot **3.5.7**                                 |
| HTTP client              | Spring **WebClient** (WebFlux Starter)                |
| Seguridad                | Spring Security + **JWT (jjwt)**                      |
| Persistencia             | **H2** en memoria (seed de usuario) + Spring Data JPA |
| Utilidades               | **Lombok**                                            |
| Documentación            | **springdoc-openapi** (Swagger UI)                    |
| Test Unitarios           | **JUnit 5**, **Mockito**                              |
| Test de integración HTTP | **OkHttp MockWebServer**                              |

---

## Estructura de paquetes

```
com.example.swapi
 ├─ auth/
 │   ├─ controller/    (AuthController)
 │   ├─ dto/           (AuthRequest, RegisterRequest, AuthResponse)
 │   ├─ entity/        (User)
 |   ├─ filter/        (JwtAuthFilter)
 │   ├─ repository/    (UserRepository)
 │   └─ service/       (AuthService, JwtService)
 ├─ conn/
 │   ├─ client/        (SwapiClient)
 │   ├─ controller/    (SwapiController)
 │   ├─ dto/           (SwapiListResponse, SwapiListItem, SwapiGenericResponse, SwapiGenericResult, SwapiDTO, SwapiItemResponse)
 │   └─ service/       (SwapiService)
 ├─ config/            (WebClientConfig, SecurityConfig, SecurityBeans)
 └─ shared/
     ├─ dto/           (BaseResponse, BasePagResponse)
     └─ exception/     (BaseException, GlobalExceptionHandler)
```
---

## Configuración

### Variables de entorno

| Variable                 | Descripción                                | Default (`application.yml`)    |
|--------------------------|--------------------------------------------|--------------------------------|
| `SERVER_PORT`            | Puerto HTTP                                | `8080`                         |
| `JWT_SECRET`             | Clave HMAC para firmar JWT (mín. 32 chars) | `"ESTO_NO_ES_SEGURO_CAMBIALO"` |
| `JWT_EXPIRATION`         | Expiración del token en milisegundos       | `3600000`                      |
| `SPRING_PROFILES_ACTIVE` | Perfil Spring                              | *(vacío)*                      |

### `application.yml`

Ejemplo mínimo (ya incluido en el proyecto):

```yaml
server:
  port: 8080

spring:
  application:
    name: swapi-service

  datasource:
    url: jdbc:h2:mem:swapi-db
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

  h2:
    console:
      enabled: true

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true

jwt:
  secret: "ESTO_NO_ES_SEGURO_CAMBIALO"
  expiration: 3600000   
```

### Usuarios seed (H2)

Se precarga un usuario en `data.sql`:

| username | password |
|----------|----------|
| `admin`  | `admin`  |

> El password está hasheado con BCrypt. Solo para desarrollo.

---

## Cómo ejecutar en local

### Requisitos
- JDK **21**
- Maven **3.9+**
- IntelliJ IDEA con **plugin Lombok** y **Annotation Processing activado**
    - Settings → Build, Execution, Deployment → Compiler → **Enable annotation processing**

### Pasos

1. **Clonar e instalar dependencias**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Levantar la app**
   ```bash
   # con variables de entorno si querés override
   export JWT_SECRET="cambia-esto-por-algo-largo-y-seguro"
   export JWT_EXPIRATION=3600000
   export SERVER_PORT=8080

   mvn spring-boot:run
   ```

3. **Consola H2 (opcional)**
    - URL: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:mem:swapi-db`
    - User: `sa` / Password: *(vacío)*

---

## Documentación OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## Autenticación

- **Login / Registro** devuelven un `token` JWT.
- Usar en cada request protegida:
  ```http
  Authorization: Bearer <token>
  ```
- Expiración por defecto: **1 hora** (`jwt.expiration`).

---

## Endpoints

### Auth

| Método | Path             | Body                                       | Descripción                 | Auth |
|--------|------------------|--------------------------------------------|-----------------------------|------|
| `POST` | `/auth/register` | `{ "username": "...", "password": "..." }` | Crea usuario y devuelve JWT | ❌    |
| `POST` | `/auth/login`    | `{ "username": "...", "password": "..." }` | Autentica y devuelve JWT    | ❌    |

**Respuesta (OK)**

```json
{
  "code": 0,
  "message": "OK",
  "token": "xxxxx.yyyyy.zzzzz"
}
```

---

### Listado y filtrado

Todos protegidos con JWT.

| Método | Path             | Query params                                  | Descripción                        | Respuesta                   |
|--------|------------------|-----------------------------------------------|------------------------------------|-----------------------------|
| `GET`  | `/api/people`    | `page` (def=1), `size` (def=10), `name` (opc) | Lista paginada de personas         | `BasePagResponse<SwapiDTO>` |
| `GET`  | `/api/starships` | `page`, `size`, `name`                        | Lista paginada de naves            | `BasePagResponse<SwapiDTO>` |
| `GET`  | `/api/vehicles`  | `page`, `size`, `name`                        | Lista paginada de vehículos        | `BasePagResponse<SwapiDTO>` |
| `GET`  | `/api/films`     | `page`, `size`, `name`                        | **Paginación local** sobre 7 films | `BasePagResponse<SwapiDTO>` |

**Ejemplo**

```http
GET /api/people?page=2&size=5&name=sky
Authorization: Bearer <token>
```

**Respuesta (OK)**

```json
{
  "code": 0,
  "message": "OK",
  "page": 2,
  "size": 5,
  "total": 82,
  "items": [
    { "id": "1", "name": "Luke Skywalker" }
  ]
}
```

---

### Por ID

Mismo endpoint, usando `id` como query param. Prioriza `id` sobre paginación.

| Método | Path | Query params | Descripción | Respuesta |
|---|---|---|---|---|
| `GET` | `/api/{resource}` | `id` | Obtiene 1 entidad por ID | `SwapiItemResponse` |

**Ejemplo**

```http
GET /api/people?id=1
Authorization: Bearer <token>
```

**Respuesta (OK)**

```json
{
  "code": 0,
  "message": "OK",
  "id": "1",
  "name": "Luke Skywalker"
}
```

---

## Códigos de resultado y errores

| Código | Mensaje | Cuándo |
|---|---|---|
| `0` | OK | Operación exitosa |
| `1` | El usuario ya existe | `/auth/register` con username repetido |
| `2` | Usuario no encontrado | `/auth/login` username inexistente |
| `3` | Password incorrecto | `/auth/login` password inválida |
| `4` | No se encontró el ID | `/api/**?id=` con ID inválido |
| `6` | El recurso no soporta paginación o no hay resultados | `/api/{resource}` cuando SWAPI no devuelve `results` |
| `7` | La página solicitada está fuera de rango | `/api/films` paginación local sin items |
| `9` | error inesperado en el sistema, revisar en consola | Excepción no controlada (fallback) |

> Todas las respuestas **extienden** de `BaseResponse`.
> - Paginadas: `BasePagResponse<T>` agrega `page`, `size`, `total`, `items`.
> - Por ID: `SwapiItemResponse` agrega `id`, `name`.
> - Auth: `AuthResponse` agrega `token`.

---

## Pruebas

- **Unitarias** con JUnit 5 + Mockito sobre `AuthService` y `SwapiService` (paths “OK” y errores probables).
- **Integración HTTP** con **OkHttp MockWebServer** para simular SWAPI en `films` y validar la paginación local y filtros.

**Ejecutar tests**

```bash
mvn -q -DskipTests=false test
```

**Ejecutar una clase de test específica**

```bash
mvn -q -Dtest=SwapiServiceTest test
```

---

## Notas sobre `films` y caché

- SWAPI no entrega `results` paginados para `/films`.
- Este backend **carga una vez** los **7 films** por ID, los mapea a `SwapiDTO` y mantiene una **caché en memoria** dentro de `SwapiService`.
- La paginación y el filtro `name` se aplican **localmente** sobre esa caché.
- Es suficiente un entregable simple y se puede mejorar con Caffeine/Redis y políticas de invalidez.

---

## Problemas comunes

| Síntoma                                          | Causa                                     | Fix                                                               |
|--------------------------------------------------|-------------------------------------------|-------------------------------------------------------------------|
| `TypeTag UNKNOWN` o errores raros de compilación | Lombok sin annotation processing          | Activar **Annotation Processing** y **plugin Lombok** en IntelliJ |
| Swagger no levanta                               | Usar starter webmvc en lugar de webflux   | Usar `springdoc-openapi-starter-webflux-ui`                       |
| 401 en `/api/**`                                 | Falta `Authorization`                     | Enviar `Bearer <token>` válido                                    |
| 404 al pedir ID                                  | ID inexistente o recurso fuera de alcance | Verificar ID contra SWAPI                                         |

---

## Deploy en Render
