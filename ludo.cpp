#include <iostream>
#include <vector>
#include <cstdlib>
#include <ctime>
#include <map>

using namespace std;

class LudoGame {
private:
    int players;
    vector<int> board;
    map<int, vector<int>> home;
    map<int, int> startPositions;
    map<int, vector<int>> tokens;
    
public:
    LudoGame(int players = 4) : players(players) {
        board.resize(52, 0);
        startPositions = {{1, 0}, {2, 13}, {3, 26}, {4, 39}};
        
        for (int i = 1; i <= players; i++) {
            home[i] = vector<int>();
            tokens[i] = {0, 0, 0, 0};
        }
        
        srand(time(0));
    }
    
    int rollDice() {
        return rand() % 6 + 1;
    }
    
    bool moveToken(int player, int token, int steps) {
        int currentPos = tokens[player][token];
        
        if (currentPos == 0 && steps != 6) {
            return false; // Can't move unless you roll a 6
        }
            
        if (currentPos == 0) {
            int startPos = startPositions[player];
            if (board[startPos] != 0) {
                sendTokenHome(board[startPos]);
            }
            board[startPos] = player;
            tokens[player][token] = startPos + 1;
            return true;
        }
            
        int newPos = (currentPos + steps) % 52;
        if (newPos == 0) newPos = 52;
            
        // Check if moving to home
        if ((currentPos <= startPositions[player] && startPositions[player] < newPos) || 
            (newPos < currentPos && (startPositions[player] < currentPos || startPositions[player] >= newPos))) {
            int homePos = newPos - startPositions[player];
            if (homePos <= 6) {
                home[player].push_back(token);
                tokens[player][token] = -1; // -1 means in home
                return true;
            }
        }
                
        // Normal move
        if (board[newPos-1] != 0) {
            sendTokenHome(board[newPos-1]);
        }
        board[currentPos-1] = 0;
        board[newPos-1] = player;
        tokens[player][token] = newPos;
        return true;
    }
        
    void sendTokenHome(int player) {
        for (int i = 0; i < 4; i++) {
            if (tokens[player][i] == board[player-1] + 1) {
                tokens[player][i] = 0;
                board[tokens[player][i]-1] = 0;
                break;
            }
        }
    }
                
    void playTurn(int player) {
        int dice = rollDice();
        cout << "Player " << player << " rolled a " << dice << endl;
        
        vector<int> movableTokens;
        for (int i = 0; i < 4; i++) {
            if (tokens[player][i] != -1) {
                movableTokens.push_back(i);
            }
        }
        
        if (movableTokens.empty()) {
            cout << "No tokens to move" << endl;
            return;
        }
            
        // Simple AI - move first available token
        for (int token : movableTokens) {
            if (moveToken(player, token, dice)) {
                cout << "Moved token " << (token+1) << endl;
                break;
            }
        }
    }
        
    int checkWinner() {
        for (auto& [player, tokens] : home) {
            if (tokens.size() == 4) return player;
        }
        return 0;
    }
        
    void playGame() {
        int currentPlayer = 1;
        while (!checkWinner()) {
            playTurn(currentPlayer);
            currentPlayer = currentPlayer % players + 1;
        }
            
        cout << "Player " << checkWinner() << " wins!" << endl;
    }
};

int main() {
    LudoGame game(4);
    game.playGame();
    return 0;
}
