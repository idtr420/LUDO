import java.util.Random;

public class Ludo {
    private int players;
    private int[] board;
    private int[][] home;
    private int[] startPositions;
    private int[][] tokens;
    private Random random;
    
    public Ludo(int players) {
        this.players = players;
        this.board = new int[52];
        this.home = new int[players+1][4];
        this.startPositions = new int[]{0, 0, 13, 26, 39};
        this.tokens = new int[players+1][4];
        this.random = new Random();
    }
    
    public int rollDice() {
        return random.nextInt(6) + 1;
    }
    
    public boolean moveToken(int player, int token, int steps) {
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
                home[player][token] = 1;
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
        
    private void sendTokenHome(int player) {
        for (int i = 0; i < 4; i++) {
            if (tokens[player][i] == board[player-1] + 1) {
                tokens[player][i] = 0;
                board[tokens[player][i]-1] = 0;
                break;
            }
        }
    }
                
    public void playTurn(int player) {
        int dice = rollDice();
        System.out.println("Player " + player + " rolled a " + dice);
        
        int[] movableTokens = new int[4];
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (tokens[player][i] != -1) {
                movableTokens[count++] = i;
            }
        }
        
        if (count == 0) {
            System.out.println("No tokens to move");
            return;
        }
            
        // Simple AI - move first available token
        for (int i = 0; i < count; i++) {
            if (moveToken(player, movableTokens[i], dice)) {
                System.out.println("Moved token " + (movableTokens[i]+1));
                break;
            }
        }
    }
        
    public Integer checkWinner() {
        for (int player = 1; player <= players; player++) {
            int homeCount = 0;
            for (int i = 0; i < 4; i++) {
                if (home[player][i] == 1) homeCount++;
            }
            if (homeCount == 4) return player;
        }
        return null;
    }
        
    public void playGame() {
        int currentPlayer = 1;
        while (checkWinner() == null) {
            playTurn(currentPlayer);
            currentPlayer = currentPlayer % players + 1;
        }
            
        System.out.println("Player " + checkWinner() + " wins!");
    }
    
    public static void main(String[] args) {
        Ludo game = new Ludo(4);
        game.playGame();
    }
}
