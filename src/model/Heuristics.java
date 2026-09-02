package model;

/**
 * Evaluation functions used by minimax and alpha-beta players.
 */
public final class Heuristics {

    private static final int[][] POSITION_WEIGHTS = {
            {120, -20, 20, 5, 5, 20, -20, 120},
            {-20, -40, -5, -5, -5, -5, -40, -20},
            {20, -5, 15, 3, 3, 15, -5, 20},
            {5, -5, 3, 3, 3, 3, -5, 5},
            {5, -5, 3, 3, 3, 3, -5, 5},
            {20, -5, 15, 3, 3, 15, -5, 20},
            {-20, -40, -5, -5, -5, -5, -40, -20},
            {120, -20, 20, 5, 5, 20, -20, 120}
    };

    private Heuristics() {
    }

    public static int evaluate(Board board, int color) {
        int opponent = Board.opponent(color);
        int blackMoves = board.legalMoves(Board.BLACK).size();
        int whiteMoves = board.legalMoves(Board.WHITE).size();
        boolean terminal = board.isFull() || (blackMoves == 0 && whiteMoves == 0);

        int diskDiff = board.count(color) - board.count(opponent);
        if (terminal) {
            return diskDiff * 100_000;
        }

        int ownMoves = color == Board.BLACK ? blackMoves : whiteMoves;
        int opponentMoves = color == Board.BLACK ? whiteMoves : blackMoves;
        int mobility = normalized(ownMoves, opponentMoves);
        int coinParity = normalized(board.count(color), board.count(opponent));
        int corners = cornerScore(board, color);
        int positional = positionalScore(board, color);
        int frontier = frontierScore(board, color);
        int empty = board.emptyCount();
        int coinWeight = empty < 16 ? 24 : 2;

        return coinWeight * coinParity
                + 85 * mobility
                + 275 * corners
                + 8 * positional
                + 15 * frontier;
    }

    private static int normalized(int own, int opponent) {
        int total = own + opponent;
        return total == 0 ? 0 : (100 * (own - opponent)) / total;
    }

    private static int cornerScore(Board board, int color) {
        int opponent = Board.opponent(color);
        int own = 0;
        int opposing = 0;
        int[][] corners = {
                {0, 0}, {0, Board.SIZE - 1}, {Board.SIZE - 1, 0}, {Board.SIZE - 1, Board.SIZE - 1}
        };

        for (int[] corner : corners) {
            int value = board.get(corner[0], corner[1]);
            if (value == color) {
                own++;
            } else if (value == opponent) {
                opposing++;
            }
        }
        return normalized(own, opposing);
    }

    private static int positionalScore(Board board, int color) {
        int opponent = Board.opponent(color);
        int score = 0;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                int value = board.get(row, col);
                if (value == color) {
                    score += POSITION_WEIGHTS[row][col];
                } else if (value == opponent) {
                    score -= POSITION_WEIGHTS[row][col];
                }
            }
        }
        return score;
    }

    private static int frontierScore(Board board, int color) {
        int opponent = Board.opponent(color);
        int ownFrontier = 0;
        int opponentFrontier = 0;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                int value = board.get(row, col);
                if (value != color && value != opponent) {
                    continue;
                }
                if (touchesEmptySquare(board, row, col)) {
                    if (value == color) {
                        ownFrontier++;
                    } else {
                        opponentFrontier++;
                    }
                }
            }
        }
        return -normalized(ownFrontier, opponentFrontier);
    }

    private static boolean touchesEmptySquare(Board board, int row, int col) {
        for (int rowDelta = -1; rowDelta <= 1; rowDelta++) {
            for (int colDelta = -1; colDelta <= 1; colDelta++) {
                if (rowDelta == 0 && colDelta == 0) {
                    continue;
                }
                if (board.get(row + rowDelta, col + colDelta) == Board.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }
}
