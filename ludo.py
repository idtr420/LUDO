import random

class LudoGame:
    def __init__(self, players=4):
        self.players = players
        self.board = [0] * 52
        self.home = {i: [] for i in range(1, players+1)}
        self.start_positions = {1: 0, 2: 13, 3: 26, 4: 39}
        self.tokens = {i: [0, 0, 0, 0] for i in range(1, players+1)}
        
    def roll_dice(self):
        return random.randint(1, 6)
    
    def move_token(self, player, token, steps):
        current_pos = self.tokens[player][token]
        
        if current_pos == 0 and steps != 6:
            return False  # Can't move unless you roll a 6
            
        if current_pos == 0:
            start_pos = self.start_positions[player]
            if self.board[start_pos] != 0:
                self.send_token_home(self.board[start_pos])
            self.board[start_pos] = player
            self.tokens[player][token] = start_pos + 1
            return True
            
        new_pos = (current_pos + steps) % 52
        if new_pos == 0: new_pos = 52
            
        # Check if moving to home
        if (current_pos <= self.start_positions[player] < new_pos or 
            (new_pos < current_pos and (self.start_positions[player] < current_pos or self.start_positions[player] >= new_pos))):
            home_pos = new_pos - self.start_positions[player]
            if home_pos <= 6:
                self.home[player].append(token)
                self.tokens[player][token] = -1  # -1 means in home
                return True
                
        # Normal move
        if self.board[new_pos-1] != 0:
            self.send_token_home(self.board[new_pos-1])
        self.board[current_pos-1] = 0
        self.board[new_pos-1] = player
        self.tokens[player][token] = new_pos
        return True
        
    def send_token_home(self, player):
        for i, pos in enumerate(self.tokens[player]):
            if pos == self.board.index(player)+1:
                self.tokens[player][i] = 0
                self.board[pos-1] = 0
                break
                
    def play_turn(self, player):
        dice = self.roll_dice()
        print(f"Player {player} rolled a {dice}")
        
        movable_tokens = [i for i, pos in enumerate(self.tokens[player]) if pos != -1]
        if not movable_tokens:
            print("No tokens to move")
            return
            
        # Simple AI - move first available token
        for token in movable_tokens:
            if self.move_token(player, token, dice):
                print(f"Moved token {token+1}")
                break
                
    def check_winner(self):
        for player in self.home:
            if len(self.home[player]) == 4:
                return player
        return None
        
    def play_game(self):
        current_player = 1
        while not self.check_winner():
            self.play_turn(current_player)
            current_player = current_player % self.players + 1
            
        print(f"Player {self.check_winner()} wins!")

if __name__ == "__main__":
    game = LudoGame(4)
    game.play_game()
