class LudoGame {
    constructor(players = 4) {
        this.players = players;
        this.board = new Array(52).fill(0);
        this.home = {};
        this.startPositions = {1: 0, 2: 13, 3: 26, 4: 39};
        this.tokens = {};
        
        for (let i = 1; i <= players; i++) {
            this.home[i] = [];
            this.tokens[i] = [0, 0, 0, 0];
        }
    }
    
    rollDice() {
        return Math.floor(Math.random() * 6) + 1;
    }
    
    moveToken(player, token, steps) {
        let currentPos = this.tokens[player][token];
        
        if (currentPos === 0 && steps !== 6) {
            return false; // Can't move unless you roll a 6
        }
            
        if (currentPos === 0) {
            let startPos = this.startPositions[player];
            if (this.board[startPos] !== 0) {
                this.sendTokenHome(this.board[startPos]);
            }
            this.board[startPos] = player;
            this.tokens[player][token] = startPos + 1;
            return true;
        }
            
        let newPos = (currentPos + steps) % 52;
        if (newPos === 0) newPos = 52;
            
        // Check if moving to home
        if ((currentPos <= this.startPositions[player] && this.startPositions[player] < newPos) || 
            (newPos < currentPos && (this.startPositions[player] < currentPos || this.startPositions[player] >= newPos))) {
            let homePos = newPos - this.startPositions[player];
            if (homePos <= 6) {
                this.home[player].push(token);
                this.tokens[player][token] = -1; // -1 means in home
                return true;
            }
        }
                
        // Normal move
        if (this.board[newPos-1] !== 0) {
            this.sendTokenHome(this.board[newPos-1]);
        }
        this.board[currentPos-1] = 0;
        this.board[newPos-1] = player;
        this.tokens[player][token] = newPos;
        return true;
    }
        
    sendTokenHome(player) {
        for (let i = 0; i < 4; i++) {
            if (this.tokens[player][i] === this.board.indexOf(player) + 1) {
                this.tokens[player][i] = 0;
                this.board[this.tokens[player][i]-1] = 0;
                break;
            }
        }
    }
                
    playTurn(player) {
        let dice = this.rollDice();
        console.log(`Player ${player} rolled a ${dice}`);
        
        let movableTokens = [];
        for (let i = 0; i < 4; i++) {
            if (this.tokens[player][i] !== -1) {
                movableTokens.push(i);
            }
        }
        
        if (movableTokens.length === 0) {
            console.log("No tokens to move");
            return;
        }
            
        // Simple AI - move first available token
        for (let token of movableTokens) {
            if (this.moveToken(player, token, dice)) {
                console.log(`Moved token ${token+1}`);
                break;
            }
        }
    }
        
    checkWinner() {
        for (let player in this.home) {
            if (this.home[player].length === 4) {
                return player;
            }
        }
        return null;
    }
        
    playGame() {
        let currentPlayer = 1;
        while (!this.checkWinner()) {
            this.playTurn(currentPlayer);
            currentPlayer = currentPlayer % this.players + 1;
        }
            
        console.log(`Player ${this.checkWinner()} wins!`);
    }
}

// Start game
let game = new LudoGame(4);
game.playGame();
