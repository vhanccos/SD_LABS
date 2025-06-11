from Database import Database
import mysql.connector


def mostrar_tabla(cursor, nombre_tabla):
    print(f"\nContenido actual de [{nombre_tabla}]:")
    try:
        cursor.execute(f"SELECT * FROM {nombre_tabla}")
        filas = cursor.fetchall()
        if filas:
            for fila in filas:
                print(fila)
        else:
            print("(Sin registros)")
    except mysql.connector.Error as err:
        print(f"ERROR al mostrar {nombre_tabla}: {err}")


def main():
    db = Database()
    connection = db.get_connection()

    if not connection:
        return

    connection.autocommit = True  # Desactiva la transacción implícita

    try:
        cursor1 = connection.cursor()
        cursor2 = connection.cursor()

        # Mostrar contenido de las tablas antes de los inserts
        mostrar_tabla(cursor1, "miTabla")
        mostrar_tabla(cursor2, "miOtraTabla")

        print("\n--- Insertando datos ---")
        print("Primer INSERT tabla [miTabla]")
        cursor1.execute(
            "INSERT INTO miTabla (DNI, correo) VALUES (%s, %s)",
            ("000001", "micorreo@mail.com"),
        )

        print("Segundo INSERT tabla [miTabla]")
        cursor1.execute(
            "INSERT INTO miTabla (DNI, correo) VALUES (%s, %s)",
            ("000002", "amayuya@mail.com"),
        )

        print("Tercer INSERT tabla [miTabla]")
        cursor1.execute(
            "INSERT INTO miTabla (DNI, correo) VALUES (%s, %s)",
            ("000003", "diosdado@mail.com"),
        )

        print("Primer INSERT tabla [miOtraTabla]")
        cursor2.execute(
            "INSERT INTO miOtraTabla (nombre, apellido, edad) VALUES (%s, %s, %s)",
            ("Juan", "Perez", "Hola soy un error"),  # ERROR: edad espera INT
        )

    except mysql.connector.Error as err:
        print(f"\nERROR: {err}")
    finally:
        print("\n--- Estado final de las tablas ---")
        mostrar_tabla(cursor1, "miTabla")
        mostrar_tabla(cursor2, "miOtraTabla")

        print("\nCierra conexion a la base de datos")
        if cursor1:
            cursor1.close()
        if cursor2:
            cursor2.close()
        if connection:
            connection.close()


if __name__ == "__main__":
    main()

