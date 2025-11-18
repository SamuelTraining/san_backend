CREATE TABLE trd_archivo (
  id SERIAL PRIMARY KEY,
  nombre TEXT NOT NULL,
  ruta TEXT NOT NULL,
  tipo TEXT,
  tamano BIGINT,
  hash_md5 CHAR(32),
  estado VARCHAR(20),
  motivo TEXT,
  fecha_carga TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
