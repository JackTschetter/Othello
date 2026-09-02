package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mutable 8x8 Othello board with complete move generation and disk flipping.
 */
public class Board {

    public static final int INVALID = 2;
    public static final int EMPTY = 0;
    public static final int BLACK = -1;
    public static final int WHITE = 1;
    public static final int SIZE = 8;

    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    private final int[][] cells;

    public Board() {
        this.cells = new int[SIZE][SIZE];
        reset();
    }

    private Board(int[][] cells) {
        this.cells = cells;
    }

    public void reset() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = EMPTY;
            }
        }

        cells[3][3] = WHITE;
        cells[3][4] = BLACK;
        cells[4][3] = BLACK;
        cells[4][4] = WHITE;
    }

    public Board copy() {
        return new Board(snapshot());
    }

    public int[][] snapshot() {
        int[][] copy = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(cells[row], 0, copy[row], 0, SIZE);
        }
        return copy;
    }

    public int get(int row, int col) {
        return isInBounds(row, col) ? cells[row][col] : INVALID;
    }

    public void set(int row, int col, int value) {
        if (!isInBounds(row, col)) {
            throw new IllegalArgumentException("Board coordinate out of bounds: " + row + ", " + col);
        }
        if (value != EMPTY && value != BLACK && value != WHITE) {
            throw new IllegalArgumentException("Unsupported disk value: " + value);
        }
        cells[row][col] = value;
    }

    public List<Move> legalMoves(int color) {
        validateColor(color);

        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Move move = new Move(row, col);
                if (isLegalMove(color, move)) {
                    moves.add(move);
                }
            }
        }
        return Collections.unmodifiableList(moves);
    }

    public boolean hasAnyMove(int color) {
        validateColor(color);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (isLegalMove(color, new Move(row, col))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLegalMove(int color, Move move) {
        validateColor(color);

        if (move == null || move.isPass() || get(move.row(), move.col()) != EMPTY) {
            return false;
        }

        for (int[] direction : DIRECTIONS) {
            if (countFlips(color, move.row(), move.col(), direction[0], direction[1]) > 0) {
                return true;
            }
        }
        return false;
    }

    public int applyMove(int color, Move move) {
        if (!isLegalMove(color, move)) {
            throw new IllegalArgumentException("Illegal move for " + colorName(color) + ": " + move);
        }

        cells[move.row()][move.col()] = color;
        int flipped = 0;
        for (int[] direction : DIRECTIONS) {
            int count = countFlips(color, move.row(), move.col(), direction[0], direction[1]);
            for (int step = 1; step <= count; step++) {
                cells[move.row() + step * direction[0]][move.col() + step * direction[1]] = color;
            }
            flipped += count;
        }
        return flipped;
    }

    private int countFlips(int color, int row, int col, int rowDelta, int colDelta) {
        int opponent = opponent(color);
        int currentRow = row + rowDelta;
        int currentCol = col + colDelta;
        int count = 0;

        while (isInBounds(currentRow, currentCol) && cells[currentRow][currentCol] == opponent) {
            count++;
            currentRow += rowDelta;
            currentCol += colDelta;
        }

        if (count == 0 || !isInBounds(currentRow, currentCol)) {
            return 0;
        }
        return cells[currentRow][currentCol] == color ? count : 0;
    }

    public boolean isFull() {
        return emptyCount() == 0;
    }

    public int emptyCount() {
        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col] == EMPTY) {
                    count++;
                }
            }
        }
        return count;
    }

    public int count(int color) {
        validateColor(color);

        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col] == color) {
                    count++;
                }
            }
        }
        return count;
    }

    public int winner() {
        int black = count(BLACK);
        int white = count(WHITE);
        if (black == white) {
            return EMPTY;
        }
        return black > white ? BLACK : WHITE;
    }

    public String toCompactString() {
        StringBuilder builder = new StringBuilder(SIZE * SIZE);
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                builder.append(toChar(cells[row][col]));
            }
        }
        return builder.toString();
    }

    public static Board fromCompactString(String compact) {
        if (compact == null || compact.length() != SIZE * SIZE) {
            throw new IllegalArgumentException("Board string must contain exactly 64 cells.");
        }

        int[][] cells = new int[SIZE][SIZE];
        for (int index = 0; index < compact.length(); index++) {
            int row = index / SIZE;
            int col = index % SIZE;
            cells[row][col] = fromChar(compact.charAt(index));
        }
        return new Board(cells);
    }

    public static boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public static int opponent(int color) {
        validateColor(color);
        return -color;
    }

    public static String colorName(int color) {
        validateColor(color);
        return color == BLACK ? "Black" : "White";
    }

    public static char toChar(int value) {
        if (value == BLACK) {
            return 'B';
        }
        if (value == WHITE) {
            return 'W';
        }
        if (value == EMPTY) {
            return '.';
        }
        throw new IllegalArgumentException("Unsupported disk value: " + value);
    }

    public static int fromChar(char value) {
        switch (Character.toUpperCase(value)) {
            case 'B':
                return BLACK;
            case 'W':
                return WHITE;
            case '.':
            case '-':
            case '_':
                return EMPTY;
            default:
                throw new IllegalArgumentException("Unsupported board cell: " + value);
        }
    }

    private static void validateColor(int color) {
        if (color != BLACK && color != WHITE) {
            throw new IllegalArgumentException("Color must be Board.BLACK or Board.WHITE.");
        }
    }
}
