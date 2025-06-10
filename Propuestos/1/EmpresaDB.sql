-- Crear la base de datos
DROP DATABASE IF EXISTS EmpresaDB;
CREATE DATABASE EmpresaDB;
USE EmpresaDB;

-- Crear tabla Departamento
CREATE TABLE Departamento (
    IDDpto INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Telefono VARCHAR(20),
    Fax VARCHAR(20)
);

-- Crear tabla Ingeniero
CREATE TABLE Ingeniero (
    IDIng INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Especialidad VARCHAR(100),
    Cargo VARCHAR(50),
    IDDpto INT,
    FOREIGN KEY (IDDpto) REFERENCES Departamento(IDDpto)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Crear tabla Proyecto
CREATE TABLE Proyecto (
    IDProy INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Fec_Inicio DATE NOT NULL,
    Fec_Termino DATE NOT NULL,
    IDIng INT NOT NULL,
    FOREIGN KEY (IDIng) REFERENCES Ingeniero(IDIng)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_fechas CHECK (Fec_Termino >= Fec_Inicio)
);

-- Índices secundarios para búsqueda rápida
CREATE INDEX idx_nombre_proy ON Proyecto (Nombre);
CREATE INDEX idx_nombre_ing ON Ingeniero (Nombre);

-- Sentencia preparada de ejemplo (para insertar ingeniero)
PREPARE stmt_insert_ing FROM 'INSERT INTO Ingeniero (Nombre, Especialidad, Cargo, IDDpto) VALUES (?, ?, ?, ?)';
SET @Nombre = 'Juan Perez', @Especialidad = 'Software', @Cargo = 'Jefe de Proyecto', @IDDpto = 1;
EXECUTE stmt_insert_ing USING @Nombre, @Especialidad, @Cargo, @IDDpto;
DEALLOCATE PREPARE stmt_insert_ing;

-- Procedimiento almacenado para insertar Proyecto
DELIMITER $$

CREATE PROCEDURE InsertarProyecto (
    IN pNombre VARCHAR(100),
    IN pFec_Inicio DATE,
    IN pFec_Termino DATE,
    IN pIDIng INT
)
BEGIN
    INSERT INTO Proyecto (Nombre, Fec_Inicio, Fec_Termino, IDIng)
    VALUES (pNombre, pFec_Inicio, pFec_Termino, pIDIng);
END $$

DELIMITER ;

-- Transacción de ejemplo
START TRANSACTION;

-- Insertar un departamento
INSERT INTO Departamento (Nombre, Telefono, Fax)
VALUES ('TI', '987654321', '987654322');

-- Insertar un ingeniero usando LAST_INSERT_ID()
INSERT INTO Ingeniero (Nombre, Especialidad, Cargo, IDDpto)
VALUES ('Maria Lopez', 'Redes', 'Ingeniero Senior', LAST_INSERT_ID());

-- Confirmar transacción
COMMIT;

-- Llamar al procedimiento para insertar proyecto
CALL InsertarProyecto('Proyecto Alpha', '2025-06-01', '2025-12-31', 2);

