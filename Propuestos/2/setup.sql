-- Crear la base de datos (opcional)
CREATE DATABASE IF NOT EXISTS prueba_transacciones
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

-- Usar la base de datos
USE prueba_transacciones;

-- Crear tabla 'mitabla'
CREATE TABLE IF NOT EXISTS miTabla (
  DNI VARCHAR(12) DEFAULT NULL,
  correo VARCHAR(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Crear tabla 'miotratabla'
CREATE TABLE IF NOT EXISTS miOtraTabla (
  nombre VARCHAR(20) DEFAULT NULL,
  apellido VARCHAR(20) DEFAULT NULL,
  edad INT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
