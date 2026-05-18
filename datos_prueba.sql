-- ================================================================
-- DATOS DE PRUEBA — Sistema de Triage UQ — Hito 3
-- Ejecutar DESPUÉS de arrancar el proyecto por primera vez
-- para que JPA cree las tablas automáticamente.
-- ================================================================

-- Agregar columna password si el proyecto viene del Hito 2
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE solicitudes ADD COLUMN IF NOT EXISTS fecha_limite DATETIME;
ALTER TABLE solicitudes ADD COLUMN IF NOT EXISTS impacto_grado TINYINT(1) DEFAULT 0;

-- ── Usuarios con passwords BCrypt ────────────────────────────────
-- admin123   → hash BCrypt
-- docente123 → hash BCrypt
-- est123     → hash BCrypt
INSERT INTO usuarios (id, nombre, apellido, correo, identificacion, activo, rol, password)
VALUES
  (1, 'Carlos',  'Rodriguez', 'carlos.rodriguez@uq.edu.co', '987001', 1, 'ADMINISTRATIVO',
   '$2a$10$N.zmdr9k8rKMoH3kFzHNROuSMcKkLB0w2X1VqoXxvkGjnBivZklWW'),
  (2, 'Laura',   'Martinez',  'laura.martinez@uq.edu.co',  '987002', 1, 'ADMINISTRATIVO',
   '$2a$10$N.zmdr9k8rKMoH3kFzHNROuSMcKkLB0w2X1VqoXxvkGjnBivZklWW'),
  (3, 'Pedro',   'Inactivo',  'pedro.inactivo@uq.edu.co',  '987003', 0, 'ADMINISTRATIVO',
   '$2a$10$N.zmdr9k8rKMoH3kFzHNROuSMcKkLB0w2X1VqoXxvkGjnBivZklWW'),
  (4, 'Maria',   'Docente',   'maria.docente@uq.edu.co',   '987004', 1, 'DOCENTE',
   '$2a$10$8K1p/a0dR1xqM2LxCQgXD.wN5JXGcYgbCbILSUSx7Nt7c6D1VVi7W'),
  (5, 'Juan',    'Estudiante','juan.est@estudiante.edu.co','20231234',1, 'ESTUDIANTE',
   '$2a$10$dHZDvW4cEjXH.qF6e5JhJO1LH.eXzMfTEpq.9i3yUSmzfBmgBY5h6')
ON DUPLICATE KEY UPDATE password = VALUES(password), activo = VALUES(activo);

-- ── Responsables ──────────────────────────────────────────────────
INSERT INTO responsables (id, cargo, dependencia)
VALUES
  (1, 'Secretaria Academica', 'Direccion de Programa'),
  (2, 'Coordinador',          'Registro y Control'),
  (3, 'Auxiliar',             'Bienestar Universitario')
ON DUPLICATE KEY UPDATE cargo = VALUES(cargo);

-- ── Credenciales para pruebas ─────────────────────────────────────
-- ADMINISTRATIVO: carlos.rodriguez@uq.edu.co / admin123
-- ADMINISTRATIVO: laura.martinez@uq.edu.co   / admin123
-- DOCENTE:        maria.docente@uq.edu.co    / docente123
-- ESTUDIANTE:     juan.est@estudiante.edu.co  / est123
-- INACTIVO (error 401): pedro.inactivo@uq.edu.co / admin123
