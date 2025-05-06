class ChatMessage:
    WHOISIN = 0
    MESSAGE = 1
    LOGOUT = 2

    def __init__(self, msg_type, message):
        self.type = msg_type
        self.message = message

    def get_type(self):
        return self.type

    def get_message(self):
        return self.message
