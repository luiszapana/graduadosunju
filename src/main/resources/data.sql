-- Tipos de anuncio
INSERT INTO anuncio_tipo(id, tipo, descripcion) VALUES (1, 'LABORAL', 'Ofertas laborales') ON CONFLICT DO NOTHING;

-- Universidad/Facultad/Carrera mínimas
INSERT INTO universidad(id, nombre) VALUES (1, 'UNJu') ON CONFLICT DO NOTHING;
INSERT INTO facultad(id, nombre, etiqueta, universidad_id) VALUES (1, 'FHyCS', 'FHyCS', 1) ON CONFLICT DO NOTHING;
INSERT INTO carrera(id, nombre, titulo, facultad_id) VALUES (1, 'Informática', 'Lic. en Sistemas', 1) ON CONFLICT DO NOTHING;

-- Usuario admin
INSERT INTO usuario(id, dni, apellido, nombre, email) VALUES (1, 12345678, 'Admin', 'UNJu', 'admin@unju.edu.ar') ON CONFLICT DO NOTHING;
-- password: admin123 -> bcrypt generado
INSERT INTO usuario_login(id, usuario, password, habilitado, registro_completo, fecha_registro, usuario_id) VALUES (
    1,
    'admin@unju.edu.ar',
    '$2a$10$hQWl9D5mWw3eM6kX6zYbA.5MKRtgfiLCLWqYwPUC3m2ZQpUrH1bGu',
    true,
    true,
    now(),
    1
) ON CONFLICT DO NOTHING;
INSERT INTO perfil(id, perfil, prioridad, visible_web) VALUES (1, 'ADMIN', 1, true) ON CONFLICT DO NOTHING;
INSERT INTO usuario_login_perfiles(login_id, perfiles_id) VALUES (1,1) ON CONFLICT DO NOTHING;

-- Anuncio demo
INSERT INTO anuncio(id, titulo, contenido, lugar, fecha_registro, enviado, tipo_id) VALUES (
    1, 'Ejemplo Anuncio', 'Contenido de ejemplo', 'San Salvador de Jujuy', now(), false, 1
) ON CONFLICT DO NOTHING;
