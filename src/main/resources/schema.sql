-- Esquema mínimo para arrancar sin migraciones
CREATE TABLE IF NOT EXISTS universidad (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS facultad (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    etiqueta VARCHAR(255),
    universidad_id BIGINT REFERENCES universidad(id)
);

CREATE TABLE IF NOT EXISTS carrera (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    titulo VARCHAR(255),
    facultad_id BIGINT REFERENCES facultad(id)
);

CREATE TABLE IF NOT EXISTS perfil (
    id BIGSERIAL PRIMARY KEY,
    perfil VARCHAR(100),
    prioridad INTEGER,
    visible_web BOOLEAN
);

CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    dni BIGINT,
    apellido VARCHAR(255),
    nombre VARCHAR(255),
    fecha_nacimiento TIMESTAMPTZ,
    email VARCHAR(255) UNIQUE,
    telefono BIGINT,
    celular BIGINT,
    imagen BYTEA
);

CREATE TABLE IF NOT EXISTS usuario_login (
    id BIGSERIAL PRIMARY KEY,
    usuario VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    habilitado BOOLEAN,
    registro_completo BOOLEAN,
    codigo_verificacion VARCHAR(255),
    fecha_primer_login TIMESTAMPTZ,
    fecha_ultimo_login TIMESTAMPTZ,
    fecha_registro TIMESTAMPTZ,
    id_registrador BIGINT,
    usuario_id BIGINT REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS usuario_login_perfiles (
    login_id BIGINT REFERENCES usuario_login(id) ON DELETE CASCADE,
    perfiles_id BIGINT REFERENCES perfil(id) ON DELETE CASCADE,
    PRIMARY KEY (login_id, perfiles_id)
);

CREATE TABLE IF NOT EXISTS anuncio_tipo (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(100),
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS anuncio (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255),
    contenido TEXT,
    lugar VARCHAR(255),
    mails_reenvio VARCHAR(1000),
    id_empresa BIGINT,
    duracion_desde TIMESTAMPTZ,
    duracion_hasta TIMESTAMPTZ,
    fecha_registro TIMESTAMPTZ,
    enviado BOOLEAN,
    fecha_envio TIMESTAMPTZ,
    mail_contacto VARCHAR(255),
    telefono_contacto BIGINT,
    especializaciones VARCHAR(1000),
    mails_especificos VARCHAR(1000),
    tipo_id BIGINT REFERENCES anuncio_tipo(id)
);

CREATE TABLE IF NOT EXISTS anuncio_carreras (
    anuncio_id BIGINT REFERENCES anuncio(id) ON DELETE CASCADE,
    carreras_id BIGINT REFERENCES carrera(id) ON DELETE CASCADE,
    PRIMARY KEY (anuncio_id, carreras_id)
);
