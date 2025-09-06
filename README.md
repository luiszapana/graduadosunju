# UNJu Graduados - Gestión de Graduados y Anuncios

Proyecto Spring Boot 3 (Java 17, Maven) con Spring Web, Data JPA, Security (form login), Thymeleaf, Mail, Validación, PostgreSQL y Swagger/OpenAPI.

## Requisitos
- Java 17
- Maven 3.9+
- PostgreSQL con una base `GRADUADOS` y usuario `GRADUADOSUNJU`/`GRADUADOSUNJU`

## Configuración

1. application.properties ya apunta a PostgreSQL local:
```
spring.datasource.url=jdbc:postgresql://localhost/GRADUADOS
spring.datasource.username=GRADUADOSUNJU
spring.datasource.password=GRADUADOSUNJU
```

2. Edite `src/main/resources/configuration.properties` y reemplace las contraseñas de correo:
```
mail.sender.principal.password=REEMPLAZAR
mail.sender.noreply.password=REEMPLAZAR
```

Si no tiene credenciales SMTP válidas, el sistema imprime el enlace de recuperación en consola.

## Compilar y ejecutar

- Compilar: `mvn clean package`
- Ejecutar: `mvn spring-boot:run`
- Acceder: http://localhost:8080

## Acceso
- Login: `admin@unju.edu.ar` / `admin123`
- Página inicial: /login → /anuncios

## Funcionalidades incluidas
- Seguridad: Form login en `/login`, logout en `/logout`, BCrypt.
- CRUD de Anuncio: REST en `/api/anuncios` y vistas en `/anuncios` (listar, filtrar, crear, editar, eliminar).
- Filtros por tipo y fecha.
- Recuperación de contraseña en `/recuperar` con token de 1 hora y envío por correo (o consola).
- Swagger UI: `/swagger-ui.html`

## Estructura de paquetes
Ver código bajo `src/main/java/com/unju/graduados`:
- constants/SecurityConstants.java
- filter/* (BaseFilter, PrivateAreaFilter, PrivateAreaModServicesFilter, VerificationFilter)
- model/* (Usuario, UsuarioLogin, Perfil, Universidad, Facultad, Carrera, AnuncioTipo, Anuncio)
- model/dao/interfaces/* (IAnuncioDao, ITipoAnuncioDao, ICarreraDao, IFacultadDao, IUsuarioLoginDao)
- dto/* (AnuncioDTO, UsuarioLoginDTO)
- service/* (IAnuncioService, IUsuarioLoginService, ITipoAnuncioService)
- service/impl/*
- security/* (SecurityConfig, CustomUserDetailsService)
- rest/* (AnuncioRestController, UsuarioLoginRestController, AnuncioMvcController, RecuperacionController)
- config/* (MailConfig, MailProperties)
- exception/* (GlobalExceptionHandler)

## Datos de ejemplo
Se cargan mediante `data.sql`: usuario admin, universidad, facultad, carrera, tipo de anuncio, anuncio inicial.

## Notas
- Se incluyeron stubs de filtros de seguridad con TODO para completar lógica específica.
- Se usa ZonedDateTime en entidades y validación con jakarta.validation en DTOs y formularios.
- Para ambiente productivo, externalice credenciales y secretos.
