package questions;

import java.util.List;

class InvalidMoveException extends RuntimeException {
    InvalidMoveException(String message) {
        super(message);
    }
}

enum Symbol {

    X('X'),
    O('O'),
    EMPTY('_');

    private final char symbol;

    Symbol(char ch) {
        this.symbol = ch;
    }

    public char getSymbol() {
        return this.symbol;
    }
}

enum GameStatus {
    IN_PROGRESS,
    WINNER_X,
    WINNER_O,
    DRAW
}

class Cell {
    private Symbol symbol;

    public Cell() {
        this.symbol = Symbol.EMPTY;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public boolean isEmpty() {
        return this.symbol == Symbol.EMPTY;
    }
}

class Board {
    private final Cell[][] grid;
    private final int size;

    public Board(int size) {
        this.size = size;
        grid = new Cell[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public void makeMove(int row, int col, Symbol symbol) {
        validateMove(row, col);
        grid[row][col].setSymbol(symbol);
    }

    public boolean isCellEmpty(int row, int col) {
        validateMove(row, col);
        return grid[row][col].isEmpty();
    }

    public boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].isEmpty())
                    return false;
            }
        }
        return true;
    }

    public int getBoardSize() {
        return this.size;
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    private void validateMove(int row, int col) {
        if (row < 0 || row >= this.size || col < 0 || col >= size) {
            throw new InvalidMoveException("Invalid move");
        }
    }

    public void printBoard() {
        System.out.println();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(" " + grid[i][j].getSymbol().getSymbol() + " ");
                if (j < size - 1)
                    System.out.print("|");
            }
            System.out.println();
            if (i < size - 1) {
                System.out.println("-".repeat(size * 4 - 1));
            }
        }
        System.out.println();
    }

}

class Player {
    private final Symbol symbol;
    private final String name;

    Player(String name, Symbol symbol) {
        if (symbol == Symbol.EMPTY) {
            throw new IllegalArgumentException("Invalid Symbol");
        }
        this.name = name;
        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Player [symbol=" + symbol + ", name=" + name + "]";
    }

}

class Game {
    private final Board board;
    private final List<Player> players;
    private GameStatus gameStatus;
    private int currentPlayerIndex;

    Game(List<Player> players, Board board) {
        this.players = players;
        this.board = board;
        this.gameStatus = GameStatus.IN_PROGRESS;
        currentPlayerIndex = 0;
    }

    public synchronized void makeMove(int row, int col) {
        if (gameStatus != GameStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Game is already over");
        }

        if (!board.isCellEmpty(row, col)) {
            throw new InvalidMoveException("Cell is already occupied");
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        board.makeMove(row, col, currentPlayer.getSymbol());

        if (checkWin(row, col, currentPlayer.getSymbol())) {
            gameStatus = currentPlayer.getSymbol() == Symbol.O ? GameStatus.WINNER_O : GameStatus.WINNER_X;
            return;
        }

        if (board.isBoardFull()) {
            gameStatus = GameStatus.DRAW;
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % 2;

    }

    private boolean checkWin(int row, int col, Symbol symbol) {
        int boardSize = board.getBoardSize();

        boolean win = true;

        // check the full row
        for (int c = 0; c < boardSize; c++) {
            if (board.isCellEmpty(row, c) || board.getCell(row, c).getSymbol() != symbol) {
                win = false;
                break;
            }
        }
        if (win)
            return true;

        // check the full col
        win = true;
        for (int r = 0; r < boardSize; r++) {
            if (board.isCellEmpty(r, col) || board.getCell(r, col).getSymbol() != symbol) {
                win = false;
                break;
            }
        }
        if (win)
            return true;

        // check main diagonal - will check the diagomal if and only if row == col
        if (row == col) {
            win = true;
            for (int i = 0; i < boardSize; i++) {
                if (board.isCellEmpty(i, i) || board.getCell(i, i).getSymbol() != symbol) {
                    win = false;
                    break;
                }
            }
            if (win)
                return true;
        }

        // check anti diagonal - will check the anti diagonal if and only if
        // row + col == size - 1 (ex - (row=2,col=0),(row=1,col=1),(row=0,col=2) ,
        // boardSize = 3)
        if (row + col == boardSize - 1) { // 2 0 , 1 1, 0 2
            win = true;
            for (int i = boardSize - 1; i >= 0; i--) {
                if (board.isCellEmpty(i, (boardSize - 1) - i)
                        || board.getCell(i, (boardSize - 1) - i).getSymbol() != symbol) {
                    win = false;
                    break;
                }
            }
            if (win)
                return true;
        }

        return false;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getWinner() {
        if (gameStatus == GameStatus.WINNER_X) {
            return players.get(0).getSymbol() == Symbol.X ? players.get(0) : players.get(1);
        } else if (gameStatus == GameStatus.WINNER_O) {
            return players.get(0).getSymbol() == Symbol.O ? players.get(0) : players.get(1);
        }
        return null;
    }

    public void printBoard() {
        board.printBoard();
    }

}

public class TicTacToe {
    static void main() {
        Player player1 = new Player("Alice", Symbol.X);
        Player player2 = new Player("Bob", Symbol.O);

        List<Player> players = List.of(player1, player2);

        Game game = new Game(players, new Board(3));

        System.out.println("========== TIC TAC TOE ==========");

        // Alice (X) completes the top row and wins
        game.makeMove(0, 0); // X at (0,0)
        game.makeMove(1, 0); // O at (1,0)
        game.makeMove(0, 1); // X at (0,1)
        game.makeMove(1, 1); // O at (1,1)
        game.makeMove(0, 2); // X at (0,2) - Alice wins!

        game.printBoard();

        System.out.println("Result: " + game.getGameStatus());

        Player winner = game.getWinner();
        if (winner != null) {
            System.out.println("Winner: " + winner.getName());
        }
    }
}
