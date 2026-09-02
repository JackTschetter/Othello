package model;

import java.util.Objects;

/**
 * A board coordinate for an Othello move. Rows and columns are zero-indexed.
 */
public final class Move {

    public static final Move PASS = new Move(-1, -1);

    private final int row;
    private final int col;

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public boolean isPass() {
        return row == -1 && col == -1;
    }

    public String toDisplayString() {
        return isPass() ? "pass" : row + " " + col;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Move)) {
            return false;
        }
        Move move = (Move) other;
        return row == move.row && col == move.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return isPass() ? "Move[pass]" : "Move[row=" + row + ", col=" + col + "]";
    }
}
