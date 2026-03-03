# Sistema de Venta de Entradas - Trabajo Final Programación II


Sistema completo de venta de entradas para eventos que incluye backend, servicio proxy y aplicación móvil Android.

## 📋 Descripción del Proyecto

Este proyecto implementa un sistema distribuido de venta de entradas que se integra con un servicio externo de cátedra. El sistema permite:

- Registro y autenticación de usuarios
- Visualización de eventos disponibles
- Selección y bloqueo de asientos
- Compra de entradas con sincronización a servicio externo
- Notificaciones en tiempo real mediante WebSocket
- Aplicación móvil Android nativa

## 🏗️ Arquitectura

El proyecto está compuesto por tres módulos principales:

### 1. **Backend** (Puerto 8080)
- Framework: Spring Boot + JHipster
- Base de datos: PostgreSQL
- Autenticación: JWT
- WebSocket para notificaciones en tiempo real
- API REST para gestión de usuarios, eventos y ventas

### 2. **Proxy** (Puerto 8081)
- Framework: Spring Boot
- Integración con servicios externos de cátedra
- Consumidor de Kafka para sincronización de eventos
- Redis para caché y gestión de estado
- Comunicación con API externa en red ZeroTier (192.168.194.250)

### 3. **Mobile**
- Plataforma: Android (Kotlin)
- UI: Jetpack Compose
- Arquitectura: MVVM
- Networking: Retrofit + OkHttp
- Gestión de estado: Coroutines + Flow

## 🚀 Requisitos Previos

- **Java**: JDK 17 o superior
- **Node.js**: v18 o superior (para desarrollo frontend si aplica)
- **PostgreSQL**: 14 o superior
- **Maven**: 3.8 o superior
- **Android Studio**: Para desarrollo móvil (opcional)
- **Acceso a red ZeroTier**: Para conectar con servicios de cátedra (192.168.194.0/24)

### Servicios Externos (Red ZeroTier)
- **API Cátedra**: http://192.168.194.250:8080
- **Kafka**: 192.168.194.250:9092
- **Redis**: 192.168.194.250:6379

## 📦 Instalación y Configuración

### 1. Configurar Base de Datos PostgreSQL

```bash
# Crear base de datos
createdb backend

# Crear usuario (si es necesario)
psql -c "CREATE USER backend WITH PASSWORD 'tu_password';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE backend TO backend;"
```

### 2. Configurar Variables de Entorno

Crear archivo `.env` o configurar variables de sistema:

```bash
# Backend
export CATEDRA_AUTH_TOKEN="tu_token_de_catedra"
export PROXY_BASE_URL="http://localhost:8081"

# Proxy
export KAFKA_BOOTSTRAP_SERVERS="192.168.194.250:9092"
export REDIS_HOST="192.168.194.250"
export BACKEND_BASE_URL="http://localhost:8080"
```

### 3. Compilar y Ejecutar Backend

```bash
cd backend

# Compilar el proyecto
./mvnw clean install

# Ejecutar en modo desarrollo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

El backend estará disponible en: `http://localhost:8080`

### 4. Compilar y Ejecutar Proxy

```bash
cd proxy

# Compilar el proyecto
./mvnw clean install

# Ejecutar
./mvnw spring-boot:run
```

El proxy estará disponible en: `http://localhost:8081`

### 5. Configurar y Ejecutar Aplicación Móvil

```bash
cd mobile

# Compilar el proyecto
./gradlew build

# Ejecutar en emulador o dispositivo
./gradlew installDebug
```

**Configuración de red para móvil:**
- Emulador Android: usar `http://10.0.2.2:8080` para acceder al backend en localhost
- Dispositivo físico: usar la IP de tu máquina en la red local

## 📚 API Endpoints

### Autenticación

#### Registrar Usuario
```http
POST /api/register
Content-Type: application/json

{
  "login": "usuario",
  "email": "usuario@example.com",
  "password": "password123",
  "firstName": "Nombre",
  "lastName": "Apellido",
  "langKey": "es"
}
```

**Respuesta:** `201 Created`

#### Iniciar Sesión
```http
POST /api/authenticate
Content-Type: application/json

{
  "username": "usuario",
  "password": "password123",
  "rememberMe": false
}
```

**Respuesta:**
```json
{
  "id_token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### Verificar Autenticación
```http
GET /api/authenticate
Authorization: Bearer {token}
```

**Respuesta:** `204 No Content` (autenticado) o `401 Unauthorized`

#### Obtener Cuenta Actual
```http
GET /api/account
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "id": 1,
  "login": "usuario",
  "firstName": "Nombre",
  "lastName": "Apellido",
  "email": "usuario@example.com",
  "activated": true,
  "langKey": "es",
  "authorities": ["ROLE_USER"]
}
```

### Eventos

#### Listar Eventos Activos
```http
GET /api/eventos
Authorization: Bearer {token}
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "eventoIdCatedra": 100,
    "nombre": "Concierto de Rock",
    "descripcion": "Gran concierto de rock en vivo",
    "fecha": "2026-02-15T20:00:00Z",
    "lugar": "Estadio Central",
    "estado": "ACTIVO",
    "asientosDisponibles": 150,
    "asientosTotales": 200,
    "precioBase": 5000.00
  }
]
```

#### Obtener Detalle de Evento
```http
GET /api/eventos/{id}
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "id": 1,
  "eventoIdCatedra": 100,
  "nombre": "Concierto de Rock",
  "descripcion": "Gran concierto de rock en vivo",
  "fecha": "2026-02-15T20:00:00Z",
  "lugar": "Estadio Central",
  "estado": "ACTIVO",
  "filas": 10,
  "columnas": 20,
  "asientosDisponibles": 150,
  "asientosTotales": 200,
  "precioBase": 5000.00
}
```

#### Obtener Dimensiones de Evento
```http
GET /api/eventos/catedra/{eventoIdCatedra}/dimensiones
```

**Respuesta:**
```json
{
  "filas": 10,
  "columnas": 20
}
```

### Asientos

#### Obtener Mapa de Asientos
```http
GET /api/asientos/evento/{eventoId}
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "eventoId": 1,
  "filas": 10,
  "columnas": 20,
  "asientos": [
    {
      "fila": 1,
      "numero": 1,
      "estado": "DISPONIBLE",
      "precio": 5000.00
    },
    {
      "fila": 1,
      "numero": 2,
      "estado": "BLOQUEADO",
      "precio": 5000.00,
      "bloqueadoPorUsuario": true
    },
    {
      "fila": 1,
      "numero": 3,
      "estado": "VENDIDO",
      "precio": 5000.00
    }
  ]
}
```

**Estados de asientos:**
- `DISPONIBLE`: Asiento libre para seleccionar
- `BLOQUEADO`: Asiento temporalmente reservado (puede ser por el usuario actual u otro)
- `VENDIDO`: Asiento ya vendido, no disponible

#### Bloquear Asientos
```http
POST /api/asientos/bloquear/{eventoId}
Authorization: Bearer {token}
```

**Descripción:** Bloquea automáticamente los mejores asientos disponibles para el usuario.

**Respuesta:**
```json
{
  "exito": true,
  "mensaje": "Asientos bloqueados exitosamente",
  "asientosBloqueados": [
    {
      "fila": 5,
      "numero": 10
    },
    {
      "fila": 5,
      "numero": 11
    }
  ],
  "tiempoExpiracion": "2026-01-02T13:05:00Z"
}
```

### Ventas

#### Procesar Venta
```http
POST /api/ventas
Authorization: Bearer {token}
Content-Type: application/json

{
  "eventoId": 1,
  "asientos": [
    {
      "fila": 5,
      "numero": 10,
      "nombrePersona": "Juan",
      "apellidoPersona": "Pérez"
    },
    {
      "fila": 5,
      "numero": 11,
      "nombrePersona": "María",
      "apellidoPersona": "González"
    }
  ]
}
```

**Respuesta Exitosa:**
```json
{
  "id": 1,
  "ventaIdCatedra": "VENTA-123456",
  "eventoId": 1,
  "fechaVenta": "2026-01-02T12:51:46Z",
  "precioVenta": 10000.00,
  "resultado": "EXITOSA",
  "mensaje": "Venta procesada exitosamente",
  "asientos": [
    {
      "fila": 5,
      "numero": 10,
      "nombrePersona": "Juan",
      "apellidoPersona": "Pérez"
    },
    {
      "fila": 5,
      "numero": 11,
      "nombrePersona": "María",
      "apellidoPersona": "González"
    }
  ]
}
```

**Respuesta con Error:**
```json
{
  "resultado": "FALLIDA",
  "mensaje": "El asiento fila 5, número 10 ya está ocupado"
}
```

**Códigos de estado:**
- `201 Created`: Venta exitosa
- `400 Bad Request`: Error de validación o asiento no disponible
- `500 Internal Server Error`: Error interno del servidor

#### Obtener Ventas del Usuario
```http
GET /api/ventas
Authorization: Bearer {token}
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "eventoId": 1,
    "fechaVenta": "2026-01-02T12:51:46Z",
    "precioVenta": 10000.00,
    "resultado": "EXITOSA",
    "cantidadAsientos": 2
  }
]
```

#### Obtener Detalle de Venta
```http
GET /api/ventas/{id}
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "id": 1,
  "ventaIdCatedra": "VENTA-123456",
  "eventoId": 1,
  "fechaVenta": "2026-01-02T12:51:46Z",
  "precioVenta": 10000.00,
  "resultado": "EXITOSA",
  "mensaje": "Venta procesada exitosamente",
  "asientos": [
    {
      "fila": 5,
      "numero": 10,
      "nombrePersona": "Juan",
      "apellidoPersona": "Pérez"
    },
    {
      "fila": 5,
      "numero": 11,
      "nombrePersona": "María",
      "apellidoPersona": "González"
    }
  ]
}
```

## 🔐 Autenticación

Todos los endpoints (excepto `/api/register`, `/api/authenticate` y `/api/eventos/catedra/{id}/dimensiones`) requieren autenticación mediante JWT.

**Incluir el token en las peticiones:**
```http
Authorization: Bearer {tu_token_jwt}
```

**Duración del token:**
- Normal: 24 horas
- Remember Me: 30 días

## 🧪 Pruebas con Postman

### 1. Registrar un nuevo usuario
```
POST http://localhost:8080/api/register
```

### 2. Iniciar sesión
```
POST http://localhost:8080/api/authenticate
```
Copiar el `id_token` de la respuesta.

### 3. Configurar Authorization en Postman
- Ir a la pestaña "Authorization"
- Tipo: "Bearer Token"
- Pegar el token obtenido

### 4. Probar endpoints protegidos
```
GET http://localhost:8080/api/eventos
GET http://localhost:8080/api/account
```

## 📱 Configuración Móvil

### Configurar URL del Backend

Editar `mobile/shared/src/main/java/com/celi/mobile/network/ApiConfig.kt`:

```kotlin
object ApiConfig {
    // Para emulador Android
    const val BASE_URL = "http://10.0.2.2:8080/"
    
    // Para dispositivo físico (reemplazar con tu IP)
    // const val BASE_URL = "http://192.168.1.100:8080/"
}
```

## 📝 Notas Importantes

1. **Red ZeroTier**: Es necesario estar conectado a la red ZeroTier (192.168.194.0/24) para acceder a los servicios de cátedra.

2. **Tokens de Cátedra**: El token de autenticación con la cátedra está configurado en las variables de entorno. Asegurarse de tener un token válido.

3. **Sincronización**: El proxy consume eventos de Kafka automáticamente y sincroniza con el backend.

4. **WebSocket**: El backend expone endpoints WebSocket para notificaciones en tiempo real en `/websocket/tracker`.

5. **CORS**: El backend está configurado para aceptar peticiones desde `localhost:8100` (Ionic), emuladores Android (`10.0.2.2`) y otras fuentes de desarrollo.

## 👥 Autor

Celina Guerra - Trabajo Final Programación II 2025

