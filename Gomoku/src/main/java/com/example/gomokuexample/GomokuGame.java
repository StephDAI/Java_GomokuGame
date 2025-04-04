package com.example.gomokuexample;


class GomokuGame {
    private int[][] board;          // 0: empty, 1: player1's stone, 2: player2's stone
    private int currentPlayer;      // 1: player1, 2: player2
    private boolean gameOver;       // true: game over, false: game not over
    private int winner;             // 0: no winner, 1: player 1 wins, 2: player 2 wins
    private int movements;
    private int max_movements;
    private int boardSize;// size of the board
    public int[][] maximumLength = new int[450][3]; // maximum length of stones in a row for each player in a certain movement, 1: player1, 2: player2
    private String[] status = new String[450];// discribe the player and his move in a certain movement, e.g. "1,15,15"



    public GomokuGame(int boardSize) {
        if (boardSize < 5 || boardSize > 20) {
            throw new IllegalArgumentException("Board size should be between 5 and 20.");
        }
        this.boardSize = boardSize;
        board = new int[boardSize][boardSize];            // init to be all zeros
        currentPlayer = 1;
        gameOver = false;
        winner = 0;
        maximumLength[0] = new int[]{0, 0, 0};
    }

    public boolean checkWin(int x, int y) {
        int[][][] directionLines = {{{0, 1}, {0, -1}},                // vertical
                {{1, 0}, {-1, 0}},                // horizontal
                {{1, 1}, {-1, -1}},               // diagonal
                {{1, -1}, {-1, 1}}};              // anti-diagonal
        for (int[][] oppositeDirs : directionLines) {
            int count = 1;
            maximumLength[movements][currentPlayer%2+1] = maximumLength[movements-1][currentPlayer%2+1];
            for (int[] direction: oppositeDirs) {
                int dx = direction[0];
                int dy = direction[1];
                for (int i = 1; i < 5; i++) {
                    int newX = x + i * dx;
                    int newY = y + i * dy;
                    if (!isValidPosition(newX, newY) || board[newX][newY] != board[x][y]) {
                        break;
                    }
                    count++;
                    if (count >= 5) {
                        return true;
                    }
                }
            }
            // update the maximum length for current player
            maximumLength[movements][currentPlayer] = Math.max(maximumLength[movements-1][currentPlayer], Math.max(maximumLength[movements][currentPlayer],count));
        }
        return false;
    }


    public boolean move(int x, int y) {
        // place a piece at (x, y) for the current player, and then switch to the other player
        if (gameOver) {
            return false;
        }

        if (!isValidPosition(x, y)) {
            return false;
        }

        if (board[x][y] != 0) {
            return false;
        }
        movements++;
        status[movements] = currentPlayer + "," + x + "," + y;
        max_movements = movements;
        // after mouse clicks, clear the subsequent movement status
        for(int i = movements + 1; status[i] != null; i++){
            status[i] = null;
            maximumLength[i] = new int[]{0, 0, 0};
        }

        board[x][y] = currentPlayer;
        if (checkWin(x, y)) {
            gameOver = true;
            winner = currentPlayer;
        }
        currentPlayer = currentPlayer == 1 ? 2 : 1;// switch player

        return true;
    }

    public boolean undo() {
        if (movements == 0 | gameOver) {
            return false;
        }
        String[] lastMove = status[movements].split(",");
        int x = Integer.parseInt(lastMove[1]);
        int y = Integer.parseInt(lastMove[2]);
        board[x][y] = 0;
        movements--;
        currentPlayer = status[movements+1].charAt(0) - '0';
        return true;
    }

    public boolean redo() {
        if (movements == max_movements | gameOver) {
            return false;
        }
        movements++;
        String[] nextMove = status[movements].split(",");
        int x = Integer.parseInt(nextMove[1]);
        int y = Integer.parseInt(nextMove[2]);
        board[x][y] = nextMove[0].charAt(0) - '0';
        currentPlayer = (status[movements].charAt(0) - '0')%2+1;
        return true;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public int getMovements() {return movements;}

    public int getCurrentPlayer() {return currentPlayer;}

    public int[][] getBoard() {
        return board;
    }

}