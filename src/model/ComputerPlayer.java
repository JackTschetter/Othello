package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Minimax Othello player with optional alpha-beta pruning.
 */
public class ComputerPlayer extends Player {

    public enum Strategy {
        MINIMAX,
        ALPHA_BETA
    }

    private final int searchDepth;
    private final Strategy strategy;
    private final Random random;

    public ComputerPlayer() {
        this(4, Strategy.ALPHA_BETA);
    }

    public ComputerPlayer(int searchDepth, Strategy strategy) {
        this(searchDepth, strategy, new Random());
    }

    public ComputerPlayer(int searchDepth, Strategy strategy, Random random) {
        super(strategy == Strategy.MINIMAX ? "Minimax depth " + searchDepth : "Alpha-beta depth " + searchDepth);
        this.searchDepth = Math.max(1, searchDepth);
        this.strategy = strategy == null ? Strategy.ALPHA_BETA : strategy;
        this.random = random == null ? new Random() : random;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Move chooseMove(Game game) {
        if (game == null || game.isGameOver()) {
            return Move.PASS;
        }

        Board board = game.getBoard();
        int color = game.getCurrentPlayer();
        List<Move> legalMoves = board.legalMoves(color);
        if (legalMoves.isEmpty()) {
            return Move.PASS;
        }

        int bestScore = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();
        for (Move move : orderMoves(board, legalMoves, color)) {
            Board child = board.copy();
            child.applyMove(color, move);
            int score = search(child, Board.opponent(color), searchDepth - 1, color,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private int search(Board board, int playerToMove, int depth, int maximizingColor, int alpha, int beta) {
        if (depth == 0 || board.isFull()
                || (!board.hasAnyMove(Board.BLACK) && !board.hasAnyMove(Board.WHITE))) {
            return Heuristics.evaluate(board, maximizingColor);
        }

        List<Move> legalMoves = board.legalMoves(playerToMove);
        if (legalMoves.isEmpty()) {
            return search(board, Board.opponent(playerToMove), depth - 1, maximizingColor, alpha, beta);
        }

        boolean maximizing = playerToMove == maximizingColor;
        if (maximizing) {
            int value = Integer.MIN_VALUE;
            for (Move move : orderMoves(board, legalMoves, playerToMove)) {
                Board child = board.copy();
                child.applyMove(playerToMove, move);
                value = Math.max(value, search(child, Board.opponent(playerToMove), depth - 1,
                        maximizingColor, alpha, beta));
                if (strategy == Strategy.ALPHA_BETA) {
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return value;
        }

        int value = Integer.MAX_VALUE;
        for (Move move : orderMoves(board, legalMoves, playerToMove)) {
            Board child = board.copy();
            child.applyMove(playerToMove, move);
            value = Math.min(value, search(child, Board.opponent(playerToMove), depth - 1,
                    maximizingColor, alpha, beta));
            if (strategy == Strategy.ALPHA_BETA) {
                beta = Math.min(beta, value);
                if (alpha >= beta) {
                    break;
                }
            }
        }
        return value;
    }

    private List<Move> orderMoves(Board board, List<Move> legalMoves, int color) {
        List<Move> ordered = new ArrayList<>(legalMoves);
        ordered.sort(Comparator.comparingInt((Move move) -> {
            Board child = board.copy();
            child.applyMove(color, move);
            return Heuristics.evaluate(child, color);
        }).reversed());
        return ordered;
    }
}
