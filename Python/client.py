import socket
import threading
import sys


class Client:
    def __init__(self, server="localhost", port=1500, username="Anonymous"):
        self.server = server
        self.port = port
        self.username = username
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.notif = " *** "

    def start(self):
        try:
            self.sock.connect((self.server, self.port))
        except Exception as e:
            print(f"{self.notif}Error connecting to server: {e}{self.notif}")
            return False

        print(
            f"{self.notif}Connected to server at {self.server}:{self.port}{self.notif}"
        )

        try:
            self.sock.sendall(self.username.encode())  # send username first
        except Exception as e:
            print(f"{self.notif}Failed to send username: {e}{self.notif}")
            return False

        # Start the thread that listens for messages from server
        threading.Thread(target=self.listen_from_server, daemon=True).start()
        return True

    def send_message(self, message):
        try:
            self.sock.sendall(message.encode())
        except Exception as e:
            print(f"{self.notif}Error sending message: {e}{self.notif}")

    def listen_from_server(self):
        while True:
            try:
                data = self.sock.recv(4096)
                if not data:
                    break
                print(data.decode(), end="")
                print("> ", end="", flush=True)
            except Exception as e:
                print(f"\n{self.notif}Server closed connection: {e}{self.notif}")
                break

    def disconnect(self):
        try:
            self.sock.close()
        except:
            pass


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument("username", nargs="?", default="Anonymous")
    parser.add_argument("port", nargs="?", type=int, default=1500)
    parser.add_argument("server", nargs="?", default="localhost")
    args = parser.parse_args()

    client = Client(server=args.server, port=args.port, username=args.username)
    if not client.start():
        sys.exit()

    print("\nHello! Welcome to the chatroom.")
    print("Instructions:")
    print("1. Type a message to broadcast to all.")
    print("2. Type '@username yourmessage' to send private message.")
    print("3. Type 'WHOISIN' to see active users.")
    print("4. Type 'LOGOUT' to leave the chatroom.\n")

    while True:
        msg = input("> ")
        if msg.upper() == "LOGOUT":
            client.send_message("/logout")
            break
        elif msg.upper() == "WHOISIN":
            client.send_message("/whoisin")
        else:
            client.send_message(msg)

    client.disconnect()
