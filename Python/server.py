import socket
import threading
from datetime import datetime


class ChatMessage:
    MESSAGE = 1
    LOGOUT = 2
    WHOISIN = 3

    def __init__(self, type, message=None):
        self.type = type
        self.message = message


class ClientThread(threading.Thread):
    def __init__(self, server, conn, addr, client_id):
        super().__init__()
        self.server = server
        self.conn = conn
        self.addr = addr
        self.id = client_id
        self.username = ""
        self.date = datetime.now().strftime("%c")

    def run(self):
        try:
            self.username = self.conn.recv(1024).decode()
            self.server.broadcast(
                f"{self.server.notif}{self.username} has joined the chat room.{self.server.notif}"
            )
            self.conn.sendall("Welcome to the chat!\n".encode())

            keepGoing = True
            while keepGoing:
                data = self.conn.recv(4096).decode()
                if not data:
                    break
                type, msg = self.parse_message(data)
                if type == ChatMessage.MESSAGE:
                    success = self.server.broadcast(f"{self.username}: {msg}")
                    if not success:
                        self.write_msg(
                            f"{self.server.notif}Sorry. No such user exists.{self.server.notif}"
                        )
                elif type == ChatMessage.LOGOUT:
                    self.server.display(
                        f"{self.username} disconnected with a LOGOUT message."
                    )
                    keepGoing = False
                elif type == ChatMessage.WHOISIN:
                    self.write_msg("List of users connected:\n")
                    for i, ct in enumerate(self.server.clients):
                        self.write_msg(f"{i + 1}) {ct.username} since {ct.date}\n")
        except Exception as e:
            self.server.display(f"Exception in client thread: {e}")
        finally:
            self.server.remove(self.id)
            self.close()

    def parse_message(self, data):
        if data.startswith("/logout"):
            return ChatMessage.LOGOUT, None
        elif data.startswith("/whoisin"):
            return ChatMessage.WHOISIN, None
        else:
            return ChatMessage.MESSAGE, data

    def write_msg(self, msg):
        try:
            self.conn.sendall(msg.encode())
            return True
        except Exception:
            self.close()
            return False

    def close(self):
        try:
            self.conn.close()
        except:
            pass


class Server:
    def __init__(self, port=1500):
        self.port = port
        self.keepGoing = True
        self.clients = []
        self.uniqueId = 0
        self.notif = " *** "

    def display(self, msg):
        print(f"{datetime.now().strftime('%H:%M:%S')} {msg}")

    def broadcast(self, message):
        now = datetime.now().strftime("%H:%M:%S")
        if message.startswith("/") or " @" in message:
            parts = message.split(" ", 2)
            if len(parts) >= 2 and parts[1].startswith("@"):
                recipient = parts[1][1:]
                message = f"{parts[0]} {parts[2]}"
                messageLf = f"{now} {message}\n"
                for ct in self.clients:
                    if ct.username == recipient:
                        if not ct.write_msg(messageLf):
                            self.clients.remove(ct)
                        return True
                return False
        else:
            messageLf = f"{now} {message}\n"
            print(messageLf, end="")
            for ct in self.clients[:]:
                if not ct.write_msg(messageLf):
                    self.clients.remove(ct)
                    self.display(
                        f"Disconnected Client {ct.username} removed from list."
                    )
        return True

    def remove(self, client_id):
        disconnected = ""
        for ct in self.clients[:]:
            if ct.id == client_id:
                disconnected = ct.username
                self.clients.remove(ct)
                break
        if disconnected:
            self.broadcast(
                f"{self.notif}{disconnected} has left the chat room.{self.notif}"
            )

    def stop(self):
        self.keepGoing = False
        try:
            socket.socket(socket.AF_INET, socket.SOCK_STREAM).connect(
                ("localhost", self.port)
            )
        except:
            pass

    def start(self):
        self.display(f"Server starting on port {self.port}")
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind(("", self.port))
            s.listen()
            while self.keepGoing:
                try:
                    conn, addr = s.accept()
                    self.display(f"Connection from {addr}")
                    self.uniqueId += 1
                    client_thread = ClientThread(self, conn, addr, self.uniqueId)
                    self.clients.append(client_thread)
                    client_thread.start()
                except Exception as e:
                    self.display(f"Error accepting connections: {e}")
                    break
            for ct in self.clients:
                ct.close()
            self.display("Server stopped.")


if __name__ == "__main__":
    import sys

    port = 1500
    if len(sys.argv) == 2:
        try:
            port = int(sys.argv[1])
        except:
            print("Invalid port number.")
            sys.exit(1)
    server = Server(port)
    server.start()
