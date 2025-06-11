import mysql.connector
from mysql.connector import Error


class Database:
    def __init__(self):
        self.host = "localhost"
        self.database = "prueba_transacciones"
        self.user = "root"
        self.password = "password"

    def get_connection(self):
        try:
            conn = mysql.connector.connect(
                host=self.host,
                database=self.database,
                user=self.user,
                password=self.password,
            )
            if conn.is_connected():
                print(f"Conectado a la base de datos [{self.database}]")
                return conn
        except Error as e:
            print(f"Error al conectar: {e}")
        return None
