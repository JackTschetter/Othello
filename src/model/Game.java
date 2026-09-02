package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns a complete Othello game: board state, side to move, pass handling, and
 * undo history.
 */
public class Game {

    private Board board;
    private int currentPlayer;
    private String statusMessage;
    private final List<Snapshot> history;

    public Game() {
        this.history = new ArrayList<>();
        restart();
    }

    public Game(Board board, int currentPlayer) {
        this.history = new ArrayList<>();
        this.board = board == null ? new Board() : board.copy();
        this.currentPlayer = currentPlayer;
        this.statusMessage = Board.colorName(currentPlayer) + " to move.";
        normalizeTurn();
    }

    private Game(Board board, int currentPlayer, String statusMessage) {
        this.history = new ArrayList<>();
        this.board = board.copy();
        this.currentPlayer = currentPlayer;
        this.statusMessage = statusMessage;
    }

    public void restart() {
        this.board = new Board();
        this.currentPlayer = Board.BLACK;
        this.statusMessage = "Black to move.";
        this.history.clear();
    }

    public Game copy() {
        return new Game(board, currentPlayer, statusMessage);
    }

    public boolean play(Move move) {
        if (isGameOver() || move == null || !board.isLegalMove(currentPlayer, move)) {
            return false;
        }

        history.add(new Snapshot(board.copy(), currentPlayer, statusMessage));
        int mover = currentPlayer;
        board.applyMove(currentPlayer, move);
        currentPlayer = Board.opponent(currentPlayer);
        statusMessage = Board.colorName(mover) + " played " + move.toDisplayString() + ".";
        normalizeTurn();
        return true;
    }

    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }

        Snapshot snapshot = history.remove(history.size() - 1);
        this.board = snapshot.board.copy();
        this.currentPlayer = snapshot.currentPlayer;
        this.statusMessage = snapshot.statusMessage;
        return true;
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    private void normalizeTurn() {
        if (isGameOver()) {
            statusMessage = gameOverMessage();
            return;
        }

        if (board.hasAnyMove(currentPlayer)) {
            if (!statusMessage.endsWith("to move.")) {
                statusMessage = statusMessage + " " + Board.colorName(currentPlayer) + " to move.";
            }
            return;
        }

        int skippedPlayer = currentPlayer;
        currentPlayer = Board.opponent(currentPlayer);
        if (board.hasAnyMove(currentPlayer)) {
            statusMessage = Board.colorName(skippedPlayer) + " has no legal move and passes. "
                    + Board.colorName(currentPlayer) + " to move.";
        } else {
            statusMessage = gameOverMessage();
        }
    }

    public Board getBoard() {
        return board.copy();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Move> legalMoves() {
        return board.legalMoves(currentPlayer);
    }

    public boolean isLegalMove(Move move) {
        return board.isLegalMove(currentPlayer, move);
    }

    public boolean isGameOver() {
        return board.isFull() || (!board.hasAnyMove(Board.BLACK) && !board.hasAnyMove(Board.WHITE));
    }

    public int blackScore() {
        return board.count(Board.BLACK);
    }

    public int whiteScore() {
        return board.count(Board.WHITE);
    }

    public int winner() {
        return board.winner();
    }

    public String getStatusMessage() {
        return isGameOver() ? gameOverMessage() : statusMessage;
    }

    public String getGameState() {
        return board.toCompactString() + "|" + currentPlayer;
    }

    public void setGameState(String state) {
        if (state == null || state.trim().isEmpty()) {
            restart();
            return;
        }

        String[] parts = state.split("\\|");
        this.board = Board.fromCompactString(parts[0]);
        this.currentPlayer = parts.length > 1 ? Integer.parseInt(parts[1]) : Board.BLACK;
        this.statusMessage = Board.colorName(currentPlayer) + " to move.";
        this.history.clear();
        normalizeTurn();
    }

    private String gameOverMessage() {
        int winner = winner();
        if (winner == Board.EMPTY) {
            return "Game over. Draw, " + blackScore() + "-" + whiteScore() + ".";
        }
        return "Game over. " + Board.colorName(winner) + " wins, " + blackScore() + "-" + whiteScore() + ".";
    }

    private static class Snapshot {
        private final Board board;
        private final int currentPlayer;
        private final String statusMessage;

        private Snapshot(Board board, int currentPlayer, String statusMessage) {
            this.board = board;
            this.currentPlayer = currentPlayer;
            this.statusMessage = statusMessage;
        }
    }
}
