package logic;

import java.util.List;

import model.Board;
import model.Move;

public final class MoveGenerator {

    private MoveGenerator() {
    }

    public static List<Move> getMoves(Board board, int color) {
        return board.legalMoves(color);
    }
}
